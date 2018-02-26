//package dr.inference.prior;
//public class ContinuousVariablePrior {
//
//	public static final int UNIFORM = 0;
//	public static final int JEFFREYS = 1;
//
//	/**
//	 * Create an uninformative flat prior.
//	 */
//	public ContinuousVariablePrior() {
//	}
//
//	public static ContinuousVariablePrior createJeffreysPrior() {
//		return new ContinuousVariablePrior(0.0, Double.MAX_VALUE, JEFFREYS);
//	}
//
//	public ContinuousVariablePrior(double value) {
//		priorType = UNIFORM;
//		minimumValue = value;
//		maximumValue = value;
//	}
//
//	public ContinuousVariablePrior(double min, double max, int priorType) {
//		minimumValue = min;
//		maximumValue = max;
//		this.priorType = priorType;
//	}
//
//	public void setMinimum( double min_ ){
//		minimumValue = min_;
//	}
//
//	public void setMaximum( double max_ ){
//		maximumValue = max_;
//	}
//
//	public void setPriorType(int priorType) {
//		this.priorType = priorType;
//	}
//
//	public double getMinimum(){
//		return minimumValue;
//	}
//
//	public double getMaximum(){
//		return maximumValue;
//	}
//
//	public int getPriorType() {
//		return priorType;
//	}
//
//	public boolean failed(double value) {
//		return (getLogPrior(value) == Double.NEGATIVE_INFINITY);
//	}
//
//	public double getLogPrior(double value) {
//
//		if ((value < minimumValue) || (value > maximumValue)) {
//
//			return Double.NEGATIVE_INFINITY;
//		}
//
//		if (priorType == JEFFREYS) {
//			return -Math.log(value);
//		} else return 0.0;
//	}
//
//	public boolean fixed() {
//		return minimumValue == maximumValue;
//	}
//
//	public String toString() {
//
//		if (fixed()) {
//			return "value=\"" + minimumValue + "\"";
//		}
//		return "min=\"" + minimumValue + "\" max=\"" + maximumValue + "\" type=\"" + ((priorType == UNIFORM) ? "uniform" : "Jeffreys'") + "\"";
//	}
//
//	private int priorType = UNIFORM;
//	private double minimumValue = -Double.MAX_VALUE;
//	private double maximumValue = Double.MAX_VALUE;
//}
