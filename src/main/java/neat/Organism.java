package neat;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Organism implements Serializable {

    private NeatConfiguration configuration;

    private transient List<InputNode> inputNodes;
    private transient List<Node> hiddenNodes;
    private transient List<Node> outputNodes;
    private List<Connection> connections;
    private transient BiasNode bias;
    private double fitness;

    public Organism(NeatConfiguration configuration) {
        this.configuration = configuration;
        inputNodes = new LinkedList<>();
        hiddenNodes = new LinkedList<>();
        outputNodes = new LinkedList<>();
        connections = new LinkedList<>();
        fitness = -1.0;
        if (configuration.isBiasNodeEnabled()) {
            bias = new BiasNode();
        } else {
            bias = null;
        }
    }

    public Organism(Organism organism) {
        configuration = organism.configuration;
        inputNodes = new LinkedList<>();
        hiddenNodes = new LinkedList<>();
        outputNodes = new LinkedList<>();
        connections = new LinkedList<>();
        fitness = -1.0;
        if (organism.getBias() != null) {
            bias = new BiasNode();
        } else {
            bias = null;
        }

        organism.getInputNodes().forEach(inputNode -> inputNodes.add((InputNode) NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Input, inputNode.getInnovationNumber())));
        organism.getOutputNodes().forEach(outputNode -> outputNodes.add(NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Output, outputNode.getInnovationNumber())));

        organism.getConnections().forEach(this::cloneConnection);

    }

    public int mutate(List<Connection> currentMutations, int innovationNumber) {
        mutateWeights();
        if (Math.random() < configuration.getMutateConnectionRate()) {
            innovationNumber = mutateConnection(innovationNumber, currentMutations);
        }

        if (Math.random() < configuration.getMutateNodeRate()) {
            innovationNumber = mutateNode(innovationNumber, currentMutations);
        }
        if (Math.random() < configuration.getMutationRateEnablement()) {
            mutateEnablement();
        }

        getConnections().sort(Comparator.comparingInt(Connection::getInnovationNumber));
        getHiddenNodes().sort(Comparator.comparingInt(Node::getInnovationNumber));

        return innovationNumber;
    }

    public void mutateWeights() {
        for (Connection connection : connections) {
            if (Math.random() < configuration.getMutateWeightRate()) {
                if (Math.random() < configuration.getPerturbRate()) {
                    connection.setWeight(connection.getWeight() + (Math.random() * configuration.getMaxConnectionAbsoluteValue() * 2 - configuration.getMaxConnectionAbsoluteValue()) * configuration.getStepSize());
                } else {
                    connection.setWeight(Math.random() * configuration.getMaxConnectionAbsoluteValue() * 2 - configuration.getMaxConnectionAbsoluteValue());
                }
            }
        }
    }

    public int mutateConnection(int currentInnovationNumber, List<Connection> currentMutations) {
        Connection connection;

        int i = 0;
        boolean quit;
        do {
            int in = (int) (Math.random() * (inputNodes.size() + hiddenNodes.size()));
            int out = (int) (Math.random() * (hiddenNodes.size() + outputNodes.size()));

            Node inNode;
            Node outNode;

            if (in < inputNodes.size()) {
                inNode = inputNodes.get(in);
            } else {
                inNode = hiddenNodes.get(in - inputNodes.size());
            }

            do {
                if (out < hiddenNodes.size()) {
                    outNode = hiddenNodes.get(out);
                } else {
                    outNode = outputNodes.get(out - hiddenNodes.size());
                }
                out++;
            } while (inNode.equals(outNode));

            if (inNode.isDependentOn(outNode)) {
                connection = new Connection(outNode, inNode, Math.random() * configuration.getMaxConnectionAbsoluteValue() * 2 - configuration.getMaxConnectionAbsoluteValue());
            } else {
                connection = new Connection(inNode, outNode, Math.random() * configuration.getMaxConnectionAbsoluteValue() * 2 - configuration.getMaxConnectionAbsoluteValue());
            }

            quit = true;
            for (Connection toCompare : connections) {
                if (toCompare.equals(connection)) {
                    if (!toCompare.isEnabled()) {
                        toCompare.setEnabled(true);
                        i = 100;
                    } else {
                        quit = false;
                    }
                    break;
                }
            }

        } while (!quit && ++i < 100);

        if (i >= 100) {
            return currentInnovationNumber;
        }

        connection.getOut().addInput(connection);

        currentInnovationNumber = connection.setInnovationNumber(currentInnovationNumber, currentMutations);
        // Problem: in der Liste von Mutationen ist dieselbe Verbindung. Kann zu Problem in mutateNode führen --> Entweder so, oder nur eins von beiden pro Organismus in einer Generation (mutateNode/Connection)
        connections.add(new Connection(connection));

        return currentInnovationNumber;
    }


    public int mutateNode(int currentInnovationNumber, List<Connection> currentMutations) {
        Connection connection = connections.get((int) (Math.random() * connections.size()));

        connection.setEnabled(false);

        Node node = NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Hidden);
        Connection in = new Connection(connection.getIn(), node, connection.getWeight());
        Connection out = new Connection(node, connection.getOut(), 1.0);

        hiddenNodes.add(node);
        node.addInput(in);
        // The Problem was that connection == currentMutations.get(i); Evtl: MutateConnection fügt Connection hinzu, gleiche Connection wird durch mutateNode ausgewählt. --> Problem
        connection.getOut().addInput(out);

        int i;
        for (i = 0; i < currentMutations.size(); i++) {
            if (connection.equals(currentMutations.get(i)) && !currentMutations.get(i).isEnabled()) {
                node.setInnovationNumber(currentMutations.get(i).getOut().getIn().get(currentMutations.get(i).getOut().getIn().size() - 1).getIn().getInnovationNumber());
                in.setInnovationNumber(node.getInnovationNumber() + 1);
                out.setInnovationNumber(node.getInnovationNumber() + 2);
                break;
            }
        }

        if (i == currentMutations.size()) {
            node.setInnovationNumber(currentInnovationNumber++);
            in.setInnovationNumber(currentInnovationNumber++);
            out.setInnovationNumber(currentInnovationNumber++);
            currentMutations.add(connection);
        }

        connections.add(in);
        connections.add(out);

        if (bias != null && !in.getIn().equals(bias)) {
            connection = new Connection(bias, node, Math.random() * configuration.getMaxConnectionAbsoluteValue() * 2 - configuration.getMaxConnectionAbsoluteValue());
            node.getIn().add(connection);
            if (i == currentMutations.size()) {
                connection.setInnovationNumber(currentInnovationNumber++);
            } else {
                connection.setInnovationNumber(node.getInnovationNumber() + 3);
            }
            connections.add(connection);
        }

        return currentInnovationNumber;
    }

    public void mutateEnablement() {
        Connection connection = connections.get((int) (Math.random() * connections.size()));
        connection.setEnabled(!connection.isEnabled());
    }

    public void setInput(double[] input) throws IllegalArgumentException {
        if (input.length != inputNodes.size()) {
            throw new IllegalArgumentException("Input number doesn't match input node count. Expected: " + inputNodes.size() + "; provided: " + input.length);
        }

        for (Node node: hiddenNodes) {
            node.resetCalculated();
        }
        for (Node node: outputNodes) {
            node.resetCalculated();
        }

        for (int i = 0; i < inputNodes.size(); i++) {
            inputNodes.get(i).setValue(input[i]);
        }
    }

    synchronized public double[] getOutput() {
        double[] ret = new double[outputNodes.size()];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = outputNodes.get(i).getValue();
        }

        return ret;
    }

    public Connection cloneConnection(Connection connection) {
        Node in = null;
        Node out = null;

        if (connection.getIn().getNodePurpose().equals(NodePurpose.Bias)) {
            in = bias;
        } else if (connection.getIn().getNodePurpose().equals(NodePurpose.Input)) {
            for (Node node : getInputNodes()) {
                if (node.equals(connection.getIn())) {
                    in = node;
                    break;
                }
            }
        } else {
            for (Node node : getHiddenNodes()) {
                if (node.equals(connection.getIn())) {
                    in = node;
                    break;
                }
            }
        }
        if (in == null) {
            in = NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Hidden, connection.getIn().getInnovationNumber());
            getHiddenNodes().add(in);
        }

        if (connection.getOut().getNodePurpose().equals(NodePurpose.Hidden)) {
            for (Node node : getHiddenNodes()) {
                if (node.equals(connection.getOut())) {
                    out = node;
                    break;
                }
            }
        } else {
            for (Node node : getOutputNodes()) {
                if (node.equals(connection.getOut())) {
                    out = node;
                    break;
                }
            }
        }
        if (out == null) {
            out = NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Hidden, connection.getOut().getInnovationNumber());
            getHiddenNodes().add(out);
        }

        Connection newConnection = new Connection(connection, in, out);
        connections.add(newConnection);
        out.addInput(newConnection);
        return newConnection;
    }

    static Organism crossover(Organism father, Organism mother, NeatConfiguration configuration) {
        if (father.getFitness() < mother.getFitness()) {
            Organism temp = father;
            father = mother;
            mother = temp;
        }

        Organism child = new Organism(configuration);
        for (int i = 0; i < configuration.getInputCount(); i++) {
            child.getInputNodes().add((InputNode) NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Input, i + 1));
        }

        for (int i = 0; i < configuration.getOutputCount(); i++) {
            child.getOutputNodes().add(NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Output, i + configuration.getInputCount() + 1));
        }

        int i = 0, j = 0;
        Connection fatherConnection = father.getConnections().get(i);
        Connection motherConnection = mother.getConnections().get(j);
        boolean equalParents = father.getFitness() == mother.getFitness();
        while (i < father.getConnections().size() || (equalParents && j < mother.getConnections().size())) {
            boolean joined = fatherConnection.getInnovationNumber() == motherConnection.getInnovationNumber();
            Connection currentConnection;

            if (motherConnection.getInnovationNumber() < fatherConnection.getInnovationNumber()) {
                if (equalParents) {
                    currentConnection = motherConnection;
                } else {
                    if (++j == mother.getConnections().size()) {
                        motherConnection = new Connection(null, null, 0.0);
                        motherConnection.setInnovationNumber(Integer.MAX_VALUE);
                    } else {
                        motherConnection = mother.getConnections().get(j);
                    }
                    continue;
                }
            } else {
                currentConnection = fatherConnection;
            }


            boolean alreadyExisting = child.getConnections().contains(currentConnection);
            Connection newConnection = null;
            if (!alreadyExisting) {
                 newConnection = child.cloneConnection(currentConnection);
            }

            if (joined) {
                if (!alreadyExisting) {
                    double weight;
                    if (Math.random() < 0.5) {
                        weight = fatherConnection.getWeight();
                    } else {
                        weight = motherConnection.getWeight();
                    }

                    newConnection.setWeight(weight);
                    if (!fatherConnection.isEnabled() && !motherConnection.isEnabled()) {
                        newConnection.setEnabled(false);
                    } else if (((fatherConnection.isEnabled() && !motherConnection.isEnabled()) || (!fatherConnection.isEnabled() && motherConnection.isEnabled())) && Math.random() < configuration.getDisableRate()) {
                        newConnection.setEnabled(false);
                    }
                }

                if (++i == father.getConnections().size()) {
                    fatherConnection = new Connection(null, null, 0.0);
                    fatherConnection.setInnovationNumber(Integer.MAX_VALUE);
                } else {
                    fatherConnection = father.getConnections().get(i);
                }
                if (++j == mother.getConnections().size()) {
                    motherConnection = new Connection(null, null, 0.0);
                    motherConnection.setInnovationNumber(Integer.MAX_VALUE);
                } else {
                    motherConnection = mother.getConnections().get(j);
                }
            } else {
                if (fatherConnection.getInnovationNumber() < motherConnection.getInnovationNumber()) {
                    if (++i == father.getConnections().size()) {
                        fatherConnection = new Connection(null, null, 0.0);
                        fatherConnection.setInnovationNumber(Integer.MAX_VALUE);
                    } else {
                        fatherConnection = father.getConnections().get(i);
                    }
                } else {
                    if (++j == mother.getConnections().size()) {
                        motherConnection = new Connection(null, null, 0.0);
                        motherConnection.setInnovationNumber(Integer.MAX_VALUE);
                    } else {
                        motherConnection = mother.getConnections().get(j);
                    }
                }
            }
        }
        return child;
    }

    public boolean hasNode(Node node) {
        //noinspection SuspiciousMethodCalls
        return node.equals(bias) || inputNodes.contains(node) || hiddenNodes.contains(node) || outputNodes.contains(node);
    }

    public boolean isMember(Species species) {
        return this.calculateCompatibilityDistance(species.getRepresentative()) < configuration.getSpeciationThreshhold();
    }

    public double calculateCompatibilityDistance(Organism organism) {
        int excess = 0, disjoint = 0, joined = 0;
        double weightDifference = 0.0;


        int size = Math.max(organism.getConnections().size(), connections.size());

        if (size < 20) {
            size = 1 + (int) Math.pow(Math.sqrt(19) / 20 * size, 2);
        }/* else {
            System.out.println("Organism is now big!");
        }*/



        int i = 0, j = 0;
        Connection fatherConnection = connections.get(i);
        Connection motherConnection = organism.getConnections().get(j);
        while (i < connections.size() || j < organism.getConnections().size()) {
            if (fatherConnection.getInnovationNumber() == motherConnection.getInnovationNumber()) {
                joined++;
                weightDifference += Math.abs(fatherConnection.getWeight() - motherConnection.getWeight());

                if (++i < connections.size()) {
                    fatherConnection = connections.get(i);
                } else {
                    fatherConnection = new Connection(null, null, 0.0);
                    fatherConnection.setInnovationNumber(Integer.MAX_VALUE);
                }

                if (++j < organism.getConnections().size()) {
                    motherConnection = organism.getConnections().get(j);
                } else {
                    motherConnection = new Connection(null, null, 0.0);
                    motherConnection.setInnovationNumber(Integer.MAX_VALUE);
                }
            } else if (fatherConnection.getInnovationNumber() < motherConnection.getInnovationNumber()) {
                if (j == organism.getConnections().size()) {
                    excess++;
                } else {
                    disjoint++;
                }
                if (++i < connections.size()) {
                    fatherConnection = connections.get(i);
                } else {
                    fatherConnection = new Connection(null, null, 0.0);
                    fatherConnection.setInnovationNumber(Integer.MAX_VALUE);
                }
            } else {
                if (i == connections.size()) {
                    excess++;
                } else {
                    disjoint++;
                }
                if (++j < organism.getConnections().size()) {
                    motherConnection = organism.getConnections().get(j);
                } else {
                    motherConnection = new Connection(null, null, 0.0);
                    motherConnection.setInnovationNumber(Integer.MAX_VALUE);
                }
            }
        }
        return configuration.getC1() * excess / size + configuration.getC2() * disjoint / size + configuration.getC3() * weightDifference / joined;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public List<InputNode> getInputNodes() {
        return inputNodes;
    }

    public void setInputNodes(List<InputNode> inputNodes) {
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

    public BiasNode getBias() {
        return bias;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();

        inputNodes = new ArrayList<>(configuration.getInputCount());
        outputNodes = new ArrayList<>(configuration.getOutputCount());
        hiddenNodes = new LinkedList<>();

        for (Connection connection : connections) {
            if (connection.getIn().getNodePurpose().equals(NodePurpose.Bias) && bias != null) {
                bias = (BiasNode) connection.getIn();
            } else if (connection.getIn().getNodePurpose().equals(NodePurpose.Input) && !inputNodes.contains(connection.getIn())) {
                inputNodes.add((InputNode) connection.getIn());
            } else if (connection.getIn().getNodePurpose().equals(NodePurpose.Hidden) && !hiddenNodes.contains(connection.getIn())) {
                hiddenNodes.add(connection.getIn());
            }

            if (connection.getOut().getNodePurpose().equals(NodePurpose.Output) && !outputNodes.contains(connection.getOut())) {
                outputNodes.add(connection.getOut());
            } else if (connection.getOut().getNodePurpose().equals(NodePurpose.Hidden) && !hiddenNodes.contains(connection.getOut())) {
                hiddenNodes.add(connection.getOut());
            }

            connection.getOut().addInput(connection);
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
    }
}
