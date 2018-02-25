package dr.app.gui.tree;
import dr.evolution.tree.Tree;
import java.awt.*;
import java.awt.geom.Point2D;
public interface TreePainter  {
void setLineStyle(Stroke lineStroke, Paint linePaint);
void setHilightStyle(Stroke hilightStroke, Paint hilightPaint);
void setLabelStyle(Font labelFont, Paint labelPaint);
void setHilightLabelStyle(Font hilightLabelFont, Paint hilightLabelPaint);
void paintTree(Graphics2D g, Dimension size, Tree tree);
public int findNodeAtPoint(Point2D point);
}
