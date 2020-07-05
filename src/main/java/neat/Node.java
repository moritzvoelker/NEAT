package neat;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Node {
    private int innovationNumber;
    private List<Connection> in;
    protected double value;
    private boolean calculated;
    private NodePurpose nodePurpose;

    public Node(NodePurpose nodePurpose, int innovationNumber) {
        this.in = new LinkedList<>();
        this.innovationNumber = innovationNumber;
        this.value = 0.0;
        this.calculated = false;
        this.nodePurpose = nodePurpose;
    }

    public boolean isDependentOn(Node node) {
        if (this.equals(node)) {
            return true;
        } else {
            for (Connection connection: in) {
                if (connection.isDependentOn(node)) {
                    return true;
                }
            }
            return false;
        }
    }

    protected abstract double calculateValue(List<Connection> connections);

    public double getValue() {
        if (!calculated) {
            value = calculateValue(getIn().stream().filter(Connection::isEnabled).collect(Collectors.toList()));
            calculated = true;
        }
        return value;
    }

    public void addInput(Connection input) {
        in.add(input);
    }

    public List<Connection> getIn() {
        return in;
    }

    public NodePurpose getNodePurpose() {
        return nodePurpose;
    }

    public void setNodePurpose(NodePurpose nodePurpose) {
        this.nodePurpose = nodePurpose;
    }

    public int getInnovationNumber() {
        return innovationNumber;
    }

    public void setInnovationNumber(int innovationNumber) {
        this.innovationNumber = innovationNumber;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Node && innovationNumber == ((Node) obj).getInnovationNumber();
    }

    public void resetCalculated() {
        calculated = false;
    }
}
