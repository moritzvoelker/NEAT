package testcases.carrace;


import gui.AnimationPanel;
import gui.DefaultWidgetsPanel;
import gui.Testcase;
import gui.WidgetsPanel;
import neat.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CarRace implements Testcase {

    private NeatConfiguration neatConfiguration;
    private Neat neat;
    private GameAnimationPanel animationPanel;
    private Game game;
    private boolean hasAlreadyWorked;
    private int generation;
    private long seed;

    public CarRace() {
        neatConfiguration = new NeatConfiguration(5, 2).setPrecalculateNodes(true).setSpeciationThreshhold(8.0);
        neatConfiguration.setSpeciationThreshhold(1.0).setSurvivalRate(1.0);
        animationPanel = new GameAnimationPanel();

        neat = new Neat(neatConfiguration);
        seed = new Random().nextLong();

        reset();
    }

    @Override
    public void reset() {
        generation = 0;
        //animationPanel = new GameAnimationPanel();
    }

    @Override
    public void init() throws InvalidConfigurationException {
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
    public WidgetsPanel getWidgetsPanel() {
        return new DefaultWidgetsPanel(this);
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
    public NeatConfiguration getConfiguration() {
        return neatConfiguration;
    }

    @Override
    public boolean hasWorkingOrganism() {
        return false;
    }

    private void updateAnimationPanel() {
        ArrayList<Organism> players = new ArrayList<>(neat.getSpecies().size());
        for (Species species : neat.getSpecies()) {
            players.add(species.getChamp());
        }
        animationPanel.setPlayers(players);
        animationPanel.setSeed(seed);
    }

    // Inputs: Player y, Player velocity, distance to next pillar, y of hole of next pillar
    private boolean evaluateGeneration() {
        seed++;
        Game game;
        List<double[]> inputs = new ArrayList<>(neatConfiguration.getPopulationSize());
        List<double[]> outputs;

        double[] fitness = new double[neatConfiguration.getPopulationSize()];
//        for (int p = 0; p < neatConfiguration.getPopulationSize()) {
//            fitness[p] = 0.0;
//        }
        int num_samples = 50;

        for (int p = 0; p < num_samples; p++) {
            List<Organism> organisms = new ArrayList<>(neatConfiguration.getPopulationSize());
            for (Species specie : neat.getSpecies()) {
                for (Organism organism : specie.getMembers()) {
                    organisms.add(organism);
                }
            }
            game = new Game(organisms, seed);
            game.startGame(10, 10, 8);
            do {
                for (int i = 0; i < neatConfiguration.getPopulationSize(); i++) {
                    double[] input = new double[5];
                    input[0] = game.getPlayers().get(i).getX();
                    input[1] = game.getPlayers().get(i).getY();
                    input[2] = game.getRaceTrack().checkOffCourse(game.getPlayers().get(i).getX() + Player.getLength() * Math.cos(game.getPlayers().get(i).getOrientation()), game.getPlayers().get(i).getY() + (Player.getLength() * Math.sin(game.getPlayers().get(i).getOrientation()))) ? 1.0 : 0.0;
                    input[3] = game.getRaceTrack().checkOffCourse(game.getPlayers().get(i).getX() + Player.getLength() * Math.cos(game.getPlayers().get(i).getOrientation() + Math.PI / 6), game.getPlayers().get(i).getY() + (Player.getLength() * Math.sin(game.getPlayers().get(i).getOrientation() + Math.PI / 6))) ? 1.0 : 0.0;
                    input[3] = game.getRaceTrack().checkOffCourse(game.getPlayers().get(i).getX() + Player.getLength() * Math.cos(game.getPlayers().get(i).getOrientation() - Math.PI / 6), game.getPlayers().get(i).getY() + (Player.getLength() * Math.sin(game.getPlayers().get(i).getOrientation() - Math.PI / 6))) ? 1.0 : 0.0;

                    inputs.add(input);
                }
                neat.setInput(inputs);
                inputs.clear();

            } while (game.iterate());

            for (int i = 0; i < neatConfiguration.getPopulationSize(); i++) {
                fitness[i] += Math.pow(game.getPlayers().get(i).getScore(), 1);
            }
        }
        for (int p = 0; p < neatConfiguration.getPopulationSize(); p++) {
            fitness[p] /= num_samples;
        }
        neat.setFitness(fitness);


        return hasWorkingOrganism();
    }

    public static void main(String args[]) {
        CarRace carRace = new CarRace();
        JFrame jFrame = new JFrame();

        carRace.animationPanel.setLayout(new BorderLayout());
        carRace.animationPanel.getGame().addManuelPlayer(new ManuelController(jFrame));


        jFrame.setContentPane(carRace.animationPanel);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(640, 400);

        jFrame.setVisible(true);
        carRace.animationPanel.getGame().startGame(5, 5, 8);
        long lasttime = System.currentTimeMillis();
        while (true) {
            long curtime = System.currentTimeMillis() - lasttime;
            if (curtime > 100) {
                if (!carRace.animationPanel.getGame().iterate()) {
                    carRace.animationPanel.getGame().startGame(5, 5, 8);
                }
                lasttime = System.currentTimeMillis();
                carRace.animationPanel.repaint();
            }

        }

    }
}
