
package dr.inference.model;

public class LogarithmStatistic extends Statistic.Abstract {

    private final Statistic statistic;
    private final double base;

    public LogarithmStatistic(String name, Statistic statistic, double base) {
        super(name);
        this.statistic = statistic;
        this.base = base;
    }

    public int getDimension() {
        return statistic.getDimension();
    }

    public double getStatisticValue(int dim) {
        if (base <= 1.0) {
            return Math.log(statistic.getStatisticValue(dim));
        } else {
            return Math.log(statistic.getStatisticValue(dim)) / Math.log(base);
        }
    }

}