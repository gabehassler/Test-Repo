package dr.inference.model;
public class ExponentialStatistic extends Statistic.Abstract {
private Statistic statistic = null;
public ExponentialStatistic(String name, Statistic statistic) {
super(name);
this.statistic = statistic;
}
public int getDimension() {
return statistic.getDimension(); 
}
public double getStatisticValue(int dim) {	
return Math.exp(statistic.getStatisticValue(dim));
}
}
