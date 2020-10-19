package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class WidgetsPanel extends JPanel {
    protected Testcase testcase;
    protected List<Widget>  widgets;

    WidgetsPanel(Testcase testcase) {
        this.testcase = testcase;
        widgets = new LinkedList<>();
    }

    public abstract void reset();
    public abstract void update();
    public abstract void startAnimations();

    protected void addMouseListener() {
        MouseListener mouseListener = new MouseAdapter() {
            int focusedIndex = -1;
            LayoutManager layoutManager = null;

            @Override
            public void mouseClicked(MouseEvent e) {
                Widget widget = (Widget) e.getSource();
                removeAll();
                if (!widget.isFocused()) {
                    layoutManager = getLayout();
                    setLayout(new BorderLayout());
                    focusedIndex = widgets.indexOf(widget);
                    add(widget, BorderLayout.CENTER);
                } else {
                    setLayout(layoutManager);
                    //widgetsPanel.add(widget, focusedIndex);
                    widgets.forEach(widget1 -> add(widget1));
                }
                widget.setFocused(!widget.isFocused());
                validate();
                repaint();
            }
        };
        widgets.forEach(widget -> widget.addMouseListener(mouseListener));
    }
}
