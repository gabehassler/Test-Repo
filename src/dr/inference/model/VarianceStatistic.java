package dr.inference.model;
import dr.stats.DiscreteStatistics;
import java.util.Vector;
public class VarianceStatistic extends Statistic.Abstract {
	public VarianceStatistic(String name) {
		super(name);
	}
	public void addStatistic(Statistic statistic) {
		statistics.add(statistic);
        int dimensionCount = 0;
        for (int i = 0; i < statistics.size(); i++) {
			statistic = (Statistic)statistics.get(i);
			dimensionCount += statistic.getDimension();
        }
        values = new double[dimensionCount];
	}
	public int getDimension() { return 1; }
	public final double getStatisticValue(int dim) {
		int n;
		Statistic statistic;
        int index = 0;
        for (int i = 0; i < statistics.size(); i++) {
            statistic = (Statistic)statistics.get(i);
			n = statistic.getDimension();
            for (int j = 0; j < n; j++) {
				values[index] = statistic.getStatisticValue(j);
			    index += 1;
            }
		}
		return DiscreteStatistics.variance(values);
	}
	// ****************************************************************
	// Private and protected stuff
	// ****************************************************************
	private Vector statistics = new Vector();
    private double[] values = null;
}
