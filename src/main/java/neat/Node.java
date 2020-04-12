package neat;

import java.util.List;

public abstract class Node {
    private int innovationNumber;
    private List<Connection> in;
    protected double value;
    private boolean calculated;

    public Node(List<Connection> in, int innovationNumber) {
        this.in = in;
        this.innovationNumber = innovationNumber;
        this.value = 0.0;
        this.calculated = false;
    }

    protected abstract double calculateValue();

    public int getInnovationNumber() {
        return innovationNumber;
    }

    public double getValue() {
        if (!calculated) {
            value = calculateValue();
        }
        return value;
    }

    public void addInput(Connection input) {
        in.add(input);
    }

    @Override
    public boolean equals(Object obj) {
    }
}
