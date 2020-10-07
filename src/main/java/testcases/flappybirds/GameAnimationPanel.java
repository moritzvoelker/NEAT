package testcases.flappybirds;

import gui.AnimationPanel;
import neat.Organism;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.List;

public class GameAnimationPanel extends AnimationPanel {
    private final int frameRate = 30;

    private BufferStrategy bufferStrategy;
    private Game game;
    private List<Organism> players;
    private long seed;

    public GameAnimationPanel() {
        this.setLayout(new BorderLayout());

        game = new Game(0, seed);
        players = new LinkedList<>();
    }

    public GameAnimationPanel(long seed, List<Organism> players) {
        this.setLayout(new BorderLayout());
        this.players = players;
        game = new Game(this.players.size(), seed);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (players.size() != 0) {
            game.paint(g, getWidth(), getHeight());
        }
    }

    @Override
    public void run() {
        boolean running = true;
        Canvas canvas = new Canvas();
        this.add(canvas, BorderLayout.CENTER);
        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();
        while (running) {
            long start = System.nanoTime();

            for (int i = 0; i < players.size(); i++) {
                double[] input = new double[4];
                input[0] = game.getPlayers().get(i).getY();
                input[1] = game.getPlayers().get(i).getVy();
                input[2] = game.getX() + Game.PILLAR_DISTANCE * game.getCurrentPillar() + Pillar.WIDTH - Game.PLAYER_X + Player.RADIUS;
                input[3] = game.getPillars().get(game.getCurrentPillar()).getHoleY();

                players.get(i).setInput(input);
            }

            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getOutput()[0] > 0.5) {
                    game.jump(i);
                }
            }
            if (isDisplayable()) {
                Graphics g;
                g = bufferStrategy.getDrawGraphics();
                paintComponent(g);
                g.dispose();

                bufferStrategy.show();
            }
            if (!game.iterate()) {
                game = new Game(players.size(), seed);
            }
            long elapsedTime = (System.nanoTime() - start) / 1_000_000;
            try {
                if (1000 / frameRate > elapsedTime) {
                    System.out.println("Sleeping");
                    Thread.sleep(1000 / frameRate - elapsedTime);
                }
                System.out.println("Woke up");
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public List<Organism> getPlayers() {
        return players;
    }

    public void setPlayers(List<Organism> players) {
        this.players = players;
        game = new Game(players.size(), seed);
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
        game = new Game(players.size(), seed);
    }
}
