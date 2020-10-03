package testcases.flappybirds;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game extends Thread {
    public static final double GRAVITY = 0.0025;
    public static final double JUMP_ACCELERATION = -0.02;
    public static final double PILLAR_DISTANCE = 0.5;
    public static final double PILLAR_VELOCITY = 0.005;
    public static final double PLAYER_X = 0.3;
    public static final int PILLAR_COUNT = 10;

    private List<Player> players;
    private List<Player> activePlayers;
    private LinkedList<Pillar> pillars;
    private double x;
    private int currentPillar;
    private int frameRate;

    public Game(int playerCount, int frameRate) {
        players = new ArrayList<>(playerCount);
        activePlayers = new ArrayList<>(playerCount);
        for (int i = 0; i < playerCount; i++) {
            players.add(new Player());
        }
        activePlayers.addAll(players);
        pillars = new LinkedList<>();
        for (int i = 0; i < PILLAR_COUNT; i++) {
            pillars.add(new Pillar(getRandomHoleY()));
        }
        x = 1.0;
        currentPillar = 0;
        this.frameRate = frameRate;
    }

    @Override
    public void run() {
        while (true) {
            activePlayers.forEach(player -> {
                if (player.isJump()) {
                    player.setVy(JUMP_ACCELERATION);
                    player.setJump(false);
                } else {
                    player.applyGravity(GRAVITY);
                }
            });
            activePlayers.forEach(Player::applyVelocity);
            x -= PILLAR_VELOCITY;
            if (x <= 0.0 - Pillar.WIDTH) {
                pillarOverflow();
            }
            activePlayers.removeIf(this::collisionAndScoring);
            if (activePlayers.size() == 0) {
                break;
            }
            try {
                //noinspection BusyWait
                Thread.sleep(frameRate == 0 ? 0 : 1000 / frameRate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean collisionAndScoring(Player player) {
        if (PLAYER_X + Player.RADIUS > x + PILLAR_DISTANCE * currentPillar
                && PLAYER_X - Player.RADIUS < x + PILLAR_DISTANCE * currentPillar + Pillar.WIDTH
                && (player.getY() > pillars.get(currentPillar).getHoleY() + Pillar.HOLE_HEIGHT / 2 || player.getY() < pillars.get(currentPillar).getHoleY() - Pillar.HOLE_HEIGHT / 2)) {
            return true;
        }
        if (PLAYER_X - Player.RADIUS > x + PILLAR_DISTANCE * currentPillar + Pillar.WIDTH) {
            player.score();
            currentPillar++;
        }
        return false;
    }

    public void jump(int i) {
        players.get(i).setJump(true);
    }

    private double getRandomHoleY() {
        return Math.random() * 0.8 + 0.1;
    }

    private void pillarOverflow() {
        pillars.add(pillars.removeFirst());
        pillars.getLast().setHoleY(getRandomHoleY());
        x += PILLAR_DISTANCE;
        currentPillar--;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getActivePlayers() {
        return activePlayers;
    }

    public LinkedList<Pillar> getPillars() {
        return pillars;
    }

    public double getX() {
        return x;
    }
}
