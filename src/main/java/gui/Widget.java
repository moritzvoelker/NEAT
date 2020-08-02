package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Widget extends JPanel {
    private JLabel title;
    private JPanel content;
    private boolean focused;

    public Widget(String title, JPanel content, MouseListener mouseListener) {
        super(new BorderLayout());
        this.title = new JLabel(title);
        this.content = content;
        add(this.title, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        addMouseListener(mouseListener);
        this.focused = false;
    }

    public JLabel getTitle() {
        return title;
    }

    public void setTitle(JLabel title) {
        this.title = title;
    }

    public JPanel getContent() {
        return content;
    }

    public void setContent(JPanel content) {
        this.content = content;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }
}
