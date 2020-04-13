package neat;

import java.util.List;

public class SquashNode extends Node {

    public SquashNode(NodeType nodeType) {
        super(nodeType);
    }

    public SquashNode(NodeType nodeType, int innovationNumber) {
        super(nodeType, innovationNumber);
    }

    @Override
    protected double calculateValue() {
        return 0;
        // TODO: 11.04.2020 implement sqashing
    }
}
