package dr.app.gui.chart;
import dr.stats.Variate;
import java.awt.*;
public class LineDistributionPlot extends Plot.AbstractPlot {
    private Variate gradients = null;
    private Variate intercepts = null;
    private int lineCount = 0;
    private double x1, x2;
    private double[] y1;
    private double[] y2;
    private boolean isCalibrated = false;
    public LineDistributionPlot(Variate gradients, Variate intercepts) {
        this.gradients = gradients;
        this.intercepts = intercepts;
    }
    public void paintPlot(Graphics2D g2, double xScale, double yScale,
                          double xOffset, double yOffset) {
        if (gradients == null || intercepts == null) {
            return;
        }
        super.paintPlot(g2, xScale, yScale, xOffset, yOffset);
        if (!isCalibrated) {
            x1 = xAxis.getMinAxis();
            x2 = xAxis.getMaxAxis();
            lineCount = gradients.getCount();
            y1 = new double[lineCount];
            y2 = new double[lineCount];
            for (int i = 0; i < lineCount; i++) {
                y1[i] = ( (Double) gradients.get(i) * x1) + (Double) intercepts.get(i);
                y2[i] = ( (Double) gradients.get(i) * x2) + (Double) intercepts.get(i);
            }
            isCalibrated = true;
        }
        Paint paint = new Color((float) 0.1, (float) 0.1, (float) 0.1,
                (float) (50.0 / lineCount));
        g2.setPaint(paint);
        g2.setStroke(lineStroke);
        for (int i = 0; i < lineCount; i++) {
            drawLine(g2, x1, y1[i], x2, y2[i]);
        }
    }
    protected void paintData(Graphics2D g2, Variate.N xData, Variate.N yData) {
        // do nothing because paintPlot is overridden
	}
}
