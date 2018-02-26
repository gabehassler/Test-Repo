package dr.inference.model;
import java.util.ArrayList;
public class BooleanLikelihood extends Likelihood.Abstract {
	public BooleanLikelihood() {
		super(null);
	}
	public void addData(BooleanStatistic data) { dataList.add(data); }
	protected ArrayList<BooleanStatistic> dataList = new ArrayList<BooleanStatistic>();
	// **************************************************************
    // Likelihood IMPLEMENTATION
    // **************************************************************
	protected boolean getLikelihoodKnown() {
		return false;
	}
	public double calculateLogLikelihood() {
		if (getBooleanState()) {
			return Double.NEGATIVE_INFINITY;
		} else {
			return 0.0;
		}
	}
	public boolean getBooleanState() {
        for (BooleanStatistic statistic : dataList) {
            for (int j = 0; j < statistic.getDimension(); j++) {
                if (!statistic.getBoolean(j)) {
                    return true;
                }
            }
        }
        return false;
	}
    public boolean evaluateEarly() {
        return true;
    }
}
