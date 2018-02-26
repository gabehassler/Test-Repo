
package dr.geo;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class KMLViewer extends JComponent {

    KMLRenderer renderer;
    BufferedImage image;

    public KMLViewer(String kmlFileName) {

        renderer = new KMLRenderer(kmlFileName, Color.green, Color.blue);
    }

    public void paintComponent(Graphics g) {

        if (image == null || image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
            image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            renderer.render(image);
        }

        g.drawImage(image, 0, 0, null);

        drawGrid(5, 5, (Graphics2D) g, renderer.viewTransform.getTransform());
    }

    private void drawGrid(int dLat, int dLong, Graphics2D g2d, AffineTransform transform) {

        for (double longitude = -180; longitude < 180; longitude += dLong) {
            Line2D line = new Line2D.Double(longitude, -90, longitude, 90);
            g2d.draw(transform.createTransformedShape(line));
        }
        for (double lat = -90; lat < 90; lat += dLat) {
            Line2D line = new Line2D.Double(-180, lat, 180, lat);
            g2d.draw(transform.createTransformedShape(line));
        }
    }

    public static void main(String[] args) {

        String filename = args[0];

        JFrame frame = new JFrame("KMLViewer - " + filename);

        KMLViewer viewer = new KMLViewer(filename);
        Rectangle2D viewport = viewer.renderer.getBounds();

        frame.getContentPane().add(BorderLayout.CENTER, viewer);

        int width;
        int height;
        if (viewport.getHeight() > viewport.getWidth()) {
            height = 900;
            width = (int) (height * viewport.getWidth() / viewport.getHeight());
        } else {
            width = 900;
            height = (int) (width * viewport.getHeight() / viewport.getWidth());
        }
        System.out.println("Height = " + height + ", Width = " + width);

        frame.setSize(width, height);
        frame.setVisible(true);
    }
}