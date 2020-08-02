package graph;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class LineGraph extends Graph {

    public LineGraph(Color color, int linewidth) {
        super(color, linewidth);
    }

    public LineGraph(List<Vector> content, Color color, int linewidth) {
        super(color, linewidth);
        this.content = new LinkedList<>();
        addCoordinates(content);
        this.color = color;
        this.linewidth = linewidth;
    }

    public LineGraph(Vector c, Color color, int linewidth) {
        super(color, linewidth);
        content = new LinkedList<>();
        content.add(c);
        this.color = color;
        this.linewidth = linewidth;
    }

    @Override
    public void paintComponent(Graphics g, Axis axis, int width, int height) {
        ((Graphics2D) g).setStroke(new BasicStroke(linewidth));
        g.setColor(color);

        for (int i = 0; i < content.size() - 1; i++) {
            Vector c1 = axis.value2Pixel(content.get(i), width, height);
            Vector c2 = axis.value2Pixel(content.get(i+1), width, height);
            g.drawLine((int)c1.getX(), (int)c1.getY(), (int)c2.getX(), (int)c2.getY());
        }
    }
}
