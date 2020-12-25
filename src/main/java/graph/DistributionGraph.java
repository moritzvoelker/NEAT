/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package graph;

import neat.Species;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;


public class DistributionGraph extends Graph {
    Species species;
    public DistributionGraph(Color color, int linewidth, Species sp) {
        super(color, linewidth);
        species = sp;
    }

    public Species getSpecies() {
        return species;
    }

    @Override
    public void paintComponent(Graphics g, Axis axis, int width, int height) {
        ((Graphics2D) g).setStroke(new BasicStroke(linewidth));
        g.setColor(color);

        int[] x = Arrays.copyOfRange(content.stream().mapToInt(vector -> axis.XValue2XPixel(vector.getX(), width)).toArray(), 0, content.size() + 1);
        x[x.length - 1] = width;
        int[] y = Arrays.copyOfRange(content.stream().mapToInt(vector -> axis.YValue2YPixel(vector.getY(), height)).toArray(), 0, content.size() + 1);
        y[y.length - 1] = axis.YValue2YPixel(axis.getCenter().getY(), height);
        g.fillPolygon(x, y, content.size() + 1);
    }
}
