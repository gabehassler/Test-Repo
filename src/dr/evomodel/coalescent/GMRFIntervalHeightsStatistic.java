package dr.evomodel.coalescent;
import dr.inference.model.Statistic;
public class GMRFIntervalHeightsStatistic extends Statistic.Abstract {
public GMRFIntervalHeightsStatistic(String name, GMRFSkyrideLikelihood skyrideLikelihood) {
super(name);
this.skyrideLikelihood = skyrideLikelihood;
}
public int getDimension() {
return skyrideLikelihood.getCorrectFieldLength();
}
public double getStatisticValue(int dim) {
if (dim == 0) {
// This assumes that each dimension will be called in turn, so
// the call for dim 0 updates the array.
heights = skyrideLikelihood.getCoalescentIntervalHeights();
}
return heights[dim];
}
final private GMRFSkyrideLikelihood skyrideLikelihood;
private double[] heights = null;
}