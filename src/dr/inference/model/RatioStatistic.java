
package dr.inference.model;

public class RatioStatistic extends Statistic.Abstract {

	public RatioStatistic(String name, Statistic numerator, Statistic denominator) {
		super(name);

        this.numerator = numerator;
        this.denominator = denominator;

        if (denominator.getDimension() != 1 &&
                numerator.getDimension() != 1 &&
                denominator.getDimension() != numerator.getDimension()) {
            throw new IllegalArgumentException();
        }

        if (denominator.getDimension() == 1) {
            dimension = numerator.getDimension();
        } else {
            dimension = denominator.getDimension();
        }
    }

    public int getDimension() { return dimension; }

	public double getStatisticValue(int dim) {

        if (numerator.getDimension() == 1) {
            return numerator.getStatisticValue(0) / denominator.getStatisticValue(dim);
        } else if (denominator.getDimension() == 1) {
            return numerator.getStatisticValue(dim) / denominator.getStatisticValue(0);
        } else {
            return numerator.getStatisticValue(dim) / denominator.getStatisticValue(dim);
        }
    }

	// ****************************************************************
	// Private and protected stuff
	// ****************************************************************

    private int dimension = 0;
    private Statistic numerator = null;
	private Statistic denominator = null;
}
