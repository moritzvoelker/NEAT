package gui;

import neat.Organism;

public interface Testcase {
    void init();
    int doNGenerations(int n);
    int getGeneration();
    Organism getChamp();
}
