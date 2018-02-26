package dr.app.gui.chart;
import dr.stats.Variate;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.List;
public class AreaPlot extends Plot.AbstractPlot {
    protected Variate.N xData2 = null;
    protected Variate.N yData2 = null;
    public AreaPlot(Variate.N xData, Variate.N yData) {
        super(xData, yData);
    }
    public AreaPlot(List<Double> xData, List<Double> yData) {
        super(xData, yData);
    }
    public AreaPlot(Variate.N xData1, Variate.N yData1, Variate.N xData2, Variate.N yData2) {
        super(xData1, yData1);
        this.xData2 = xData2;
        this.yData2 = yData2;
    }
    public AreaPlot(List<Double> xData1, List<Double> yData1, List<Double> xData2, List<Double> yData2) {
        super(xData1, yData1);
        this.xData2 = new Variate.D(xData2);
        this.yData2 = new Variate.D(yData2);
    }
    public void setData(List<Double> xData1, List<Double> yData1, List<Double> xData2, List<Double> yData2) {
        setData(xData1, yData1);
        this.xData2 = new Variate.D(xData2);
        this.yData2 = new Variate.D(yData2);
    }
    public void setData(Variate.N xData1, Variate.N yData1, Variate.N xData2, Variate.N yData2) {
        setData(xData1, yData1);
        this.xData2 = xData2;
        this.yData2 = yData2;
    }
    public void setAxes(Axis xAxis, Axis yAxis) {
        super.setAxes(xAxis, yAxis);
        if (xData2 != null && yData2 != null) {
            setupAxis(xAxis, yAxis, xData2, yData2);
        }
    }
    public void resetAxes() {
        super.resetAxes();
        if (xData2 != null && yData2 != null) {
            setupAxis(xAxis, yAxis, xData2, yData2);
        }
    }
    protected void paintData(Graphics2D g2, Variate.N xData, Variate.N yData) {
        double x0 = transformX(((Number) xData.get(0)).doubleValue());
        double y0 = transformY(((Number) yData.get(0)).doubleValue());
        GeneralPath path = new GeneralPath();
        path.moveTo((float) x0, (float) y0);
        double x = x0;
        double y = y0;
        for (int i = 1, n = xData.getCount(); i < n; i++) {
            x = transformX(((Number) xData.get(i)).doubleValue());
            y = transformY(((Number) yData.get(i)).doubleValue());
            path.lineTo((float) x, (float) y);
        }
        if (xData2 != null & yData2 != null) {
            for (int i = xData2.getCount() - 1; i >= 0; i--) {
                x = transformX(((Number) xData2.get(i)).doubleValue());
                y = transformY(((Number) yData2.get(i)).doubleValue());
                path.lineTo((float) x, (float) y);
            }
        } else {
            double y1 = transformY(0.0);
            path.lineTo((float) x, (float) y1);
            path.lineTo((float) x0, (float) y1);
            path.lineTo((float) x0, (float) y0);
        }
        path.closePath();
        g2.setPaint(linePaint);
        g2.setStroke(lineStroke);
		g2.fill(path);
	}
}
