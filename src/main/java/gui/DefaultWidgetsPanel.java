package gui;

import graph.*;
import neat.Organism;
import neat.Species;
import networkdisplay.Display;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultWidgetsPanel extends WidgetsPanel {


    private GraphPanel fitnessGraphPanel;
    private GraphPanel fitnessDistributionPanel;
    private JPanel champDisplay;
    private GraphPanel speciesDistributionPanel;
    private JPanel organisms;
    private JScrollPane scrollPane;
    private JPanel organismList;
    private Thread animationThread;

    public DefaultWidgetsPanel(Testcase testcase) {
        super(testcase);
        setLayout(new GridLayout(3, 2));

        fitnessGraphPanel = new GraphPanel();

        fitnessDistributionPanel = new GraphPanel();
        fitnessDistributionPanel.getAxis().setResolutionY(20.0);


        fitnessGraphPanel.addGraph(new LineGraph(new Color(0, 0, 0, 150), 3));
        fitnessGraphPanel.addGraph(new LineGraph(new Color(255, 0, 0, 150), 3));

        fitnessDistributionPanel.addGraph(new BarGraph(new Color(0), 3));

        resetFitnessPanels();

        speciesDistributionPanel = new GraphPanel();
        speciesDistributionPanel.getAxis().setResolutionY(50);

        organisms = new JPanel(new GridBagLayout());
        scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(166 / 2);
        organismList = new JPanel(new BorderLayout());
        organismList.add(scrollPane);

        champDisplay = new JPanel(new GridLayout(1, 1));



        widgets.add(new Widget("Fitness graph", fitnessGraphPanel));
        widgets.add(new Widget("Fitness distribution", fitnessDistributionPanel));
        widgets.add(new Widget("Champion structure", champDisplay));
        widgets.add(new Widget("Species distribution", speciesDistributionPanel));
        widgets.add(new Widget("Organism list", organismList));
        widgets.add(new Widget("Animation", testcase.getAnimationPanel()));
        addMouseListener();
        scrollPane.addMouseListener(scrollPane.getParent().getMouseListeners()[0]);
        widgets.forEach(this::add);
    }

    @Override
    public void reset() {
        champDisplay.removeAll();

        scrollPane.setViewportView(null);

        resetFitnessPanels();
        if (animationThread != null) {
            animationThread.interrupt();
        }

        speciesDistributionPanel.removeAllGraphs();
        speciesDistributionPanel.resetAxis();
    }

    @Override
    public void update() {
        champDisplay.removeAll();
        champDisplay.add(new Display(testcase.getChamp()));

        double[] y = new double[2];
        y[0] = testcase.getChamp().getFitness();
        y[1] = ((testcase.getChamp().getFitness() > fitnessGraphPanel.getGraph(0).getCoordinates().get(testcase.getGeneration() - 1).getY()) ? 0.5 : 0);
        fitnessGraphPanel.addCoordinates(testcase.getGeneration(), y);
        fitnessGraphPanel.getAxis().setResolutionX(Math.ceil(testcase.getGeneration() / 10.0));
        fitnessGraphPanel.getAxis().setResolutionY(Math.ceil((fitnessGraphPanel.getAxis().getMaxY() - fitnessGraphPanel.getAxis().getMinY()) / 10.0));



        fitnessDistributionPanel.getGraphs().forEach(Graph::clear);
        fitnessDistributionPanel.resetAxis();

        int[] distribution = testcase.getFitnessDistribution();
        for (int i = 0; i < distribution.length; i++) {
            fitnessDistributionPanel.addCoordinate(0, i, distribution[i]);
        }

        List<Species> speciesList = new ArrayList<>(testcase.getSpecies());
        int i = 1;
        int value = testcase.getConfiguration().getPopulationSize();
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
    }

    @Override
    public void startAnimations() {
        animationThread = new Thread(testcase.getAnimationPanel());
        animationThread.start();
    }

    private void resetFitnessPanels() {
        fitnessGraphPanel.getGraphs().forEach(Graph::clear);
        fitnessGraphPanel.resetAxis();
        double[] y = {0.0, 0.0};
        fitnessGraphPanel.addCoordinates(0, y);

        fitnessDistributionPanel.getGraphs().forEach(Graph::clear);
        fitnessDistributionPanel.resetAxis();
    }
}
