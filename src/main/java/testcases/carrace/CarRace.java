package testcases.carrace;


import gui.AnimationPanel;
import gui.DefaultWidgetsPanel;
import gui.Testcase;
import gui.WidgetsPanel;
import neat.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CarRace implements Testcase {

    private NeatConfiguration neatConfiguration;
    private Neat neat;
    private GameAnimationPanel animationPanel;
    private Game game;
    private GameConfiguration gameConfiguration;
    private boolean hasAlreadyWorked;
    private int generation;
    private long seed;

    public CarRace() {
        gameConfiguration = new GameConfiguration().setLength(50).setSizex(25).setSizey(25);

        neatConfiguration = new NeatConfiguration(gameConfiguration.getNumRays(), 2).setPrecalculateNodes(true).setSpeciationThreshhold(2.0).setMutateNodeRate(0.15).setPopulationSize(10);
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
        return neat.getChamp().getFitness() >= 101;
    }

    private void updateAnimationPanel() {
////        ArrayList<Organism> players = new ArrayList<>(neat.getSpecies().size());
//        for (Species species : neat.getSpecies()) {
//            players.add(species.getChamp());
//        }
        ArrayList<Organism> players = new ArrayList<>(neatConfiguration.getPopulationSize());
        for (Species species : neat.getSpecies()) {
            for (Organism organism : species.getMembers()) {
                players.add(organism);
            }
        }
        animationPanel.setControllers(players);
        animationPanel.setSeed(seed);
        animationPanel.startGame();
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
        int num_samples = 100;

        List<Organism> organisms = new ArrayList<>(neatConfiguration.getPopulationSize());
        for (Species specie : neat.getSpecies()) {
            organisms.addAll(specie.getMembers());
        }

        game = new Game(neatConfiguration.getPopulationSize(), seed);


        List<Player> players = game.getPlayers();
        List<NNController> controllers = new ArrayList<>(neatConfiguration.getPopulationSize());
        for (int i = 0; i < neatConfiguration.getPopulationSize(); i++) {
            controllers.add(new NNController(players.get(i), organisms.get(i), gameConfiguration));
        }

        OrganismPreCalculator calculator = new OrganismPreCalculator(8);

        for (int p = 0; p < num_samples; p++) {
            game.startGame(gameConfiguration);
            do {
                for (NNController controller : controllers) {
                    controller.extractInput(game, calculator);
                }
                for (NNController controller : controllers) {
                    controller.controlPlayer();
                }

            } while (game.iterate());

            for (int i = 0; i < neatConfiguration.getPopulationSize(); i++) {
                fitness[i] += Math.pow(players.get(i).getScore() / gameConfiguration.getLength(), 2) * 100 + 1;
            }
        }
        for (int p = 0; p < neatConfiguration.getPopulationSize(); p++) {
            fitness[p] /= num_samples;
        }
//        fitness[0] = 1.0;
        neat.setFitness(fitness);


        return hasWorkingOrganism();
    }

    public static void main(String args[]) {
        CarRace carRace = new CarRace();
        JFrame jFrame = new JFrame();

        carRace.animationPanel.setLayout(new BorderLayout());

        Controller controller = new ManuelController(jFrame, carRace.animationPanel.getGame().addPlayer());


        jFrame.setContentPane(carRace.animationPanel);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(640, 400);

        jFrame.setVisible(true);
        carRace.animationPanel.getGame().startGame(5, 5, 8);
        controller.controlPlayer();
        long lasttime = System.currentTimeMillis();
        while (true) {
            long curtime = System.currentTimeMillis() - lasttime;
            if (curtime > 1000 / 30) {
                if (!carRace.animationPanel.getGame().iterate()) {
                    carRace.animationPanel.getGame().startGame(5, 5, 8);
                }
                lasttime = System.currentTimeMillis();
                controller.controlPlayer();
                carRace.animationPanel.repaint();
            }

        }

    }
}
