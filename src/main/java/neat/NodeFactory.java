package neat;

// TODO: 13.05.2020 Actually doing something with type!
public class NodeFactory {

    public static Node create(CreateStrategy strategy, NodePurpose nodePurpose) {
        return create(strategy, nodePurpose, -1);
    }

    public static Node create(CreateStrategy strategy, NodePurpose nodePurpose, int innovationNumber) {
        return strategy.create(nodePurpose, innovationNumber);
    }
}
