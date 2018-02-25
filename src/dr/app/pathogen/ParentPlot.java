package dr.app.pathogen;
import dr.app.gui.chart.Plot;
import dr.stats.Variate;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
public class ParentPlot extends Plot.AbstractPlot {
public ParentPlot(Variate xData, Variate yData, List<Double> xParentData, List<Double> yParentData) {
super(xParentData, yParentData);
this.xTipData = xData;
this.yTipData = yData;
this.xParentData = new Variate.D(xParentData);
this.yParentData = new Variate.D(yParentData);
}
protected void paintData(Graphics2D g2, Variate.N xData, Variate.N yData) {
g2.setPaint(linePaint);
g2.setStroke(lineStroke);
if (getSelectedPoints() != null && getSelectedPoints().size() > 0) {
for (int i : getSelectedPoints()) {
double x = ((Number) xTipData.get(i)).doubleValue();
double y = ((Number) yTipData.get(i)).doubleValue();
double x1 = transformX(x);
double y1 = transformY(y);
double x2 = transformX(((Number) xData.get(0)).doubleValue());
double y2 = transformY(((Number) yData.get(0)).doubleValue());
GeneralPath path = new GeneralPath();
path.moveTo((float) x1, (float) y1);
//            path.lineTo((float) x2, (float) y1);
path.lineTo((float) x2, (float) y2);
g2.draw(path);
}
} else {
for (int i = 0; i < xData.getCount(); i++) {
double x1 = transformX(((Number) xTipData.get(i)).doubleValue());
double y1 = transformY(((Number) yTipData.get(i)).doubleValue());
double x2 = transformX(((Number) xData.get(i)).doubleValue());
double y2 = transformY(((Number) yData.get(i)).doubleValue());
GeneralPath path = new GeneralPath();
path.moveTo((float) x1, (float) y1);
//            path.lineTo((float) x2, (float) y1);
path.lineTo((float) x2, (float) y2);
g2.draw(path);
}
}
}
private final Variate xTipData;
private final Variate yTipData;
private final Variate.N xParentData;
private final Variate.N yParentData;
public void setSelectedPoints(Set<Integer> selectedPoints, double mrcaTime, double mrcaDistance) {
List<Double> x = new ArrayList<Double>();
x.add(mrcaTime);
List<Double> y = new ArrayList<Double>();
y.add(mrcaDistance);
setData(x, y);
setSelectedPoints(selectedPoints);
}
public void clearSelection() {
setData(xParentData, yParentData);
super.clearSelection();
}
}
