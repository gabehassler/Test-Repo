package dr.app.gui.chart;
import dr.inference.trace.DensityEstimate;
import dr.stats.Variate;
import java.awt.*;
import java.awt.geom.GeneralPath;
public class DensityEstimatePlot extends Plot.AbstractPlot {
    public boolean isSolid() {
        return solid;
    }
    public void setSolid(boolean solid) {
        this.solid = solid;
    }
    boolean solid = true;
    public DensityEstimatePlot(DensityEstimate densityEstimate) {
        setData(densityEstimate.getXCoordinates(), densityEstimate.getYCoordinates());
    }
    public void setRelativeDensity(boolean relative) {
//        relativeDensity = relative;
//        setData(getRawData(), minimumBinCount);
    }
    public void setBarFillStyle(Paint barPaint) {
        throw new IllegalArgumentException();
    }
    protected void paintData(Graphics2D g2, Variate.N xData, Variate.N yData) {
        int n = xData.getCount();
        float x = (float) transformX(((Number)xData.get(0)).doubleValue());
        float y = (float) transformY(((Number)yData.get(0)).doubleValue());
        GeneralPath path = new GeneralPath();
        path.moveTo(x, y);
        for (int i = 1; i < n; i++) {
            x = (float) transformX(((Number)xData.get(i)).doubleValue());
            y = (float) transformY(((Number)yData.get(i)).doubleValue());
            path.lineTo(x, y);
        }
        if (solid) {
            path.closePath();
            Paint fillPaint = new Color(
                    ((Color) linePaint).getRed(),
                    ((Color) linePaint).getGreen(),
                    ((Color) linePaint).getBlue(), 32);
            g2.setPaint(fillPaint);
            g2.fill(path);
        }
        g2.setStroke(lineStroke);
        g2.setPaint(linePaint);
        g2.draw(path);
	}
}