package neat;

public class InputNode extends Node {

    public InputNode() {
        super(NodeType.Input);
    }

    public InputNode(int innovationNumber) {
        super(NodeType.Input, innovationNumber);
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean isDependentOn(Node node) {
        return this.equals(node);
    }

    @Override
    protected double calculateValue() {
        return value;
    }
}
