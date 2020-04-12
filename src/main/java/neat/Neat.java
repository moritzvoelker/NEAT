package neat;

import java.util.ArrayList;
import java.util.List;

public class Neat {
    private int populationSize;
    private double speciationThreshhold;
    private double mutationRateNode;
    private double mutationRateConnection;
    private List<Organism> population;

    private int inputCount;
    private int outputCount;

    public Neat() {
    }

    public Neat(int populationSize, double speciationThreshhold, double mutationRateNode, double mutationRateConnection, int inputCount, int outputCount) {
        this.populationSize = populationSize;
        this.speciationThreshhold = speciationThreshhold;
        this.mutationRateNode = mutationRateNode;
        this.mutationRateConnection = mutationRateConnection;
        this.inputCount = inputCount;
        this.outputCount = outputCount;
    }

    public void setInput(List<double[]> input) throws IllegalArgumentException {
        for (int i = 0; i < populationSize; i++) {
            population.get(i).setInput(input.get(i));
        }
    }

    public List<double[]> getOutput() {
        List<double[]> output = new ArrayList<>(populationSize);

        for (Organism organism: population) {
            output.add(organism.getOutput());
        }

        return output;
    }

    public void nextGeneration() {
        // TODO: 11.04.2020 sort nodes and connections after mutations
    }

    private void mutate() {
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public double getSpeciationThreshhold() {
        return speciationThreshhold;
    }

    public void setSpeciationThreshhold(double speciationThreshhold) {
        this.speciationThreshhold = speciationThreshhold;
    }

    public double getMutationRateNode() {
        return mutationRateNode;
    }

    public void setMutationRateNode(double mutationRateNode) {
        this.mutationRateNode = mutationRateNode;
    }

    public double getMutationRateConnection() {
        return mutationRateConnection;
    }

    public void setMutationRateConnection(double mutationRateConnection) {
        this.mutationRateConnection = mutationRateConnection;
    }

    public List<Organism> getPopulation() {
        return population;
    }

    public int getInputCount() {
        return inputCount;
    }

    public void setInputCount(int inputCount) {
        this.inputCount = inputCount;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public void setOutputCount(int outputCount) {
        this.outputCount = outputCount;
    }
}
