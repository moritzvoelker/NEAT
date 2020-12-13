package neat;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Neat implements Serializable {
    private NeatConfiguration configuration;

    private List<Species> species;
    private int globalInnovationNumber;
    private int generationsSinceLastImprovement;
    private Organism lastChamp;
    private transient List<Thread> threads;
    private transient List<Organism> organismsToCalculate;

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
                threads.add(new Thread(new NodePreCalculator(this), "Thread " + i));
                threads.get(i).start();
            }
        }
    }

    public void setInput(List<double[]> input) throws IllegalArgumentException {
        int offset = 0;
        for (Species currentSpecies : species) {
            try {
                currentSpecies.setInput(input, offset);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
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

    public List<double[]> getOutput() {
        List<double[]> output = new ArrayList<>(configuration.getPopulationSize());

        for (Species currentSpecies : species) {
            output.addAll(currentSpecies.getOutput());
        }
        return output;
    }

    public void setFitness(double[] fitness) {
        int offset = 0;
        for (Species currentSpecies : species) {
            currentSpecies.setFitness(fitness, offset);
            offset += currentSpecies.getMembers().size();
        }
    }

    public void firstGeneration() {
        firstGeneration(configuration);
    }

    public void firstGeneration(NeatConfiguration configuration) {
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
                    Connection biasConnection = new Connection(organism.getBias(), out, configuration.getMaxConnectionAbsoluteValue() * 2 - configuration.getMaxConnectionAbsoluteValue());
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
        }
    }

    // TODO: 05.06.2020 Interspecies mating
    public void nextGeneration() {
        List<Connection> currentMutations = new LinkedList<>();
        List<Organism> newPopulation = new ArrayList<>(configuration.getPopulationSize());
        Organism champ = getChamp();
        int speciesOfChamp = 0;

        System.out.println("Number of Species: " + species.size());
        if (species.size() == 1) {
            System.out.println("Too few species");
        }


        if (champ.equals(lastChamp)) {
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

        for (int j = species.size() - 1; j >= 0; j--) {
            if (species.get(j).getMembers().isEmpty()) {
                species.remove(species.get(j));
            }
        }
    }

    private void specify(Organism organism) {
        for (Species currentSpecies : species) {
            if (organism.isMember(currentSpecies)) {
                currentSpecies.getMembers().add(organism);
                return;
            }
        }
        species.add(new Species(organism, configuration));
    }

    public List<Species> getSpecies() {
        return species;
    }


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

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        initializeThreads();
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        species = new LinkedList<>(species);
        objectOutputStream.defaultWriteObject();
    }
}
