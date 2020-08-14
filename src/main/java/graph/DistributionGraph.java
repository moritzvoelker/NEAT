package graph;

import neat.Species;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;


// TODO: 15.08.2020 Breaks after an amount of generations or species. 
public class DistributionGraph extends Graph {
    Species species;
    public DistributionGraph(Color color, int linewidth, Species sp) {
        super(color, linewidth);
        species = sp;
    }

    public Species getSpecies() {
        return species;
    }

    @Override
    public void paintComponent(Graphics g, Axis axis, int width, int height) {
        ((Graphics2D) g).setStroke(new BasicStroke(linewidth));
        g.setColor(color);

        int[] x = Arrays.copyOfRange(content.stream().mapToInt(vector -> axis.XValue2XPixel(vector.getX(), width)).toArray(), 0, content.size() + 1);
        x[x.length - 1] = width;
        int[] y = Arrays.copyOfRange(content.stream().mapToInt(vector -> axis.YValue2YPixel(vector.getY(), height)).toArray(), 0, content.size() + 1);
        y[y.length - 1] = axis.YValue2YPixel(axis.getCenter().getY(), height);
        g.fillPolygon(x, y, content.size() + 1);
    }
}
