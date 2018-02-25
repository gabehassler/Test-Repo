package dr.evomodel.coalescent;
import dr.evolution.coalescent.IntervalType;
import dr.inference.model.Statistic;
public class CoalescentEventsStatistic extends Statistic.Abstract {
public static final boolean DEBUG = true;
private static final boolean FULL_FINAL_INTERVAL = true;
private static final boolean LOG_COMBINATIONS = true;
private static final boolean RETURN_RECIPROCAL = false;
private final CoalescentIntervalProvider coalescent;
//treeModel currently only required for debugging purposes
//private TreeModel treeModel;
private int coalescentEvents;
private double[] coalescentValues;
//public CoalescentEventsStatistic(CoalescentIntervalProvider coalescent, TreeModel treeModel) {
public CoalescentEventsStatistic(CoalescentIntervalProvider coalescent) {
this.coalescent = coalescent;
//this.treeModel = treeModel;
this.coalescentEvents = 0;
if (DEBUG) {
System.err.println("CoalescentIntervalDimension: " + coalescent.getCoalescentIntervalDimension());
}
if (coalescent instanceof GMRFSkyrideLikelihood) {
this.coalescentEvents = coalescent.getCoalescentIntervalDimension();
} else {
for (int i = 0; i < coalescent.getCoalescentIntervalDimension(); i++) {
//Not yet implemented for the skygrid model
if (coalescent.getCoalescentIntervalType(i) == IntervalType.COALESCENT) {
coalescentEvents++;
}
}
}
//System.err.println("Number of coalescent events: " + this.coalescentEvents);
this.coalescentValues = new double[coalescentEvents];
if (DEBUG) {
System.err.println("CoalescentEventsStatistic constructor: " + this.coalescentEvents);
}
}
public int getDimension() {
return this.coalescentEvents;
}
public double getStatisticValue(int i) {
//System.err.println(treeModel);
//i will go from 0 to getDimension()
if (i == 0) {
//reset array of coalescent events
for (int j = 0; j < coalescentValues.length; j++) {
coalescentValues[j] = 0.0;
}
//recalculate everything
int counter = 0;
for (int j = 0; j < coalescent.getCoalescentIntervalDimension(); j++) {
if (coalescent instanceof GMRFSkyrideLikelihood) {
this.coalescentValues[counter] = ((GMRFSkyrideLikelihood)coalescent).getSufficientStatistics()[j];
} else {
//System.err.println(coalescent.getCoalescentIntervalType(j) + "   " + coalescent.getCoalescentInterval(j));
if (coalescent.getCoalescentIntervalType(j) == IntervalType.COALESCENT) {
if (LOG_COMBINATIONS) {
this.coalescentValues[counter] += coalescent.getCoalescentInterval(j)*(coalescent.getCoalescentIntervalLineageCount(j)*(coalescent.getCoalescentIntervalLineageCount(j)-1.0))/2.0;
//System.err.println("interval length: " + coalescent.getCoalescentInterval(j));
//System.err.println("lineage count: " + coalescent.getCoalescentIntervalLineageCount(j));
//System.err.println("factorial: " + (coalescent.getCoalescentIntervalLineageCount(j)*coalescent.getCoalescentIntervalLineageCount(j)-1.0)/2.0);
//System.err.println("counter " + counter + ": " + this.coalescentValues[counter] + "\n");
//this.coalescentValues[counter] += coalescent.getCoalescentInterval(j);
//this.coalescentValues[counter] = (coalescent.getCoalescentIntervalLineageCount(j)*coalescent.getCoalescentIntervalLineageCount(j)-1.0)/(2.0*this.coalescentValues[counter]);
} else {
this.coalescentValues[counter] += coalescent.getCoalescentInterval(j);
}
counter++;
} else if (!FULL_FINAL_INTERVAL) {
if (coalescent.getCoalescentIntervalType(j) == IntervalType.SAMPLE && counter != 0) {
if (LOG_COMBINATIONS) {
this.coalescentValues[counter] += coalescent.getCoalescentInterval(j)*(coalescent.getCoalescentIntervalLineageCount(j)*(coalescent.getCoalescentIntervalLineageCount(j)-1.0))/2.0;
//System.err.println("interval length: " + coalescent.getCoalescentInterval(j));
//System.err.println("lineage count: " + coalescent.getCoalescentIntervalLineageCount(j));
//System.err.println("factorial: " + (coalescent.getCoalescentIntervalLineageCount(j)*coalescent.getCoalescentIntervalLineageCount(j)-1.0)/2.0);
//System.err.println("counter " + counter + ": " + this.coalescentValues[counter] + "\n");
//this.coalescentValues[counter] += coalescent.getCoalescentInterval(j);
} else {
this.coalescentValues[counter] += coalescent.getCoalescentInterval(j);
}
}
} else {
if (coalescent.getCoalescentIntervalType(j) == IntervalType.SAMPLE) {
if (LOG_COMBINATIONS) {
//System.err.println("interval length: " + coalescent.getCoalescentInterval(j));
//System.err.println("lineage count: " + coalescent.getCoalescentIntervalLineageCount(j));
//System.err.println("factorial: " + (coalescent.getCoalescentIntervalLineageCount(j)*coalescent.getCoalescentIntervalLineageCount(j)-1.0)/2.0);
//System.err.println("counter " + counter + ": " + this.coalescentValues[counter] + "\n");
this.coalescentValues[counter] += coalescent.getCoalescentInterval(j)*(coalescent.getCoalescentIntervalLineageCount(j)*(coalescent.getCoalescentIntervalLineageCount(j)-1.0))/2.0;
//this.coalescentValues[counter] += coalescent.getCoalescentInterval(j);
} else {
this.coalescentValues[counter] += coalescent.getCoalescentInterval(j);
}
}
}
}
}
}
System.err.println(this.coalescentValues[j]);
}
System.exit(0);*/
if (RETURN_RECIPROCAL) {
return 1.0/this.coalescentValues[i];
} else {
return this.coalescentValues[i];
}
}
public String getStatisticName() {
return "coalescentEventsStatistic";
}
}
