package neat;

import java.util.List;

public class SquashNode extends Node {

    public SquashNode(NodePurpose nodePurpose, int innovationNumber) {
        super(nodePurpose, innovationNumber);
    }

    @Override
    protected double calculateValue(List<Connection> connections) {
        double comultativeValue = connections.stream().mapToDouble(Connection::getValue).sum();
        return 1 / (1 + Math.pow(Math.E, -4.9 * comultativeValue));
    }
}
