package neat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Species {
    private Organism representative;
    private List<Organism> members;
    private int overallFitness;

    public Species(Organism representative) {
        this.representative = representative;
        members = new LinkedList<>();
        members.add(representative);
        this.overallFitness = 0;
    }

    public void calculateOverallFitness() {
        overallFitness = 0;
        for (Organism organism : members) {
            overallFitness += organism.getFitness();
        }
        overallFitness /= members.size();
    }

    public List<Organism> produceOffspring() {
        return null;
    }

    public void setInput(List<double[]> input){
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setInput(input.get(i));
        }
    }

    public List<double[]> getOutput() {
        List<double[]> output = new ArrayList<>(members.size());
        for (Organism organism : members){
            output.add(organism.getOutput());
        }
        return output;
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

    public int getOverallFitness() {
        return overallFitness;
    }
}
