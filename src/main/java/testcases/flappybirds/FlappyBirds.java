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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlappyBirds implements Testcase {

    private NeatConfiguration neatConfiguration;
    private Neat neat;
    private JPanel animationPanel;
    private Game game;
    private boolean hasAlreadyWorked;
    private int generation;
    private long seed;


    public FlappyBirds() {
        neatConfiguration = new NeatConfiguration(4, 1);

        neat = new Neat(neatConfiguration);
        animationPanel = new JPanel();
        seed = new Random().nextLong();

        reset();
    }

    @Override
    public void reset() {
        generation = 0;
        animationPanel = new JPanel(new GridLayout(4, 1));
    }

    @Override
    public void init() {
        neat.firstGeneration();
        evaluateGeneration();
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
    public JPanel getAnimationPanel() {
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
    // Inputs: Player y, Player velocity, distance to next pillar, y of hole of next pillar
    private boolean evaluateGeneration() {
        seed++;
        Game game = new Game(neatConfiguration.getPopulationSize(), 0, seed);
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

        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.SERIF, Font.BOLD, 30));
        g.drawString(String.valueOf(game.getPillarsPassed()), (int)(0.1 * height), (int)(0.1 * height));
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
