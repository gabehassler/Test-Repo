package dr.geo.contouring;

import dr.geo.contouring.ContourAttrib;


public class ContourPath implements Cloneable, java.io.Serializable {

	//	Tolerance for path closure.
	private static final double kSmallX = 0.001;
	private static final double kSmallY = kSmallX;

	//	X & Y coordinate arrays.
	private double[] xArr, yArr;

	//	The level index for this contour path.
	private int levelIndex;

	//	Indicates if this path is open or closed.
	private boolean closed = false;

	//	The attributes assigned to this contour level.
	private ContourAttrib attributes;


	public ContourPath(ContourAttrib attr, int levelIndex, double[] x, double[] y) {

		xArr = x;
		yArr = y;
		this.levelIndex = levelIndex;
		attributes = attr;
		int np = xArr.length;

		//	Determine if the contour path is open or closed.
		if (Math.abs(x[0] - x[np-1]) < kSmallX && Math.abs(y[0] - y[np-1]) < kSmallY) {
			closed = true;
			x[np-1] = x[0];  y[np-1] = y[0];	//	Guarantee closure.
		} else
			closed = false;						//	Contour not closed.

	}


	public double[] getAllX() {
		return xArr;
	}

	public double[] getAllY() {
		return yArr;
	}

	public int getLevelIndex() {
		return levelIndex;
	}

	public ContourAttrib getAttributes() {
		return attributes;
	}

	public boolean isClosed() {
		return closed;
	}

	public Object clone() {
		ContourPath newObject = null;

		try {
			// Make a shallow copy of this object.
			newObject = (ContourPath) super.clone();

			// There is no "deep" data to be cloned.

		} catch (CloneNotSupportedException e) {
			// Can't happen.
			e.printStackTrace();
		}

		// Output the newly cloned object.
		return newObject;
	}


}
