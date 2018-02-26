package dr.inference.ml;
import dr.inference.markovchain.Acceptor;
public class HillClimbingCriterion implements Acceptor  {
	protected double bound = Double.NEGATIVE_INFINITY;
	public HillClimbingCriterion() {}
	public boolean accept(double oldScore, double newScore, double hastingsRatio, double[] logr) {
		if (newScore > bound) {
			bound = newScore;
			return true;
		}
		return false;
	}
}
