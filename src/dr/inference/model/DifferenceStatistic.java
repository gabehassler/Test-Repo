
package dr.inference.model;

public class DifferenceStatistic extends Statistic.Abstract {

    private final boolean absolute;

    public DifferenceStatistic(String name, Statistic term1, Statistic term2, boolean absolute) {
        super(name);

        this.term1 = term1;
        this.term2 = term2;

        if (term1.getDimension() != 1 &&
                term2.getDimension() != 1 &&
                term1.getDimension() != term2.getDimension()) {
            throw new IllegalArgumentException();
        }

        if (term2.getDimension() == 1) {
            dimension = term1.getDimension();
        } else {
            dimension = term2.getDimension();
        }

        this.absolute = absolute;
    }

    public int getDimension() {
        return dimension;
    }

    public double getStatisticValue(int dim) {

        double statistic;

        if (term1.getDimension() == 1) {
            statistic = term1.getStatisticValue(0) - term2.getStatisticValue(dim);
        } else if (term2.getDimension() == 1) {
            statistic = term1.getStatisticValue(dim) - term1.getStatisticValue(0);
        } else {
            statistic = term1.getStatisticValue(dim) - term2.getStatisticValue(dim);
        }
        if (absolute) statistic = Math.abs(statistic);

        return statistic;
    }

    // ****************************************************************
    // Private and protected stuff
    // ****************************************************************

    private int dimension = 0;
    private Statistic term1 = null;
    private Statistic term2 = null;
}
