package neat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Species {
    private int generationsSinceInprovement;
    private Organism representative;
    private List<Organism> members;
    private double averageFitness;
    private double oldAverageFitness;

    private NeatConfiguration configuration;

    public Species(Organism representative, NeatConfiguration configuration) {
        this.configuration = configuration;
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
        return averageFitness;
    }

    public List<Organism> getWeightedRandomMember(int number) throws IllegalArgumentException {
        if (members.size() < number) {
            throw new IllegalArgumentException("number has to be smaller or equal to members.size() for number = " + number + " and members.size() = " + members.size());
        }
        List<Double> randoms = new ArrayList<>(number);
        List<Organism> organisms = new ArrayList<>(number);
        for (int i = 0; i < number; i++) {
            randoms.add(Math.random() * averageFitness * members.size());
        }
        double comulativeFitness = 0.0;
        for (Organism organism : members) {
            comulativeFitness += organism.getFitness();
            for (Double random : randoms) {
                if (random < comulativeFitness) {
                    organisms.add(organism);
                    randoms.remove(random);
                    break;
                }
            }
            if (randoms.size() == 0) {
                break;
            }
        }
        while (organisms.size() < number) {
            for (int i = 1; i <= members.size(); i++) {
                Organism organism = members.get(members.size() - i);
                if (!organisms.contains(organism)) {
                    organisms.add(organism);
                    break;
                }
            }
        }
        return organisms;
    }

    // TODO: 17.04.2020 API nochmal überprüfen und überarbeiten
    public int produceOffspring(List<Organism> newPopulation, List<Connection> currentMutations, int numberOfChildren, int innovationNumber) throws IllegalStateException {
        if (numberOfChildren == 0) {
            members.clear();
            return innovationNumber;
        }

        Organism child;

        members.sort((organism1, organism2) -> Double.compare(organism2.getFitness(), organism1.getFitness()));

        int numberOfSurvivors = (int)(members.size() * configuration.getSurvivalRate()) + 1;
        if (numberOfSurvivors > members.size()) {
            numberOfSurvivors = members.size();
        }
        members = members.subList(0, numberOfSurvivors);
        calculateAverageFitness();

        newPopulation.add(new Organism(members.get(0)));
        numberOfChildren--;


        for (int i = 0; i < numberOfChildren; i++) {
            double productionType = Math.random();
            if (members.size() == 1) {
                child = new Organism(members.get(0));
            } else if (productionType < configuration.getMutateOnlyRate()) {
                child = new Organism(getWeightedRandomMember(1).get(0));

            } else {
                List<Organism> organisms = getWeightedRandomMember(2);
                child = Organism.crossover(organisms.get(0), organisms.get(1), configuration);
            }
            innovationNumber = child.mutate(currentMutations, innovationNumber);
            newPopulation.add(child);
        }

        representative = members.get((int) (Math.random() * members.size()));
        members.clear();

        return innovationNumber;
    }

    public int generationsSinceLastImprovement() {
        calculateAverageFitness();
        if (oldAverageFitness >= averageFitness) {
            generationsSinceInprovement++;
        } else {
            generationsSinceInprovement = 0;
            oldAverageFitness = averageFitness;
        }

        return generationsSinceInprovement;
    }

    public void setInput(List<double[]> input) {
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setInput(input.get(i));
        }
    }

    public List<double[]> getOutput() {
        List<double[]> output = new ArrayList<>(members.size());
        for (Organism organism : members) {
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
        for (Organism organism : members) {
            if (organism.getFitness() > champ.getFitness()) {
                champ = organism;
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

    public void setGenerationsSinceImprovement(int generationsSinceInprovement) {
        this.generationsSinceInprovement = generationsSinceInprovement;
    }
}
