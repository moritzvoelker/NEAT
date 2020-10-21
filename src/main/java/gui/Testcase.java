package gui;

import neat.NeatConfiguration;
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
    WidgetsPanel getWidgetsPanel();
    AnimationPanel getAnimationPanel();
    int[] getFitnessDistribution();
    List<Species> getSpecies();
    NeatConfiguration getConfiguration();
    boolean hasWorkingOrganism();
}
