
package dr.geo;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class SpaceTime {

    double[] space;
    double time;

    public SpaceTime(SpaceTime s) {

        this.space = new double[s.space.length];
        System.arraycopy(s.space, 0, this.space, 0, s.space.length);
        this.time = s.time;
    }

    public SpaceTime(double time, Point2D space) {
        this.time = time;
        this.space = new double[]{space.getX(), space.getY()};
    }

    public SpaceTime(double time, double[] space) {
        this.time = time;
        this.space = space;
    }

    public double[] getX() {
        return space;
    }

    public double getX(int index) {
        return space[index];
    }

    public double getTime() {
        return time;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(time);
        for (double s : space) {
            builder.append("\t").append(s);
        }
        return builder.toString();
    }

    public static void paintDot(SpaceTime s, double radius, AffineTransform transform, Graphics2D g2d) {

        Point2D pointRaw = new Point2D.Double(s.getX(0), s.getX(1));
        Point2D pointT = new Point2D.Double();

        transform.transform(pointRaw, pointT);

        Shape pointShape = new Ellipse2D.Double(pointT.getX() - radius, pointT.getY() - radius, 2.0 * radius, 2.0 * radius);

        g2d.fill(pointShape);
    }

}
