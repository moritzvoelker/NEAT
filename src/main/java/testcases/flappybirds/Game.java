package testcases.flappybirds;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Game {
    public static final double GRAVITY = 0.0025;
    public static final double JUMP_ACCELERATION = -0.02;
    public static final double PILLAR_DISTANCE = 0.5;
    public static final double PILLAR_VELOCITY = 0.005;
    public static final double PLAYER_X = 0.3;
    public static final int PILLAR_COUNT = 10;

    private Random generator;
    private Vector<Player> players;
    private Vector<Player> activePlayers;
    private Vector<Pillar> pillars;
    private double x;
    private int currentPillar;
    private int pillarsPassed;

    public Game(int playerCount) {
        players = new Vector<>(playerCount);
        activePlayers = new Vector<>(playerCount);
        for (int i = 0; i < playerCount; i++) {
            players.add(new Player());
        }
        activePlayers.addAll(players);
        pillars = new Vector<>();
        generator = new Random();
        for (int i = 0; i < PILLAR_COUNT; i++) {
            pillars.add(new Pillar(getRandomHoleY()));
        }
        x = PILLAR_DISTANCE;
        currentPillar = 0;
        pillarsPassed = 0;
    }

    public Game(int playerCount, long seed) {
        players = new Vector<>(playerCount);
        activePlayers = new Vector<>(playerCount);
        for (int i = 0; i < playerCount; i++) {
            players.add(new Player());
        }
        activePlayers.addAll(players);
        pillars = new Vector<>();
        generator = new Random(seed);
        for (int i = 0; i < PILLAR_COUNT; i++) {
            pillars.add(new Pillar(getRandomHoleY()));
        }
        x = PILLAR_DISTANCE;
        currentPillar = 0;
        pillarsPassed = 0;
    }


    // TODO: 06.10.2020 Aufsplitten von Game in Timer, Painter, usw. Überklasse verwaltet alles und läuft als Thread. Berechnet alles und wartet dann bis zum nächsten geplanten Aufruf vom Timer
    public boolean iterate() {
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
        if (PLAYER_X - Player.RADIUS > x + PILLAR_DISTANCE * currentPillar + Pillar.WIDTH) {
            currentPillar++;
            pillarsPassed++;
        }
        return activePlayers.size() != 0;
    }



    private boolean collisionAndScoring(Player player) {
        if (PLAYER_X + Player.RADIUS > x + PILLAR_DISTANCE * currentPillar
                && PLAYER_X - Player.RADIUS < x + PILLAR_DISTANCE * currentPillar + Pillar.WIDTH
                && (player.getY() + Player.RADIUS > pillars.get(currentPillar).getHoleY() + Pillar.HOLE_HEIGHT / 2
                    || player.getY() - Player.RADIUS < pillars.get(currentPillar).getHoleY() - Pillar.HOLE_HEIGHT / 2)
                || player.getY() + Player.RADIUS < 0.0 || player.getY() - Player.RADIUS > 1.0) {
            player.setScore(pillarsPassed + 1.0 - (x + PILLAR_DISTANCE * currentPillar + Pillar.WIDTH - PLAYER_X + Player.RADIUS));
            return true;
        }
        return false;
    }

    public void jump(int i) {
        players.get(i).setJump(true);
    }

    private double getRandomHoleY() {
        return generator.nextDouble() * (1.0 - Pillar.HOLE_HEIGHT) + Pillar.HOLE_HEIGHT / 2;
    }

    private void pillarOverflow() {
        pillars.add(pillars.remove(0));
        pillars.get(pillars.size() - 1).setHoleY(getRandomHoleY());
        x += PILLAR_DISTANCE;
        currentPillar--;
    }

    public void paint(Graphics g, int width, int height) {

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.GREEN);

        for (int i = 0; i < getPillars().size(); i++) {
            g.fillRect((int) ((getX() + PILLAR_DISTANCE * i) * height),
                    0,
                    (int) (Pillar.WIDTH * height),
                    (int) ((getPillars().get(i).getHoleY() - Pillar.HOLE_HEIGHT / 2) * height));

            g.fillRect((int) ((getX() + PILLAR_DISTANCE * i) * height),
                    (int) ((getPillars().get(i).getHoleY() + Pillar.HOLE_HEIGHT / 2) * height),
                    (int) (Pillar.WIDTH * height),
                    height - (int) ((getPillars().get(i).getHoleY() + Pillar.HOLE_HEIGHT / 2) * height));
        }

        g.setColor(Color.RED);

        for (Player player : getActivePlayers()) {
            g.fillOval((int) ((PLAYER_X - Player.RADIUS) * height),
                    (int) ((player.getY() - Player.RADIUS) * height),
                    (int) ((Player.RADIUS * 2) * height),
                    (int) ((Player.RADIUS * 2) * height));
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.SERIF, Font.BOLD, 30));
        g.drawString(String.valueOf(getPillarsPassed()), (int)(0.1 * height), (int)(0.1 * height));
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getActivePlayers() {
        return activePlayers;
    }

    public Vector<Pillar> getPillars() {
        return pillars;
    }

    public double getX() {
        return x;
    }

    public int getCurrentPillar() {
        return currentPillar;
    }

    public int getPillarsPassed() {
        return pillarsPassed;
    }
}
