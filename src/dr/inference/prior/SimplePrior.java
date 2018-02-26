
//package dr.inference.prior;
//
//import dr.inference.model.Parameter;

//public class SimplePrior implements Prior {
//
//	ContinuousVariablePrior cvp;
//	Parameter parameter;
//
//	public SimplePrior(ContinuousVariablePrior cvp, Parameter parameter) {
//		this.cvp = cvp;
//		this.parameter = parameter;
//	}
//
//	public double getLogPrior(dr.inference.model.Model model) {
//
//		double logPrior = 0.0;
//		for (int i =0; i < parameter.getDimension(); i++) {
//			logPrior += cvp.getLogPrior(parameter.getParameterValue(i));
//		}
//
//		return logPrior;
//	}
//
//	public final String getPriorName() {
//		return parameter.getParameterName();
//	}
//}
