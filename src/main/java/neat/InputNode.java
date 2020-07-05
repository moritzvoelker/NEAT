package neat;

import java.util.List;

public class InputNode extends Node {

    public InputNode(int innovationNumber) {
        super(NodePurpose.Input, innovationNumber);
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean isDependentOn(Node node) {
        return this.equals(node);
    }

    @Override
    protected double calculateValue(List<Connection> connections) {
        return value;
    }
}
