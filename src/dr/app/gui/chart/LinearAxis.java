
package dr.app.gui.chart;



public class LinearAxis extends Axis.AbstractAxis {

	public LinearAxis() { }

	public LinearAxis(int minAxisFlag, int maxAxisFlag) {

		setAxisFlags(minAxisFlag, maxAxisFlag);
	}

	public double transform(double value) {
		return value;	// a linear transform !
	}

	public double untransform(double value) {
		return value;	// a linear transform !
	}
}

