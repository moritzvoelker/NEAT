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
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Game {


    private Random generator;
    private Vector<Player> players;
    private Vector<Player> activePlayers;
    private RaceTrack raceTrack;

    private int currentScore;


    public Game(long seed) {
        players = new Vector<>(0);
        activePlayers = new Vector<>(0);
    }

    public Game(List<Organism> organisms) {
        players = new Vector<>(organisms.size());
        for (Organism organism : organisms) {
            players.add(new Player(new NNController(organism)));
        }
        activePlayers = new Vector<>(organisms.size());


    }

    public Game(List<Organism> organisms, long seed) {
        players = new Vector<>(organisms.size());
        for (Organism organism : organisms) {
            players.add(new Player(new NNController(organism)));
        }
        activePlayers = new Vector<>(organisms.size());

    }

    public void startGame(int sizex, int sizey, int length) {
        raceTrack = new RaceTrack(sizex, sizey, length);
        activePlayers.addAll(players);
        RaceTrack.Position start = raceTrack.getStart();
        for (Player player : activePlayers) {
            player.setX(start.x);
            player.setY(start.y);
        }
        this.currentScore = 0;
    }

    public void startGame(RaceTrack raceTrack) {
        this.raceTrack = raceTrack;
        activePlayers.addAll(players);
        RaceTrack.Position start = this.raceTrack.getStart();
        for (Player player : activePlayers) {
            player.setX(start.x);
            player.setY(start.y);
        }
        this.currentScore = 0;
    }

    public void addManuelPlayer(Controller manuel) {
        Player player = new Player(manuel);
        players.add(player);
    }

    public void setVelocity(int i, int dir) {
        players.get(i).setVel(dir * 5);
    }

    public void setSteeringAngle(int i, int dir) {
        players.get(i).setAngleVel(dir * 3.14 / 4);
    }

    public boolean iterate() {
        activePlayers.forEach(Player::applyVelocity);
        activePlayers.removeIf(this::collisionAndScoring);
        currentScore++;
        return activePlayers.size() > 0;
    }



    private boolean collisionAndScoring(Player player) {
        int collision_type = raceTrack.checkCollision(player);
        if (collision_type == -1) {
            player.setScore(currentScore);
            return true;
        } else if (collision_type == 1) {
            player.setScore(currentScore + 1000);
            return true;
        }

        return false;
    }


    public void paint(Graphics g, int width, int height) {
        // Paint Background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);


        int segmentSize = Math.min(width / this.raceTrack.sizex, height / this.raceTrack.sizey);

        paintRaceTrack(g, segmentSize);



        paintPlayers(g, segmentSize);

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
}
