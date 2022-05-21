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

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class GameAnimationPanel extends AnimationPanel {
    private int frameRate = 30;

    private Game game;
    private List<Organism> players;
    private long seed;

    public GameAnimationPanel() {
        this.setLayout(new BorderLayout());

        game = new Game(seed);
        players = new LinkedList<>();
    }


    public GameAnimationPanel(long seed, List<Organism> players) {
        this.setLayout(new BorderLayout());
        this.players = players;
        game = new Game(players, seed);
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

    }

    public List<Organism> getPlayers() {
        return players;
    }

    public void setPlayers(List<Organism> players) {
        this.players = players;
        game = new Game(players, seed);
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
        game = new Game(players, seed);
    }
}
