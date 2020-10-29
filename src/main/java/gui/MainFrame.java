package gui;

import neat.NeatConfiguration;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class MainFrame extends JFrame {
    private JPanel content;
    private Testcase testcase;
    private JButton settingsButton;
    private WidgetsPanel widgetsPanel;

    private JLabel generationLabel;

    private JButton doGenButton;
    private JButton doNGenButton;

    private boolean hasAlreadyWorked;
    private Thread calculationThread;
    private int generationsToDo;

    private List<Long> times;

    public MainFrame(Testcase testcase) {
        times = new LinkedList<>();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.testcase = testcase;
        generationsToDo = 0;

        content = new JPanel(new BorderLayout());

        widgetsPanel = testcase.getWidgetsPanel();

        content.add(getHeader(), BorderLayout.NORTH);
        content.add(getControls(), BorderLayout.EAST);
        content.add(widgetsPanel, BorderLayout.CENTER);

        setContentPane(content);
        calculationThread = new Thread(() -> {
            while (true) {
                if (generationsToDo == 0) {
                    doGenButton.setEnabled(true);
                    doNGenButton.setEnabled(true);
                    synchronized (calculationThread) {
                        try {
                            calculationThread.wait();
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
                generationsToDo--;
                if (generationsToDo <= 0) {
                    doNGenButton.setText("Do n generations");
                    doNGenButton.setEnabled(false);
                    generationsToDo = 0;
                }
                long startingTime = System.nanoTime();
                System.out.println("Starting time: " + startingTime);
                if (doGeneration()) {
                    doNGenButton.setText("Do n generations");
                    generationsToDo = 0;
                }
                long endingTime = System.nanoTime();
                System.out.println("End time: " + endingTime);
                System.out.println("Took: " + (endingTime - startingTime));
                times.add(endingTime - startingTime);
                System.out.println("Took on average: " + times.stream().mapToLong(Long::longValue).average());
            }
        });
        calculationThread.start();


        setSize(1000, 800);
        setVisible(true);
    }

    private JPanel getHeader() {
        generationLabel = new JLabel("Generation 0");
        generationLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));

        int size = generationLabel.getPreferredSize().height;
        try {
            settingsButton = new JButton(getScaledIcon(ImageIO.read(new File("./src/main/resources/settings.png")), size, size));
        } catch (IOException e) {
            settingsButton = new JButton("Settings");
        }
        // TODO: 21.10.2020 Input and output nodes are changeable currently (not wanted)
        // TODO: 21.10.2020 Idea: add all settings that should be changeable to a (Hash-)Map and use that to get all keys and values.
        settingsButton.addActionListener(e -> {
            JDialog dialog = new JDialog(this, true);

            JPanel content = new JPanel(new BorderLayout());
            JPanel settings = new JPanel(new GridLayout(NeatConfiguration.class.getDeclaredFields().length, 1));
            JScrollPane scrollPane = new JScrollPane(settings);
            NeatConfiguration neatConfiguration = testcase.getConfiguration();

            try {
                for (Field field : neatConfiguration.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    JPanel setting = new JPanel(new BorderLayout());
                    setting.add(new JLabel(field.getName()), BorderLayout.CENTER);
                    if (field.getType().equals(boolean.class)) {
                        setting.add(new JCheckBox("", field.getBoolean(neatConfiguration)), BorderLayout.EAST);
                    } else if (field.getType().equals(double.class)) {
                        setting.add(new JSpinner(new SpinnerNumberModel(field.getDouble(neatConfiguration), 0.0, Integer.MAX_VALUE, 0.01)), BorderLayout.EAST);
                    } else if (field.getType().equals(int.class)) {
                        setting.add(new JSpinner(new SpinnerNumberModel(field.getInt(neatConfiguration), 0, Integer.MAX_VALUE, 1)), BorderLayout.EAST);
                    } else {
                        continue;
                    }
                    settings.add(setting);
                    field.setAccessible(false);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
                dialog.dispose();
            }
            System.out.println(scrollPane.getVerticalScrollBar().getPreferredSize().width);
            Dimension k = new Dimension(settings.getPreferredSize().width + scrollPane.getVerticalScrollBar().getPreferredSize().width  + 20, 400);
            dialog.setMinimumSize(k);
            dialog.setPreferredSize(k);

            JPanel buttons = new JPanel(/*new GridLayout(1, 2)*/);
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e1 -> dialog.dispose());
            JButton applyButton = new JButton("Apply");
            applyButton.addActionListener(e1 -> {
                try {
                    int i = 0;
                    for (Field field : neatConfiguration.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        if (field.getType().equals(boolean.class)) {
                            field.setBoolean(neatConfiguration, ((JCheckBox) ((JPanel) settings.getComponent(i)).getComponent(1)).isSelected());
                        } else if (field.getType().equals(double.class)) {
                            field.setDouble(neatConfiguration, (double) ((JSpinner) ((JPanel) settings.getComponent(i)).getComponent(1)).getValue());
                        } else if (field.getType().equals(int.class)) {
                            field.setInt(neatConfiguration, (int) ((JSpinner) ((JPanel) settings.getComponent(i)).getComponent(1)).getValue());
                        } else {
                            continue;
                        }
                        i++;
                    }
                } catch (IllegalAccessException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                }
                dialog.dispose();
            });
            buttons.add(cancelButton);
            buttons.add(applyButton);

            content.add(scrollPane, BorderLayout.CENTER);
            content.add(buttons, BorderLayout.SOUTH);



            dialog.setContentPane(content);
            dialog.setVisible(true);
        });

        JPanel header = new JPanel(new BorderLayout());
        header.add(generationLabel, BorderLayout.CENTER);
        header.add(settingsButton, BorderLayout.EAST);

        return header;
    }

    private Icon getScaledIcon(Image srcImg, int w, int h) {
        return new ImageIcon(srcImg.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    private JPanel getControls() {
        JPanel controls = new JPanel(new GridLayout(4, 1));

        doGenButton = new JButton("Do one generation");
        JSpinner numberOfGenerations = new JSpinner(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
        doNGenButton = new JButton("Do n generations");
        JButton resetButton = new JButton("Reset");


        doGenButton.addActionListener(e -> {
            generationsToDo = 1;
            doGenButton.setEnabled(false);
            doNGenButton.setEnabled(false);
            synchronized (calculationThread) {
                calculationThread.notify();
            }
        });



        doNGenButton.addActionListener(e -> {
            if (generationsToDo == 0) {
                doNGenButton.setText("Break");
                generationsToDo = (int)numberOfGenerations.getValue();
            } else {
                doNGenButton.setText("Do n generations");
                doNGenButton.setEnabled(false);
                generationsToDo = 0;
            }
            doGenButton.setEnabled(false);
            //doNGenButton.setEnabled(false);
            synchronized (calculationThread) {
                calculationThread.notify();
            }
        });


        resetButton.addActionListener(e -> {
            generationsToDo = 0;
            hasAlreadyWorked = false;
            testcase.reset();
            generationLabel.setText("Generation 0");

            widgetsPanel.reset();

            content.validate();
            content.repaint();
        });

        controls.add(resetButton);
        controls.add(doGenButton);
        controls.add(numberOfGenerations);
        controls.add(doNGenButton);
        return controls;
    }

    private boolean doGeneration() {
        if (testcase.getGeneration() == 0) {
            testcase.init();
            widgetsPanel.startAnimations();
        } else {
            testcase.doNGenerations(1);
        }
        generationLabel.setText("Generation " + testcase.getGeneration());

        widgetsPanel.update();


        content.validate();
        content.repaint();
        if (!hasAlreadyWorked && testcase.hasWorkingOrganism()) {
            hasAlreadyWorked = true;
            JOptionPane.showMessageDialog(this, "Found working organism!", "Success", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        new MainFrame((Testcase) Class.forName(args[0]).getConstructors()[0].newInstance());
    }
}
