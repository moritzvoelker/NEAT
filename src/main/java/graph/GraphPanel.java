package graph;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class GraphPanel extends JPanel {
    private int lineWidth = 5;
    private Axis axis;
    private LinkedList<Graph> graphs;
    private double normX, normY;

    public GraphPanel(int count, Color color, int linewidth) {
        graphs = new LinkedList<>();
        addGraphs(count, color, linewidth);
        axis = new Axis(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, new Vektor(0, 0));
        lineWidth = linewidth;
        normX = normY = 1.0;
    }

    public GraphPanel(int count, Color color, int linewidth, double normX, double normY) {
        graphs = new LinkedList<>();
        addGraphs(count, color, linewidth);
        axis = new Axis(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, new Vektor(0, 0));
        lineWidth = linewidth;
        this.normX = normX;
        this.normY = normY;
    }

    public GraphPanel(int count, Color color, int linewidth, double normX, double normY, double resolutionX, double resolutionY) {
        graphs = new LinkedList<>();
        addGraphs(count, color, linewidth);
        axis = new Axis(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, resolutionX, resolutionY, new Vektor(0, 0));
        lineWidth = linewidth;
        this.normX = normX;
        this.normY = normY;
    }



    @Override
    public void paintComponent(Graphics g) {
        for (Graph graph : graphs) {
            graph.paintComponent(g, axis, getWidth(), getHeight());
        }
        axis.paintComponent(g, getWidth(), getHeight(), normX, normY);


    }



    public void addCoordinates(double x, double[] y) throws IllegalArgumentException {
        if (axis.getMaxX() < x) {
            axis.setMaxX(x);
        }
        if (axis.getMinX() > x) {
            axis.setMinX(x);
        }

        if (y.length != graphs.size()) {
            throw new IllegalArgumentException("Number of Coordinates (" + y.length + ") is not equal to number of Graphs (" + graphs.size() + ")!");
        }
        for (int i = 0; i < graphs.size(); i++) {
            graphs.get(i).addCoordinate(new Vektor(x, y[i]));
            if (axis.getMaxY() < y[i]) {
                axis.setMaxY(y[i]);
            }
            if (axis.getMinY() > y[i]) {
                axis.setMinY(y[i]);
            }
        }
    }

    public void addCoordinate(int index, double x, double y) {
        if (axis.getMaxX() < x) {
            axis.setMaxX(x);
        }
        if (axis.getMinX() > x) {
            axis.setMinX(x);
        }
        if (axis.getMaxY() < y) {
            axis.setMaxY(y);
        }
        if (axis.getMinY() > y) {
            axis.setMinX(y);
        }

        graphs.get(index).addCoordinate(new Vektor(x, y));
    }

    public void addGraphs(int count) {
        Color color = new Color(0);
        addGraphs(count, color, lineWidth);
    }

    public void addGraphs(int count, Color color, int linewidth) {
        for (int i = 0; i < count; i++) {
            graphs.add(new Graph(color, linewidth));
        }
    }

    // Either just select it and then configure via GraphPanel or return the Graph
    public Graph getGraph(int index) {
        return graphs.get(index);
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }


    public void setNorm(double normX, double normY) {
        this.normX = normX;
        this.normY = normY;
    }

    public Axis getAxis() {
        return axis;
    }

    public void setAxis(Axis axis) {
        this.axis = axis;
    }

    public static void main(String[] args) {
        GraphPanel graphPanel = new GraphPanel(3, new Color(0), 3);
        graphPanel.getAxis().setResolutionY(1.0);
        graphPanel.getAxis().setCenter(new Vektor(0, 0));
        double step = 0.5;
        for (int i = -3; i <= 3; i++) {
            double[] y = {Math.exp(i*step), i*step, 1.0};
            graphPanel.addCoordinates(i*step, y);
        }

        graphPanel.setNorm(1.0,1.0);


        graphPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JFrame frame = new JFrame();
        frame.setContentPane(graphPanel);
        graphPanel.repaint();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.exit(0);
            }
        });
        frame.setSize(400, 400);
        frame.setVisible(true);
    }
}
