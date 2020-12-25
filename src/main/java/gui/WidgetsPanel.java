/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

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
