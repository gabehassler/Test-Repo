package dr.inference.model;
public class NegativeStatistic extends Statistic.Abstract {
private Statistic statistic = null;
public NegativeStatistic(String name, Statistic statistic) {
super(name);
this.statistic = statistic;
}
public int getDimension() {
return statistic.getDimension();
}
public double getStatisticValue(int dim) {	
return -statistic.getStatisticValue(dim);
}
}
