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

/**
 * Configuration for {@link Neat}. All the setters follow the builder pattern for ease in use.
 */
public class NeatConfiguration implements Serializable {
    /**
     * The number of all {@link Organism}s together.
     */
    private int populationSize = 150;
    /**
     * The number of generations without overall improvement, after which it is assumed the evolution ran into a dead end and all {@link Species} except for two and the champion are deleted.
     */
    private int purgeAge = 15;
    /**
     * The number of generations without improvement a specific {@link Species} can have after which it is assumed the species ran into a dead end and is deleted.
     */
    private int maxGenerationsWithoutImprovement = 20;
    /**
     * The threshold for the difference between two {@link Organism}s. If it is surpassed the organisms get sorted into different {@link Species}.
     */
    private double speciationThreshhold = 3.0;
    /**
     * The chance for an {@link Organism} to mutate a new {@link Node}.
     */
    private double mutateNodeRate = 0.03;
    /**
     * The chance for an {@link Organism} to mutate a new {@link Connection}.
     */
    private double mutateConnectionRate = 0.05;
    /**
     * The chance for a {@link Connection} to mutate it's weight.
     */
    private double mutateWeightRate = 0.8;
    /**
     * The chance for a {@link Connection} weight to get lightly perturbed. Otherwise the weight is reset completely reset.
     */
    private double perturbRate = 0.9;
    /**
     * The chance for a {@link Connection} to get en- or disabled.
     */
    private double mutationRateEnablement = 0.01;
    /**
     * The percentage of the maximal possible value of a {@link Connection} weight which is the maximum a the weight is perturbed.
     */
    private double stepSize = 0.1;
    /**
     * ???
     */
    private double disableRate = 0.75;
    /**
     * The chance for an {@link Organism} to mate with an organism from another {@link Species}.
     */
    private double mateInterspeciesRate = 0.2;
    /**
     * The chance for an {@link Organism} to not mate and get to the next generation directly but mutated.
     */
    private double mutateOnlyRate = 0.25; // => mateRate = 0.75
    /**
     * The percentage of {@link Organism}s which will be kept for reproduction into the new generation.
     */
    private double survivalRate = 0.5;
    /**
     * The weight of the excess genes in the difference calculation between two {@link Organism}s.
     */
    private double c1 = 1.0;
    /**
     * The weight of the disjoint genes in the difference calculation between two {@link Organism}s.
     */
    private double c2 = 1.0;
    /**
     * The weight of the average weight difference of matching genes in the difference calculation between two {@link Organism}s.
     */
    private double c3 = 0.4;
    /**
     * The number of {@link InputNode}s.
     */
    private int inputCount;
    /**
     * The number of output {@link Node}s.
     */
    private int outputCount;
    /**
     * If true every {@link Organism} has a {@link BiasNode}, which is a {@link Node} which has a {@link Connection} to every other node, except the input nodes and returns a constant value.
     */
    private boolean biasNodeEnabled = true;
    /**
     * The maximal value the weight of an {@link Connection} can have.
     */
    private double maxConnectionAbsoluteValue = 2.0;
    /**
     * The number of {@link Thread}s which are used to precalculate the {@link Node}s of the {@link Organism}s, which inputs have been set.
     * Only relevant if {@link #precalculateNodes} is true.
     */
    private int numberOfThreads = 4;
    /**
     * If true, every {@link Organism} which gets its inputs set gets added to a {@link List}. It is then taken and evaluated. If {@link Neat#getOutput()} is called later on the precalculated values can be just read and returned.
     */
    private boolean precalculateNodes = true;
    /**
     * The {@link CreateStrategy} which is used by the {@link NodeFactory} to create new {@link Node}s.
     */
    private CreateStrategy createStrategy = new DefaultCreateStrategy();

    /**
     * Constructs an instance of {@link NeatConfiguration} with the given input and output count and default values for every field, inspired by the original paper on which this implementation is based on.
     * @param inputCount the number of {@link InputNode}s the {@link Organism}s will have.
     * @param outputCount the number of output {@link Node}s the organisms will have.
     */
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

    /**
     * Validates all fields of the configuration to prevent strange behaviour and exceptions that would occur otherwise.
     * @return a {@link List<Exception>} containing all errors that were found.
     */
    public List<Exception> validate() {
        List<Exception> shitYouMadeWrong = new LinkedList<>();
        if (purgeAge > maxGenerationsWithoutImprovement) {
            shitYouMadeWrong.add(new IllegalStateException("purgeAge has to be smaller than maxGenerationsWithoutImprovement"));
        }
        return shitYouMadeWrong;
    }
}
