package testcases.carrace;

import neat.Organism;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class NNController implements Controller {
    int vel;
    int turn;

    Organism organism;

    NNController(Organism organism) {
        this.vel = 0;
        this.turn = 0;
        this.organism = organism;

    }

    @Override
    public void controlPlayer(Player player) {
        double[] output = organism.getOutput();
        player.setAngleVel(output[0]);
        player.setVel(output[1]);
    }
}
