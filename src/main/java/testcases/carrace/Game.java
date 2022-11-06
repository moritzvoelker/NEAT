/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package testcases.carrace;


import neat.Organism;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Game {


    private Random generator;
    private Vector<Player> players;
    private Vector<Player> activePlayers;
    private RaceTrack raceTrack;

    private int currentScore;
    private int iterations;


    public Game(long seed) {
        players = new Vector<>(0);
        activePlayers = new Vector<>(0);
        generator = new Random(seed);
    }

    public Game(int numPlayers) {
        players = new Vector<>(numPlayers);
        activePlayers = new Vector<>(numPlayers);
        addPlayers(numPlayers);

        generator = new Random();
    }

    public Game(int numPlayers, long seed) {

        players = new Vector<>(numPlayers);
        activePlayers = new Vector<>(numPlayers);
        addPlayers(numPlayers);

        generator = new Random(seed);

    }

    public Player addPlayer() {
        Player player = new Player();
        players.add(player);
        return player;
    }

    public List<Player> addPlayers(int num) {
        if (num <= 0)
            return new ArrayList<>(0);

        List<Player> ret = new ArrayList<>(num);
        for (int i = 0; i < num; i++)
            ret.add(new Player());

        players.addAll(ret);
        return ret;
    }

    public void startGame(GameConfiguration gameConfiguration) {
        raceTrack = new RaceTrack(gameConfiguration.getSizex(), gameConfiguration.getSizey(), gameConfiguration.getLength(), generator);
        startGame(raceTrack);
    }

    public void startGame(int sizex, int sizey, int length) {
        raceTrack = new RaceTrack(sizex, sizey, length, generator);
        startGame(raceTrack);
    }

    public void startGame(RaceTrack raceTrack) {
        this.raceTrack = raceTrack;
        activePlayers.clear();
        activePlayers.addAll(players);
        RaceTrack.Position start = this.raceTrack.getStart();
        for (Player player : activePlayers) {
            player.setX(start.x);
            player.setY(start.y);
            player.setScore(0.0);
            RaceTrack.Position next = this.raceTrack.getPos(1);
            player.setOrientation(Math.atan2(next.y - start.y, next.x - start.x));
        }
        this.iterations = 0;
        this.currentScore = 0;
    }

    public boolean iterate() {
        activePlayers.forEach(Player::applyVelocity);
        activePlayers.removeIf(this::collisionAndScoring);
        if (++iterations > 30 *  1) {
            System.out.println("Kill all with score lower than " + currentScore);
            currentScore++;
            iterations = 0;
        }

        return activePlayers.size() > 0;
    }



    private boolean collisionAndScoring(Player player) {
        int collision_type = raceTrack.checkCollision(player);
        if (collision_type == -1 || collision_type == 1)
            return true;

        double score = raceTrack.checkScore(player.getX(), player.getY());
        if (player.getScore() < score)
            player.setScore(score);

        return score < currentScore;
    }


    public void paint(Graphics g, int width, int height) {
        // Paint Background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);


        int segmentSize = Math.min(width / this.raceTrack.sizex, height / this.raceTrack.sizey);

        paintRaceTrack(g, segmentSize);



        paintPlayers(g, segmentSize);
        double max_score = 0;
        for (Player player : players) {
            if (player.getScore() > max_score) {
                max_score = player.getScore();
            }
        }
        g.drawString("Best: " + max_score, width- 40, 10);
        g.setColor(Color.BLUE);
        g.drawString("Kill: " + currentScore, width- 40, 20);

    }

    private void paintRaceTrack(Graphics g, int segmentSize) {
        Graphics2D g2d = (Graphics2D) g;



        g2d.setColor(Color.BLUE);
        AffineTransform old = g2d.getTransform();

        Rectangle rect = new Rectangle(0, 0, segmentSize, segmentSize);

        for (int i = 0; i < this.raceTrack.sizex; i++) {
            for (int j = 0; j < this.raceTrack.sizey; j++) {
                switch(this.raceTrack.board[i][j]) {
                    case Start:
                        g2d.setColor(Color.CYAN);
                        break;
                    case Goal:
                        g2d.setColor(Color.MAGENTA);
                        break;
                    case Track:
                        g2d.setColor(Color.BLUE);
                        break;
                    case Empty:
                        continue;
                }

                g2d.translate(i * segmentSize, j * segmentSize);
                g2d.fill(rect);
                g2d.setColor(Color.BLACK);
                g2d.drawString("" + this.raceTrack.board_steps[i][j], segmentSize / 2, segmentSize / 2);
                g2d.setTransform(old);

            }

        }
    }

    private void paintPlayers(Graphics g, int segmentSize) {
        Graphics2D g2d = (Graphics2D) g;


        g2d.setColor(Color.RED);
        AffineTransform old = g2d.getTransform();

        int playerWidth = (int)(Player.getWidth() * segmentSize);
        int playerLength = (int)(Player.getLength() * segmentSize);
        Rectangle rect = new Rectangle(-playerLength/2, -playerWidth/2, playerLength, playerWidth);

        for (Player player : getActivePlayers()) {

            g2d.translate(player.getX() * segmentSize, player.getY() * segmentSize);
            g2d.rotate(player.getOrientation());
            g2d.fill(rect);
            g2d.setTransform(old);

        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getActivePlayers() {
        return activePlayers;
    }

    public RaceTrack getRaceTrack() {
        return raceTrack;
    }

    public void setSeed(long seed) {
        generator.setSeed(seed);
    }
}
