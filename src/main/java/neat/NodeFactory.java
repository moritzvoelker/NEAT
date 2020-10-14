package neat;

public class NodeFactory {

    public static Node create(CreateStrategy strategy, NodePurpose nodePurpose) {
        return create(strategy, nodePurpose, -1);
    }

    public static Node create(CreateStrategy strategy, NodePurpose nodePurpose, int innovationNumber) {
        return strategy.create(nodePurpose, innovationNumber);
    }
}
