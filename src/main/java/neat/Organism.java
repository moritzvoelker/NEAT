package neat;

import java.util.List;

public class Organism {

    private List<InputNode> inputNodes;
    private List<Node> hiddenNodes;
    private List<Node> outputNodes;
    private List<Connection> connections;
    private int fitness;

    public void mutateWeights(double mutationRate, double perturbRate, double stepSize) {
        for (Connection connection : connections) {
            if (Math.random() < mutationRate) {
                if (Math.random() < perturbRate) {
                    connection.setWeight(connection.getWeight() + (Math.random() * 2 - 1) * stepSize);
                } else {
                    connection.setWeight(Math.random() * 2 - 1);
                }
            }
        }
    }

    public int mutateConnection(int currentInnovationNumber, List<Connection> currentMutations) {
        Connection connection;

        int i = 0;
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
                connection = new Connection(outNode, inNode, Math.random() * 2 - 1);
            } else {
                connection = new Connection(inNode, outNode, Math.random() * 2 - 1);
            }

        } while (connections.contains(connection) && i++ < 100);

        if (i == 100) {
            return currentInnovationNumber;
        }

        currentInnovationNumber = connection.setInnovationNumber(currentInnovationNumber, currentMutations);

        connections.add(connection);

        return currentInnovationNumber;
    }



    public int mutateNode(int currentInnovationNumber, List<Connection> currentMutations) {
        Connection connection = connections.get((int) (Math.random() * connections.size()));

        connection.setEnabled(false);

        Node node = new SquashNode(NodeType.Hidden);
        Connection in = new Connection(connection.getIn(), node, connection.getWeight());
        Connection out = new Connection(hiddenNodes.get(hiddenNodes.size() - 1), connection.getOut(), 1.0);

        hiddenNodes.add(node);
        connections.add(in);
        connections.add(out);
        node.addInput(in);
        connection.getOut().addInput(out);

        int i;
        for (i = 0; i < currentMutations.size(); i++) {
            if (connection.equals(currentMutations.get(i))) {
                node.setInnovationNumber(currentMutations.get(i).getOut().getIn().get(currentMutations.get(i).getOut().getIn().size() - 1).getIn().getInnovationNumber());
                in.setInnovationNumber(node.getInnovationNumber() + 1);
                out.setInnovationNumber(node.getInnovationNumber() + 2);
                break;
            }
        }

        if (i == currentMutations.size() - 1) {
            node.setInnovationNumber(currentInnovationNumber++);
            in.setInnovationNumber(currentInnovationNumber++);
            out.setInnovationNumber(currentInnovationNumber++);
            currentMutations.add(connection);
        }

        return currentInnovationNumber;
    }

    public void mutateEnablement() {
        Connection connection = connections.get((int) (Math.random() * connections.size()));
        connection.setEnabled(!connection.isEnabled());
    }

    public void setInput(double[] input) throws IllegalArgumentException {
        if (input.length != inputNodes.size()) {
            throw new IllegalArgumentException("Input number doesn't match input node count.");
        }

        for (int i = 0; i < inputNodes.size(); i++) {
            inputNodes.get(i).setValue(input[i]);
        }
    }

    public double[] getOutput() {
        double[] ret = new double[outputNodes.size()];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = outputNodes.get(i).getValue();
        }

        return ret;
    }

    static Organism crossover(Organism father, Organism mother, NeatConfiguration configuration) {
        if (father.getFitness() < mother.getFitness()) {
            Organism temp = father;
            father = mother;
            mother = temp;
        }

        Organism child = new Organism();

        int i = 0, j = 0;
        Connection fatherConnection = father.getConnections().get(i);
        Connection motherConnection = mother.getConnections().get(j);
        boolean equalParents = father.getFitness() == mother.getFitness();
        while (i < father.getConnections().size() || (equalParents && j < mother.getConnections().size())) {
            boolean joined = fatherConnection.getInnovationNumber() == motherConnection.getInnovationNumber();

            Node in = null;
            Node out = null;

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


            for (Node node : child.getInputNodes()) {
                if (node.equals(currentConnection.getIn())) {
                    in = node;
                    break;
                }
            }
            if (in == null) {
                for (Node node : child.getHiddenNodes()) {
                    if (node.equals(currentConnection.getIn())) {
                        in = node;
                        break;
                    }
                }
            }
            if (in == null) {
                if (currentConnection.getIn().getNodeType().equals(NodeType.Input)) {
                    in = new InputNode(currentConnection.getIn().getInnovationNumber());
                    child.getInputNodes().add((InputNode) in);
                } else {
                    in = new SquashNode(NodeType.Hidden, currentConnection.getIn().getInnovationNumber());
                    child.getHiddenNodes().add(in);
                }
            }

            for (Node node : child.getHiddenNodes()) {
                if (node.equals(currentConnection.getOut())) {
                    out = node;
                    break;
                }
            }
            if (out == null) {
                for (Node node : child.getOutputNodes()) {
                    if (node.equals(currentConnection.getOut())) {
                        out = node;
                        break;
                    }
                }
            }
            if (out == null) {
                if (currentConnection.getOut().getNodeType().equals(NodeType.Hidden)) {
                    out = new SquashNode(NodeType.Hidden, currentConnection.getOut().getInnovationNumber());
                    child.getHiddenNodes().add(out);
                } else {
                    out = new SquashNode(NodeType.Output, currentConnection.getOut().getInnovationNumber());
                    child.getOutputNodes().add(out);
                }
            }

            if (joined) {
                double weight;
                if (Math.random() < 0.5) {
                    weight = fatherConnection.getWeight();
                } else {
                    weight = motherConnection.getWeight();
                }
                child.getConnections().add(new Connection(in, out, weight));
                Connection newConnection = child.getConnections().get(child.getConnections().size() - 1);
                if (!fatherConnection.isEnabled() && !motherConnection.isEnabled()) {
                    newConnection.setEnabled(false);
                } else if (((fatherConnection.isEnabled() && !motherConnection.isEnabled()) || (!fatherConnection.isEnabled() && motherConnection.isEnabled())) && Math.random() < configuration.getDisableRate()) {
                    newConnection.setEnabled(false);
                }
                newConnection.setInnovationNumber(currentConnection.getInnovationNumber());
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
                child.getConnections().add(new Connection(currentConnection, in, out));
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
        return inputNodes.contains(node) || hiddenNodes.contains(node) || outputNodes.contains(node);
    }

    public boolean isMember(Species species, NeatConfiguration config) {
        return this.calculateCompatibilityDistance(species.getRepresentative(), config) < config.getSpeciationThreshhold();
    }

    public double calculateCompatibilityDistance(Organism organism, NeatConfiguration config) {
        int excess = 0, disjoint = 0, joined = 0;
        double weightDifference = 0.0;

        int size;

        if (organism.getConnections().size() > connections.size()){
            size = organism.getConnections().size();
        } else {
            size = connections.size();
        }

        if (size < 20) {
            size = 1;
        }

        int i = 0, j = 0;
        Connection fatherConnection = connections.get(i);
        Connection motherConnection = organism.getConnections().get(j);
        while (i < connections.size() || j < organism.getConnections().size()){
            if (fatherConnection.getInnovationNumber() == motherConnection.getInnovationNumber()){
                joined++;
                weightDifference += Math.abs(fatherConnection.getWeight() - motherConnection.getWeight());
            } else if (fatherConnection.getInnovationNumber() < motherConnection.getInnovationNumber()){
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
        return config.getC1() * excess / size + config.getC2() * disjoint / size + config.getC3() * weightDifference / joined;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
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
}
