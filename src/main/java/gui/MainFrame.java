package gui;

import graph.*;
import neat.Species;
import networkdisplay.Display;
import testcases.XOR;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFrame extends JFrame {
    private MainFrame self;
    private JPanel content;
    private Testcase testcase;
    private List<Widget> widgets;


    private JLabel generationLabel;
    private GraphPanel fitnessGraphPanel;
    private GraphPanel fitnessDistributionPanel;
    private JPanel widgetPanel;
    private JPanel champDisplay;
    private GraphPanel speciesDistributionPanel;

    private boolean hasAlreadyWorked;

    public MainFrame() {
        self = this;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                self.dispose();
                System.exit(0);
            }
        });

        testcase = new XOR();

        content = new JPanel(new BorderLayout());

        fitnessGraphPanel = new GraphPanel();
        fitnessGraphPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        fitnessDistributionPanel = new GraphPanel();
        fitnessDistributionPanel.getAxis().setResolutionY(20.0);
        fitnessDistributionPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));


        speciesDistributionPanel = new GraphPanel();
        speciesDistributionPanel.getAxis().setResolutionY(50);
        speciesDistributionPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));


        generationLabel = new JLabel("Generation " + testcase.getGeneration());
        generationLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));

        JPanel controls = new JPanel(new GridLayout(4, 1));

        champDisplay = new JPanel(new GridLayout(1, 1));
        champDisplay.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JButton doGenButton = new JButton("Do one generation");
        doGenButton.addActionListener(e -> {
            doGeneration();
        });
        doGenButton.setEnabled(false);

        JTextField numberOfGenerations = new JTextField("1");
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
            for (int i = 0; i < Integer.parseInt(numberOfGenerations.getText()); i++) {
                if (doGeneration()) {
                    break;
                }
            }
        });
        doNGenButton.setEnabled(false);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            hasAlreadyWorked = false;
            testcase.init();
            doGenButton.setEnabled(true);
            doNGenButton.setEnabled(true);
            generationLabel.setText("Generation " + testcase.getGeneration());

            champDisplay.removeAll();
            champDisplay.add(new Display(testcase.getChamp()));

            fitnessGraphPanel.removeAllGraphs();
            fitnessGraphPanel.addGraph(new LineGraph(new Color(0, 0, 0, 150), 3));
            fitnessGraphPanel.addGraph(new LineGraph(new Color(255, 0, 0, 150), 3));
            fitnessGraphPanel.resetAxis();

            double[] y = {0.0, 0.0};
            fitnessGraphPanel.addCoordinates(0, y);
            y[0] = testcase.getChamp().getFitness();
            y[1] = ((testcase.getChamp().getFitness() > 0) ? 0.5 : 0);
            fitnessGraphPanel.addCoordinates(1, y);



            fitnessDistributionPanel.removeAllGraphs();
            fitnessDistributionPanel.addGraph(new BarGraph(new Color(0), 3));
            fitnessDistributionPanel.resetAxis();
            int[] distribution = testcase.getFitnessDistribution();
            System.out.println(Arrays.toString(distribution));
            for (int i = 0; i < distribution.length; i++) {
                fitnessDistributionPanel.addCoordinate(0, i, distribution[i]);
            }

            speciesDistributionPanel.removeAllGraphs();
            speciesDistributionPanel.resetAxis();
            int i = 1;
            int value = testcase.getPopulationSize();
            for (Species species : testcase.getSpecies()) {
                speciesDistributionPanel.addGraph(new DistributionGraph(new Color((100 * i) % 256, (150 * i) % 256, (200 * i) % 256), 3, species));
                speciesDistributionPanel.addCoordinate(i - 1, 0, 0);
                speciesDistributionPanel.addCoordinate(i - 1, 1, value);

                value -= species.getMembers().size();
                i++;
            }


            System.out.println("Initialized testcase");
            content.validate();
            content.repaint();
        });

        widgets = getWidgets();
        widgetPanel = new JPanel(new GridLayout(4, 4));

        for (Widget widget : widgets) {
            widgetPanel.add(widget);
        }


        controls.add(resetButton);
        controls.add(doGenButton);
        controls.add(numberOfGenerations);
        controls.add(doNGenButton);


        content.add(generationLabel, BorderLayout.NORTH);
        content.add(controls, BorderLayout.EAST);
        content.add(widgetPanel, BorderLayout.CENTER);

        setContentPane(content);

        setSize(1000, 800);
        setVisible(true);
    }

    private List<Widget> getWidgets() {
        List<Widget> widgets = new ArrayList<>(16);
        MouseListener mouseListener = new MouseAdapter() {
            int focusedIndex = -1;

            @Override
            public void mouseClicked(MouseEvent e) {
                Widget widget = (Widget) e.getComponent();
                if (!widget.isFocused()) {
                    focusedIndex = Arrays.asList(widgetPanel.getComponents()).indexOf(e.getComponent());
                    content.remove(widgetPanel);
                    content.add(e.getComponent(), BorderLayout.CENTER);
                } else {
                    content.remove(e.getComponent());
                    widgetPanel.add(e.getComponent(), focusedIndex);
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
        return widgets;
    }

    private boolean doGeneration() {
        testcase.doNGenerations(1);
        generationLabel.setText("Generation " + testcase.getGeneration());

        champDisplay.removeAll();
        champDisplay.add(new Display(testcase.getChamp()));

        double[] y = new double[2];
        y[0] = testcase.getChamp().getFitness();
        y[1] = ((testcase.getChamp().getFitness() > fitnessGraphPanel.getGraph(0).getCoordinates().get(testcase.getGeneration() - 1).getY()) ? 0.5 : 0);
        fitnessGraphPanel.addCoordinates(testcase.getGeneration(), y);
        fitnessGraphPanel.getAxis().setResolutionX(Math.ceil(testcase.getGeneration() / 10.0));

        fitnessDistributionPanel.removeAllGraphs();
        fitnessDistributionPanel.addGraph(new BarGraph(new Color(0), 3));
        fitnessDistributionPanel.resetAxis();
        int[] distribution = testcase.getFitnessDistribution();
        System.out.println(Arrays.toString(distribution));
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
            if (value < 0)
                System.out.println("value = " + value);
            i++;
        }
        for (Species species : speciesList) {
            speciesDistributionPanel.addGraph(new DistributionGraph(new Color((100 * i) % 256, (150 * i) % 256, (200 * i) % 256), 3, species));
            speciesDistributionPanel.addCoordinate(i - 1, testcase.getGeneration() - 1, 0);
            speciesDistributionPanel.addCoordinate(i - 1, testcase.getGeneration(), value);


            value -= species.getMembers().size();
            if (value < 0)
                System.out.println("value = " + value);
            i++;
        }
        speciesDistributionPanel.getAxis().setResolutionX(Math.ceil(testcase.getGeneration() / 10.0));

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
