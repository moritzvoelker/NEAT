package neat;

public class DefaultCreateStrategy implements CreateStrategy {
    @Override
    public Node create(NodePurpose purpose, int innovationNumber) {
        if (purpose == NodePurpose.Input) {
            return new InputNode(innovationNumber);
        }
        return new SquashNode(purpose, innovationNumber);
    }
}
