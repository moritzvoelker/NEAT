package neat;

public class NodePreCalculator implements Runnable {
    private final Neat neat;

    public NodePreCalculator(Neat neat) {
        this.neat = neat;
    }

    @Override
    public void run() {
        Organism organism;
        while (true) {
            organism = neat.getOrganismToCalculate();
            if (organism != null) {
                organism.getOutput();
            }
        }
    }
}
