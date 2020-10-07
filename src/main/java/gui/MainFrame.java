package gui;

import graph.*;
import neat.Organism;
import neat.Species;
import networkdisplay.Display;
import testcases.XOR;
import testcases.flappybirds.FlappyBirds;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFrame extends JFrame {
    private JPanel content;
    private Testcase testcase;
    private List<Widget> widgets;

    private JLabel generationLabel;
    private GraphPanel fitnessGraphPanel;
    private GraphPanel fitnessDistributionPanel;
    private JPanel widgetPanel;
    private JPanel champDisplay;
    private GraphPanel speciesDistributionPanel;
    private JPanel organisms;
    private JScrollPane scrollPane;
    private JPanel organismList;

    private boolean hasAlreadyWorked;

    public MainFrame() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        testcase = new FlappyBirds();

        content = new JPanel(new BorderLayout());

        generationLabel = new JLabel("Generation 0");
        generationLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));

        initializeWidgets();

        widgets = getWidgets();
        widgetPanel = new JPanel(new GridLayout(4, 4));

        for (Widget widget : widgets) {
            widgetPanel.add(widget);
        }

        content.add(generationLabel, BorderLayout.NORTH);
        content.add(getControls(), BorderLayout.EAST);
        content.add(widgetPanel, BorderLayout.CENTER);

        setContentPane(content);
        new Thread(testcase.getAnimationPanel()).start();

        setSize(1000, 800);
        setVisible(true);
    }

    private void initializeWidgets() {
        fitnessGraphPanel = new GraphPanel();
        fitnessGraphPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        fitnessDistributionPanel = new GraphPanel();
        fitnessDistributionPanel.getAxis().setResolutionY(20.0);
        fitnessDistributionPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));


        fitnessGraphPanel.addGraph(new LineGraph(new Color(0, 0, 0, 150), 3));
        fitnessGraphPanel.addGraph(new LineGraph(new Color(255, 0, 0, 150), 3));

        fitnessDistributionPanel.addGraph(new BarGraph(new Color(0), 3));

        resetFitnessPanels();

        speciesDistributionPanel = new GraphPanel();
        speciesDistributionPanel.getAxis().setResolutionY(50);
        speciesDistributionPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        organisms = new JPanel(new GridBagLayout());
        scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(166 / 2);
        organismList = new JPanel(new BorderLayout());
        organismList.add(scrollPane);

        champDisplay = new JPanel(new GridLayout(1, 1));
        champDisplay.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    }

    private JPanel getControls() {
        JPanel controls = new JPanel(new GridLayout(4, 1));

        JButton doGenButton = new JButton("Do one generation");
        doGenButton.addActionListener(e -> {
            doGeneration();
        });

        JTextField numberOfGenerations = new JTextField("10");
        numberOfGenerations.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() != '\b' && (e.getKeyChar() < '0' || e.getKeyChar() > '9')) {
                    Toolkit.getDefaultToolkit().beep();
                    e.consume();
                }
            }
        });


        JButton doNGenButton = new JButton("Do n generations");
        doNGenButton.addActionListener(e -> {
            int generations = Integer.parseInt(numberOfGenerations.getText());
            for (int i = 0; i < generations; i++) {
                if (doGeneration()) {
                    break;
                }
            }
        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            hasAlreadyWorked = false;
            testcase.reset();
            generationLabel.setText("Generation 0");

            champDisplay.removeAll();

            scrollPane.setViewportView(null);

            resetFitnessPanels();

            speciesDistributionPanel.removeAllGraphs();
            speciesDistributionPanel.resetAxis();

            System.out.println("Initialized testcase");
            content.validate();
            content.repaint();
        });

        controls.add(resetButton);
        controls.add(doGenButton);
        controls.add(numberOfGenerations);
        controls.add(doNGenButton);
        return controls;
    }

    private void resetFitnessPanels() {
        fitnessGraphPanel.getGraphs().forEach(Graph::clear);
        fitnessGraphPanel.resetAxis();
        double[] y = {0.0, 0.0};
        fitnessGraphPanel.addCoordinates(0, y);

        fitnessDistributionPanel.getGraphs().forEach(Graph::clear);
        fitnessDistributionPanel.resetAxis();
    }

    private List<Widget> getWidgets() {
        List<Widget> widgets = new ArrayList<>(16);
        MouseListener mouseListener = new MouseAdapter() {
            int focusedIndex = -1;

            @Override
            public void mouseClicked(MouseEvent e) {
                Widget widget = getWidget(e.getComponent());
                if (!widget.isFocused()) {
                    focusedIndex = Arrays.asList(widgetPanel.getComponents()).indexOf(widget);
                    content.remove(widgetPanel);
                    content.add(widget, BorderLayout.CENTER);
                } else {
                    content.remove(widget);
                    widgetPanel.add(widget, focusedIndex);
                    content.add(widgetPanel, BorderLayout.CENTER);
                }
                widget.setFocused(!widget.isFocused());
                content.validate();
                content.repaint();
            }
        };

        widgets.add(new Widget("Fitness graph", fitnessGraphPanel, mouseListener));
        widgets.add(new Widget("Fitness distribution", fitnessDistributionPanel, mouseListener));
        widgets.add(new Widget("Champion structure", champDisplay, mouseListener));
        widgets.add(new Widget("Species distribution", speciesDistributionPanel, mouseListener));
        scrollPane.addMouseListener(mouseListener);
        widgets.add(new Widget("Organism list", organismList, mouseListener));
        widgets.add(new Widget("Animation", testcase.getAnimationPanel(), mouseListener));
        return widgets;
    }

    private Widget getWidget(Component component) {
        return component instanceof Widget ? (Widget) component : getWidget(component.getParent());
    }

    private boolean doGeneration() {
        if (testcase.getGeneration() == 0) {
            testcase.init();
        } else {
            testcase.doNGenerations(1);
        }
        generationLabel.setText("Generation " + testcase.getGeneration());

        champDisplay.removeAll();
        champDisplay.add(new Display(testcase.getChamp()));

        double[] y = new double[2];
        y[0] = testcase.getChamp().getFitness();
        y[1] = ((testcase.getChamp().getFitness() > fitnessGraphPanel.getGraph(0).getCoordinates().get(testcase.getGeneration() - 1).getY()) ? 0.5 : 0);
        fitnessGraphPanel.addCoordinates(testcase.getGeneration(), y);
        fitnessGraphPanel.getAxis().setResolutionX(Math.ceil(testcase.getGeneration() / 10.0));



        fitnessDistributionPanel.getGraphs().forEach(Graph::clear);
        fitnessDistributionPanel.resetAxis();

        int[] distribution = testcase.getFitnessDistribution();
        for (int i = 0; i < distribution.length; i++) {
            fitnessDistributionPanel.addCoordinate(0, i, distribution[i]);
        }

        List<Species> speciesList = new ArrayList<>(testcase.getSpecies());
        int i = 1;
        int value = testcase.getPopulationSize();
        for (Graph graph : speciesDistributionPanel.getGraphs()) {
            speciesList.remove(((DistributionGraph) graph).getSpecies());
            speciesDistributionPanel.addCoordinate(i - 1, testcase.getGeneration(), value);
            value -= ((DistributionGraph) graph).getSpecies().getMembers().size();
            i++;
        }
        for (Species species : speciesList) {
            speciesDistributionPanel.addGraph(new DistributionGraph(new Color((100 * i) % 256, (150 * i) % 256, (200 * i) % 256), 3, species));
            speciesDistributionPanel.addCoordinate(i - 1, testcase.getGeneration() - 1, 0);
            speciesDistributionPanel.addCoordinate(i - 1, testcase.getGeneration(), value);
            value -= species.getMembers().size();
            i++;
        }
        speciesDistributionPanel.getAxis().setResolutionX(Math.ceil(testcase.getGeneration() / 10.0));

        organisms.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridwidth = GridBagConstraints.REMAINDER;
//        gbc.weightx = 1;
//        gbc.weighty = 1;

        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        for (int j = 1, k = 0; j <= testcase.getSpecies().size(); j++) {
            Color c;
            while (!((DistributionGraph) speciesDistributionPanel.getGraph(k)).getSpecies().equals(testcase.getSpecies().get(j-1))) {
                k++;
            }

            for (Organism organism : testcase.getSpecies().get(j - 1).getMembers()) {
                Display display = new Display(organism);
                display.setPreferredSize(new Dimension(50, 166));
                display.setBackground(speciesDistributionPanel.getGraph(k).getColor());

                organisms.add(display, gbc);

            }
        }


        scrollPane.setViewportView(organisms);


        content.validate();
        content.repaint();
        if (!hasAlreadyWorked && testcase.hasWorkingOrganism()) {
            hasAlreadyWorked = true;
            JOptionPane.showMessageDialog(this, "Found working organism!", "Success", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
