package networkdisplay;

import neat.NodeType;

public class Blop {
    private int x;
    private int y;

    private int innovationNumber;
    private NodeType nodeType;
    private int depth;

    public Blop(int x, int y, int innovationNumber, NodeType nodeType) {
        this.x = x;
        this.y = y;
        this.innovationNumber = innovationNumber;
        depth = 0;
        this.nodeType = nodeType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getInnovationNumber() {
        return innovationNumber;
    }

    public void setInnovationNumber(int innovationNumber) {
        this.innovationNumber = innovationNumber;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }
}
