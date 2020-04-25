package neat;

public class SquashNode extends Node {

    public SquashNode(NodeType nodeType) {
        super(nodeType);
    }

    public SquashNode(NodeType nodeType, int innovationNumber) {
        super(nodeType, innovationNumber);
    }

    @Override
    protected double calculateValue() {
        double comultativeValue = getIn().stream().filter(Connection::isEnabled).mapToDouble(Connection::getValue).sum();
        return 1 / (1 + Math.pow(Math.E, -4.9 * comultativeValue));
    }
}
