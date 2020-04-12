package neat;

import java.util.List;

public class SquashNode extends Node {

    public SquashNode(List<Connection> in, int innovationNumber) {
        super(in, innovationNumber);
    }

    @Override
    protected double calculateValue() {
        return 0;
        // TODO: 11.04.2020 implement sqashing
    }
}
