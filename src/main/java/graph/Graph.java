package graph;

import java.awt.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public abstract class Graph {
    LinkedList<Vector> content;
    Color color;
    int linewidth;


    public Graph(Color color, int linewidth) {
        content = new LinkedList<>();
        this.color = color;
        this.linewidth = linewidth;
    }

    public void addCoordinates(List<Vector> coordinates) {
        content.addAll(coordinates);
        content.sort(Comparator.comparing(Vector::getX));

        int i, j;


        for (i = 0, j = 0; i < content.size() && j < coordinates.size();) {
            double diff = content.get(i).getX() - coordinates.get(j).getX();
            if (diff == 0) {
                j++;
            } else if (diff < 0) {
                i++;
            } else {
                content.add(i, coordinates.get(j++));
            }
        }
        if (j < coordinates.size()) {
            content.add(coordinates.get(j++));
        }

        for (; j < coordinates.size(); j++) {
            double diff = content.get(i).getX() - coordinates.get(j).getX();
            if (diff == 0) {
                j++;
            } else if(diff > 0) {
                content.add(coordinates.get(j++));
                i++;
            }
        }
    }

    public void addCoordinate(Vector c) {
        for (int i = 0; i < content.size(); i++) {
            double diff = content.get(i).getX() - c.getX();
            if (diff == 0) {
                return;
            } else if (diff < 0) {
                continue;
            } else {
                content.add(i, c);
                return;
            }
        }
        content.add(c);
    }

    public List<Vector> getCoordinates() {
        return content;
    }


    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public abstract void paintComponent(Graphics g, Axis axis, int width, int height);
}
