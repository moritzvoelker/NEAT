package testcases.carrace;

import neat.Organism;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public interface Controller {

    void controlPlayer(Player player);

//    Organism controller;
//    int accelerate;
//    int turn;

//    public Controller() {
//
//        new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent e) {
//
//            }
//
//            @Override
//            public void keyPressed(KeyEvent e) {
//                switch (e.getKeyCode()) {
//                    case KeyEvent.VK_UP -> accelerate = 1;
//                    case KeyEvent.VK_DOWN -> accelerate = -1;
//                    case KeyEvent.VK_LEFT -> turn = 1;
//                    case KeyEvent.VK_RIGHT -> turn = -1;
//                }
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//                switch (e.getKeyCode()) {
//                    case KeyEvent.VK_UP, KeyEvent.VK_DOWN -> accelerate = 0;
//                    case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> turn = 0;
//                }
//            }
//        };
//        accelerate = 0;
//        turn = 0;
//    }
//
//    void setInput(Player player){
//        player.setVel((double) accelerate);
//        player.setAngleVel(turn);
//    }

}
