package dr.geo;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
public class VisualizeKMLJointProb extends VisualizeKMLNumericalProbs {
Point2D B;
public VisualizeKMLJointProb(String kmlFileName) {
super(kmlFileName);
B = athens;
if (rejector.reject(0.0, new double[]{B.getX(), B.getY()})) {
throw new RuntimeException("The B position was rejected!");
}
System.out.println("Populating Athens transition probs");
probs.populate(start, 50000, false);
probs.populate(B, 50000, false);
}
public void paintComponent(Graphics g) {
System.out.println("entering paintComponent()");
computeScales();
Graphics2D g2d = (Graphics2D) g;
g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
g2d.setStroke(new BasicStroke(1.5f));
int sx1 = probs.x(start.getX());
int sy1 = probs.y(start.getY());
int sx2 = probs.x(B.getX());
int sy2 = probs.y(B.getY());
int t = 49;
AffineTransform transform = getFullTransform();
double maxProb = 0.0;
double[][] p = new double[probs.latticeWidth][probs.latticeHeight];
for (int i = 0; i < probs.latticeWidth; i++) {
for (int j = 0; j < probs.latticeHeight; j++) {
p[i][j] = probs.p(sx1, sy1, i, j, t) * probs.p(sx2, sy2, i, j, t);
if (p[i][j] > maxProb) maxProb = p[i][j];
}
}
System.out.println("Painting lattice probs");
for (int i = 0; i < probs.latticeWidth; i++) {
for (int j = 0; j < probs.latticeHeight; j++) {
p[i][j] /= maxProb;
Rectangle2D rect = new Rectangle2D.Double(i * probs.dx + probs.minx, j * probs.dy + probs.miny, probs.dx, probs.dy);
g.setColor(cf.getColor((float) p[i][j]));
g2d.fill(transform.createTransformedShape(rect));
g.setColor(Color.black);
g2d.draw(transform.createTransformedShape(rect));
}
}
System.out.println("Painting shapes");
for (Shape s : shapes) {
System.out.print(".");
System.out.flush();
GeneralPath path = new GeneralPath(s);
path.transform(transform);
g2d.setPaint(Color.BLACK);
g2d.fill(path);
}
g2d.setColor(Color.yellow);
SpaceTime.paintDot(new SpaceTime(0, start), 4, transform, g2d);
SpaceTime.paintDot(new SpaceTime(0, B), 4, transform, g2d);
}
public static void main(String[] args) {
JFrame frame = new JFrame("Europe");
frame.getContentPane().add(BorderLayout.CENTER, new VisualizeKMLJointProb(args[0]));
frame.setSize(900, 900);
frame.setVisible(true);
}
}