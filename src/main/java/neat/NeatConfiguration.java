/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package neat;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class NeatConfiguration implements Serializable {
    private int populationSize = 150;
    private int purgeAge = 15;
    private int maxGenerationsWithoutImprovement = 20;
    private double speciationThreshhold = 3.0;
    private double mutateNodeRate = 0.03;
    private double mutateConnectionRate = 0.05;
    private double mutateWeightRate = 0.8;
    private double perturbRate = 0.9;
    private double mutationRateEnablement = 0.01;
    private double stepSize = 0.1;
    private double disableRate = 0.75;
    private double mateInterspeciesRate = 0.2;
    private double mutateOnlyRate = 0.25; // => mateRate = 0.75
    private double survivalRate = 0.5;
    private double c1 = 1.0;
    private double c2 = 1.0;
    private double c3 = 0.4;
    private int inputCount;
    private int outputCount;
    private boolean biasNodeEnabled = true;
    private double maxConnectionAbsoluteValue = 2.0;
    private int numberOfThreads = 4;
    private boolean precalculateNodes = true;

    private CreateStrategy createStrategy = new DefaultCreateStrategy();

    public NeatConfiguration(int inputCount, int outputCount) {
        this.inputCount = inputCount;
        this.outputCount = outputCount;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public NeatConfiguration setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public int getPurgeAge() {
        return purgeAge;
    }

    public NeatConfiguration setPurgeAge(int purgeAge) throws IllegalStateException {
        this.purgeAge = purgeAge;
        return this;
    }

    public int getMaxGenerationsWithoutImprovement() {
        return maxGenerationsWithoutImprovement;
    }

    public NeatConfiguration setMaxGenerationsWithoutImprovement(int maxGenerationsWithoutImprovement) {
        this.maxGenerationsWithoutImprovement = maxGenerationsWithoutImprovement;
        return this;
    }

    public double getSpeciationThreshhold() {
        return speciationThreshhold;
    }

    public NeatConfiguration setSpeciationThreshhold(double speciationThreshhold) {
        this.speciationThreshhold = speciationThreshhold;
        return this;
    }

    public double getMutateNodeRate() {
        return mutateNodeRate;
    }

    public NeatConfiguration setMutateNodeRate(double mutateNodeRate) {
        this.mutateNodeRate = mutateNodeRate;
        return this;
    }

    public double getMutateConnectionRate() {
        return mutateConnectionRate;
    }

    public NeatConfiguration setMutateConnectionRate(double mutateConnectionRate) {
        this.mutateConnectionRate = mutateConnectionRate;
        return this;
    }

    public double getMutateWeightRate() {
        return mutateWeightRate;
    }

    public NeatConfiguration setMutateWeightRate(double mutateWeightRate) {
        this.mutateWeightRate = mutateWeightRate;
        return this;
    }

    public double getPerturbRate() {
        return perturbRate;
    }

    public NeatConfiguration setPerturbRate(double perturbRate) {
        this.perturbRate = perturbRate;
        return this;
    }

    public double getStepSize() {
        return stepSize;
    }

    public NeatConfiguration setStepSize(double stepSize) {
        this.stepSize = stepSize;
        return this;
    }

    public double getDisableRate() {
        return disableRate;
    }

    public NeatConfiguration setDisableRate(double disableRate) {
        this.disableRate = disableRate;
        return this;
    }

    public double getMutationRateEnablement() {
        return mutationRateEnablement;
    }

    public NeatConfiguration setMutationRateEnablement(double mutationRateEnablement) {
        this.mutationRateEnablement = mutationRateEnablement;
        return this;
    }

    public double getMateInterspeciesRate() {
        return mateInterspeciesRate;
    }

    public NeatConfiguration setMateInterspeciesRate(double mateInterspeciesRate) {
        this.mateInterspeciesRate = mateInterspeciesRate;
        return this;
    }

    public double getMutateOnlyRate() {
        return mutateOnlyRate;
    }

    public NeatConfiguration setMutateOnlyRate(double mutateOnlyRate) {
        this.mutateOnlyRate = mutateOnlyRate;
        return this;
    }

    public double getSurvivalRate() {
        return survivalRate;
    }

    public NeatConfiguration setSurvivalRate(double survivalRate) {
        this.survivalRate = survivalRate;
        return this;
    }

    public double getC1() {
        return c1;
    }

    public NeatConfiguration setC1(double c1) {
        this.c1 = c1;
        return this;
    }

    public double getC2() {
        return c2;
    }

    public NeatConfiguration setC2(double c2) {
        this.c2 = c2;
        return this;
    }

    public double getC3() {
        return c3;
    }

    public NeatConfiguration setC3(double c3) {
        this.c3 = c3;
        return this;
    }

    public int getInputCount() {
        return inputCount;
    }

    public NeatConfiguration setInputCount(int inputCount) {
        this.inputCount = inputCount;
        return this;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public NeatConfiguration setOutputCount(int outputCount) {
        this.outputCount = outputCount;
        return this;
    }

    public boolean isBiasNodeEnabled() {
        return biasNodeEnabled;
    }

    public NeatConfiguration setBiasNodeEnabled(boolean biasNodeEnabled) {
        this.biasNodeEnabled = biasNodeEnabled;
        return this;
    }

    public double getMaxConnectionAbsoluteValue() {
        return maxConnectionAbsoluteValue;
    }

    public NeatConfiguration setMaxConnectionAbsoluteValue(double maxConnectionAbsoluteValue) {
        this.maxConnectionAbsoluteValue = maxConnectionAbsoluteValue;
        return this;
    }

    public CreateStrategy getCreateStrategy() {
        return createStrategy;
    }

    public NeatConfiguration setCreateStrategy(CreateStrategy createStrategy) {
        this.createStrategy = createStrategy;
        return this;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public NeatConfiguration setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        return this;
    }

    public boolean isPrecalculateNodes() {
        return precalculateNodes;
    }

    public NeatConfiguration setPrecalculateNodes(boolean precalculateNodes) {
        this.precalculateNodes = precalculateNodes;
        return this;
    }

    public List<Exception> validate() {
        List<Exception> shitYouMadeWrong = new LinkedList<>();
        if (purgeAge > maxGenerationsWithoutImprovement) {
            shitYouMadeWrong.add(new IllegalStateException("purgeAge has to be smaller than maxGenerationsWithoutImprovement"));
        }
        return shitYouMadeWrong;
    }
}
