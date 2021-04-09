/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package neat;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Container for parts of the population of an associated instance of {@link Neat}.
 * Every population gets distributed into {@link Species}. This helps to protect new innovations in the topology of the organisms.
 */
public class Species implements Serializable {
    private int generationsSinceImprovement;
    private Organism representative;
    private List<Organism> members;
    private transient double averageFitness;
    private double oldChampFitness;

    private NeatConfiguration configuration;

    /**
     * Constructs an instance of {@link Species} with an empty members {@link List} and the given {@link NeatConfiguration}.
     * @param representative The {@link Organism} which is used as reference for all other organisms to calculate the difference between them and henceforth decide if the organism belongs to this species.
     * @param configuration Has to be the configuration from the associated {@link Neat} instance.
     */
    public Species(Organism representative, NeatConfiguration configuration) {
        this.configuration = configuration;
        this.representative = representative;
        members = new LinkedList<>();
        members.add(representative);
        this.averageFitness = 0.0;
        this.oldChampFitness = 0.0;
    }

    /**
     * Calculates and sets the average fitness of all members.
     * @return The average fitness of all members.
     * @throws IllegalStateException if the fitness of one {@link Organism} from members hasn't been set. Average fitness gets set to -1.0.
     */
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

    /**
     * Returns the specified number of {@link Organism}s. They get randomly chosen, but the ones with a higher fitness are more likely to be chosen.
     * @param number The number of organisms to return.
     * @return A {@link List} of in respect of their fitness randomly chosen organisms.
     * @throws IllegalArgumentException If number exceeds the number of members.
     */
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

    /**
     * Deletes the old members and produces offspring of them for the next generation. The produced offspring are not added to the {@link Species}. The species is not ready for the next generation.
     * @param newPopulation    The {@link List} to which the new organisms are added.
     * @param currentMutations The list of already occurred mutations which is necessary to correctly align genes in the next reproduction.
     * @param species          The list of all current species.
     * @param numberOfChildren The number of children this species has to generate.
     * @param innovationNumber The current innovation number.
     * @return the new innovation number.
     * @throws IllegalStateException ???
     */
    public int produceOffspring(List<Organism> newPopulation, List<Connection> currentMutations, List<Species> species, int numberOfChildren, int innovationNumber) throws IllegalStateException {
        if (numberOfChildren == 0) {
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
            if (members.size() == 1) {
                child = new Organism(members.get(0));
            } else if (Math.random() < configuration.getMutateOnlyRate()) {
                child = new Organism(getWeightedRandomMember(1).get(0));

            } else if (species.size() >= 2 && Math.random() < configuration.getMateInterspeciesRate()) {
                List<Species> tmp = new ArrayList<>(species);
                tmp.remove(this);
                child = Organism.crossover(getWeightedRandomMember(1).get(0), tmp.get((int)(Math.random() * tmp.size())).getWeightedRandomMember(1).get(0), configuration);
            } else {
                List<Organism> organisms = getWeightedRandomMember(2);
                child = Organism.crossover(organisms.get(0), organisms.get(1), configuration);
            }
            innovationNumber = child.mutate(currentMutations, innovationNumber);
            newPopulation.add(child);
        }

        representative = members.get((int) (Math.random() * members.size()));
        return innovationNumber;
    }

    /**
     * Calculates, if the fitness of the champion of this {@link Species} has improved. If not, it increments {@link #generationsSinceLastImprovement}. Otherwise it sets it to zero.
     * @return {@link #generationsSinceLastImprovement}
     */
    public int generationsSinceLastImprovement() {
        double champFitness = getChamp().getFitness();
        if (oldChampFitness >= champFitness) {
            generationsSinceImprovement++;
        } else {
            generationsSinceImprovement = 0;
            oldChampFitness = champFitness;
        }

        return generationsSinceImprovement;
    }

    /**
     * Sets the inputs of all {@link Organism}s of this {@link Species}. When in the same generation, the order of organisms doesn't change, but is not deterministic.
     *
     * @param input A {@link List} of double arrays containing all inputs.
     *              Each element of the list corresponds to an {@link Organism}. The order should not be changed throughout the complete process of one generation.
     *              Each element of an array corresponds to an {@link InputNode}. The order should not be changed throughout the complete process of evolution.
     * @param offset The offset for this particular species. It will start at offset and end at offset + members.size().
     */
    public void setInput(List<double[]> input, int offset) {
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setInput(input.get(offset + i));
        }
    }
    /**
     * Starts the evaluation of all {@link Organism}s of this {@link Species}. Depending on the size of the organisms this might take a while, because the complete network has to be calculated. The order of the organisms is the same as for the inputs.
     *
     * @return a {@link List} of double arrays containing all outputs.
     * Each element of the list corresponds to an {@link Organism}. The order doesn't change throughout the complete process of one generation.
     * Each element of an array corresponds to an output {@link Node}. The order doesn't change throughout the complete process of evolution.
     */
    public List<double[]> getOutput() {
        List<double[]> output = new ArrayList<>(members.size());
        for (Organism organism : members) {
            output.add(organism.getOutput());
        }
        return output;
    }
    /**
     * Sets the fitness for each {@link Organism} of this {@link Species} in the same order as the inputs and outputs are set and gotten. The fitness should directly correspond to how well an organism did in the designated task, because it is preferred for reproduction into the next generation.
     *
     * @param fitness an array containing the fitness for each organism in the same order as the inputs and outputs are set and gotten.
     * @param offset The offset for this particular species. It will start at offset and end at offset + members.size().
     */
    public void setFitness(double[] fitness, int offset) {
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setFitness(fitness[offset + i]);
        }
    }

    /**
     * Returns the {@link Organism} with the highest fitness. Respectively it should only be called after {@link #setFitness(double[],int)}, otherwise the organisms chosen to reproduce are not unpredictable.
     * In order to get the organisms every organism has to be compared with every other organism from this {@link Species} everytime, so with big member sizes it can be rather costly.
     * @return the organism with the highest fitness.
     */
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

    public void setGenerationsSinceImprovement(int generationsSinceImprovement) {
        this.generationsSinceImprovement = generationsSinceImprovement;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        averageFitness = 0.0;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        members = new LinkedList<>(members);
        objectOutputStream.defaultWriteObject();
    }
}
