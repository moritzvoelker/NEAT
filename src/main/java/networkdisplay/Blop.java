package networkdisplay;

import neat.NodePurpose;

public class Blop {
    private double x;
    private double y;

    private int innovationNumber;
    private NodePurpose nodePurpose;
    private int depth;

    public Blop(double x, double y, int innovationNumber, NodePurpose nodePurpose) {
        this.x = x;
        this.y = y;
        this.innovationNumber = innovationNumber;
        depth = 0;
        this.nodePurpose = nodePurpose;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
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

    public NodePurpose getNodePurpose() {
        return nodePurpose;
    }

    public void setNodePurpose(NodePurpose nodePurpose) {
        this.nodePurpose = nodePurpose;
    }
}
