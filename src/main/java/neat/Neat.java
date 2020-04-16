package neat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Neat {
    private NeatConfiguration configuration;

    private List<Species> species;
    private int globalInnovationNumber;

    public Neat(NeatConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setInput(List<double[]> input) throws IllegalArgumentException {
        for (Species currentSpecies : species){
            currentSpecies.setInput(input.subList(0, currentSpecies.getMembers().size()));
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

                globalInnovationNumber = organism.getConnections().get(organism.getConnections().size() - 1).setInnovationNumber(globalInnovationNumber, addedConnections);
                organism.getConnections().add(connection);
            }
            specify(organism);
        }
    }

    public void nextGeneration() {
        List<Connection> currentMutations = new LinkedList<>();
        List<Organism> newPopulation = new ArrayList<>(configuration.getPopulationSize());
        int[] speciesSizes = new int[species.size()];

        double overallFitness = 0.0;
        for (Species currentSpecies : species) {
            overallFitness += currentSpecies.calculateOverallFitness();
        }

        int i = 0;
        int currentPopulationSize = 0;
        for (Species currentSpecies : species) {
            currentPopulationSize += speciesSizes[i] = (int)(configuration.getPopulationSize() * (currentSpecies.getOverallFitness() / overallFitness));
            i++;
        }

        for (i = 0; i < configuration.getPopulationSize() - currentPopulationSize; i++) {
            speciesSizes[i % species.size()]++;
        }

        i = 0;
        for (Species currentSpecies : species) {
            newPopulation.addAll(currentSpecies.produceOffspring(speciesSizes[i]));
            i++;
        }

        for (Organism organism : newPopulation) {
            mutate(organism, currentMutations);
            specify(organism);
        }
    }

    private void mutate(Organism organism, List<Connection> currentMutations) {
        organism.mutateWeights(configuration.getMutationRateWeight(), configuration.getPerturbRate(), configuration.getStepSize());
        if (Math.random() < configuration.getMutationRateConnection()) {
            globalInnovationNumber = organism.mutateConnection(globalInnovationNumber, currentMutations);
        }
        if (Math.random() < configuration.getMutationRateNode()) {
            globalInnovationNumber = organism.mutateNode(globalInnovationNumber, currentMutations);
        }
        if (Math.random() < configuration.getMutationRateEnablement()) {
            organism.mutateEnablement();
        }

        organism.getConnections().sort(Comparator.comparingInt(Connection::getInnovationNumber));
        organism.getHiddenNodes().sort(Comparator.comparingInt(Node::getInnovationNumber));
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
}
