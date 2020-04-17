package neat;

public class NodeFactory {
    public static Node create(String type, NodeType nodeType) {
        if (nodeType == NodeType.Input) {
            return new InputNode();
        }
        return new LinearNode(nodeType);
    }

    public static Node create(String type, NodeType nodeType, int innovationNumber) {
        if (nodeType == NodeType.Input) {
            return new InputNode(innovationNumber);
        }
        return new LinearNode(nodeType, innovationNumber);
    }
}
