package testcases;

import neat.Neat;
import neat.NeatConfiguration;
import neat.Organism;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class XOR {

    // TODO: 26.04.2020 Champion gets deleted, Fitness does not grow above 2.0, After a few generations only 1 Species 
    public static void main(String[] args) {
        NeatConfiguration configuration = new NeatConfiguration(2, 1);
        Neat neat = new Neat(configuration);
        neat.firstGeneration();;

        int generations = 0;
        Scanner scanner = new Scanner(System.in);
        boolean hasAlreadyWorked = false;
        int i = 0;
        do {
            System.out.println("How many generations?");
            generations += scanner.nextInt();

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
                        fitness[k] += calculateFitness(possibleInputs[j], outputs.get(k));
                    }
                }

                neat.setFitness(fitness);
                System.out.println("Fitness of Champion: " + neat.getChamp().getFitness());
                if (works(neat.getChamp()) && !hasAlreadyWorked) {
                    hasAlreadyWorked = true;
                    System.out.println("\u001B[31m Found working organism. \u001B[0m");
                    break;
                }
                neat.nextGeneration();
            }
        } while (generations >= 0);
    }

    private static double calculateFitness(double[] input, double[] output) {
        if (input[0] == input[1]) {
            return Math.pow(1.0 - output[0], 2);
        } else {
            return Math.pow(0.0 - output[0], 2);
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
