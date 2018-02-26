package dr.inference.prior;
//public abstract class AbstractParameterPrior implements ParameterPrior {
//
//	//************************************************************************
//	// abstract methods
//	//************************************************************************
//
//	/**
//	 * @return the log prior of a single-value component of the parameter.
//	 */
//	public abstract double getLogPriorComponent(double value);
//
//	//************************************************************************
//	// final methods
//	//************************************************************************
//
//	public final void setParameter(Parameter param) { this.parameter = param; }
//	public final Parameter getParameter() { return parameter; }
//	public final void setDimension(int dim) { dimension = dim; }
//
//	public final double getLogPrior(Model model) {
//
//		if (dimension == -1) {
//			double logL = 0.0;
//			for (int i =0; i < parameter.getDimension(); i++) {
//				logL += getLogPriorComponent(parameter.getParameterValue(i));
//			}
//			return logL;
//		} else return getLogPriorComponent(parameter.getParameterValue(dimension));
//	}
//
//	public final String getPriorName() { return toString(); }
//
//	//************************************************************************
//	// protected instance variables
//	//************************************************************************
//
//	protected NumberFormatter formatter = new NumberFormatter(6);
//
//	//************************************************************************
//	// private instance variables
//	//************************************************************************
//
//	/** the parameter this prior acts on */
//	private Parameter parameter = null;
//
//	/** the dimension of the parameter that this prior works on, -1 signifies all dimensions */
//	private int dimension = -1;
//}
