/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package neat;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The core of this implementation of the NEAT-Algorithm. It provides the the API which is to be used.
 */
public class Neat implements Serializable {
    private NeatConfiguration configuration;

    private List<Species> species;
    private int globalInnovationNumber;
    private int generationsSinceLastImprovement;
    private Organism lastChamp;
    private transient List<Thread> threads;
    private transient List<Organism> organismsToCalculate;

    /**
     * Constructs an instance of Neat with the given configuration.
     *
     * @param configuration the configuration for the NEAT-Algorithm
     */
    public Neat(NeatConfiguration configuration) {
        this.configuration = configuration;
        species = new LinkedList<>();
        initializeThreads();
    }

    private void initializeThreads() {
        if (configuration.isPrecalculateNodes()) {
            organismsToCalculate = new Vector<>();
            threads = new ArrayList<>(configuration.getNumberOfThreads());
            for (int i = 0; i < configuration.getNumberOfThreads(); i++) {
                threads.add(new Thread(new NodePreCalculator(this), "Precalculator-Thread " + i));
                threads.get(i).start();
            }
        }
    }

    /**
     * Sets the inputs of all {@link Organism}s. When in the same generation, the order of organisms doesn't change, but is not deterministic. One cannot judge the {@link Species} of an organism by the position it has in the population.
     *
     * @param input A {@link List} of double arrays containing all inputs.
     *              Each element of the list corresponds to an {@link Organism}. The order should not be changed throughout the complete process of one generation.
     *              Each element of an array corresponds to an {@link InputNode}. The order should not be changed throughout the complete process of evolution.
     * @throws IllegalArgumentException when the list doesn't have the same length as there are organisms or if the array-length doesn't match up the input count.
     */
    public void setInput(List<double[]> input) throws IllegalArgumentException {
        if (input.size() != configuration.getPopulationSize()) {
            throw new IllegalArgumentException("Input size doesn't match population size. Expected: " + configuration.getPopulationSize() + "; provided: " + input.size());
        }
        int offset = 0;
        for (Species currentSpecies : species) {
            currentSpecies.setInput(input, offset);
            offset += currentSpecies.getMembers().size();
            if (configuration.isPrecalculateNodes()) {
                synchronized (this) {
                    organismsToCalculate.addAll(currentSpecies.getMembers());
                    notifyAll();
                }
            }
        }
    }

    synchronized Organism getOrganismToCalculate() {
        Organism organism = null;
        do {
            if (organismsToCalculate.size() > 0) {
                organism = organismsToCalculate.remove(0);
            } else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    return null;
                }
            }
        } while (organism == null);
        return organism;
    }

    /**
     * Starts the evaluation of all {@link Organism}s. Depending on the size of the organisms this might take a while, because the complete network has to be calculated. The order of the organisms is the same as for the inputs.
     *
     * @return a {@link List} of double arrays containing all outputs.
     * Each element of the list corresponds to an {@link Organism}. The order doesn't change throughout the complete process of one generation.
     * Each element of an array corresponds to an output {@link Node}. The order doesn't change throughout the complete process of evolution.
     */
    public List<double[]> getOutput() {
        List<double[]> output = new ArrayList<>(configuration.getPopulationSize());

        for (Species currentSpecies : species) {
            output.addAll(currentSpecies.getOutput());
        }
        return output;
    }

    /**
     * Sets the fitness for each {@link Organism} in the same order as the inputs and outputs are set and gotten. The fitness should directly correspond to how well an organism did in the designated task, because it is preferred for reproduction into the next generation.
     *
     * @param fitness an array containing the fitness for each organism in the same order as the inputs and outputs are set and gotten.
     * @throws IllegalArgumentException  when the array doesn't have the same length as there are organisms.
     */
    public void setFitness(double[] fitness) throws IllegalArgumentException {
        if (fitness.length != configuration.getPopulationSize()) {
            throw new IllegalArgumentException("Input size doesn't match population size. Expected: " + configuration.getPopulationSize() + "; provided: " + fitness.length);
        }
        int offset = 0;
        for (Species currentSpecies : species) {
            currentSpecies.setFitness(fitness, offset);
            offset += currentSpecies.getMembers().size();
        }
    }

    /**
     * Clears this instance of {@link Neat} from the current population, if one is present, and generates a new one according to the current {@link NeatConfiguration}. The first generation is ready to be evaluated.
     *
     * @throws InvalidConfigurationException if the configuration is has invalid fields.
     */
    public void firstGeneration() throws InvalidConfigurationException {
        firstGeneration(configuration);
    }

    /**
     * Clears this instance of {@link Neat} from the current population, if one is present, and generates a new one according to the passed {@link NeatConfiguration}. The first generation is ready to be evaluated.
     *
     * @param configuration the new configuration which is used from now on.
     * @throws InvalidConfigurationException if the configuration is has invalid fields.
     */
    public void firstGeneration(NeatConfiguration configuration) throws InvalidConfigurationException {
        List<Exception> exceptions = configuration.validate();
        if (!exceptions.isEmpty()) {
            throw new InvalidConfigurationException(exceptions);
        }
        this.configuration = configuration;

        species.clear();
        lastChamp = null;

        globalInnovationNumber = configuration.getInputCount() + configuration.getOutputCount() + 1;

        generateOrganisms(configuration.getPopulationSize());

        generationsSinceLastImprovement = 0;
    }

    private void generateOrganisms(int numberOfOrganisms) {
        List<Connection> addedConnections = new LinkedList<>();
        for (int k = 0; k < numberOfOrganisms; k++) {
            Organism organism = new Organism(configuration);
            for (int i = 0; i < configuration.getOutputCount(); i++) {
                Node out = organism.getOutputNodes().get(i);
                Node in = organism.getInputNodes().get((int) (Math.random() * configuration.getInputCount()));

                Connection connection = new Connection(in, out, Math.random() * configuration.getMaxConnectionAbsoluteValue() * 2 - configuration.getMaxConnectionAbsoluteValue());
                out.getIn().add(connection);
                if (configuration.isBiasNodeEnabled()) {
                    Connection biasConnection = new Connection(organism.getBias(), out, Math.random() * configuration.getMaxConnectionAbsoluteValue() * 2 - configuration.getMaxConnectionAbsoluteValue());
                    out.getIn().add(biasConnection);
                    organism.getConnections().add(biasConnection);
                    globalInnovationNumber = biasConnection.setInnovationNumber(globalInnovationNumber, addedConnections);
                }


                in.getIn().add(connection); // Sorry... Abusing the InputConnection-List to look up which InputNodes already have connections.

                globalInnovationNumber = connection.setInnovationNumber(globalInnovationNumber, addedConnections);

                organism.getConnections().add(connection);
            }
            for (InputNode node : organism.getInputNodes()) {
                if (node.getIn().isEmpty()) {
                    Node out = organism.getOutputNodes().get((int) (Math.random() * configuration.getOutputCount()));
                    Connection connection = new Connection(node, out, configuration.getMaxConnectionAbsoluteValue() * 2 - configuration.getMaxConnectionAbsoluteValue());
                    out.getIn().add(connection);
                    globalInnovationNumber = connection.setInnovationNumber(globalInnovationNumber, addedConnections);
                    organism.getConnections().add(connection);
                } else {
                    node.getIn().clear();
                }
            }
            specify(organism);
            if (organism.getConnections().stream().noneMatch(connection -> connection.getWeight() == 2.0)) {
                System.out.println("Error");
            }
        }
    }

    // TODO: 05.06.2020 Interspecies mating

    /**
     * Builds the next generation from the old one. {@link #setFitness(double[])} has to be called first, otherwise the organisms chosen to reproduce are not unpredictable.
     * The order of the organisms is unpredictable. The number and size of species is likely to change as well. The new generation is ready to be evaluated.
     */
    public void nextGeneration() {
        List<Connection> currentMutations = new LinkedList<>();
        List<Organism> newPopulation = new ArrayList<>(configuration.getPopulationSize());
        Organism champ = getChamp();
        int speciesOfChamp = 0;

        System.out.println("Number of Species: " + species.size());
        if (species.size() == 1) {
            System.out.println("Too few species");
        }

        if (lastChamp != null && champ.getFitness() <= lastChamp.getFitness()) {
            if (++generationsSinceLastImprovement > configuration.getPurgeAge() && species.size() > 1) {
                if (species.get(1).getAverageFitness() > species.get(0).getAverageFitness()) {
                    species.set(0, species.set(1, species.get(0)));
                }
                for (int i = 2; i < species.size(); i++) {
                    if (species.get(i).getAverageFitness() > species.get(0).getAverageFitness()) {
                        species.get(1).getMembers().clear();
                        species.set(1, species.set(0, species.get(i)));
                    } else if (species.get(i).getAverageFitness() > species.get(1).getAverageFitness()) {
                        species.get(1).getMembers().clear();
                        species.set(1, species.get(i));
                    }
                }
                species.stream().skip(2).forEach(species1 -> species1.getMembers().clear());
                species = species.subList(0, 2);
                if (!species.get(0).getMembers().contains(champ) && !species.get(1).getMembers().contains(champ)) {
                    newPopulation.add(champ);
                }

                species.forEach(species1 -> species1.setGenerationsSinceImprovement(0));
            }
        } else {
            generationsSinceLastImprovement = 0;
            lastChamp = champ;
        }

        if (species.size() > 2) {
            species = species.stream().filter(species1 -> {
                if (species1.generationsSinceLastImprovement() >= configuration.getMaxGenerationsWithoutImprovement()) {
                    if (species1.getChamp().equals(champ)) {
                        newPopulation.add(champ);
                    }
                    species1.getMembers().clear();
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
        }

        double overallFitness = species.stream().mapToDouble(Species::getAverageFitness).sum();
        int[] speciesSizes = new int[species.size()];

        int i = 0;
        int currentPopulationSize = newPopulation.size();
        for (Species currentSpecies : species) {
            currentPopulationSize += speciesSizes[i] = (int) ((configuration.getPopulationSize() - newPopulation.size()) * (currentSpecies.getAverageFitness() / overallFitness));
            if (currentSpecies.getChamp().equals(champ)) {
                speciesOfChamp = i;
            }
            i++;
        }

        if (newPopulation.size() == 0 && configuration.getPopulationSize() > currentPopulationSize) {
            speciesSizes[speciesOfChamp]++;
            currentPopulationSize++;
        }

        // TODO: 31.08.2020 Maybe randomize, since it favors the first species
        for (i = 0; i < configuration.getPopulationSize() - currentPopulationSize; i++) {
            speciesSizes[i % species.size()]++;
        }

        i = 0;
        for (Species currentSpecies : species) {
            globalInnovationNumber = currentSpecies.produceOffspring(newPopulation, currentMutations, species, speciesSizes[i], globalInnovationNumber);
            i++;
        }
        species.forEach(specie -> specie.getMembers().clear());

        for (Organism organism : newPopulation) {
            specify(organism);
        }
        System.out.println("Species with member size");
        for (int j = species.size() - 1; j >= 0; j--) {
            System.out.println(species.get(j) + " has " + species.get(j).getMembers().size());
            if (species.get(j).getMembers().isEmpty()) {
                species.remove(species.get(j));
            }
        }
        System.out.println("Number of Species: " + species.size());
    }

    private void specify(Organism organism) {
        for (Species currentSpecies : species) {
            if (organism.isMember(currentSpecies)) {
                currentSpecies.getMembers().add(organism);
                return;
            }
        }
        try {
            species.add(new Species(organism, configuration));
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns all current species. The order doesn't change during one generation.
     * @return all current species.
     */
    public List<Species> getSpecies() {
        return species;
    }

    /**
     * Returns the {@link Organism} with the highest fitness. Respectively it should only be called after {@link #setFitness(double[])}, otherwise the organisms chosen to reproduce are not unpredictable.
     * In order to get the organisms every organism has to be compared with every other organism from its {@link Species} everytime, so with big population sizes it can be rather costly.
     * @return the organism with the highest fitness.
     * @throws IllegalStateException if {@link #firstGeneration()} hasn't been called yet.
     */
    public Organism getChamp() throws IllegalStateException {
        try {
            return species.stream()
                    .map(Species::getChamp)
                    .sorted(Comparator.comparingDouble(Organism::getFitness))
                    .skip(species.size() - 1)
                    .findFirst().get();
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("There are no species. Call firstGeneration() first.");
        }
    }

    public List<Organism> getOrganismsToCalculate() {
        return organismsToCalculate;
    }

    @Serial
    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        initializeThreads();
    }

    @Serial
    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        species = new LinkedList<>(species);
        objectOutputStream.defaultWriteObject();
    }
}
