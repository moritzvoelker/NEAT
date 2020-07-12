package graph;

import java.awt.*;
import java.text.NumberFormat;

public class Axis {
    private double minX, maxX, minY, maxY;
    private double resolutionX, resolutionY;
    private Vektor center;

    public Axis(double minX, double maxX, double minY, double maxY, Vektor center) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.center = center;
        resolutionX = 1.0;
        resolutionY = 1.0;
    }

    public Axis(double minX, double maxX, double minY, double maxY, double resolutionX, double resolutionY, Vektor center) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.center = center;
    }

    void paintComponent(Graphics g, int width, int height, double normX, double normY) {
        ((Graphics2D) g).setStroke(new BasicStroke(2));
        g.setColor(new Color(0));

        Vektor centerPixel = Value2Pixel(center, width, height);
        System.out.println("Center: " + center.getX() + ", " + center.getY());
        System.out.println("CenterPixel: " + centerPixel.getX() + ", " + centerPixel.getY());
        g.drawLine((int)centerPixel.getX(), 0,(int)centerPixel.getX(), height);
        g.drawLine(0, (int)centerPixel.getY(), width, (int)centerPixel.getY());

        ((Graphics2D) g).setStroke(new BasicStroke(1));
        int ysteppixel = (int)(resolutionY / (maxY - minY) * height * normY);
        int ytpixel = (int) (centerPixel.getY());   // Die Pixelkoordinaten von denen aus die Achsenbeschriftung gemalt wird.
        double yt = center.getY();                  // Die Koordinaten von denen aus die Achsenbeschriftung gemalt wird.

        if (ytpixel > height || ytpixel < 0) {
            yt = yt + ((int)((maxY - minY) / (2 * normY * resolutionY))) * resolutionY;
            ytpixel = YValue2YPixel(yt, height);
        }

        System.out.println("ysteppixel: " + ysteppixel);

        int i = 1;
        for (boolean cont = true; cont ; i++) {
            int y1 = ytpixel -  i * ysteppixel;
            int y2 = ytpixel +  i * ysteppixel;
            // System.out.println(y1 + ", " + y2);

            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            NumberFormat nf  = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);

            cont = false;
            if (y1 > 0) {
                cont = true;
                g.drawLine(0, y1, 4, y1);

                y1 += 4;
                if (y1 < 10) {
                    y1 = 10;
                } else if (y1 > height - 3) {
                    y1 = height - 3;
                }
                g.drawString("" + nf.format((yt + i * resolutionY)), 6, y1);
            }
            if (y2  < height) {
                cont = true;
                g.drawLine(0, y2, 4, y2);

                y2 += 4;
                if (y2 < 10) {
                    y2 = 10;
                } else if (y2 > height - 3) {
                    y2 = height - 3;
                }
                g.drawString("" + nf.format((yt - i * resolutionY)), 6, y2);
            }
        }


        /*
        int ystep = height / 5;
        double ystepvalue = (maxY-minY) / 5.0;

        int yt = (int)centerPixel.getY();
        double ytvalue = center.getY();
        if (yt > height || yt < 0) {
            yt = height / 2;
            ytvalue = (maxY - minY) / 2;
        }
        int i = 0;
        for (boolean cont = true; cont ; i++) {
            int y1 = yt -  i * ystep;
            int y2 = yt +  i * ystep;

            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            NumberFormat nf  = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);

            cont = false;
            if (y1 > 0) {
                cont = true;
                g.drawLine(0, y1, 4, y1);

                y1 += 4;
                if (y1 < 10) {
                    y1 = 10;
                } else if (y1 > height - 3) {
                    y1 = height - 3;
                }
                g.drawString("" + nf.format((ytvalue + i * ystepvalue) / norm), 6, y1);
            }
            if (y2  < height) {
                cont = true;
                g.drawLine(0, y2, 4, y2);

                y2 += 4;
                if (y2 < 10) {
                    y2 = 10;
                } else if (y2 > height - 3) {
                    y2 = height - 3;
                }
                g.drawString("" + nf.format((ytvalue - i * ystepvalue) / norm), 6, y2);
            }
        }
        */
    }


    Vektor Value2Pixel(Vektor c, int width, int height) {
        return new Vektor(((c.getX() - minX) / (maxX - minX)) * width, (1.0 - (c.getY() - minY) / (maxY - minY)) * height);
    }

    int XValue2XPixel(double x, int width) {
        return (int) (((x - minX) / (maxX - minX)) * width);
    }

    int YValue2YPixel(double y, int height) {
        return (int) ((1.0 - (y - minY) / (maxY - minY)) * height);
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
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
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

    public Vektor getCenter() {
        return center;
    }

    public void setCenter(Vektor center) {
        this.center = center;
    }
}
