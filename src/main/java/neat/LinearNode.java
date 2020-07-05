package neat;

import java.util.List;

public class LinearNode extends Node {

    public LinearNode(NodePurpose nodePurpose, int innovationNumber) {
        super(nodePurpose, innovationNumber);
    }

    @Override
    protected double calculateValue(List<Connection> connections) {
        return connections.stream().mapToDouble(Connection::getValue).sum();
    }
}
