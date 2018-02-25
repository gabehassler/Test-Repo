package dr.inference.model;
public class SubStatistic extends Statistic.Abstract {
private final int[] dimensions;
private final Statistic statistic;
public SubStatistic(String name, int[] dimensions, Statistic stat) {
super(name);
this.dimensions = dimensions;
this.statistic = stat;
}
public int getDimension() {
return dimensions.length;
}
public double getStatisticValue(int dim) {
return statistic.getStatisticValue(dimensions[dim]);
}
public String getDimensionName(int dim) {
return statistic.getDimensionName(dimensions[dim]);
}
}
