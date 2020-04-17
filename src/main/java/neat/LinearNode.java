package neat;

public class LinearNode extends Node {
    public LinearNode(NodeType nodeType) {
        super(nodeType);
    }

    public LinearNode(NodeType nodeType, int innovationNumber) {
        super(nodeType, innovationNumber);
    }

    @Override
    protected double calculateValue() {
        return getIn().stream().mapToDouble(Connection::getValue).sum();
    }
}
