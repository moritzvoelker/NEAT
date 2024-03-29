/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package graph;

import java.util.List;
import java.awt.*;
import java.text.NumberFormat;

public class Axis {
    private double minX, maxX, minY, maxY;
    private double paddingY;
    private double resolutionX, resolutionY;
    private Vector center;

    public Axis(double minX, double maxX, double minY, double maxY, Vector center) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.center = center;
        resolutionX = 1.0;
        resolutionY = 1.0;
    }

    public Axis(double minX, double maxX, double minY, double maxY, double resolutionX, double resolutionY, Vector center) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.center = center;
    }

    void paintComponent(Graphics g, int width, int height, double normX, double normY) {
        ((Graphics2D) g).setStroke(new BasicStroke(1));
        g.setColor(new Color(0x000000));

        Vector centerPixel = value2Pixel(center, width, height);
        g.drawLine((int) centerPixel.getX(), 0, (int) centerPixel.getX(), height);
        g.drawLine(0, (int) centerPixel.getY(), width, (int) centerPixel.getY());

        ((Graphics2D) g).setStroke(new BasicStroke(1));
        double ysteppixel = (resolutionY / (maxY - minY + 2 * paddingY) * height * normY);
        int ytpixel = (int) (centerPixel.getY());   // Die Pixelkoordinaten von denen aus die Achsenbeschriftung gemalt wird.
        double yt = center.getY();                  // Die Koordinaten von denen aus die Achsenbeschriftung gemalt wird.


        if (ytpixel > height || ytpixel < 0) {
            yt = yt + ((int) ((maxY - minY + 2 * paddingY) / (2 * normY * resolutionY))) * resolutionY;
            ytpixel = YValue2YPixel(yt, height);
        }

        double xsteppixel = (resolutionX / (maxX - minX) * width * normX);
        int xtpixel = (int) (centerPixel.getX());   // Die Pixelkoordinaten von denen aus die Achsenbeschriftung gemalt wird.
        double xt = center.getX();                  // Die Koordinaten von denen aus die Achsenbeschriftung gemalt wird.

        if (xtpixel > height || xtpixel < 0) {
            xt = xt + ((int) ((maxX - minX) / (2 * normX * resolutionX))) * resolutionX;
            xtpixel = XValue2XPixel(xt, width);
        }

        int i = 1;
        for (boolean cont = true; cont; i++) {
            int y1 = ytpixel - (int)(i * ysteppixel);
            int y2 = ytpixel + (int)(i * ysteppixel);
            int x1 = xtpixel + (int)(i * xsteppixel);
            int x2 = xtpixel - (int)(i * xsteppixel);


            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);

            cont = false;

            if (y1 > 0) {
                cont = true;
                g.drawLine(xtpixel - 2, y1, xtpixel + 2, y1);
                g.drawString("" + nf.format((yt + i * resolutionY)), xtpixel + 6, y1 + 4);
            }
            if (y2 <= height) {
                cont = true;
                g.drawLine(xtpixel - 2, y2, xtpixel + 2, y2);
                g.drawString("" + nf.format((yt + i * resolutionY)),  xtpixel + 6, y2 + 4);
            }
            if (x1 < width) {
                cont = true;
                g.drawLine(x1, ytpixel + 2, x1, ytpixel - 2);

                String str = "" + nf.format((xt - i * resolutionX));
                int lengthpixel = (str.length() * 3);
                g.drawString("" + nf.format((xt + i * resolutionX)), x1 - lengthpixel / 2, ytpixel - 13);
            }

            if (x2 > 0) {
                cont = true;
                g.drawLine(x2, ytpixel + 2, x2, ytpixel - 2);

                String str = "" + nf.format((xt - i * resolutionX));
                int lengthpixel = (str.length() * 3);
                g.drawString("" + nf.format((xt + i * resolutionX)), x2 - lengthpixel / 2, ytpixel - 13);
            }
        }
    }


    public Vector value2Pixel(Vector c, int width, int height) {
        return new Vector(((c.getX() - minX) / (maxX - minX)) * width, (1.0 - (c.getY() - (minY - paddingY)) / (maxY - minY + 2 * paddingY)) * height);
    }

    public int XValue2XPixel(double x, int width) {
        return (int) (((x - minX) / (maxX - minX)) * width);
    }

    public int YValue2YPixel(double y, int height) {
        return (int) ((1.0 - (y - (minY - paddingY)) / (maxY - minY + 2 * paddingY)) * height);
    }

    public void update(List<Vector> newContent) {
        for (Vector vector : newContent) {
            update(vector);
        }
    }

    public void update(Vector newContent) {
            if (getMaxX() < newContent.getX()) {
                setMaxX(newContent.getX());
            }
            if (getMinX() > newContent.getX()) {
                setMinX(newContent.getX());
            }
            if (getMaxY() < newContent.getY()) {
                setMaxY(newContent.getY());
            }
            if (getMinY() > newContent.getY()) {
                setMinY(newContent.getY());
            }
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
        paddingY = (maxY - minY) * 0.05;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
        paddingY = (maxY - minY) * 0.05;
    }

    public double getResolutionX() {
        return resolutionX;
    }

    public void setResolutionX(double resolutionX) {
        this.resolutionX = resolutionX;
    }

    public double getResolutionY() {
        return resolutionY;
    }

    public void setResolutionY(double resolutionY) {
        this.resolutionY = resolutionY;
    }

    public Vector getCenter() {
        return center;
    }

    public void setCenter(Vector center) {
        this.center = center;
    }
}
