package dr.app.gui.chart;
import dr.stats.Variate;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.List;
public class LinePlot extends Plot.AbstractPlot {
    public LinePlot(Variate.N xData, Variate.N yData) {
        super(xData, yData);
    }
    public LinePlot(List<Double> xData, List<Double> yData) {
        super(xData, yData);
    }
    protected void paintData(Graphics2D g2, Variate.N xData, Variate.N yData) {
        double x = transformX(((Number) xData.get(0)).doubleValue());
        double y = transformY(((Number) yData.get(0)).doubleValue());
        GeneralPath path = new GeneralPath();
        path.moveTo((float) x, (float) y);
        int n = xData.getCount();
        boolean failed = false;
        for (int i = 1; i < n; i++) {
            x = transformX(((Number) xData.get(i)).doubleValue());
            y = transformY(((Number) yData.get(i)).doubleValue());
            if (x == Double.NEGATIVE_INFINITY || y == Double.NEGATIVE_INFINITY ||
                    Double.isNaN(x) || Double.isNaN(y)) {
                failed = true;
            } else if (failed) {
                failed = false;
                path.moveTo((float) x, (float) y);
            } else {
                path.lineTo((float) x, (float) y);
            }
        }
        g2.setPaint(linePaint);
        g2.setStroke(lineStroke);
        g2.draw(path);
	}
}
