/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package graph;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class LineGraph extends Graph {

    public LineGraph(Color color, int linewidth) {
        super(color, linewidth);
    }

    public LineGraph(List<Vector> content, Color color, int linewidth) {
        super(color, linewidth);
        this.content = new LinkedList<>();
        addCoordinates(content);
        this.color = color;
        this.linewidth = linewidth;
    }

    public LineGraph(Vector c, Color color, int linewidth) {
        super(color, linewidth);
        content = new LinkedList<>();
        content.add(c);
        this.color = color;
        this.linewidth = linewidth;
    }

    @Override
    public void paintComponent(Graphics g, Axis axis, int width, int height) {
        ((Graphics2D) g).setStroke(new BasicStroke(linewidth));
        g.setColor(color);

        for (int i = 0; i < content.size() - 1; i++) {
            Vector c1 = axis.value2Pixel(content.get(i), width, height);
            Vector c2 = axis.value2Pixel(content.get(i+1), width, height);
            g.drawLine((int)c1.getX(), (int)c1.getY(), (int)c2.getX(), (int)c2.getY());
        }
    }
}
