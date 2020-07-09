package gui;

import networkdisplay.Display;
import testcases.XOR;

import javax.swing.*;
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

        JLabel generationLabel = new JLabel("Generation " + testcase.getGeneration());
        generationLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));

        JPanel champDisplay = new JPanel(new GridLayout(1, 1));

        JButton doGenButton = new JButton("Do one generation");
        doGenButton.addActionListener(e -> {
            testcase.doNGenerations(1);
            generationLabel.setText("Generation " + testcase.getGeneration());
            champDisplay.removeAll();
            champDisplay.updateUI();
            champDisplay.add(new Display(testcase.getChamp()));
        });
        doGenButton.setEnabled(false);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            testcase.init();
            doGenButton.setEnabled(true);
            generationLabel.setText("Generation " + testcase.getGeneration());
            champDisplay.removeAll();
            champDisplay.updateUI();
            champDisplay.add(new Display(testcase.getChamp()));
            System.out.println("Initialized testcase");
        });

        JPanel controls = new JPanel(new GridLayout(3, 1));
        controls.add(resetButton);
        controls.add(doGenButton);
        controls.add(champDisplay);

        content.add(generationLabel, BorderLayout.NORTH);
        content.add(controls, BorderLayout.EAST);

        setContentPane(content);

        setSize(640, 400);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
