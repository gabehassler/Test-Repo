package dr.app.gui.chart;
import dr.app.gui.components.JVerticalLabel;
import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
public class JChartPanel extends JPanel implements Printable {
	private static final long serialVersionUID = -737128752110140264L;
	public JChartPanel(JChart chart, String title, String xAxisTitle, String yAxisTitle) {
		setOpaque(false);
		ChartLayout layout = new ChartLayout(4, 4);
		setLayout(layout);
		if (chart != null) {
			add(chart, "Chart");
		}
		setTitle(title);
		setXAxisTitle(xAxisTitle);
		setYAxisTitle(yAxisTitle);
	}
	public void setTitle(String title) {
		if (titleLabel != null) {
			remove(titleLabel);
		}
		if (title != null) {
			titleLabel = new JLabel(title, JLabel.CENTER);
			add(titleLabel, "Title");
		}
	}
	public void setXAxisTitle(String xAxisTitle) {
		if (xAxisLabel != null) {
			remove(xAxisLabel);
		}
		if (xAxisTitle != null) {
			xAxisLabel = new JLabel(xAxisTitle, JLabel.CENTER);
			add(xAxisLabel, "XLabel");
		}
	}
	public void setYAxisTitle(String yAxisTitle) {
		if (yAxisLabel != null) {
			remove(yAxisLabel);
		}
		if (yAxisTitle != null) {
			yAxisLabel = new JVerticalLabel(yAxisTitle, JLabel.CENTER, false);
			add(yAxisLabel, "YLabel");
		}
	}
    public String getTitle() {
        return titleLabel.getText();
    }
    public String getXAxisTitle() {
        return xAxisLabel.getText();
    }
    public String getYAxisTitle() {
        return yAxisLabel.getText();
    }
    //********************************************************************
    //********************************************************************
	// Printable interface
	//********************************************************************
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if (pageIndex > 0) {
			return(NO_SUCH_PAGE);
		} else {
			Graphics2D g2d = (Graphics2D)g;
			double x0 = pageFormat.getImageableX();
			double y0 = pageFormat.getImageableY();
			double w0 = pageFormat.getImageableWidth();
			double h0 = pageFormat.getImageableHeight();
			double w1 = getWidth();
			double h1 = getHeight();
			double scale;
			if (w0 / w1 < h0 / h1) {
				scale = w0 / w1;
			} else {
				scale = h0 /h1;
			}
			g2d.translate(x0, y0);
			g2d.scale(scale, scale);
			Color bg = getBackground();
			setBackground(Color.white);
			paint(g2d);
			setBackground(bg);
			return(PAGE_EXISTS);
		}
	}
	private JLabel titleLabel = null;
	private JLabel xAxisLabel = null;
	private JLabel yAxisLabel = null;
}
