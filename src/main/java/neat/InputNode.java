package neat;

public class InputNode extends Node {

    public InputNode(int innovationNumber) {
        super(null, innovationNumber);
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    protected double calculateValue() {
        return value;
    }
}
