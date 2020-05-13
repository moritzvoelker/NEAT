package neat;

// TODO: 13.05.2020 Actually doing something with type!
public class NodeFactory {
    public static Node create(String type, NodeType nodeType) {
        if (nodeType == NodeType.Input) {
            return new InputNode();
        }
        return new SquashNode(nodeType);
    }

    public static Node create(String type, NodeType nodeType, int innovationNumber) {
        if (nodeType == NodeType.Input) {
            return new InputNode(innovationNumber);
        }
        return new SquashNode(nodeType, innovationNumber);
    }
}
