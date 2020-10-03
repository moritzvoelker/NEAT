package testcases.flappybirds;

public class Pillar {

    public static final double WIDTH = 0.05;
    public static final double HOLE_HEIGHT = 0.2;

    private double holeY;

    public Pillar(double holeY) {
        this.holeY = holeY;
    }

    public double getHoleY() {
        return holeY;
    }

    public void setHoleY(double holeY) {
        this.holeY = holeY;
    }
}
