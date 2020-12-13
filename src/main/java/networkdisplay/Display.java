package networkdisplay;

import neat.*;
import neat.Node;
import util.SortedList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Display extends JPanel {
    public double blopSize = 0.25;
    public double lineWidth = 0.02;

    private Organism organism;
    private List<Layer> layers;
    private int maxDepth;
    private List<Blop> blops;
    private Display self;

    public Display(Organism organism) {
        layers = new ArrayList<>();
        maxDepth = 0;
        blops = new LinkedList<>();
        this.organism = organism;
        this.self = this;

        for (Node node : organism.getOutputNodes()) {
            layerNodes(0, node);
        }

        addBlopsToLayers();
        blops.clear();

        double columnWidth = 1.0 / layers.size();
        for (int i = 0; i < layers.size(); i++) {
            List<Blop> layerNodes = layers.get(layers.size() - 1 - i).nodes;
            double rowHeight = 1.0 / layerNodes.size();
            for (int j = 0; j < layerNodes.size(); j++) {
                blops.add(new Blop(i * columnWidth + columnWidth * 0.5, j * rowHeight + rowHeight * 0.5, layerNodes.get(j).getInnovationNumber(), layerNodes.get(j).getNodePurpose()));
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setStroke(new BasicStroke((int) (lineWidth * (getWidth() < getHeight() ? getWidth() : getHeight()))));
        double maxAbsoluteWeight = 0.0;
        for (Connection connection : organism.getConnections()) {
            if (Math.abs(connection.getWeight()) > maxAbsoluteWeight) {
                maxAbsoluteWeight = Math.abs(connection.getWeight());
            }
        }
        for (Connection connection : organism.getConnections()) {
            if (connection.getWeight() > 0) {
                g.setColor(new Color(0, 0, 255, (int) (255 * Math.abs(connection.getWeight() / maxAbsoluteWeight))));
            } else {
                g.setColor(new Color(255, 0, 0, (int) (255 * Math.abs(connection.getWeight() / maxAbsoluteWeight))));
            }
            Blop inBlop = getBlop(connection.getIn().getInnovationNumber());
            Blop outBlop = getBlop(connection.getOut().getInnovationNumber());
            if (outBlop == null) {
                System.out.println("Oh no");
            }
            int pixelX1 = (int) (inBlop.getX() * getWidth());
            int pixelY1 = (int) (inBlop.getY() * getHeight());
            int pixelX2 = (int) (outBlop.getX() * getWidth());
            int pixelY2 = (int) (outBlop.getY() * getHeight());
            g.drawLine(pixelX1, pixelY1, pixelX2, pixelY2);
        }

        g.setColor(Color.DARK_GRAY);
        for (Blop blop : blops) {
            int pixelBlopSize = (int) (blopSize * (1.0 / Math.sqrt(blops.size())) * (getWidth() < getHeight() ? getWidth() : getHeight()));
            int pixelX = (int) (blop.getX() * getWidth()) - pixelBlopSize / 2;
            int pixelY = (int) (blop.getY() * getHeight()) - pixelBlopSize / 2;
            g.fillOval(pixelX, pixelY, pixelBlopSize, pixelBlopSize);
            g.drawString(blop.getNodePurpose().toString().substring(0, 1) + blop.getInnovationNumber(), pixelX, pixelY);
        }
    }

    private void layerNodes(int depth, Node node) {
        Blop blop = getBlop(node.getInnovationNumber());
        if (blop != null && blop.getDepth() < depth) {
            blop.setDepth(depth);
        } else if (blop == null) {
            blops.add(new Blop(-1, -1, node.getInnovationNumber(), node.getNodePurpose()));
            blops.get(blops.size() - 1).setDepth(depth);
        }
        if (depth > maxDepth) {
            maxDepth = depth;
        }
        for (Connection connection : node.getIn()) {
            layerNodes(depth + 1, connection.getIn());
        }
    }

    private void addBlopsToLayers() {
        layers = new ArrayList<>(maxDepth + 1);
        for (int i = 0; i <= maxDepth; i++) {
            layers.add(new Layer());
        }

        for (Blop blop : blops) {
            if (blop.getNodePurpose().equals(NodePurpose.Input) || blop.getNodePurpose().equals(NodePurpose.Bias)) {
                layers.get(layers.size() - 1).nodes.add(blop);
            } else {
                layers.get(blop.getDepth()).nodes.add(blop);
            }
        }
    }

    public Blop getBlop(int innovationNumber) {
        for (Blop blop : blops) {
            if (blop.getInnovationNumber() == innovationNumber) {
                return blop;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        NeatConfiguration configuration = new NeatConfiguration(2, 2);
        Organism organism = new Organism(configuration);

        SortedList<InputNode> inputNodes = organism.getInputNodes();
        List<Node> hiddenNodes = new LinkedList<>();
        SortedList<Node> outputNodes = organism.getOutputNodes();
        List<Connection> connections = new LinkedList<>();

        int i = configuration.getInputCount() + configuration.getOutputCount() + 1;
        hiddenNodes.add(NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Hidden, i++)); // 0
        hiddenNodes.add(NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Hidden, i++)); // 1
        hiddenNodes.add(NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Hidden, i++)); // 2

        connections.add(new Connection(inputNodes.get(0), hiddenNodes.get(0), 1.0));
        connections.add(new Connection(inputNodes.get(0), hiddenNodes.get(1), -1.0));
        connections.add(new Connection(inputNodes.get(1), hiddenNodes.get(2), 5.0));
        connections.add(new Connection(hiddenNodes.get(0), outputNodes.get(0), 3.0));
        connections.add(new Connection(hiddenNodes.get(1), outputNodes.get(0), 2.0));
        connections.add(new Connection(hiddenNodes.get(1), outputNodes.get(1), -0.5));
        connections.add(new Connection(hiddenNodes.get(2), outputNodes.get(1), -2.0));


        hiddenNodes.get(0).addInput(connections.get(0));
        hiddenNodes.get(1).addInput(connections.get(1));
        hiddenNodes.get(2).addInput(connections.get(2));
        outputNodes.get(0).addInput(connections.get(3));
        outputNodes.get(0).addInput(connections.get(4));
        outputNodes.get(1).addInput(connections.get(5));
        outputNodes.get(1).addInput(connections.get(6));

        organism.getHiddenNodes().addAll(hiddenNodes);
        organism.getConnections().addAll(connections);

        JFrame frame = new JFrame();
        frame.setContentPane(new Display(organism));
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
