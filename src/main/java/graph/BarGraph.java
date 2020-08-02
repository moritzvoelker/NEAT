package graph;

import java.awt.*;

public class BarGraph extends Graph {

    public BarGraph(Color color, int linewidth) {
        super(color, linewidth);
    }

    @Override
    public void paintComponent(Graphics g, Axis axis, int width, int height) {
        ((Graphics2D) g).setStroke(new BasicStroke(linewidth));
        g.setColor(color);

        for (int i = 0; i < content.size() - 1; i++) {
            Vector c1 = axis.value2Pixel(content.get(i), width, height);
            g.drawLine((int)c1.getX(), axis.YValue2YPixel(axis.getCenter().getY(), height), (int)c1.getX(), (int)c1.getY());
        }
    }
}
