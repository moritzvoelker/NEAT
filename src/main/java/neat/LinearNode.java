package neat;

import java.util.List;

public class LinearNode extends Node {
    public LinearNode(NodeType nodeType) {
        super(nodeType);
    }

    public LinearNode(NodeType nodeType, int innovationNumber) {
        super(nodeType, innovationNumber);
    }

    @Override
    protected double calculateValue(List<Connection> connections) {
        return connections.stream().mapToDouble(Connection::getValue).sum();
    }
}
