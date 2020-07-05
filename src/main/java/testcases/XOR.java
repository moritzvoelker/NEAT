package testcases;

import neat.Neat;
import neat.NeatConfiguration;
import neat.Organism;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class XOR {

    public static void main(String[] args) {
        NeatConfiguration configuration = new NeatConfiguration(2, 1);
//        configuration.setMutationRateNode(0.05);
//        configuration.setMutationRateConnection(0.1);
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
                    scannerOutput = Integer.parseInt(scanner.nextLine());
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

    private static double calculateDifference(double[] input, double[]output) {
        if (input[0] == input[1]) {
            return output[0];
        } else {
            return 1.0 - output[0];
        }
    }

    private static double calculateFitness(double[] input, double[] output) {
        if (input[0] == input[1]) {
            return Math.pow(1.0 - output[0], 2);
        } else {
            return Math.pow(output[0], 2);
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
