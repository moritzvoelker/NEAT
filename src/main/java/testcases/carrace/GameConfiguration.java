package testcases.carrace;

public class GameConfiguration {
    int sizex = 10;
    int sizey = 10;
    int length = 50;
    int numRays = 6;

    public int getSizex() {
        return sizex;
    }

    public GameConfiguration setSizex(int sizex) {
        this.sizex = sizex;
        return this;
    }

    public int getSizey() {
        return sizey;
    }

    public GameConfiguration setSizey(int sizey) {
        this.sizey = sizey;
        return this;
    }

    public int getLength() {
        return length;
    }

    public GameConfiguration setLength(int length) {
        this.length = length;
        return this;
    }

    public int getNumRays() {
        return numRays;
    }

    public GameConfiguration setNumRays(int numRays) {
        this.numRays = numRays;
        return this;
    }
}
