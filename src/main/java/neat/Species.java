package neat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Species {
    private Organism representative;
    private List<Organism> members;
    private double overallFitness;

    public Species(Organism representative) {
        this.representative = representative;
        members = new LinkedList<>();
        members.add(representative);
        this.overallFitness = 0.0;
    }

    public double calculateOverallFitness() throws IllegalStateException {
        overallFitness = 0.0;
        for (Organism organism : members) {
            if (organism.getFitness() >= 0.0)
                overallFitness += organism.getFitness();
            else {
                overallFitness = -1.0;
                throw new IllegalStateException("the fitness of organism " + organism.toString() + " from species " + this.toString() + " has not been set");
            }
        }
        return overallFitness /= members.size();
    }

    public List<Organism> produceOffspring(int numberOfChildren, NeatConfiguration configuration) {
        /* 1.: Organismen abtöten
        *  2.: Fortpflanzen
        * */
        List<Organism> children = new ArrayList<>(numberOfChildren);
        Organism father;
        Organism mother;

        members.sort(Comparator.comparingDouble(Organism::getFitness));
        members = members.subList(0, (int) (members.size() * configuration.getSurvivalRate()));

        children.add(members.get(0));
        numberOfChildren--;
        for (int i = 0; i < numberOfChildren; i++) {
            if (Math.random() < configuration.getMutateOnlyRate()) {
                double random = Math.random() * overallFitness;
                double comulativeFitness = 0.0;
                for (Organism organism : members) {
                    comulativeFitness += organism.getFitness();
                    if (comulativeFitness > random) {
                        father = organism;
                        break;
                    }
                }
                children.add(father.mutate);
            }
        }

        // TODO: 16.04.2020 Mating of different Organisms, Mutation of organisms,






        return null;
    }

    public void setInput(List<double[]> input){
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setInput(input.get(i));
        }
    }

    public List<double[]> getOutput() {
        List<double[]> output = new ArrayList<>(members.size());
        for (Organism organism : members){
            output.add(organism.getOutput());
        }
        return output;
    }

    public Organism getRepresentative() {
        return representative;
    }

    public void setRepresentative(Organism representative) {
        this.representative = representative;
    }

    public List<Organism> getMembers() {
        return members;
    }

    public void setMembers(List<Organism> members) {
        this.members = members;
    }

    public double getOverallFitness() {
        return overallFitness;
    }
}
