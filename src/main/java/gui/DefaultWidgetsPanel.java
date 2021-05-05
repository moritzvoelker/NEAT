/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package gui;

import graph.*;
import graph.Vector;
import neat.Organism;
import neat.Species;
import networkdisplay.Display;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DefaultWidgetsPanel extends WidgetsPanel {


    private GraphPanel fitnessGraphPanel;
    private GraphPanel fitnessDistributionPanel;
    private JPanel champDisplay;
    private GraphPanel speciesDistributionPanel;
    private LinkedHashMap<Species, DistributionGraph> speciesGraph;
    private JPanel organismPanel;
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

        speciesGraph = new LinkedHashMap<>();

        organismPanel = new JPanel(new GridBagLayout());
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
        speciesGraph.clear();
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
        List<Vector> newContent = new LinkedList<>();
        int value = testcase.getConfiguration().getPopulationSize();
        // Handle all species that we already know
        for (Iterator<Species> iterator = speciesGraph.keySet().iterator(); iterator.hasNext();) {
            Species species = iterator.next();
            // Add coordinate value to the graph of current species
            Vector vector = new Vector(testcase.getGeneration(), value);
            newContent.add(vector);
            speciesGraph.get(species).addCoordinate(vector);
            if (!speciesList.remove(species)) {
                // Since the list does not contain curr, the species should be empty and not necessary anymore --> remove from hashtable
                iterator.remove();
            } else {
                // This is only necessary if curr is contained, since members should be empty otherwise
                value -= species.getMembers().size();
            }
        }
        // Handle all species we did not know yet
        for (Species species : speciesList) {
            DistributionGraph dgraph = new DistributionGraph(new Color((50 * (speciesGraph.size()+1)) % 256, (100 * (speciesGraph.size()+1)) % 256, (75 * (speciesGraph.size()+1)) % 256), 3);
            speciesGraph.put(species, dgraph);
            speciesDistributionPanel.addGraph(dgraph);

            Vector vector = new Vector(testcase.getGeneration() - 1, 0);
            newContent.add(vector);
            dgraph.addCoordinate(vector);

            vector = new Vector(testcase.getGeneration(), value);
            newContent.add(vector);
            dgraph.addCoordinate(vector);

            value -= species.getMembers().size();
        }
        // Update the axis of the GraphPanel with the new content we added to the graphs
        speciesDistributionPanel.getAxis().update(newContent);
        speciesDistributionPanel.getAxis().setResolutionX(Math.ceil(testcase.getGeneration() / 10.0));

        organismPanel.removeAll();

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
        for (int j = 0; j < testcase.getSpecies().size(); j++) {
            for (Organism organism : testcase.getSpecies().get(j).getMembers()) {
                Display display = new Display(organism);
                display.setPreferredSize(new Dimension(50, 166));
                display.setBackground(speciesGraph.get(testcase.getSpecies().get(j)).getColor());
                organismPanel.add(display, gbc);
            }
        }

        scrollPane.setViewportView(organismPanel);
    }

    @Override
    public void startAnimations() {
        animationThread = new Thread(testcase.getAnimationPanel(), "Animation Thread");
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
