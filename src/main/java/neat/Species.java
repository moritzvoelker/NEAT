package neat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Species {
    int generationsSinceInprovement;
    private Organism representative;
    private List<Organism> members;
    private double averageFitness;
    private double oldAverageFitness;

    public Species(Organism representative) {
        this.representative = representative;
        members = new LinkedList<>();
        members.add(representative);
        this.averageFitness = 0.0;
        this.oldAverageFitness = 0.0;
    }

    public double calculateAverageFitness() throws IllegalStateException {
        averageFitness = 0.0;
        for (Organism organism : members) {
            if (organism.getFitness() >= 0.0)
                averageFitness += organism.getFitness();
            else {
                averageFitness = -1.0;
                throw new IllegalStateException("the fitness of organism " + organism.toString() + " from species " + this.toString() + " has not been set");
            }
        }
        averageFitness /= members.size();
        // TODO: 25.04.2020 How da fuq???
        return averageFitness;
    }

    // TODO: 17.04.2020 Maybe package private?
    // TODO: 23.04.2020 Think of an elegant solution for when fitness not properly set, at the moment NullPointerException
    public int produceOffspring(List<Organism> newPopulation, List<Connection> currentMutations, int numberOfChildren, int innovationNumber, NeatConfiguration configuration) throws NullPointerException {
        if (numberOfChildren == 0) {
            return innovationNumber;
        }

        Organism father = null;
        Organism mother = null;

        members.sort((organism1, organism2) -> {
            if (organism1.getFitness() > organism2.getFitness()) {
                return -1;
            } else if (organism1.getFitness() < organism2.getFitness()) {
                return 1;
            } else {
                return 0;
            }
        });

        members = members.subList(0, (int) (members.size() * configuration.getSurvivalRate()) + 1);
        calculateAverageFitness();

        newPopulation.add(members.get(0));
        numberOfChildren--;
        for (int i = 0; i < numberOfChildren; i++) {
            if (Math.random() < configuration.getMutateOnlyRate()) {
                double random = Math.random() * averageFitness * members.size();
                double comulativeFitness = 0.0;
                for (Organism organism : members) {
                    comulativeFitness += organism.getFitness();
                    if (comulativeFitness > random) {
                        father = new Organism(organism);
                        break;
                    }
                }
                innovationNumber = father.mutate(currentMutations, innovationNumber, configuration);
                newPopulation.add(father);
            } else {
                double randomFather = Math.random() * averageFitness * members.size();
                double randomMother = Math.random() * averageFitness * members.size();
                double cumulativeFitness = 0.0;
                for (Organism organism : members) {
                    cumulativeFitness += organism.getFitness();
                    if (cumulativeFitness > randomFather && father == null) {
                        father = organism;
                        continue;
                    }
                    if (cumulativeFitness > randomMother && mother == null) {
                        mother = organism;
                    }
                    if (father != null && mother != null) {
                        break;
                    }
                }
                if (mother == null) {
                    mother = members.get(0);
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

    public boolean hasNotImprovedRecently(NeatConfiguration configuration) {
        calculateAverageFitness();
        if (oldAverageFitness >= averageFitness) {
            generationsSinceInprovement++;
        } else {
            generationsSinceInprovement = 0;
            oldAverageFitness = averageFitness;
        }

        return generationsSinceInprovement == configuration.getPurgeAge();
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

    public void setFitness(double[] fitness, int offset) {
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setFitness(fitness[offset + i]);
        }
    }

    public Organism getChamp() {
        Organism champ = members.get(0);
        for (int i = 1; i < members.size(); i++) {
            if (members.get(i).getFitness() > champ.getFitness()) {
                champ = members.get(i);
            }
        }
        return champ;
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

    public double getAverageFitness() {
        return averageFitness;
    }
}
