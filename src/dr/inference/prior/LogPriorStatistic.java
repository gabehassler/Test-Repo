//package dr.inference.prior;
//
//import dr.inference.model.Model;
//import dr.inference.model.Statistic;
//public class LogPriorStatistic extends Statistic.Abstract {
//
//	public LogPriorStatistic(Prior prior, Model model) {
//		this.prior = prior;
//		this.model = model;
//	}
//
//	public String getStatisticName() { return "logPrior"; }
//
//	public int getDimension() { return 1; }
//	public double getStatisticValue(int dim) { return prior.getLogPrior(model); }
//
//	private Prior prior;
//	private Model model;
//}
