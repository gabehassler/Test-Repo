
package dr.geo.contouring;


public class ContourAttrib implements Cloneable, java.io.Serializable {

	//	The level (altitude) of a contour path.
	private double level;


	public ContourAttrib(double level) {
		this.level = level;
	}

	public double getLevel() {
		return level;
	}

	public void setLevel(double level) {
		this.level = level;
	}

	public Object clone() {
		ContourAttrib newObject = null;

		try {
			// Make a shallow copy of this object.
			newObject = (ContourAttrib) super.clone();

			// There is no "deep" data to be cloned.

		} catch (CloneNotSupportedException e) {
			// Can't happen.
			e.printStackTrace();
		}

		// Output the newly cloned object.
		return newObject;
	}

}

