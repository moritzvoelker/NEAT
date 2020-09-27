package neat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Neat {
    private NeatConfiguration configuration;

    private List<Species> species;
    private int globalInnovationNumber;
    private int generationsSinceLastImprovement;
    private Organism lastChamp;

    public Neat(NeatConfiguration configuration) {
        this.configuration = configuration;
        species = new LinkedList<>();
    }

    // TODO: 23.04.2020 Offset instead of clearing?
    public void setInput(List<double[]> input) throws IllegalArgumentException {
        for (Species currentSpecies : species) {
            try {
                currentSpecies.setInput(input.subList(0, currentSpecies.getMembers().size()));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            input.subList(0, currentSpecies.getMembers().size()).clear();
        }
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
            for (int i = 0; i < configuration.getInputCount(); i++) {
                organism.getInputNodes().add((InputNode) NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Input, i + 1));
            }
            for (int i = 0; i < configuration.getOutputCount(); i++) {
                Node out = NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Output, i + configuration.getInputCount() + 1);
                Node in = organism.getInputNodes().get((int) (Math.random() * configuration.getInputCount()));
                organism.getOutputNodes().add(out);

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
    // TODO: 05.06.2020 Sometimes evolution gets trapped and doesn't get to the goal in a 1000 generations. Why?

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
            globalInnovationNumber = currentSpecies.produceOffspring(newPopulation, currentMutations, speciesSizes[i], globalInnovationNumber);
            i++;
        }

        for (Organism organism : newPopulation) {
            specify(organism);
        }

        for (int j = species.size() - 1; j >= 0; j--) {
            if (species.get(j).getMembers().isEmpty()) {
                species.remove(species.get(j));
            }
        }
    }

    // TODO: 06.07.2020 Die Anzahl von Spezien springt komisch: von 1 auf 27, von 27 auf 1 (Vermutlich zu tun mit dem Sprung von size  19 -> 20)
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


    public Organism getChamp() {
        Organism champ = species.get(0).getChamp();
        for (int i = 1; i < species.size(); i++) {
            if (species.get(i).getChamp().getFitness() > champ.getFitness()) {
                champ = species.get(i).getChamp();
            }
        }
        return champ;
    }
}
