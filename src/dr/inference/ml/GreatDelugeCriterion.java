package dr.inference.ml;
import dr.inference.markovchain.Acceptor;
public class GreatDelugeCriterion implements Acceptor  {
	protected double rate;
	protected double bound = Double.NEGATIVE_INFINITY;
    protected double maxScore = Double.NEGATIVE_INFINITY;
    public GreatDelugeCriterion(double rate) {
		this.rate = rate;
	}
	public boolean accept(double oldScore, double newScore, double hastingsRatio, double[] logr) {
        // HACK HACK HACK
        if (newScore > maxScore) {
            maxScore = newScore;
            bound = maxScore - 1;
        }
        if (newScore > bound) {
			//if (bound == Double.NEGATIVE_INFINITY) {
			//	bound = oldScore * 2;
			//}
			//bound += (newScore - bound) * rate;
            return true;
		}
		return false;
	}
}
