
package dr.evomodel.coalescent;

import dr.inference.model.Statistic;
import dr.math.Binomial;


public class CoalescentIntervalStatistic extends Statistic.Abstract {

    private final CoalescentIntervalProvider coalescent;
    private final boolean rescaleToNe;

    public CoalescentIntervalStatistic(CoalescentIntervalProvider coalescent) {
        this(coalescent, false);
    }

    public CoalescentIntervalStatistic(CoalescentIntervalProvider coalescent, boolean rescaleToNe) {
        this.coalescent = coalescent;
        this.rescaleToNe = rescaleToNe;
    }

    public int getDimension() {
        return coalescent.getCoalescentIntervalDimension();
    }

    public double getStatisticValue(int i) {
        double interval = coalescent.getCoalescentInterval(i);

        if (rescaleToNe) {
            int lineages = coalescent.getCoalescentIntervalLineageCount(i);
            interval *= Binomial.choose2(lineages);
            // TODO Double-check; maybe need to return 1/interval or divide by choose2(lineages)
        }

        return interval;
    }
    
    public String getStatisticName() {
    	return "coalescentIntervalStatistic";
    }
    
}
