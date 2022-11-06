package testcases.carrace;

import java.util.Random;

public class RaceTrack{

    // World coordinate System: A tile has the width and height of 1.0. So 0.5 is at the half of the first tile.
    public static class Position {
        double x, y;

        public Position(int x, int y) {
            this.x = x + 0.5;
            this.y = y + 0.5;
        }

        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }


    public enum TILE_TYPE {
        Empty,
        Start,
        Track,
        Goal
    }

    TILE_TYPE[][] board;
    int[][] board_steps;

    int sizex, sizey;

    RaceTrack(int sizex, int sizey, int len) {
        this.sizex = sizex;
        this.sizey = sizey;


        board = new TILE_TYPE[sizex][sizey];
        board_steps = new int[sizex][sizey];
        Random generator = new Random();
        while (!this.generateRandomTrack2(len, generator));
    }

    RaceTrack(int sizex, int sizey, int len, Random generator) {
        this.sizex = sizex;
        this.sizey = sizey;


        board = new TILE_TYPE[sizex][sizey];
        board_steps = new int[sizex][sizey];
        while (!this.generateRandomTrack2(len, generator));
    }

    boolean generateRandomTrack2(int len, Random generator) {
        Position cur = new Position(generator.nextInt(sizex), generator.nextInt(sizey));
        Position next = new Position(generator.nextInt(sizex), generator.nextInt(sizey));
        for (int i = 0; i < sizex; i++) {
            for (int j = 0; j < sizey; j++) {
                board[i][j] = TILE_TYPE.Empty;
                board_steps[i][j] = -1;
            }
        }
        board[(int)cur.x][(int)cur.y] = TILE_TYPE.Start;
        board_steps[(int)cur.x][(int)cur.y] = 0;
        int i;
        for (i = 0; i < len; i++) {
            int dir = generator.nextInt(4);
            int z;
            for (z = 0; z < 4; z++) {
                next.x = cur.x;
                next.y = cur.y;
                switch ((dir + z) % 4) {
                    case 0 -> next.y -= 1;
                    case 1 -> next.x += 1;
                    case 2 -> next.y += 1;
                    case 3 -> next.x -= 1;
                }
                int num_track = 0;
                if (next.x < 0 || next.x >= sizex || next.y < 0 || next.y >= sizey || board[(int)next.x][(int)next.y] != TILE_TYPE.Empty) {
                    continue;
                }

                if (next.x - 1 >= 0 && board[(int)next.x-1][(int)next.y] != TILE_TYPE.Empty) {
                    num_track += 1;
                }
                if (next.x + 1 < sizex && board[(int)next.x+1][(int)next.y] != TILE_TYPE.Empty) {
                    num_track += 1;
                }
                if (next.y - 1 >= 0  && board[(int)next.x][(int)next.y - 1] != TILE_TYPE.Empty) {
                    num_track += 1;
                }
                if (next.y + 1 < sizex && board[(int)next.x][(int)next.y+1] != TILE_TYPE.Empty) {
                    num_track += 1;
                }

                if (num_track <= 1) {
                    board[(int)next.x][(int)next.y] = i == len-1 ? TILE_TYPE.Goal : TILE_TYPE.Track;
                    board_steps[(int)next.x][(int)next.y] = i+1;
                    break;
                }
            }
            if (z == 4) {
                break;
            }
            cur.x = next.x;
            cur.y = next.y;

        }
        return i == len;
    }

    public Position getStart() {
        for (int i = 0; i < sizex; i++) {
            for (int j = 0; j < sizey; j++) {
                if (board[i][j] == TILE_TYPE.Start) {
                    return new Position(i, j);
                }
            }
        }
        return new Position(-1, -1);
    }

    public int checkCollision(Player player) {
        double dx;
        double dy;

        boolean touch_goal = false;

        for (int i = 0; i < 4; i++) {
            int factorWidth = 1 - 2 *  (i % 2);
            int factorLength = 1 - 2 * (int)(i / 2);
            dx = factorLength * Player.getLength() / 2 * Math.cos(player.getOrientation()) - factorWidth * Player.getWidth() / 2 * Math.sin(player.getOrientation());
            dy = factorLength * Player.getLength() / 2 * Math.sin(player.getOrientation()) + factorWidth * Player.getWidth() / 2 * Math.cos(player.getOrientation());
            Position corner = new Position(player.getX() + dx, player.getY() + dy);
            if (checkOffCourse(corner.x, corner.y)) {
                return -1;
            } else
                touch_goal = board[(int) corner.x][(int) corner.y] == TILE_TYPE.Goal;
        }
        if (touch_goal)
            return 1;
        else
            return 0;
    }

    public boolean checkOffCourse(double x, double y) {
        return x < 0 || x >= sizex || y < 0 || y >= sizey || board[(int) x][(int) y] == TILE_TYPE.Empty;
    }

    public double distanceOffCourse(double x, double y, double dx, double dy) {
        double testX = x;
        double testY = y;
        double distance = 0.0;
        double norm = Math.sqrt(dx*dx+dy*dy);
        dx /= norm;
        dy /= norm;
        double stepsize = 0.05;
        int numSteps = 0;

        while (!checkOffCourse(testX, testY)) {
            testX += stepsize * dx;
            testY += stepsize * dy;
            numSteps++;
        }
        distance = numSteps * stepsize;
        return distance;
    }

    public Position getPos(int steps) {
        Position ret;
        for (int i = 0; i < sizex; i++) {
            for (int j = 0;  j < sizey; j++) {
                if (board_steps[i][j] == steps) {
                    ret = new Position(i, j);
                    return ret;
                }
            }
        }
        ret = new Position(-1, -1);
        return ret;
    }

    public double checkScore(double x, double y) {
        if (x < 0 || x >= sizex || y < 0 || y >= sizey) {
            return 0;
        }

        return board_steps[(int) x][(int)y];
//        for (prevX = 0; prevX < sizex; prevX++) {
//            for (prevY = 0; prevY < sizey; prevY++) {
//                if (board_steps[prevX][prevY] == board_steps[(int) x][(int) y] - 1)
//                    break;
//            }
//            if (prevY != sizey)
//                break;
//        }
//        if (prevX == sizex) {
//            prevX = (int)x;
//            prevY = (int)y;
//        }
//        return board_steps[(int) x][(int) y] + Math.sqrt(Math.pow(x - (double)(prevX) - 0.5, 2) + Math.pow(y - (double)(prevY) - 0.5, 2)) - 0.5;
    }


}
