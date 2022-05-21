package testcases.carrace;

import java.awt.*;
import java.awt.geom.AffineTransform;
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

    int sizex, sizey;

    RaceTrack(int sizex, int sizey, int len) {
        this.sizex = sizex;
        this.sizey = sizey;


        board = new TILE_TYPE[sizex][sizey];
        while (!this.generateRandomTrack2(len));
    }

    boolean generateRandomTrack2(int len) {
        Random r = new Random();
        Position cur = new Position(r.nextInt(sizex), r.nextInt(sizey));
        Position next = new Position(r.nextInt(sizex), r.nextInt(sizey));
        for (int i = 0; i < sizex; i++) {
            for (int j = 0; j < sizey; j++) {
                board[i][j] = TILE_TYPE.Empty;
            }
        }
        board[(int)cur.x][(int)cur.y] = TILE_TYPE.Start;
        int i;
        for (i = 0; i < len; i++) {
            int dir = r.nextInt(4);
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
                if ((int) corner.x < 0 || (int) corner.x >= sizex || (int) corner.y < 0 || (int) corner.y >= sizey || board[(int) corner.x][(int) corner.y] == TILE_TYPE.Empty) {
                    return -1;
                } else if (board[(int) corner.x][(int) corner.y] == TILE_TYPE.Goal)
                    touch_goal = true;
        }
        if (touch_goal)
            return 1;
        else
            return 0;
    }

    public boolean checkOffCourse(double x, double y) {
        return x < 0 || x >= sizex || y < 0 || y >= sizey || board[(int) x][(int) y] == TILE_TYPE.Empty;
    }


}
