
package dr.inference.model;

public class NotStatistic extends Statistic.Abstract {

    private Statistic statistic = null;

    public NotStatistic(String name, Statistic statistic) {
        super(name);
        this.statistic = statistic;
    }

    public int getDimension() {
        return statistic.getDimension();
    }

    public double getStatisticValue(int dim) {

        return 1.0 - statistic.getStatisticValue(dim);
    }

}
