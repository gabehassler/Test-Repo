
package dr.evomodel.speciation;

import dr.inference.model.Statistic;



public class AlloppNumHybsStatistic  extends Statistic.Abstract {
    AlloppSpeciesNetworkModel aspnet;

    public AlloppNumHybsStatistic(AlloppSpeciesNetworkModel aspnet) {
        super("NumHybs");
        this.aspnet = aspnet;
    }


    @Override
    public int getDimension() {
        return 1;
    }

    @Override
    public double getStatisticValue(int dim) {
        return aspnet.getNumberOfTetraTrees();
    }
}
