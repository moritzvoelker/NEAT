package testcases.carrace;

import neat.OrganismPreCalculator;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ManuelController implements Controller {
    private int vel;
    private int turn;
    private Player player;

    ManuelController(JFrame context, Player player) {
        this.vel = 0;
        this.turn = 0;
        this.player = player;

        context.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> vel = 1;
                    case KeyEvent.VK_DOWN -> vel = -1;
                    case KeyEvent.VK_LEFT -> turn = 1;
                    case KeyEvent.VK_RIGHT -> turn = -1;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP, KeyEvent.VK_DOWN -> vel = 0;
                    case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> turn = 0;
                }
            }
        });

    }

    @Override
    public void controlPlayer() {
        player.setAngleVel(turn);
        player.setVel(vel);
    }

    @Override
    public double getScore() {
        return player.getScore();
    }
}
