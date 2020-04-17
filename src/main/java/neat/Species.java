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

    // TODO: 17.04.2020 Maybe package private?
    public int produceOffspring(List<Organism> newPopulation, List<Connection> currentMutations, int numberOfChildren, int innovationNumber, NeatConfiguration configuration) {
        if (numberOfChildren == 0) {
            return innovationNumber;
        }

        Organism father = null;
        Organism mother = null;

        members.sort(Comparator.comparingDouble(Organism::getFitness));
        members = members.subList(0, (int) (members.size() * configuration.getSurvivalRate()));

        newPopulation.add(members.get(0));
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
                innovationNumber = father.mutate(currentMutations, innovationNumber, configuration);
                newPopulation.add(father);
            } else {
                double randomFather = Math.random() * overallFitness;
                double randomMother = Math.random() * overallFitness;
                double comulativeFitness = 0.0;
                for (Organism organism : members) {
                    comulativeFitness += organism.getFitness();
                    if (comulativeFitness > randomFather) {
                        father = organism;
                        continue;
                    }
                    if (comulativeFitness > randomMother) {
                        mother = organism;
                    }
                    if (father != null && mother != null) {
                        break;
                    }
                }
                Organism child = Organism.crossover(father, mother, configuration);
                innovationNumber = child.mutate(currentMutations, innovationNumber, configuration);
                newPopulation.add(child);
            }
        }

        representative = members.get((int)(Math.random() * members.size()));
        members.clear();

        return innovationNumber;
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
