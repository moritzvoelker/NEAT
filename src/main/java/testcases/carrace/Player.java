/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package testcases.carrace;

public class Player {
    private final double baseVel = 0.05;
    private final double baseAngleVel = 0.1;
    private static final double length = 0.25;
    private static final double width = 0.125;

    private double x;
    private double y;
    private double vel;
    private double orientation;
    private double angleVel;
    private double score;

    public Player() {
        this.x = 10;
        this.y = 10;
        this.vel = 0;
        this.orientation = 0;
        this.angleVel = 0;
        this.score = 0;
    }

    public void applyVelocity() {
        orientation += angleVel;
        x += vel * Math.cos(orientation);
        y += vel * Math.sin(orientation);
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

    public double getVel() {
        return vel;
    }

    public void setVel(double vel) {
        this.vel = vel * baseVel;
    }

    public double getOrientation() {
        return orientation;
    }

    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }

    public double getAngleVel() {
        return angleVel;
    }

    public void setAngleVel(double angleVel) {
        this.angleVel = angleVel * baseAngleVel;
    }

    public static double getLength() {
        return length;
    }

    public static double getWidth() {
        return width;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
