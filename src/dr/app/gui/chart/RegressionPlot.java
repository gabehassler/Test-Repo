package dr.app.gui.chart;
import dr.stats.Regression;
import dr.stats.Variate;
import java.awt.*;
import java.util.List;
public class RegressionPlot extends Plot.AbstractPlot {
    private Regression regression;
    public RegressionPlot(Regression regression) {
        super(regression.getXData(), regression.getYData());
        this.regression = regression;
    }
    public RegressionPlot(Variate.N xData, Variate.N yData, boolean forceOrigin) {
        super(xData, yData);
        setForceOrigin(forceOrigin);
    }
    public RegressionPlot(List<Double> xData, List<Double> yData, boolean forceOrigin) {
        super(xData, yData);
        setForceOrigin(forceOrigin);
    }
    public void setData(List<Double> xData, List<Double> yData) {
        super.setData(xData, yData);
        regression = new Regression(this.xData, this.yData);
    }
    public void setData(Variate.N xData, Variate.N yData) {
        super.setData(xData, yData);
        regression = new Regression(this.xData, this.yData);
    }
    public void setForceOrigin(boolean forceOrigin) {
        regression.setForceOrigin(forceOrigin);
    }
    public double getGradient() {
        return regression.getGradient();
    }
    public double getYIntercept() {
        return regression.getYIntercept();
    }
    public double getXIntercept() {
        return regression.getXIntercept();
    }
    public double getResidualMeanSquared() {
        return regression.getResidualMeanSquared();
    }
    public Regression getRegression() {
        return regression;
    }
    public String toString() {
        StringBuffer statString = new StringBuffer("Gradient=");
        statString.append(Double.toString(getGradient()));
        statString.append(", Intercept=");
        statString.append(Double.toString(getYIntercept()));
        statString.append(", RMS=");
        statString.append(Double.toString(getResidualMeanSquared()));
        return statString.toString();
    }
    protected void paintData(Graphics2D g2, Variate.N xData, Variate.N yData) {
        g2.setPaint(linePaint);
        g2.setStroke(lineStroke);
        double gradient = getGradient();
        double intercept = getYIntercept();
        double x1 = xAxis.getMinAxis();
        double y1 = (gradient * x1) + intercept;
        double x2 = xAxis.getMaxAxis();
        double y2 = (gradient * x2) + intercept;
        drawLine(g2, x1, y1, x2, y2);
	}
}
