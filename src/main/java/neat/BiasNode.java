package neat;

import java.util.List;

public class BiasNode extends Node{
    BiasNode() { super(NodePurpose.Bias, 0); }

    @Override
    public boolean isDependentOn(Node node) {
        return this.equals(node);
    }

    @Override
    protected double calculateValue(List<Connection> connections) {
        return 1.0;
    }
}
