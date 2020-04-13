package neat;

public class NeatConfiguration {
    private int populationSize = 100;
    private double speciationThreshhold;
    private double mutationRateNode;
    private double mutationRateConnection;
    private double mutationRateWeight = 0.8;
    private double perturbRate = 0.9;
    private double mutationRateEnablement;
    private double stepSize = 0.1;
    private double disableRate = 0.75;
    private int inputCount;
    private int outputCount;

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

    public double getSpeciationThreshhold() {
        return speciationThreshhold;
    }

    public NeatConfiguration setSpeciationThreshhold(double speciationThreshhold) {
        this.speciationThreshhold = speciationThreshhold;
        return this;
    }

    public double getMutationRateNode() {
        return mutationRateNode;
    }

    public NeatConfiguration setMutationRateNode(double mutationRateNode) {
        this.mutationRateNode = mutationRateNode;
        return this;
    }

    public double getMutationRateConnection() {
        return mutationRateConnection;
    }

    public NeatConfiguration setMutationRateConnection(double mutationRateConnection) {
        this.mutationRateConnection = mutationRateConnection;
        return this;
    }

    public double getMutationRateWeight() {
        return mutationRateWeight;
    }

    public NeatConfiguration setMutationRateWeight(double mutationRateWeight) {
        this.mutationRateWeight = mutationRateWeight;
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
}
