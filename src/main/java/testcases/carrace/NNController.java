package testcases.carrace;

import neat.Organism;
import neat.OrganismPreCalculator;


public class NNController implements Controller {

    Player player;
    Organism organism;
    GameConfiguration gameConfiguration;

    NNController(Player player, Organism organism, GameConfiguration gameConfiguration) {
        this.player = player;
        this.organism = organism;
        this.gameConfiguration = gameConfiguration;
    }

    void extractInput(Game game) {
        double[] input = new double[gameConfiguration.getNumRays()];
        for (int i = 0; i < gameConfiguration.getNumRays(); i++) {
            double angle = player.getOrientation() + i * 2 * Math.PI / gameConfiguration.getNumRays();
            input[i] = game.getRaceTrack().distanceOffCourse(player.getX(), player.getY(), Math.cos(angle), Math.sin(angle));
        }

        organism.setInput(input);
    }

    void extractInput(Game game, OrganismPreCalculator calculator) {
        extractInput(game);

        calculator.addOrganismToCalculate(organism);
    }

    @Override
    public void controlPlayer() {
        double[] output = organism.getOutput();
        double vel = 2 * output[0] - 1;
        if (vel > 0.5)
            player.setAngleVel(1.0);
        else if (vel < -0.5)
            player.setAngleVel(-1.0);
        else
            player.setAngleVel(0.0);
        player.setVel(output[1] > 0.5 ? 1.0 : 0.0);
    }

    @Override
    public double getScore() {
        return player.getScore();
    }
}
