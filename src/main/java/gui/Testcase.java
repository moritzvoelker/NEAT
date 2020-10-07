package gui;

import neat.Organism;
import neat.Species;

import javax.swing.*;
import java.util.List;

public interface Testcase {
    void reset();
    void init();
    int doNGenerations(int n);
    int getGeneration();
    Organism getChamp();
    AnimationPanel getAnimationPanel();
    int[] getFitnessDistribution();
    List<Species> getSpecies();
    int getPopulationSize();
    boolean hasWorkingOrganism();
}
