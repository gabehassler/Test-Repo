
package dr.inference.prior;

//public class UniformParameterPrior extends AbstractParameterPrior {
//
//	private double lower;
//	private double upper;
//
//	public UniformParameterPrior(Parameter parameter, double lower, double upper) {
//
//		this(parameter, -1, lower, upper);
//	}
//
//	public UniformParameterPrior(Parameter parameter, int dimension, double lower, double upper) {
//		this.upper = upper;
//		this.lower = lower;
//		setParameter(parameter);
//		setDimension(dimension);
//	}
//
//	public final double getLogPriorComponent(double value) {
//		if (value >= lower && value <= upper) return 0.0;
//		return Double.NEGATIVE_INFINITY;
//	}
//
//	public Element createElement(Document d) {
//		Element e = d.createElement("uniformPrior");
//		e.setAttribute("lower", lower + "");
//		e.setAttribute("upper", upper + "");
//		return e;
//	}
//
//	public double getLowerLimit() { return lower; }
//	public double getUpperLimit() { return upper; }
//
//	public String toString() {
//
//		StringBuilder buffer = new StringBuilder();
//		if (lower == -Double.MAX_VALUE) {
//            buffer.append("(").append(formatter.format(Double.NEGATIVE_INFINITY).trim());
//		} else if (lower == Double.MIN_VALUE) {
//            buffer.append("(").append(formatter.format(0.0).trim());
//		} else {
//            buffer.append("[").append(formatter.format(lower).trim());
//		}
//
//		buffer.append(", ");
//
//		if (upper == Double.MAX_VALUE) {
//            buffer.append(formatter.format(Double.POSITIVE_INFINITY).trim()).append(")");
//		} else if (upper == -Double.MIN_VALUE) {
//            buffer.append(formatter.format(0.0).trim()).append(")");
//		} else {
//            buffer.append(formatter.format(upper).trim()).append("]");
//		}
//
//		return buffer.toString();
//	}
//
//	public String toHTML() {
//		return "<font color=\"#FF00FF\">" + toString() + "</font>";
//	}
//}
