package testcases;

import gui.Testcase;
import neat.Neat;
import neat.NeatConfiguration;
import neat.Organism;
import neat.Species;
import networkdisplay.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class XOR implements Testcase {
    private final NeatConfiguration configuration;
    private final Neat neat;
    private boolean hasAlreadyWorked;
    private int generation;

    private JPanel panel;
    private JLabel[] labels;

    public XOR() {
        configuration = new NeatConfiguration(2, 1);
        neat = new Neat(configuration);
        hasAlreadyWorked = false;
        generation = 0;

        panel = new JPanel(new GridLayout(4, 1));
        labels = new JLabel[] {new JLabel(), new JLabel(), new JLabel(), new JLabel()};

        for (JLabel label : labels) {
            panel.add(label);
        }
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

        double[][] possibleInputs = new double[][]{
                new double[]{0.0, 0.0},
                new double[]{0.0, 1.0},
                new double[]{1.0, 0.0},
                new double[]{1.0, 1.0}
        };

        for (int j = 0; j < labels.length; j++) {
            neat.getChamp().setInput(possibleInputs[j]);
            labels[j].setText("Input: " + possibleInputs[j][0] + ", " + possibleInputs[j][1] + "; Output: " + neat.getChamp().getOutput()[0]);
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
        return panel;
    }

    @Override
    public int[] getFitnessDistribution() {
        int[] distribution = new int[16];
        for (Species species :
                neat.getSpecies()) {
            for (Organism organism: species.getMembers()) {
                // TODO: 02.08.2020 If it works to good, it breaks (if fitness == 16.0 throws ArrayIndexOutOfBoundsException)
                distribution[(int)organism.getFitness()]++;
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
        return configuration.getPopulationSize();
    }

    private boolean evaluateGeneration() {
        List<double[]> inputs = new ArrayList<>(configuration.getPopulationSize());
        double[][] possibleInputs = new double[][]{
                new double[]{0.0, 0.0},
                new double[]{0.0, 1.0},
                new double[]{1.0, 0.0},
                new double[]{1.0, 1.0}
        };

        double[] fitness = new double[configuration.getPopulationSize()];
        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < configuration.getPopulationSize(); k++) {
                inputs.add(possibleInputs[j]);
            }
            neat.setInput(inputs);
            List<double[]> outputs = neat.getOutput();
            for (int k = 0; k < configuration.getPopulationSize(); k++) {
                fitness[k] += calculateDifference(possibleInputs[j], outputs.get(k));
            }
        }

        for (int k = 0; k < configuration.getPopulationSize(); k++) {
            fitness[k] = Math.pow(4.0 - fitness[k], 2);
        }

        neat.setFitness(fitness);
        System.out.println("Fitness of Champion: " + neat.getChamp().getFitness());
        if (works(neat.getChamp())) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        NeatConfiguration configuration = new NeatConfiguration(2, 1);
        Neat neat = new Neat(configuration);
        neat.firstGeneration();

        int generations = 0;
        Scanner scanner = new Scanner(System.in);
        int scannerOutput = 0;
        boolean hasAlreadyWorked = false;
        int i = 0;
        do {
            System.out.println("How many generations?");
            boolean inputNotSet = true;
            while (inputNotSet) {
                try {
                    String scannerOutputString = scanner.nextLine();
                    if (scannerOutputString.equals("show")) {
                        JFrame frame = new JFrame();
                        frame.setContentPane(new Display(neat.getChamp()));
                        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                        frame.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyPressed(KeyEvent e) {
                                frame.dispose();
                            }
                        });
                        frame.setSize(400, 400);
                        frame.setVisible(true);
                        continue;
                    }
                    scannerOutput = Integer.parseInt(scannerOutputString);
                    inputNotSet = false;
                } catch (NumberFormatException e) {
                    System.out.println("\u001B[31mInvalid input\u001B[0m");
                }
            }
            if (scannerOutput == -2) {
                neat.firstGeneration();
                generations = 0;
                i = 0;
                hasAlreadyWorked = false;
                continue;
            }
            generations += scannerOutput;
            if (scannerOutput < 0) {
                i = generations;
            }

            if (scannerOutput == -3) {
                neat.firstGeneration();
                generations = 1000;
                i = 0;
                hasAlreadyWorked = false;

            }

            for (; i < generations; i++) {
                System.out.println(i);


                List<double[]> inputs = new ArrayList<>(configuration.getPopulationSize());
                double[][] possibleInputs = new double[][]{
                        new double[]{0.0, 0.0},
                        new double[]{0.0, 1.0},
                        new double[]{1.0, 0.0},
                        new double[]{1.0, 1.0}
                };

                double[] fitness = new double[configuration.getPopulationSize()];
                for (int j = 0; j < 4; j++) {
                    for (int k = 0; k < configuration.getPopulationSize(); k++) {
                        inputs.add(possibleInputs[j]);
                    }
                    neat.setInput(inputs);
                    List<double[]> outputs = neat.getOutput();
                    for (int k = 0; k < configuration.getPopulationSize(); k++) {
                        fitness[k] += calculateDifference(possibleInputs[j], outputs.get(k));
                    }
                }

                for (int k = 0; k < configuration.getPopulationSize(); k++) {
                    fitness[k] = Math.pow(4.0 - fitness[k], 2);
                }

                neat.setFitness(fitness);
                System.out.println("Fitness of Champion: " + neat.getChamp().getFitness());
                if (works(neat.getChamp()) && !hasAlreadyWorked) {
                    hasAlreadyWorked = true;
                    System.out.println("\u001B[32mFound working organism.\u001B[0m");
                    break;
                }
                neat.nextGeneration();
            }
        } while (scannerOutput != -1);
    }

    private static double calculateDifference(double[] input, double[] output) {
        if (input[0] == input[1]) {
            return output[0];
        } else {
            return 1.0 - output[0];
        }
    }

    private static boolean works(Organism organism) {
        double[][] possibleInputs = new double[][]{
                new double[]{0.0, 0.0},
                new double[]{0.0, 1.0},
                new double[]{1.0, 0.0},
                new double[]{1.0, 1.0}
        };

        for (int i = 0; i < 4; i++) {
            organism.setInput(possibleInputs[i]);
            if ((possibleInputs[i][0] == possibleInputs[i][1] && organism.getOutput()[0] >= 0.5) || (possibleInputs[i][0] != possibleInputs[i][1] && organism.getOutput()[0] < 0.5)) {
                return false;
            }
        }

        return true;
    }
}
