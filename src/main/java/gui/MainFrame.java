package gui;

import graph.*;
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

    private JPanel fitnessGraphPanel;
    private GraphPanel fitnessDistributionPanel;
    private JPanel widgetPanel;

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

        fitnessGraphPanel = new JPanel(new GridLayout(1, 1));
        fitnessGraphPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        fitnessDistributionPanel = new GraphPanel();
        fitnessDistributionPanel.getAxis().setResolutionY(20.0);
        fitnessDistributionPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JLabel generationLabel = new JLabel("Generation " + testcase.getGeneration());
        generationLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));

        JPanel controls = new JPanel(new GridLayout(5, 1));

        JPanel champDisplay = new JPanel(new GridLayout(1, 1));
        champDisplay.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JButton doGenButton = new JButton("Do one generation");
        doGenButton.addActionListener(e -> {
            testcase.doNGenerations(1);
            generationLabel.setText("Generation " + testcase.getGeneration());

            champDisplay.removeAll();
            champDisplay.add(new Display(testcase.getChamp()));

            double[] y = new double[1];
            y[0] = testcase.getChamp().getFitness();
            ((GraphPanel) (fitnessGraphPanel.getComponent(0))).addCoordinates(testcase.getGeneration(), y);
//            ((GraphPanel)(graphPanel.getComponent(0))).getAxis().setResolutionX(testcase.getGeneration() / 10.0);

            fitnessDistributionPanel.removeAllGraphs();
            fitnessDistributionPanel.addGraph(new BarGraph(new Color(0), 3));
            fitnessDistributionPanel.resetAxis();
            int[] distribution = testcase.getFitnessDistribution();
            System.out.println(Arrays.toString(distribution));
            for (int i = 0; i < distribution.length; i++) {
                fitnessDistributionPanel.addCoordinate(0, i, distribution[i]);
            }

            content.validate();
            content.repaint();
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
            testcase.doNGenerations(Integer.parseInt(numberOfGenerations.getText()));
            generationLabel.setText("Generation " + testcase.getGeneration());

            champDisplay.removeAll();
            champDisplay.add(new Display(testcase.getChamp()));

            double[] y = new double[1];
            y[0] = testcase.getChamp().getFitness();
            ((GraphPanel) (fitnessGraphPanel.getComponent(0))).addCoordinates(testcase.getGeneration(), y);
//            ((GraphPanel)(graphPanel.getComponent(0))).getAxis().setResolutionX(testcase.getGeneration() / 10.0);

            /*fitnessDistributionPanel.removeAll();
            GraphPanel graphPanel = new GraphPanel(new BarGraph(new Color(0), 3));
            fitnessDistributionPanel.add(graphPanel);
            int[] distribution = testcase.getFitnessDistribution();
            System.out.println(Arrays.toString(distribution));
            for (int i = 0; i < distribution.length; i++) {
                graphPanel.addCoordinate(0, i, distribution[i]);
            }*/

            fitnessDistributionPanel.removeAllGraphs();
            fitnessDistributionPanel.addGraph(new BarGraph(new Color(0), 3));
            fitnessDistributionPanel.resetAxis();
            int[] distribution = testcase.getFitnessDistribution();
            System.out.println(Arrays.toString(distribution));
            for (int i = 0; i < distribution.length; i++) {
                fitnessDistributionPanel.addCoordinate(0, i, distribution[i]);
            }

            content.validate();
            content.repaint();
        });
        doNGenButton.setEnabled(false);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            testcase.init();
            doGenButton.setEnabled(true);
            doNGenButton.setEnabled(true);
            generationLabel.setText("Generation " + testcase.getGeneration());

            champDisplay.removeAll();
            champDisplay.add(new Display(testcase.getChamp()));

            fitnessGraphPanel.removeAll();
            fitnessGraphPanel.add(new GraphPanel(new LineGraph(new Color(0, 0, 0, 150), 3), 1.0, 16.0, 1.0, 0.1));
            double[] y = {0.0};
            ((GraphPanel) (fitnessGraphPanel.getComponent(0))).addCoordinates(0, y);
            y[0] = testcase.getChamp().getFitness();
            ((GraphPanel) (fitnessGraphPanel.getComponent(0))).addCoordinates(1, y);

            /*fitnessDistributionPanel.removeAll();
            GraphPanel graphPanel = new GraphPanel(new BarGraph(new Color(0), 3));
            fitnessDistributionPanel.add(graphPanel);
            int[] distribution = testcase.getFitnessDistribution();
            System.out.println(Arrays.toString(distribution));
            for (int i = 0; i < distribution.length; i++) {
                graphPanel.addCoordinate(0, i, distribution[i]);
            }*/

            fitnessDistributionPanel.removeAllGraphs();
            fitnessDistributionPanel.addGraph(new BarGraph(new Color(0), 3));
            fitnessDistributionPanel.resetAxis();
            int[] distribution = testcase.getFitnessDistribution();
            System.out.println(Arrays.toString(distribution));
            for (int i = 0; i < distribution.length; i++) {
                fitnessDistributionPanel.addCoordinate(0, i, distribution[i]);
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
        controls.add(champDisplay);


        content.add(generationLabel, BorderLayout.NORTH);
        content.add(controls, BorderLayout.EAST);
        content.add(widgetPanel, BorderLayout.CENTER);

        setContentPane(content);

        setSize(640, 400);
        setVisible(true);
    }

    private List<Widget> getWidgets() {
        List<Widget> widgets = new ArrayList<>(16);
        MouseListener mouseListener = new MouseAdapter() {
            int focusedIndex = -1;
            @Override
            public void mouseClicked(MouseEvent e) {
                Widget widget =(Widget)e.getComponent();
                if (!widget.isFocused()) {
                    content.remove(widgetPanel);
                    content.add(e.getComponent(), BorderLayout.CENTER);
                    focusedIndex = widgetPanel.getComponentZOrder(e.getComponent());
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
        widgets.add(new Widget("Fitress distribution", fitnessDistributionPanel, mouseListener));
        return widgets;
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
