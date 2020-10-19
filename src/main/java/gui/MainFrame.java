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
                long startingTime = System.nanoTime();
                System.out.println("Starting time: " + startingTime);
                if (doGeneration()) {
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

        settingsButton.addActionListener(e -> {
            JPanel content = new JPanel(new BorderLayout());
            JPanel settings = new JPanel(new GridBagLayout());
            JScrollPane scrollPane = new JScrollPane(settings);

            for (Field field : NeatConfiguration.class.getDeclaredFields()) {
                JPanel setting = new JPanel(new BorderLayout());
                setting.add(new JLabel(field.getName()), BorderLayout.CENTER);
                if (field.getType().equals(boolean.class)) {
                    setting.add(new JCheckBox(), BorderLayout.EAST);
                } else if (field.getType().equals(double.class)) {
                    settings.add(new JSpinner(new SpinnerNumberModel()));
                } else if (field.getType().equals(int.class)) {
                    JTextField textField = new JTextField();
                    textField.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyTyped(KeyEvent e) {
                            if (e.getKeyChar() != '\b' && (e.getKeyChar() < '0' || e.getKeyChar() > '9')) {
                                Toolkit.getDefaultToolkit().beep();
                                e.consume();
                            }
                        }
                    });
                    settings.add(textField);
                }
            }

            content.add(scrollPane, BorderLayout.CENTER);

            JDialog dialog = new JDialog(this, true);
            dialog.setContentPane(content);
            dialog.setSize(400, 400);
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
        JTextField numberOfGenerations = new JTextField("10");
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


        numberOfGenerations.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() != '\b' && (e.getKeyChar() < '0' || e.getKeyChar() > '9')) {
                    Toolkit.getDefaultToolkit().beep();
                    e.consume();
                }
            }
        });

        doNGenButton.addActionListener(e -> {
            generationsToDo = Integer.parseInt(numberOfGenerations.getText());
            doGenButton.setEnabled(false);
            doNGenButton.setEnabled(false);
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
