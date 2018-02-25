package dr.app.gui.chart;
import dr.math.distributions.Distribution;
import dr.stats.Variate;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import java.awt.*;
public class PDFPlot extends Plot.AbstractPlot {
private Distribution distribution = null;
private double offset;
private double xMax, xMin;
private double yMax;
private int stepCount = 100;
private static final double headRoom = 0.1;
public PDFPlot(Distribution distribution, double offset) {
this.distribution = distribution;
this.offset = offset;
}
public void setData(double[] xData, double[] yData) {
throw new UnsupportedOperationException("Not available");
}
public void setData(Variate.N xData, Variate.N yData) {
throw new UnsupportedOperationException("Not available");
}
public void setupAxis(Axis xAxis, Axis yAxis, Variate xData, Variate yData) {
if (distribution == null) {
return;
}
// if the distribution has a bound then use it, otherwise find a range using a small quantile
if (!Double.isInfinite(distribution.getProbabilityDensityFunction().getLowerBound())) {
// the actual bound may be undefined so come just inside it...
xMin = distribution.getProbabilityDensityFunction().getLowerBound() + 1.0E-20;
} else {
xMin = distribution.quantile(0.005);
}
// if the distribution has a bound then use it, otherwise find a range using a small quantile
if (!Double.isInfinite(distribution.getProbabilityDensityFunction().getUpperBound())) {
// the actual bound may be undefined so come just inside it...
xMax = distribution.getProbabilityDensityFunction().getUpperBound() - 1.0E-20;
} else {
xMax = distribution.quantile(0.995);
}
if (Double.isNaN(xMin) || Double.isInfinite(xMin)) {
xMin = 0.0;
}
if (Double.isNaN(xMax) || Double.isInfinite(xMax)) {
xMax = 1.0;
}
if (xMin == xMax) xMax += 1;
double x = xMin + offset;
yMax = distribution.pdf(x - offset);
double step = (xMax - xMin) / stepCount;
for (int i = 1; i < stepCount; i++) {
x += step;
double y = distribution.pdf(x - offset);
if (y > yMax) yMax = y;
}
if (xAxis instanceof LogAxis) {
throw new IllegalArgumentException("Log axis are not compatible to PDFPlot");
} else {
xAxis.setRange(offset + xMin, offset + xMax);
}
if (yAxis instanceof LogAxis) {
throw new IllegalArgumentException("Log axis are not compatible to PDFPlot");
} else {
yAxis.setRange(0.0, yMax * (1.0 + headRoom));
}
}
public void paintPlot(Graphics2D g2, double xScale, double yScale,
double xOffset, double yOffset) {
if (distribution == null) {
return;
}
super.paintPlot(g2, xScale, yScale, xOffset, yOffset);
g2.setPaint(linePaint);
g2.setStroke(lineStroke);
double x1 = xMin + offset;
double y1 = distribution.pdf(x1 - offset);
double step = (xMax - xMin) / stepCount;
for (int i = 1; i < stepCount; i++) {
double x2 = x1 + step;
double y2 = distribution.pdf(x2 - offset);
drawLine(g2, x1, y1, x2, y2);
x1 = x2;
y1 = y2;
}
}
protected void paintData(Graphics2D g2, Variate.N xData, Variate.N yData) {
// do nothing because paintPlot is overridden
}
}
