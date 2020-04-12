package neat;

import java.util.LinkedList;
import java.util.List;

public class Organism {
    protected static int globalInnovationNumber;
    public static List<Connection> currentMutations;

    private List<Node> inputNodes;
    private List<Node> hiddenNodes;
    private List<Node> outputNodes;
    private List<Connection> connections;

    public void mutateWeights() {
    }

    public void mutateConnection() {
    }

    public void mutateNode() {
    }

    public void setInput(double[] input) throws IllegalArgumentException {
        if (input.length != inputNodes.size()) {
            throw new IllegalArgumentException("Input number doesn't match input node count.");
        }

        for (int i = 0; i < inputNodes.size(); i++) {
            ((InputNode) inputNodes.get(i)).setValue(input[i]);
        }
    }

    public double[] getOutput() {
        double[] ret = new double[outputNodes.size()];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = outputNodes.get(i).getValue();
        }

        return ret;
    }

    static Organism crossover(Organism father, Organism mother) {
        return null;
    }

    public List<Node> getInputNodes() {
        return inputNodes;
    }

    public void setInputNodes(List<Node> inputNodes) {
        this.inputNodes = inputNodes;
    }

    public List<Node> getHiddenNodes() {
        return hiddenNodes;
    }

    public void setHiddenNodes(List<Node> hiddenNodes) {
        this.hiddenNodes = hiddenNodes;
    }

    public List<Node> getOutputNodes() {
        return outputNodes;
    }

    public void setOutputNodes(List<Node> outputNodes) {
        this.outputNodes = outputNodes;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }
}
