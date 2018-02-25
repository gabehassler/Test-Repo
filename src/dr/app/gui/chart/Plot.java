package dr.app.gui.chart;
import dr.stats.Variate;
import java.awt.*;
import java.awt.geom.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public interface Plot {
// These constants are used for automatic scaling to select exactly
// where the axis starts and stops.
static public final int NO_MARK = 0;
static public final int POINT_MARK = 1;
static public final int CROSS_MARK = 2;
static public final int PLUS_MARK = 3;
static public final int CIRCLE_MARK = 4;
static public final int SQUARE_MARK = 5;
static public final int DIAMOND_MARK = 6;
void setAxes(Axis xAxis, Axis yAxis);
void resetAxes();
void setData(List<Double> xData, List<Double> yData);
void setData(Variate.N xData, Variate.N yData);
void setLineStyle(Stroke lineStroke, Paint linePaint);
void setLineStroke(Stroke lineStroke);
void setLineColor(Paint linePaint);
Paint getLineColor();
Stroke getLineStroke();
void setMarkStyle(int markType, double markSize, Stroke markStroke,
Paint markPaint, Paint markFillPaint);
void setMarkStyle(Shape mark, Stroke markStroke,
Paint markPaint, Paint markFillPaint);
void paintPlot(Graphics2D g2, double xScale, double yScale,
double xOffset, double yOffset);
void setName(String name);
String getName();
void pointClicked(Point2D point, boolean isShiftDown);
void selectPoint(int index, boolean addToSelection);
void selectPoints(Rectangle2D dragRectangle, boolean addToSelection);
void clearSelection();
void setSelectedPoints(final Collection<Integer> selectedPoints);
Set<Integer> getSelectedPoints();
Variate getXData();
Variate getYData();
public interface Listener {
void pointClicked(double x, double y, boolean isShiftDown);
void markClicked(int index, double x, double y, boolean isShiftDown);
void selectionChanged(Set<Integer> selectedPoints);
void rangeXSelected(double lower, double upper);
void rangeYSelected(double lower, double upper);
void rangeXYSelected(double lowerX, double lowerY, double upperX, double upperY);
}
public class Adaptor implements Listener {
public void pointClicked(double x, double y, boolean isShiftDown) {
}
public void markClicked(int index, double x, double y, boolean isShiftDown) {
}
public void selectionChanged(final Set<Integer> selectedPoints) {
}
public void rangeXSelected(double lower, double upper) {
}
public void rangeYSelected(double lower, double upper) {
}
public void rangeXYSelected(double lowerX, double lowerY, double upperX, double upperY) {
}
}
public abstract class AbstractPlot implements Plot {
protected Axis xAxis, yAxis;
protected Variate.N xData = null;
protected Variate.N yData = null;
protected List<Color> colours = null;
protected Shape mark;
protected Stroke lineStroke = new BasicStroke(1.5f);
protected Paint linePaint = Color.black;
protected Stroke markStroke = new BasicStroke(0.5f);
protected Paint markPaint = Color.black;
protected Paint markFillPaint = Color.black;
private final Rectangle2D bounds = null;
private double xScale, yScale, xOffset, yOffset;
private String name;
private Set<Integer> selectedPoints = new HashSet<Integer>();
public AbstractPlot() {
}
public AbstractPlot(Variate.N xData, Variate.N yData) {
setData(xData, yData);
}
public AbstractPlot(List<Double> xData, List<Double> yData) {
setData(xData, yData);
}
public void setData(List<Double> xData, List<Double> yData) {
Variate.D xd = new Variate.D(xData);
Variate.D yd = new Variate.D(yData);
this.xData = xd;
this.yData = yd;
}
public void setData(Variate.N xData, Variate.N yData) {
this.xData = xData;
this.yData = yData;
}
public void setColours(List<Color> colours) {
this.colours = colours;
}
public void setAxes(Axis xAxis, Axis yAxis) {
this.xAxis = xAxis;
this.yAxis = yAxis;
setupAxis(xAxis, yAxis, xData, yData);
}
public void resetAxes() {
setupAxis(xAxis, yAxis, xData, yData);
}
public void setupAxis(Axis xAxis, Axis yAxis, Variate xData, Variate yData) {
if (xData != null) {
if (xAxis instanceof LogAxis) {
double minValue = java.lang.Double.POSITIVE_INFINITY;
for (int i = 0; i < xData.getCount(); i++) {
double value = (Double) xData.get(i);
if (value > 0.0 && value < minValue)
minValue = value;
}
xAxis.addRange(minValue, (Double) xData.getMax());
} else {
xAxis.addRange((Double) xData.getMin(), (Double) xData.getMax());
}
}
if (yData != null) {
if (yAxis instanceof LogAxis) {
double minValue = java.lang.Double.POSITIVE_INFINITY;
for (int i = 0; i < yData.getCount(); i++) {
double value = (Double) yData.get(i);
if (value > 0.0 && value < minValue)
minValue = value;
}
yAxis.addRange(minValue, (Double) yData.getMax());
} else {
yAxis.addRange((Double) yData.getMin(), (Double) yData.getMax());
}
}
}
public void setLineStyle(Stroke lineStroke, Paint linePaint) {
this.lineStroke = lineStroke;
this.linePaint = linePaint;
}
public void setLineStroke(Stroke lineStroke) {
this.lineStroke = lineStroke;
}
public void setLineColor(Paint linePaint) {
this.linePaint = linePaint;
}
public final Paint getLineColor() {
return linePaint;
}
public final Stroke getLineStroke() {
return lineStroke;
}
public final void setName(String name) {
this.name = name;
}
public final String getName() {
return name;
}
public void setMarkStyle(int markType, double markSize, Stroke markStroke,
Paint markPaint, Paint markFillPaint) {
float w = (float) (markSize / 2.0);
GeneralPath path;
switch (markType) {
case POINT_MARK:
path = new GeneralPath();
path.moveTo(0, 0);
path.lineTo(0, 0);
setMarkStyle(path, markStroke, markPaint, markFillPaint);
break;
case CROSS_MARK:
path = new GeneralPath();
path.moveTo(-w, -w);
path.lineTo(w, w);
path.moveTo(w, -w);
path.lineTo(-w, w);
setMarkStyle(path, markStroke, markPaint, markFillPaint);
break;
case PLUS_MARK:
path = new GeneralPath();
path.moveTo(-w, 0);
path.lineTo(w, 0);
path.moveTo(0, -w);
path.lineTo(0, w);
setMarkStyle(path, markStroke, markPaint, markFillPaint);
break;
case CIRCLE_MARK:
setMarkStyle(new Ellipse2D.Double(0.0, 0.0, markSize, markSize), markStroke, markPaint, markFillPaint);
break;
case SQUARE_MARK:
setMarkStyle(new Rectangle2D.Double(-w, -w, markSize, markSize), markStroke, markPaint, markFillPaint);
break;
case DIAMOND_MARK:
path = new GeneralPath();
path.moveTo(0, -w);
path.lineTo(w, 0);
path.lineTo(0, w);
path.lineTo(-w, 0);
path.closePath();
setMarkStyle(path, markStroke, markPaint, markFillPaint);
break;
}
}
public void setMarkStyle(Shape mark, Stroke markStroke,
Paint markPaint, Paint markFillPaint) {
this.mark = mark;
this.markStroke = markStroke;
this.markPaint = markPaint;
this.markFillPaint = markFillPaint;
}
public Variate getXData() {
return xData;
}
public Variate getYData() {
return yData;
}
protected double transformX(double value) {
double tx = xAxis.transform(value);
if (tx == Double.NaN || tx == Double.NEGATIVE_INFINITY) {
return Double.NEGATIVE_INFINITY;
}
return ((tx - xAxis.transform(xAxis.getMinAxis())) * xScale) + xOffset;
}
protected double transformY(double value) {
double ty = yAxis.transform(value);
if (ty == Double.NaN || ty == Double.NEGATIVE_INFINITY) {
return Double.NEGATIVE_INFINITY;
}
return ((ty - yAxis.transform(yAxis.getMinAxis())) * yScale) + yOffset;
}
protected double untransformX(double value) {
return xAxis.untransform(
xAxis.transform(xAxis.getMinAxis()) + ((value - xOffset) / xScale));
}
protected double untransformY(double value) {
return yAxis.untransform(
yAxis.transform(yAxis.getMinAxis()) + ((value - yOffset) / yScale));
}
protected void drawLine(Graphics2D g2, double x1, double y1, double x2, double y2) {
Line2D line = new Line2D.Double(transformX(x1), transformY(y1),
transformX(x2), transformY(y2));
g2.draw(line);
}
protected void drawRect(Graphics2D g2, double x1, double y1, double x2, double y2) {
float tx1 = (float) transformX(x1);
float ty1 = (float) transformY(y1);
float tx2 = (float) transformX(x2);
float ty2 = (float) transformY(y2);
GeneralPath path = new GeneralPath();
path.moveTo(tx1, ty1);
path.lineTo(tx1, ty2);
path.lineTo(tx2, ty2);
path.lineTo(tx2, ty1);
path.closePath();
//			Rectangle2D rect = new Rectangle2D.Double(x, y,	w, h);
g2.draw(path);
}
protected void fillRect(Graphics2D g2, double x1, double y1, double x2, double y2) {
float tx1 = (float) transformX(x1);
float ty1 = (float) transformY(y1);
float tx2 = (float) transformX(x2);
float ty2 = (float) transformY(y2);
GeneralPath path = new GeneralPath();
path.moveTo(tx1, ty1);
path.lineTo(tx1, ty2);
path.lineTo(tx2, ty2);
path.lineTo(tx2, ty1);
path.closePath();
//			Rectangle2D rect = new Rectangle2D.Double(x, y,	w, h);
g2.fill(path);
}
public void paintPlot(Graphics2D g2, double xScale, double yScale,
double xOffset, double yOffset) {
if (xAxis == null || yAxis == null)
return;
this.xScale = xScale;
this.yScale = yScale;
this.xOffset = xOffset;
this.yOffset = yOffset;
// variable is assigned to itself
//this.bounds = bounds;
if (xData != null && yData != null && xData.getCount() > 0)
paintData(g2, xData, yData);
}
abstract protected void paintData(Graphics2D g2, Variate.N xData, Variate.N yData);
public void pointClicked(Point2D point, boolean isShiftDown) {
double x = untransformX(point.getX());
double y = untransformY(point.getY());
firePointClickedEvent(x, y, isShiftDown);
}
public void selectPoints(final Rectangle2D dragRectangle, final boolean addToSelection) {
if (dragRectangle == null) {
return;
}
if (!addToSelection) {
selectedPoints.clear();
}
double x0 = untransformX(dragRectangle.getX());
double y0 = untransformY(dragRectangle.getY() + dragRectangle.getHeight());
double x1 = untransformX(dragRectangle.getX() + dragRectangle.getWidth());
double y1 = untransformY(dragRectangle.getY());
for (int i = 0; i < xData.getCount(); i ++) {
double x = (Double) xData.get(i);
double y = (Double) yData.get(i);
if (x >= x0 && x <= x1 && y >= y0 && y <= y1) {
selectedPoints.add(i);
}
}
fireSelectionChanged();
}
public void selectPoint(final int index, final boolean addToSelection) {
if (!addToSelection) {
selectedPoints.clear();
}
selectedPoints.add(index);
fireSelectionChanged();
}
public void clearSelection() {
selectedPoints.clear();
fireSelectionChanged();
}
public void setSelectedPoints(final Collection<Integer> selectedPoints) {
this.selectedPoints.clear();
this.selectedPoints.addAll(selectedPoints);
}
public Set<Integer> getSelectedPoints() {
return selectedPoints;
}
// Listeners
private final java.util.Vector<Listener> listeners = new java.util.Vector<Listener>();
public void addListener(Listener listener) {
listeners.add(listener);
}
protected void firePointClickedEvent(double x, double y, boolean isShiftDown) {
for (int i = 0; i < listeners.size(); i++) {
final Listener listener = listeners.elementAt(i);
listener.pointClicked(x, y, isShiftDown);
}
}
protected void fireMarkClickedEvent(int index, double x, double y, boolean isShiftDown) {
for (int i=0; i < listeners.size(); i++) {
final Listener listener = listeners.elementAt(i);
listener.markClicked(index, x, y, isShiftDown);
}
}
protected void fireSelectionChanged() {
for (int i = 0; i < listeners.size(); i++) {
final Listener listener = listeners.elementAt(i);
listener.selectionChanged(selectedPoints);
}
}
}
}
