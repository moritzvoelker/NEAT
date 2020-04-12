package neat;

public class Connection {
    private Node in;
    private Node out;

    private int innovationNumber;
    private double weight;
    private boolean enabled;



    public Connection(Node in, Node out, double weight, int innovationNumber) {
        this.in = in;
        this.out = out;
        this.weight = weight;
        this.innovationNumber = innovationNumber;
        this.enabled = true;
    }

    public Connection(Node in, Node out, double weight, int innovationNumber, boolean enabled) {
        this.in = in;
        this.out = out;
        this.weight = weight;
        this.innovationNumber = innovationNumber;
        this.enabled = enabled;
    }

    public Connection(Connection connection) {
        this.in = connection.in;
        this.out = connection.out;
        this.weight = connection.weight;
        this.innovationNumber = connection.innovationNumber;
        this.enabled = connection.enabled;
    }

    public double getValue() {
        return in.getValue() * weight;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Node getIn() {
        return in;
    }

    public Node getOut() {
        return out;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getInnovationNumber() {
        return innovationNumber;
    }

    public void setInnovationNumber(int innovationNumber) {
        this.innovationNumber = innovationNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Connection) {
            return in.getInnovationNumber() == ((Connection) obj).getIn().getInnovationNumber() && out.getInnovationNumber() == ((Connection) obj).getOut().getInnovationNumber();
        } else {
            return false;
        }
    }
}
