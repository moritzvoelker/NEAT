package networkdisplay;

import neat.*;
import neat.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Display extends JFrame {
    public static final int BLOP_SIZE = 15;
    public static final int BLOP_PADDING = 100;
    public static final int LINE_WIDTH = 3;

    JPanel content;
    Organism organism;
    List<Layer> layers;
    int maxDepth;
    int maxHeight;
    List<Blop> blops;
    Display self;

    public Display(Organism organism) {
        content = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                ((Graphics2D) g).setStroke(new BasicStroke(LINE_WIDTH));
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
                    g.drawLine(inBlop.getX() + BLOP_SIZE / 2, inBlop.getY() + BLOP_SIZE / 2, outBlop.getX() + BLOP_SIZE / 2, outBlop.getY() + BLOP_SIZE / 2);
                }

                g.setColor(Color.DARK_GRAY);
                for (Blop blop : blops) {
                    g.fillOval(blop.getX(), blop.getY(), BLOP_SIZE, BLOP_SIZE);
                    g.drawString(blop.getNodePurpose().toString().substring(0, 1) + blop.getInnovationNumber(), blop.getX(), blop.getY());
                }
            }
        };
        layers = new ArrayList<>();
        maxDepth = 0;
        maxHeight = 0;
        blops = new LinkedList<>();
        this.setContentPane(content);
        this.organism = organism;
        this.self = this;

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                self.dispose();
            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                self.dispose();
            }
        });

        for (Node node : organism.getOutputNodes()) {
            layerNodes(0, node);
        }

        addBlopsToLayers();
        blops.clear();

        for (Layer layer : layers) {
            if (layer.nodes.size() > maxHeight) {
                maxHeight = layer.nodes.size();
            }
        }

        int height = maxHeight * (BLOP_SIZE + BLOP_PADDING);

        for (int i = 0; i < layers.size(); i++) {
            List<Blop> layerNodes = layers.get(layers.size() - 1 - i).nodes;
            int blopHeightSum = BLOP_SIZE * (layerNodes.size());
            int spaceHeightSum = (height - blopHeightSum) / (layerNodes.size() + 1);
            for (int j = 0; j < layerNodes.size(); j++) {
                blops.add(new Blop(i * (BLOP_SIZE + BLOP_PADDING) + BLOP_PADDING, (blopHeightSum / layerNodes.size() * j) + (spaceHeightSum * (j + 1)), layerNodes.get(j).getInnovationNumber(), layerNodes.get(j).getNodePurpose()));
            }
        }

        setSize((BLOP_SIZE + BLOP_PADDING) * layers.size() + BLOP_PADDING, height);
        setVisible(true);
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

        List<InputNode> inputNodes = new LinkedList<>();
        List<Node> hiddenNodes = new LinkedList<>();
        List<Node> outputNodes = new LinkedList<>();
        List<Connection> connections = new LinkedList<>();

        int i = 0;
        inputNodes.add((InputNode) NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Input, i++)); // 0
        inputNodes.add((InputNode) NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Input, i++)); // 1
        hiddenNodes.add(NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Hidden, i++)); // 0
        hiddenNodes.add(NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Hidden, i++)); // 1
        hiddenNodes.add(NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Hidden, i++)); // 2
        outputNodes.add(NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Output, i++)); // 0
        outputNodes.add(NodeFactory.create(configuration.getCreateStrategy(), NodePurpose.Output, i++)); // 1

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

        organism.getInputNodes().addAll(inputNodes);
        organism.getHiddenNodes().addAll(hiddenNodes);
        organism.getOutputNodes().addAll(outputNodes);
        organism.getConnections().addAll(connections);

        new Display(organism);
    }
}
