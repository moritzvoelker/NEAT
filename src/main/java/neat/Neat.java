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

    public void nextGeneration() {
        List<Connection> currentMutations;
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

    public List<Species> getSpecies() {
        return species;
    }
}
