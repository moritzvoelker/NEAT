package testcases.flappybirds;

public class Player {

    public static final double RADIUS = 0.02;

    private double y;
    private double vy;
    private double score;
    private boolean jump;

    public Player() {
        y = 0.5;
        vy = 0.0;
        score = 0;
    }

    public void applyGravity(double gravity) {
        vy += gravity;
    }

    public void applyVelocity() {
        y += vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVy() {
        return vy;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }
}
