package gui;

import graph.GraphPanel;
import networkdisplay.Display;
import testcases.XOR;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private MainFrame self;
    private JPanel content;
    private Testcase testcase;

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

        JPanel graphPanel = new JPanel(new GridLayout(1,1));
        graphPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        JLabel generationLabel = new JLabel("Generation " + testcase.getGeneration());
        generationLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));

        JPanel  controls = new JPanel(new GridLayout(3, 1));;

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
            ((GraphPanel)(graphPanel.getComponent(0))).addCoordinates(testcase.getGeneration(), y);

            content.validate();
            content.repaint();
        });
        doGenButton.setEnabled(false);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            testcase.init();
            doGenButton.setEnabled(true);
            generationLabel.setText("Generation " + testcase.getGeneration());

            champDisplay.removeAll();
            champDisplay.add(new Display(testcase.getChamp()));

            graphPanel.removeAll();
            graphPanel.add(new GraphPanel(1, new Color(0, 0, 0, 150), 3, 1.0, 16.0, 1.0, 0.1));
            double[] y = {0.0};
            ((GraphPanel)(graphPanel.getComponent(0))).addCoordinates(0, y);
            y[0] = testcase.getChamp().getFitness();
            ((GraphPanel)(graphPanel.getComponent(0))).addCoordinates(1, y);

            System.out.println("Initialized testcase");
            content.validate();;
            content.repaint();
        });


        controls.add(resetButton);
        controls.add(doGenButton);
        controls.add(champDisplay);



        content.add(generationLabel, BorderLayout.NORTH);
        content.add(controls, BorderLayout.EAST);
        content.add(graphPanel, BorderLayout.CENTER);

        setContentPane(content);

        setSize(640, 400);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
