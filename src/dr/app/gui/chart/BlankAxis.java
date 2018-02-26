
package dr.app.gui.chart;

public class BlankAxis extends Axis.AbstractAxis {

    public BlankAxis() {
    }

    public BlankAxis(int minAxisFlag, int maxAxisFlag) {

        setAxisFlags(minAxisFlag, maxAxisFlag);
    }

    public int getMajorTickCount() {
        return 0;
    }

    public int getMinorTickCount(int majorTickNo) {
        return 0;
    }

    public double transform(double value) {
        return value;    // a linear transform !
    }

    public double untransform(double value) {
        return value;    // a linear transform !
	}
}

