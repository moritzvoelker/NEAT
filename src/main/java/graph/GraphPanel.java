package graph;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

public class GraphPanel extends JPanel {
    private Axis axis;
    private List<Graph> graphs;
    private double normX, normY;

    public GraphPanel() {
        this.graphs = new LinkedList<>();
        axis = new Axis(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, new Vector(0, 0));
        normX = normY = 1.0;
    }

    public GraphPanel(List<Graph> graphs) {
        this.graphs = graphs;
        axis = new Axis(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, new Vector(0, 0));
        normX = normY = 1.0;
    }

    public GraphPanel(Graph graph) {
        this.graphs = new LinkedList<>();
        this.graphs.add(graph);
        axis = new Axis(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, new Vector(0, 0));
        normX = normY = 1.0;
    }

    public GraphPanel(List<Graph> graphs, double normX, double normY) {
        this.graphs = graphs;
        axis = new Axis(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, new Vector(0, 0));
        this.normX = normX;
        this.normY = normY;
    }

    public GraphPanel(List<Graph> graphs, double normX, double normY, double resolutionX, double resolutionY) {
        this.graphs = graphs;
        axis = new Axis(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, resolutionX, resolutionY, new Vector(0, 0));
        this.normX = normX;
        this.normY = normY;
    }

    public GraphPanel(Graph graph, double normX, double normY, double resolutionX, double resolutionY) {
        this.graphs = new LinkedList<>();
        this.graphs.add(graph);
        axis = new Axis(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, resolutionX, resolutionY, new Vector(0, 0));
        this.normX = normX;
        this.normY = normY;
    }


    @Override
    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        boolean coordinates = false;
        for (Graph graph : graphs) {
            graph.paintComponent(g, axis, getWidth(), getHeight());
            if (graph.content.size() > 0) {
                coordinates = true;
            }
        }

        if (!coordinates) {
            new Axis(0.0, 1.0, 0.0, 1.0, 1.0, 1.0, new Vector(0.0, 0.0)).paintComponent(g, getWidth(), getHeight(), normX, normY);
        } else {
            axis.paintComponent(g, getWidth(), getHeight(), normX, normY);
        }

    }


    public synchronized void addCoordinates(double x, double[] y) throws IllegalArgumentException {
        if (y.length != graphs.size()) {
            throw new IllegalArgumentException("Number of Coordinates (" + y.length + ") is not equal to number of Graphs (" + graphs.size() + ")!");
        }

        if (axis.getMaxX() < x) {
            axis.setMaxX(x);
        }
        if (axis.getMinX() > x) {
            axis.setMinX(x);
        }
        for (int i = 0; i < graphs.size(); i++) {
            graphs.get(i).addCoordinate(new Vector(x, y[i]));
            if (axis.getMaxY() < y[i]) {
                axis.setMaxY(y[i]);
            }
            if (axis.getMinY() > y[i]) {
                axis.setMinY(y[i]);
            }
        }
    }

    public synchronized void addCoordinate(int index, double x, double y) {
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
            axis.setMinY(y);
        }

        graphs.get(index).addCoordinate(new Vector(x, y));
    }

    public synchronized void addGraphs(List<Graph> graphs) {
        this.graphs.addAll(graphs);
    }

    public synchronized void addGraph(Graph graph) {
        this.graphs.add(graph);
    }

    public synchronized void removeAllGraphs() {
        graphs.clear();
    }

    public synchronized void removeGraph(int index) throws IndexOutOfBoundsException {
        graphs.remove(index);
    }

    // nicht sicher ob funktioniert. Braucht eventuell eine Implementierung von equals
    public synchronized void removeGraph(Graph graph) throws NullPointerException {
        graphs.remove(graph);
    }

    public void resetAxis() {
        axis.setMaxX(Double.NEGATIVE_INFINITY);
        axis.setMinX(Double.POSITIVE_INFINITY);
        axis.setMaxY(Double.NEGATIVE_INFINITY);
        axis.setMinY(Double.POSITIVE_INFINITY);
    }

    // Either just select it and then configure via GraphPanel or return the Graph
    public Graph getGraph(int index) {
        return graphs.get(index);
    }

    public List<Graph> getGraphs() {
        return graphs;
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
        /*
        List<Graph> graphs = new ArrayList<>(3);
        graphs.add(new LineGraph(new Color(0), 3));
        graphs.add(new LineGraph(new Color(0), 3));
        graphs.add(new LineGraph(new Color(0), 3));
        GraphPanel graphPanel = new GraphPanel(graphs);
        graphPanel.getAxis().setResolutionY(1.0);
        graphPanel.getAxis().setResolutionX(0.5);
        graphPanel.getAxis().setCenter(new Vector(0, 0));
        double step = 0.5;
        for (int i = -3; i <= 3; i++) {
            double[] y = {Math.exp(i*step), i*step, 1.0};
            graphPanel.addCoordinates(i*step, y);
        }

        graphPanel.setNorm(1.0,1.0);
        */
        GraphPanel graphPanel = new GraphPanel(new BarGraph(new Color(54, 255, 34, 230), 3));
        graphPanel.getAxis().setResolutionY(1.0);
        graphPanel.getAxis().setResolutionX(0.5);
        graphPanel.getAxis().setCenter(new Vector(0, 0));
        double step = 0.5;
//        for (int i = -3; i <= 3; i++) {
//            graphPanel.addCoordinate(0, step * i, i);
//        }

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
