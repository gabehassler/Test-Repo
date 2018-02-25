//package dr.inference.prior;
//
//import dr.inference.model.Parameter;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//public class ExponentialParameterPrior extends AbstractParameterPrior {
//
//	/**
//	 * the mean of the exponential distribution.
//	 */
//	double mean;
//
//	public ExponentialParameterPrior(Parameter parameter, double mean) {
//		this(parameter, -1, mean);
//	}
//
//	public ExponentialParameterPrior(Parameter parameter, int dimension, double mean) {
//		this.mean = mean;
//		setParameter(parameter);
//		setDimension(dimension);
//	}
//
//	public double getLogPriorComponent(double value) {
//		return - value / mean;
//	}
//
//	public Element createElement(Document d) {
//		Element e = d.createElement("exponentialPrior");
//		e.setAttribute("mean", mean + "");
//		return e;
//	}
//
//	public final double getMean() { return mean; }
//
//	public String toString() {
//		return "Exponential(" + formatter.format(mean).trim() + ")";
//	}
//
//	public String toHTML() {
//		return "<font color=\"#FF00FF\">Exponential(" + formatter.format(mean).trim() + ")</font>";
//	}
//}
