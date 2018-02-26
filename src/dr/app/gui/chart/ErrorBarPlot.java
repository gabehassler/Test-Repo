package dr.app.gui.chart;
import dr.stats.Variate;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.List;
public class ErrorBarPlot extends Plot.AbstractPlot {
         public enum Orientation {
             HORIZONTAL,
             VERTICAL
         }
    public ErrorBarPlot(Orientation orientation, Variate.N xData, Variate.N yData, Variate.N errorData) {
        super(xData, yData);
        this.errorData = errorData;
        this.orientation = orientation;
    }
    public ErrorBarPlot(Orientation orientation, List<Double> xData, List<Double> yData, List<Double> errorData) {
        super(xData, yData);
        this.errorData = new Variate.D(errorData);
        this.orientation = orientation;
    }
    protected void paintData(Graphics2D g2, Variate.N xData, Variate.N yData) {
        g2.setPaint(linePaint);
        g2.setStroke(lineStroke);
        int n = xData.getCount();
        for (int i = 0; i < n; i++) {
            GeneralPath path = new GeneralPath();
            double x0 = ((Number) xData.get(i)).doubleValue();
            double y0 = ((Number) yData.get(i)).doubleValue();
            double e = ((Number) errorData.get(i)).doubleValue() / 2;
            float fx = (float)transformX(x0);
            float fy = (float)transformY(y0);
            if (!Double.isInfinite(fx) && !Double.isInfinite(fy) &&
                    !Double.isNaN(fx) && !Double.isNaN(fy)) {
                if (orientation == Orientation.HORIZONTAL) {
                    float fx1 = (float)transformX(x0 - e);
                    float fx2 = (float)transformX(x0 + e);
                    path.moveTo(fx, fy);
                    path.lineTo(fx1, fy);
                    path.moveTo(fx, fy);
                    path.lineTo(fx2, fy);
                } else if (orientation == Orientation.VERTICAL) {
                    float fy1 = (float)transformY(y0 - e);
                    float fy2 = (float)transformY(y0 + e);
                    path.moveTo(fx, fy);
                    path.lineTo(fx, fy1);
                    path.moveTo(fx, fy);
                    path.lineTo(fx, fy2);
                }
            }
            g2.draw(path);
        }
	}
    private Orientation orientation;
    protected Variate.N errorData = null;
}
