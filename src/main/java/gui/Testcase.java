package gui;

import neat.Organism;

import javax.swing.*;

public interface Testcase {
    void init();
    int doNGenerations(int n);
    int getGeneration();
    Organism getChamp();
    JPanel getAnimationPanel();
    int[] getFitnessDistribution();
}
