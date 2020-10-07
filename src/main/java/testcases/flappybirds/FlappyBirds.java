package testcases.flappybirds;

import gui.AnimationPanel;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlappyBirds implements Testcase {

    private NeatConfiguration neatConfiguration;
    private Neat neat;
    private GameAnimationPanel animationPanel;
    private Game game;
    private boolean hasAlreadyWorked;
    private int generation;
    private long seed;


    public FlappyBirds() {
        neatConfiguration = new NeatConfiguration(4, 1);

        neat = new Neat(neatConfiguration);
        seed = new Random().nextLong();

        reset();
    }

    @Override
    public void reset() {
        generation = 0;
        animationPanel = new GameAnimationPanel();
    }

    @Override
    public void init() {
        neat.firstGeneration();
        evaluateGeneration();
        updateAnimationPanel();
        generation = 1;
        hasAlreadyWorked = false;
    }

    @Override
    public int doNGenerations(int n) {
        int i;
        for (i = 0; i < n; i++) {
            neat.nextGeneration();
            System.out.println("Generation " + generation);
            generation++;
            if (evaluateGeneration() && !hasAlreadyWorked) {
                hasAlreadyWorked = true;
                System.out.println("\u001B[32mFound working organism.\u001B[0m");
                break;
            }
        }
        updateAnimationPanel();
        return i;
    }

    @Override
    public int getGeneration() {
        return generation;
    }

    @Override
    public Organism getChamp() {
        return neat.getChamp();
    }

    @Override
    public AnimationPanel getAnimationPanel() {
        return animationPanel;
    }

    @Override
    public int[] getFitnessDistribution() {
        int[] distribution = new int[11];
        for (Species species :
                neat.getSpecies()) {
            for (Organism organism: species.getMembers()) {
                if (organism.getFitness() >= 10.0) {
                    distribution[10]++;
                } else {
                    distribution[(int) organism.getFitness()]++;
                }
            }
        }
        return distribution;
    }

    @Override
    public List<Species> getSpecies() {
        return neat.getSpecies();
    }

    @Override
    public int getPopulationSize() {
        return neatConfiguration.getPopulationSize();
    }

    @Override
    public boolean hasWorkingOrganism() {
        return neat.getChamp().getFitness() >= 10.0;
    }

    private void updateAnimationPanel() {
        ArrayList<Organism> players = new ArrayList<>(neat.getSpecies().size());
        for (Species species : neat.getSpecies()) {
            players.add(species.getMembers().get(0));
        }
        animationPanel.setPlayers(players);
        animationPanel.setSeed(seed);
    }

    // Inputs: Player y, Player velocity, distance to next pillar, y of hole of next pillar
    private boolean evaluateGeneration() {
        seed++;
        Game game = new Game(neatConfiguration.getPopulationSize(), seed);
        List<double[]> inputs = new ArrayList<>(neatConfiguration.getPopulationSize());
        List<double[]> outputs;

        do {
            for (int i = 0; i < neatConfiguration.getPopulationSize(); i++) {
                double[] input = new double[4];
                input[0] = game.getPlayers().get(i).getY();
                input[1] = game.getPlayers().get(i).getVy();
                input[2] = game.getX() + Game.PILLAR_DISTANCE * game.getCurrentPillar() + Pillar.WIDTH - Game.PLAYER_X + Player.RADIUS;
                input[3] = game.getPillars().get(game.getCurrentPillar()).getHoleY();

                inputs.add(input);
            }
            neat.setInput(inputs);
            inputs.clear();

            outputs = neat.getOutput();
            for (int i = 0; i < neatConfiguration.getPopulationSize(); i++) {
                if (outputs.get(i)[0] > 0.5) {
                    game.jump(i);
                }
            }
        } while(game.iterate());

        double[] fitness = new double[neatConfiguration.getPopulationSize()];
        for (int i = 0; i < neatConfiguration.getPopulationSize(); i++) {
            fitness[i] = game.getPlayers().get(i).getScore();
        }
        neat.setFitness(fitness);


        return hasWorkingOrganism();
    }

    public static void main(String[] args) {
        FlappyBirds flappyBirds = new FlappyBirds();
        JFrame jFrame = new JFrame();

        flappyBirds.animationPanel.setLayout(new BorderLayout());
        Canvas canvas = new Canvas();
        flappyBirds.animationPanel.add(canvas, BorderLayout.CENTER);


        jFrame.setContentPane(flappyBirds.animationPanel);
        jFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    flappyBirds.game.jump(0);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
        });
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    flappyBirds.game.jump(0);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
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


        flappyBirds.game = new Game(1);
        long start;
        long startForTimer = System.currentTimeMillis();
        int k = 0;
        while (true) {
            start = System.nanoTime();
            Graphics g;
            g = strategy.getDrawGraphics();
            flappyBirds.game.paint(g, flappyBirds.animationPanel.getWidth(), flappyBirds.animationPanel.getHeight());
            g.dispose();

            strategy.show();
            if (!flappyBirds.game.iterate()) {
                flappyBirds.game = new Game(1);
            }

            k++;
            if (k >= 30) {
                System.out.println("Needed time: " + (System.currentTimeMillis() - startForTimer));
                k = 0;
                startForTimer = System.currentTimeMillis();
            }

            long elapsedTime = (System.nanoTime() - start) / 1_000_000;
            try {
                if (1000 / 30 > elapsedTime) {
                    Thread.sleep(1000 / 30 - elapsedTime);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
