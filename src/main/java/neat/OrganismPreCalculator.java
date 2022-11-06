package neat;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class OrganismPreCalculator {
    List<Organism> organismsToCalculate;
    List<Thread> threads;

    private static class OrganismPreCalculatorThread implements Runnable {
        private final OrganismPreCalculator calculator;

        public OrganismPreCalculatorThread(OrganismPreCalculator calculator) {
            this.calculator = calculator;
        }

        @Override
        public void run() {
            Organism organism;
            while (true) {
                organism = calculator.getOrganismToCalculate();
                if (organism != null) {
                    organism.getOutput();
                } else {
                    return;
                }
            }
        }
    }

    public OrganismPreCalculator(int numThreads) {
        organismsToCalculate = new Vector<>();
        threads = new ArrayList<>(numThreads);
        for (int i = 0; i < numThreads; i++) {
            threads.add(new Thread(new OrganismPreCalculatorThread(this), "Precalculator-Thread " + i));
            threads.get(i).start();
        }
    }

    synchronized public void addOrganismsToCalculate(List<Organism> organisms) {
        organismsToCalculate.addAll(organisms);
        notifyAll();
    }

    synchronized public void addOrganismToCalculate(Organism organism) {
        organismsToCalculate.add(organism);
        notifyAll();
    }

    synchronized private Organism getOrganismToCalculate() {
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

}
