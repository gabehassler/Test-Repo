package dr.inference.model;
public abstract class BooleanStatistic extends Statistic.Abstract {
	public BooleanStatistic(String name) { super(name); }
	public double getStatisticValue(int dim) {
		if (getBoolean(dim))
			return 1.0;
		else
			return 0.0;
	}
	public abstract boolean getBoolean(int dim);
}
