/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package testcases;

import gui.AnimationPanel;
import gui.DefaultWidgetsPanel;
import gui.Testcase;
import gui.WidgetsPanel;
import neat.*;
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

    private AnimationPanel animationPanel;
    private JLabel[] labels;

    public XOR() {
        configuration = new NeatConfiguration(2, 1).setSpeciationThreshhold(1.0)/*.setSurvivalRate(1.0)*//*.setPopulationSize(150).setSpeciationThreshhold(4.0).setPurgeAge(9).setMaxGenerationsWithoutImprovement(10)*/;

        neat = new Neat(configuration);
        animationPanel = new AnimationPanel() {
            @Override
            public void run() {

            }
        };
        animationPanel.setLayout(new GridLayout(4, 1));
        labels = new JLabel[] {new JLabel(), new JLabel(), new JLabel(), new JLabel()};

        for (JLabel label : labels) {
            animationPanel.add(label);
        }


        reset();
    }

    @Override
    public void reset() {
        generation = 0;
        for (JLabel label : labels) {
            label.setText("");
        }
    }

    @Override
    public void init() throws InvalidConfigurationException {
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
    public WidgetsPanel getWidgetsPanel() {
        return new DefaultWidgetsPanel(this);
    }

    @Override
    public AnimationPanel getAnimationPanel() {
        return animationPanel;
    }

    @Override
    public int[] getFitnessDistribution() {
        int[] distribution = new int[16];
        for (Species species :
                neat.getSpecies()) {
            for (Organism organism: species.getMembers()) {
                if (organism.getFitness() == 16.0) {
                    distribution[15]++;
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
        return configuration;
    }

    @Override
    public boolean hasWorkingOrganism() {
        return works(neat.getChamp());
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

        for (int j = 0; j < labels.length; j++) {
            neat.getChamp().setInput(possibleInputs[j]);
            labels[j].setText("Input: " + possibleInputs[j][0] + ", " + possibleInputs[j][1] + "; Output: " + neat.getChamp().getOutput()[0]);
        }

        return works(neat.getChamp());
    }

    public static void main(String[] args) throws InvalidConfigurationException {
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
