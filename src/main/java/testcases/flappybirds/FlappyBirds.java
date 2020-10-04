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
import java.awt.image.BufferStrategy;
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

    private void paintGame(Graphics g, int height) {
        //Graphics2D g =  (Graphics2D) strategy.getDrawGraphics();
        //int height = animationPanel.getHeight();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, animationPanel.getWidth(), animationPanel.getHeight());

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

    }

    public static void main(String[] args) {
        FlappyBirds flappyBirds = new FlappyBirds();
        JFrame jFrame = new JFrame();

        flappyBirds.animationPanel.setLayout(new BorderLayout());
        Canvas canvas = new Canvas();
        flappyBirds.animationPanel.add(canvas, BorderLayout.CENTER);


        jFrame.setContentPane(flappyBirds.animationPanel);
        flappyBirds.animationPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    flappyBirds.game.getPlayers().get(0).setJump(true);
                }
            }
        });
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(640, 400);

        canvas.setSize(640, 400);
        canvas.setIgnoreRepaint(true);
        jFrame.setVisible(true);

        canvas.createBufferStrategy(2);
        BufferStrategy strategy = canvas.getBufferStrategy();


        flappyBirds.game.start();
        while (true) {
            Graphics g;
            g = strategy.getDrawGraphics();
            flappyBirds.paintGame(g, flappyBirds.animationPanel.getHeight());
            g.dispose();

            strategy.show();

            if (!flappyBirds.game.isAlive()) {
                flappyBirds.game = new Game(1, 30);
                flappyBirds.game.start(); 
            }
        }
    }
}
