package dr.inference.model;
import java.util.ArrayList;
import java.util.List;
public class SumStatistic extends Statistic.Abstract {
    private int dimension = 0;
    private final boolean elementwise;
    private final boolean absolute;
    public SumStatistic(String name, boolean elementwise, boolean absolute) {
        super(name);
        this.elementwise = elementwise;
        this.absolute = absolute;
    }
    public SumStatistic(String name, boolean elementwise) {
        this(name, elementwise, false);
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
        return elementwise ? 1 : dimension;
    }
    public double getStatisticValue(int dim) {
        double sum = 0.0;
        if (elementwise) {
            assert dim == 0;
        }
        for (Statistic statistic : statistics) {
            if (elementwise) {
                if (absolute) {
                    //  System.err.println("statistic.getDimension(): " + statistic.getDimension());
                    for (int j = 0; j < statistic.getDimension(); j++) {
                        sum += Math.abs(statistic.getStatisticValue(j));
                    }
                } else {
                    for (int j = 0; j < statistic.getDimension(); j++) {
                        sum += statistic.getStatisticValue(j);
                        //    if(statistic.getStatisticValue(j)<0) {
                        //      System.err.println("statisticValue: " + statistic.getStatisticValue(j));
                        //  }
                    }
                }
            } else {
                sum += statistic.getStatisticValue(dim);
            }
        }
        return sum;
    }
    // ****************************************************************
    // Private and protected stuff
    // ****************************************************************
    private final List<Statistic> statistics = new ArrayList<Statistic>();
}
