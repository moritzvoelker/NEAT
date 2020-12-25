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

import java.util.ArrayList;
import java.util.List;

public class MetaXOR implements Testcase {
    private NeatConfiguration configuration;
    private Neat neat;
    private XOR[] xors;
    private boolean hasAlreadyWorked;
    private int generation;

    public MetaXOR() {
        configuration = new NeatConfiguration(1, 19)
                .setCreateStrategy(new LinearNodeCreateStrategy())
                .setBiasNodeEnabled(false);
        neat = new Neat(configuration);
        xors = new XOR[configuration.getPopulationSize()];
        for (int i = 0; i < configuration.getPopulationSize(); i++) {
            xors[i] = new XOR();
        }
        reset();
    }

    @Override
    public void reset() {
        generation = 0;
        hasAlreadyWorked = false;
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
            evaluateGeneration();
            /*if (evaluateGeneration() && !hasAlreadyWorked) {
                hasAlreadyWorked = true;
                System.out.println("\u001B[32mFound working organism.\u001B[0m");
                break;
            }*/
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
        return new AnimationPanel() {
            @Override
            public void run() {
            }
        };
    }

    @Override
    public int[] getFitnessDistribution() {
        return new int[0];
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
        return hasAlreadyWorked;
    }

    private void evaluateGeneration() {
        double[] input = new double[] {1.0};
        List<double[]> inputs = new ArrayList<>(configuration.getPopulationSize());
        for (int i = 0; i < configuration.getPopulationSize(); i++) {
            inputs.add(input);
        }
        neat.setInput(inputs);
        List<double[]> outputs = neat.getOutput();
        double[] fitness = new double[configuration.getPopulationSize()];
        for (int i = 0; i < configuration.getPopulationSize(); i++) {
            mapOutputs(outputs.get(i), xors[i].getConfiguration());
            xors[i].reset();
            try {
                xors[i].init();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
            int gens = xors[i].doNGenerations(30);
            if (gens < 30) {
                xors[i].doNGenerations(30 - gens);
            }
            fitness[i] = xors[i].getChamp().getFitness();
        }

        neat.setFitness(fitness);
    }

    private NeatConfiguration mapOutputs(double[] outputs, NeatConfiguration configuration) {
        return configuration
                .setPopulationSize(Math.abs((int) (outputs[0] * 100)) + 1)
                .setPurgeAge(Math.abs((int) (outputs[1] * 10)))
                .setMaxGenerationsWithoutImprovement((int) (Math.max(Math.abs(outputs[1]), Math.abs(outputs[2])) * 10) + 1)
                .setSpeciationThreshhold(outputs[3])
                .setMutateNodeRate(ensureIsPercentage(outputs[4]))
                .setMutateConnectionRate(ensureIsPercentage(outputs[5]))
                .setMutateWeightRate(ensureIsPercentage(outputs[6]))
                .setPerturbRate(ensureIsPercentage(outputs[7]))
                .setMutationRateEnablement(ensureIsPercentage(outputs[8]))
                .setStepSize(outputs[9])
                .setDisableRate(ensureIsPercentage(outputs[10]))
                .setMateInterspeciesRate(ensureIsPercentage(outputs[11]))
                .setMutateOnlyRate(ensureIsPercentage(outputs[12]))
                .setSurvivalRate(ensureIsPercentage(outputs[13]))
                .setC1(outputs[14])
                .setC2(outputs[15])
                .setC3(outputs[16])
                .setBiasNodeEnabled(outputs[17] >= 0)
                .setMaxConnectionAbsoluteValue(Math.abs(outputs[18]));
    }

    private double ensureIsPercentage(double toEnsure) {
        toEnsure = Math.abs(toEnsure);
        while (toEnsure > 1.0) {
            toEnsure /= 10.0;
        }
        return toEnsure;
    }
}
