package neat;

import java.util.*;

public class Neat {
    private NeatConfiguration configuration;

    private List<Species> species;
    private int globalInnovationNumber;

    public Neat(NeatConfiguration configuration) {
        this.configuration = configuration;
    }

    // TODO: 23.04.2020 Offset instead of clearing?
    public void setInput(List<double[]> input) throws IllegalArgumentException {
        for (Species currentSpecies : species){
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

        for (Species currentSpecies: species) {
            output.addAll(currentSpecies.getOutput());
        }

        return output;
    }

    public void setFitness(double[] fitness) {
        int offset = 0;
        for (Species currentSpecies : species){
            currentSpecies.setFitness(fitness, offset);
            offset += currentSpecies.getMembers().size();
        }
    }

    public void firstGeneration() {
        firstGeneration(configuration);
    }

    public void firstGeneration(NeatConfiguration configuration) {
        this.configuration = configuration;
        List<Connection> addedConnections = new LinkedList<>();
        species = new LinkedList<>();
        globalInnovationNumber = configuration.getInputCount() + configuration.getOutputCount();

        for (int k = 0; k < configuration.getPopulationSize(); k++) {
            Organism organism = new Organism();
            for (int i = 0; i < configuration.getInputCount(); i++) {
                organism.getInputNodes().add((InputNode) NodeFactory.create("", NodeType.Input, i));
            }
            for(int i = 0; i < configuration.getOutputCount(); i++) {
                Node out = NodeFactory.create("", NodeType.Output, i + configuration.getInputCount());
                organism.getOutputNodes().add(out);

                Connection connection = new Connection(organism.getInputNodes().get((int)(Math.random() * configuration.getInputCount())), out, Math.random() * 2 - 1);
                out.getIn().add(connection);
                globalInnovationNumber = connection.setInnovationNumber(globalInnovationNumber, addedConnections);

                organism.getConnections().add(connection);
            }
            specify(organism);
        }
    }

    // TODO: 23.04.2020 If there is no improvement in a species for a certain time, EXTERMINATE
    public void nextGeneration() {
        List<Connection> currentMutations = new LinkedList<>();
        List<Organism> newPopulation = new ArrayList<>(configuration.getPopulationSize());
        int[] speciesSizes = new int[species.size()];

        double overallFitness = species.stream().mapToDouble(Species::calculateAverageFitness).sum();


        int i = 0;
        int currentPopulationSize = 0;
        for (Species currentSpecies : species) {
            currentPopulationSize += speciesSizes[i] = (int)(configuration.getPopulationSize() * (currentSpecies.getAverageFitness() / overallFitness));
            i++;
        }

        for (i = 0; i < configuration.getPopulationSize() - currentPopulationSize; i++) {
            speciesSizes[i % species.size()]++;
        }

        i = 0;
        for (Species currentSpecies : species) {
            globalInnovationNumber = currentSpecies.produceOffspring(newPopulation, currentMutations, speciesSizes[i], globalInnovationNumber, configuration);
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

    private void specify(Organism organism){
        for (Species currentSpecies : species) {
            if (organism.isMember(currentSpecies, configuration)) {
                currentSpecies.getMembers().add(organism);
                return;
            }
        }
        species.add(new Species(organism));
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
