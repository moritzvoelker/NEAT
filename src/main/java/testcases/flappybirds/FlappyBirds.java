package testcases.flappybirds;

import gui.Testcase;
import neat.Neat;
import neat.NeatConfiguration;
import neat.Organism;
import neat.Species;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class FlappyBirds implements Testcase {

    private NeatConfiguration neatConfiguration;
    private Neat neat;
    private JPanel animationPanel;
    private Game game;


    public FlappyBirds() {
        neatConfiguration = new NeatConfiguration(0, 1);

        neat = new Neat(neatConfiguration);
        animationPanel = new JPanel();
        game = new Game(1, 30);

        reset();
    }

    @Override
    public void reset() {

    }

    @Override
    public void init() {

    }

    @Override
    public int doNGenerations(int n) {
        return 0;
    }

    @Override
    public int getGeneration() {
        return 0;
    }

    @Override
    public Organism getChamp() {
        return null;
    }

    @Override
    public JPanel getAnimationPanel() {
        return null;
    }

    @Override
    public int[] getFitnessDistribution() {
        return new int[0];
    }

    @Override
    public List<Species> getSpecies() {
        return null;
    }

    @Override
    public int getPopulationSize() {
        return 0;
    }

    @Override
    public boolean hasWorkingOrganism() {
        return false;
    }

    private void paintGame() {
        Graphics g = animationPanel.getGraphics();
        int height = animationPanel.getHeight();

        g.setColor(Color.GREEN);

        for (int i = 0; i < game.getPillars().size(); i++) {
            g.fillRect((int) ((game.getX() + Game.PILLAR_DISTANCE * i) * height),
                    0,
                    (int) (Pillar.WIDTH * height),
                    (int) ((game.getPillars().get(i).getHoleY() - Pillar.HOLE_HEIGHT / 2) * height));

            g.fillRect((int) ((game.getX() + Game.PILLAR_DISTANCE * i) * height),
                    (int) ((game.getPillars().get(i).getHoleY() + Pillar.HOLE_HEIGHT / 2) * height),
                    (int) (Pillar.WIDTH * height),
                    height - (int) ((game.getPillars().get(i).getHoleY() + Pillar.HOLE_HEIGHT / 2) * height));
        }

        g.setColor(Color.RED);

        for (Player player : game.getPlayers()) {
            g.fillOval((int) ((Game.PLAYER_X - Player.RADIUS) * height),
                    (int) ((player.getY() - Player.RADIUS) * height),
                    (int) ((Player.RADIUS * 2) * height),
                    (int) ((Player.RADIUS * 2) * height));
        }
        animationPanel.validate();
        animationPanel.repaint();
    }

    public static void main(String[] args) {
        FlappyBirds flappyBirds = new FlappyBirds();
        JFrame jFrame = new JFrame();
        jFrame.setContentPane(flappyBirds.animationPanel);
        jFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    flappyBirds.game.getPlayers().get(0).setJump(true);
                }
            }
        });
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(640, 400);
        jFrame.setVisible(true);
        flappyBirds.game.start();
        while (true) {
            flappyBirds.paintGame();
            if (!flappyBirds.game.isAlive()) {
                flappyBirds.game = new Game(1, 30);
                flappyBirds.game.start(); 
            }
        }
    }
}
