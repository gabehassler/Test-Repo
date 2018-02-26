package dr.inference.model;
import java.util.ArrayList;
import java.util.List;
public class MeanStatistic extends Statistic.Abstract {
	public MeanStatistic(String name) {
		super(name);
	}
	public void addStatistic(Statistic statistic) {
		statistics.add(statistic);
	}
	public int getDimension() { return 1; }
	public double getStatisticValue(int dim) {
		double sum = 0.0;
		int dimensionCount = 0;
		int n;
		for (Statistic statistic : statistics) {
			n = statistic.getDimension();
			for (int j = 0; j < n; j++) {
				sum += statistic.getStatisticValue(j);
			}
			dimensionCount += n;
		}
		return sum / dimensionCount;
	}
	// ****************************************************************
	// Private and protected stuff
	// ****************************************************************
	private List<Statistic> statistics = new ArrayList<Statistic>();
}
