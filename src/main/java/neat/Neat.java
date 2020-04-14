package neat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Neat {
    private NeatConfiguration configuration;

    private List<Organism> population;
    private int globalInnovationNumber;

    public Neat(NeatConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setInput(List<double[]> input) throws IllegalArgumentException {
        for (int i = 0; i < configuration.getPopulationSize(); i++) {
            population.get(i).setInput(input.get(i));
        }
    }

    public List<double[]> getOutput() {
        List<double[]> output = new ArrayList<>(configuration.getPopulationSize());

        for (Organism organism : population) {
            output.add(organism.getOutput());
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

    public List<Organism> getPopulation() {
        return population;
    }
}
