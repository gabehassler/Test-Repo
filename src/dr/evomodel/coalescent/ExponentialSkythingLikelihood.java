package dr.evomodel.coalescent;
import dr.evolution.coalescent.ConstantPopulation;
import dr.evolution.coalescent.ExponentialBSPGrowth;
import dr.evolution.tree.Tree;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.coalescent.BayesianSkylineLikelihoodParser;
import dr.inference.model.Parameter;
import dr.inference.model.Statistic;
import dr.inference.model.Variable;
import dr.math.MathUtils;
import java.util.Date;
public class ExponentialSkythingLikelihood extends OldAbstractCoalescentLikelihood {
// PUBLIC STUFF
public ExponentialSkythingLikelihood(Tree tree,
Parameter slopeParameter,
Parameter startingPopSize,
int type) {
super(BayesianSkylineLikelihoodParser.SKYLINE_LIKELIHOOD);
groupSizeParameter = new Parameter.Default(slopeParameter.getDimension(), 1);
popSizeParameter = new Parameter.Default(slopeParameter.getDimension()-1);
this.slopeParameter = slopeParameter;
this.startingPopSize = startingPopSize;
int events = tree.getExternalNodeCount() - 1;
int paramDim = slopeParameter.getDimension();
this.type = type;
if (paramDim != events) {
throw new IllegalArgumentException("There are more groups than coalescent nodes in the tree.");
}
this.tree = tree;
if (tree instanceof TreeModel) {
addModel((TreeModel)tree);
}
addVariable(slopeParameter);
setupIntervals();
addStatistic(new GroupHeightStatistic());
}
// **************************************************************
// Likelihood IMPLEMENTATION
// **************************************************************
public double getLogLikelihood() {
setupIntervals();
double logL = 0.0;
double currentTime = 0.0;
int groupIndex=0;
int[] groupSizes = getGroupSizes();
double[] groupEnds = getGroupHeights();
int subIndex = 0;
ExponentialBSPGrowth eg = new ExponentialBSPGrowth(Type.YEARS);
double startGroupPopSize = 0;
double endGroupPopSize;
for (int j = intervalCount - 1; j >=0; j--) {
if(j==intervalCount - 1){
endGroupPopSize = startingPopSize.getParameterValue(0);
} else {
endGroupPopSize = startGroupPopSize;
}
double startTime = currentTime;
double endTime = currentTime + intervals[j];
startGroupPopSize = endGroupPopSize*Math.exp(slopeParameter.getParameterValue(j)*(endTime - startTime));
eg.setupN1(endGroupPopSize, slopeParameter.getParameterValue(j), endTime - startTime);
if (getIntervalType(j) == CoalescentEventType.COALESCENT) {
subIndex += 1;
if (subIndex >= groupSizes[groupIndex]) {
groupIndex += 1;
subIndex = 0;
}
}
logL += calculateIntervalLikelihood(eg, intervals[j], currentTime, lineageCounts[j], getIntervalType(j));
// insert zero-length coalescent intervals
int diff = getCoalescentEvents(j)-1;
for (int k = 0; k < diff; k++) {
eg.setup(startGroupPopSize, startGroupPopSize, endTime - startTime);
logL += calculateIntervalLikelihood(eg, 0.0, currentTime, lineageCounts[j]-k-1,
CoalescentEventType.COALESCENT);
subIndex += 1;
if (subIndex >= groupSizes[groupIndex]) {
groupIndex += 1;
subIndex = 0;
}
}
currentTime += intervals[j];
}
return logL;
}
public final double getPopSize(int groupIndex, double midTime, double[] groupHeights) {
return popSizeParameter.getParameterValue(groupIndex);
}
public final int[] getGroupSizes() {
if (groupSizeParameter.getParameterValue(0) < 2.0) {
throw new IllegalArgumentException("For linear model first group size must be >= 2.");
}
int[] groupSizes = new int[groupSizeParameter.getDimension()];
for (int i = 0; i < groupSizes.length; i++) {
double g = groupSizeParameter.getParameterValue(i);
if (g != Math.round(g)) {
throw new RuntimeException("Group size " + i + " should be integer but found:" + g);
}
groupSizes[i] = (int)Math.round(g);
}
return groupSizes;
}
private  int getGroupCount() {
return groupSizeParameter.getDimension();
}
private  int getGroupSize(int groupIndex) {
double g = groupSizeParameter.getParameterValue(groupIndex);
if (g != Math.round(g)) {
throw new RuntimeException("Group size " + groupIndex + " should be integer but found:" + g);
}
return (int)Math.round(g);
}
public final double[] getGroupHeights() {
double[] groupEnds = new double[getGroupCount()];
double timeEnd = 0.0;
int groupIndex = 0;
int subIndex = 0;
for (int i = 0; i < intervalCount; i++) {
timeEnd += intervals[i];
if (getIntervalType(i) == CoalescentEventType.COALESCENT) {
subIndex += 1;
if (subIndex >= getGroupSize(groupIndex)) {
groupEnds[groupIndex] = timeEnd;
groupIndex += 1;
subIndex = 0;
}
}
}
groupEnds[getGroupCount()-1] = timeEnd;
return groupEnds;
}
private double getGroupHeight(int groupIndex) {
return getGroupHeights()[groupIndex];
}
final public int getType() {
return type;
}
final public Parameter getPopSizeParameter() {
return popSizeParameter;
}
final public Parameter getGroupSizeParameter() {
return groupSizeParameter;
}
// ****************************************************************
// Implementing Demographic Reconstructor
// ****************************************************************
public String getTitle() {
final String title = "Bayesian Skything (exponential)\n" +
"Generated " + (new Date()).toString() + " [seed=" + MathUtils.getSeed() + "]";
return title;
}
// ****************************************************************
// Inner classes
// ****************************************************************
public class GroupHeightStatistic extends Statistic.Abstract {
public GroupHeightStatistic() {
super("groupHeight");
}
public int getDimension() { return getGroupCount(); }
public double getStatisticValue(int i) {
return getGroupHeight(i);
}
}
// ****************************************************************
// Private and protected stuff
// ****************************************************************
private final Parameter slopeParameter;
private final Parameter popSizeParameter;
private final Parameter groupSizeParameter;
private final Parameter startingPopSize;
private final int type;
}
