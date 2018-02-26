package dr.inference.model;
import java.util.ArrayList;
import java.util.List;
public class ProductStatistic extends Statistic.Abstract {
    private int dimension = 0;
    private final boolean elementwise;
    public ProductStatistic(String name, boolean elementwise) {
        super(name);
        this.elementwise = elementwise;
    }
    public void addStatistic(Statistic statistic) {
        if (!elementwise) {
            if (dimension == 0) {
                dimension = statistic.getDimension();
            } else if (dimension != statistic.getDimension()) {
                throw new IllegalArgumentException();
            }
        } else {
            dimension = 1;
        }
        statistics.add(statistic);
    }
    public int getDimension() {
        return dimension;
    }
    public double getStatisticValue(int dim) {
        double product = 1.0;
        for (Statistic statistic : statistics) {
            if (elementwise) {
                for (int j = 0; j < statistic.getDimension(); j++) {
                    product *= statistic.getStatisticValue(j);
                }
            } else {
                product *= statistic.getStatisticValue(dim);
            }
        }
        return product;
    }
    // ****************************************************************
    // Private and protected stuff
    // ****************************************************************
    private final List<Statistic> statistics = new ArrayList<Statistic>();
}
