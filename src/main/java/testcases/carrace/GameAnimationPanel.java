/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package testcases.carrace;

import gui.AnimationPanel;
import neat.Organism;
import neat.OrganismPreCalculator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.*;
import java.util.List;

public class GameAnimationPanel extends AnimationPanel {
    private int frameRate = 15;

    private BufferStrategy bufferStrategy;
    private Game game;
    private List<NNController> controllers;
    private long seed;
    private OrganismPreCalculator calculator;
    private GameConfiguration gameConfiguration;

    public GameAnimationPanel() {
        this.setLayout(new BorderLayout());
        this.setDoubleBuffered(true);

        game = new Game(seed);
        gameConfiguration = new GameConfiguration();
        controllers = new LinkedList<>();
        calculator = new OrganismPreCalculator(4);
    }


    public GameAnimationPanel(long seed, List<Organism> organisms) {
        this.setLayout(new BorderLayout());
        game = new Game(controllers.size(), seed);
        this.controllers = new LinkedList<>();
        for (Organism player : organisms) {
            this.controllers.add(new NNController(game.addPlayer(), player, gameConfiguration));
        }
        gameConfiguration = new GameConfiguration();

        calculator = new OrganismPreCalculator(4);
    }

    public void setGameLayout(int sizex, int sizey, int length) {
        gameConfiguration = new GameConfiguration().setSizex(sizex).setSizey(sizey).setLength(length);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (game.getActivePlayers().size() != 0) {
            game.paint(g, getWidth(), getHeight());

        }
    }


    @Override
    public void run() {
//        Canvas canvas = new Canvas();
        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (game.getActivePlayers().size() != 0) {
                    game.paint(g, getWidth(), getHeight());
                }
            }
        };

        JSlider slider = new JSlider(JSlider.VERTICAL, -1, 3, 0);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        List<double[]> inputs = new ArrayList<>(controllers.size());

        labelTable.put(-1, new JLabel("0.5x"));
        labelTable.put(0, new JLabel("1.0x"));
        labelTable.put(1, new JLabel("2.0x"));
        labelTable.put(2, new JLabel("4.0x"));
        labelTable.put(3, new JLabel("8.0x"));
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> {
            frameRate = (int)(30 * Math.pow(2, slider.getValue()));
        });
        this.add(panel, BorderLayout.CENTER);
        this.add(slider, BorderLayout.EAST);
//        canvas.addMouseListener(this.getMouseListeners()[0]);
//        canvas.createBufferStrategy(2);
//        bufferStrategy = canvas.getBufferStrategy();


        game.startGame(gameConfiguration);
        while (true) {
            long start = System.nanoTime();

            for (NNController controller : controllers) {
                controller.extractInput(game, calculator);
            }
            for (NNController controller : controllers) {
                controller.controlPlayer();
            }

            if (!game.iterate()) {
//                game = new Game(players, seed);
                game.startGame(gameConfiguration);
            }


//            if (canvas.isDisplayable()) {
//                do {
//                    Graphics g = bufferStrategy.getDrawGraphics();
//                    paintComponent(g);
//                    g.dispose();
//                } while (bufferStrategy.contentsRestored());
//
//                bufferStrategy.show();
//            }
            this.repaint();

            long elapsedTime = (System.nanoTime() - start) / 1_000_000;

            try {
//                Thread.sleep(100);
                if (1000 / frameRate > elapsedTime) {
                    Thread.sleep(1000 / frameRate - elapsedTime);
                }
            } catch (InterruptedException e) {
                controllers.clear();
                this.removeAll();
                return;
            }
        }
    }

    public List<Organism> getControllers() {
        List<Organism> ret = new ArrayList<>(controllers.size());
        for (NNController player : controllers) {
            ret.add(player.organism);
        }
        return ret;
    }

    public void setControllers(List<Organism> controllers) {
        this.controllers.clear();
        game.addPlayers(controllers.size() - game.getPlayers().size());
        for (int i = 0; i < controllers.size(); i++) {
            this.controllers.add(new NNController(game.getPlayers().get(i), controllers.get(i), gameConfiguration));
        }
    }

    public void startGame() {
        game.startGame(gameConfiguration);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
        game.setSeed(seed);
    }
}
