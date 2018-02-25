package dr.evomodel.coalescent;
import dr.evolution.coalescent.Coalescent;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.ScaledDemographic;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.Units;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.coalescent.CoalescentLikelihoodParser;
import dr.inference.model.*;
import dr.math.Binomial;
import dr.util.ComparableDouble;
import dr.util.HeapSort;
import java.util.ArrayList;
public class OldAbstractCoalescentLikelihood extends AbstractModelLikelihood implements  Units {
// PUBLIC STUFF
//public static final String COALESCENT_LIKELIHOOD = "oldcoalescentLikelihood";
//    public static final String ANALYTICAL = "analytical";
//    public static final String MODEL = "model";
//
//    public static final String POPULATION_TREE = "populationTree";
//    public static final String POPULATION_FACTOR = "factor";
protected MultiLociTreeSet treesSet = null;
public enum CoalescentEventType {
COALESCENT,
NEW_SAMPLE,
NOTHING
}
public OldAbstractCoalescentLikelihood(Tree tree, DemographicModel demoModel) {
this(CoalescentLikelihoodParser.COALESCENT_LIKELIHOOD, tree, demoModel, true);
}
public OldAbstractCoalescentLikelihood(MultiLociTreeSet treesSet, DemographicModel demoModel) {
super(CoalescentLikelihoodParser.COALESCENT_LIKELIHOOD);
this.demoModel = demoModel;
this.tree = null;
this.treesSet = treesSet;
if (demoModel != null) {
addModel(demoModel);
}
for (int nt = 0; nt < treesSet.nLoci(); ++nt) {
final Tree t = treesSet.getTree(nt);
if (t instanceof Model) {
addModel((Model) t);
}
}
}
public OldAbstractCoalescentLikelihood(String name, Tree tree, DemographicModel demoModel, boolean setupIntervals) {
super(name);
this.demoModel = demoModel;
this.tree = tree;
if (tree instanceof TreeModel) {
addModel((TreeModel) tree);
}
if (demoModel != null) {
addModel(demoModel);
}
if (setupIntervals) setupIntervals();
addStatistic(new DeltaStatistic());
}
OldAbstractCoalescentLikelihood(String name) {
super(name);
}
// **************************************************************
// Extendable methods
// **************************************************************
public NodeRef getMRCAOfCoalescent(Tree tree) {
return tree.getRoot();
}
public NodeRef[] getExcludedMRCAs(Tree tree) {
return null;
}
// **************************************************************
// ModelListener IMPLEMENTATION
// **************************************************************
protected void handleModelChangedEvent(Model model, Object object, int index) {
if (model == tree) {
// treeModel has changed so recalculate the intervals
intervalsKnown = false;
} else {
// demoModel has changed so we don't need to recalculate the intervals
}
likelihoodKnown = false;
}
// **************************************************************
// VariableListener IMPLEMENTATION
// **************************************************************
// No parameters to respond to
protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
}
// **************************************************************
// Model IMPLEMENTATION
// **************************************************************
protected void storeState() {
if (tree != null) {
System.arraycopy(intervals, 0, storedIntervals, 0, intervals.length);
System.arraycopy(lineageCounts, 0, storedLineageCounts, 0, lineageCounts.length);
storedIntervalsKnown = intervalsKnown;
storedIntervalCount = intervalCount;
storedLikelihoodKnown = likelihoodKnown;
} else if (treesSet != null) {
treesSet.storeTheState();
}
storedLogLikelihood = logLikelihood;
}
protected void restoreState() {
if (tree != null) {
System.arraycopy(storedIntervals, 0, intervals, 0, storedIntervals.length);
System.arraycopy(storedLineageCounts, 0, lineageCounts, 0, storedLineageCounts.length);
intervalsKnown = storedIntervalsKnown;
intervalCount = storedIntervalCount;
} else if (treesSet != null) {
treesSet.restoreTheState();
}
likelihoodKnown = storedLikelihoodKnown;
logLikelihood = storedLogLikelihood;
if (!intervalsKnown) {
likelihoodKnown = false;
}
}
protected final void acceptState() {
} // nothing to do
// **************************************************************
// Likelihood IMPLEMENTATION
// **************************************************************
public final Model getModel() {
return this;
}
public double getLogLikelihood() {
if (!likelihoodKnown) {
logLikelihood = calculateLogLikelihood();
likelihoodKnown = true;
}
return logLikelihood;
}
public final void makeDirty() {
likelihoodKnown = false;
intervalsKnown = false;
}
public double calculateLogLikelihood() {
if (treesSet != null) {
final int nTrees = treesSet.nLoci();
final DemographicFunction demogFunction = demoModel.getDemographicFunction();
double logLike = 0.0;
for (int nt = 0; nt < nTrees; ++nt) {
final double popFactor = treesSet.getPopulationFactor(nt);
DemographicFunction df = popFactor != 1.0 ?
new ScaledDemographic(demogFunction, popFactor) : demogFunction;
logLike += Coalescent.calculateLogLikelihood(treesSet.getTreeIntervals(nt), df);
}
return logLike;
}
if (!intervalsKnown) setupIntervals();
if (demoModel == null) return calculateAnalyticalLogLikelihood();
double logL = 0.0;
double currentTime = 0.0;
DemographicFunction demoFunction = demoModel.getDemographicFunction();
for (int j = 0; j < intervalCount; j++) {
logL += calculateIntervalLikelihood(demoFunction, intervals[j], currentTime, lineageCounts[j],
getIntervalType(j));
// insert zero-length coalescent intervals
final int diff = getCoalescentEvents(j) - 1;
for (int k = 0; k < diff; k++) {
logL += calculateIntervalLikelihood(demoFunction, 0.0, currentTime, lineageCounts[j] - k - 1,
CoalescentEventType.COALESCENT);
}
currentTime += intervals[j];
}
return logL;
}
private double calculateAnalyticalLogLikelihood() {
final double lambda = getLambda();
final int n = tree.getExternalNodeCount();
// assumes a 1/theta prior
//logLikelihood = Math.log(1.0/Math.pow(lambda,n));
// assumes a flat prior
//double logL = Math.log(1.0/Math.pow(lambda,n-1));
//final double logL = - Math.log(Math.pow(lambda,n-1));
return -(n - 1) * Math.log(lambda);
}
public final double calculateIntervalLikelihood(DemographicFunction demoFunction, double width,
double timeOfPrevCoal, int lineageCount) {
return calculateIntervalLikelihood(demoFunction, width, timeOfPrevCoal, lineageCount,
CoalescentEventType.COALESCENT);
}
public static double calculateIntervalLikelihood(DemographicFunction demogFunction,
double width, double timeOfPrevCoal, int lineageCount,
CoalescentEventType type) {
final double timeOfThisCoal = width + timeOfPrevCoal;
final double intervalArea = demogFunction.getIntegral(timeOfPrevCoal, timeOfThisCoal);
final double kchoose2 = Binomial.choose2(lineageCount);
double like = -kchoose2 * intervalArea;
switch (type) {
case COALESCENT:
final double demographic = demogFunction.getLogDemographic(timeOfThisCoal);
like += -demographic;
break;
case NEW_SAMPLE:
break;
}
return like;
}
public final double calculateIntervalShapeParameter(DemographicFunction demogFunction,
double width, double timeOfPrevCoal, int lineageCount, CoalescentEventType type) {
switch (type) {
case COALESCENT:
return 1.0;
case NEW_SAMPLE:
return 0.0;
}
throw new Error("Unknown event found");
}
public final double calculateIntervalRateParameter(DemographicFunction demogFunction, double width,
double timeOfPrevCoal, int lineageCount, CoalescentEventType type) {
final double timeOfThisCoal = width + timeOfPrevCoal;
final double intervalArea = demogFunction.getIntegral(timeOfPrevCoal, timeOfThisCoal);
return Binomial.choose2(lineageCount) * intervalArea;
}
private double getLambda() {
double lambda = 0.0;
for (int i = 0; i < getIntervalCount(); i++) {
lambda += (intervals[i] * lineageCounts[i]);
}
lambda /= 2;
return lambda;
}
public final void setupIntervals() {
if (intervals == null) {
int maxIntervalCount = tree.getNodeCount();
intervals = new double[maxIntervalCount];
lineageCounts = new int[maxIntervalCount];
storedIntervals = new double[maxIntervalCount];
storedLineageCounts = new int[maxIntervalCount];
}
XTreeIntervals ti = new XTreeIntervals(intervals, lineageCounts);
getTreeIntervals(tree, getMRCAOfCoalescent(tree), getExcludedMRCAs(tree), ti);
intervalCount = ti.nIntervals;
intervalsKnown = true;
}
private static void collectAllTimes(Tree tree, NodeRef top, NodeRef[] excludeBelow,
ArrayList<ComparableDouble> times, ArrayList<Integer> childs) {
times.add(new ComparableDouble(tree.getNodeHeight(top)));
childs.add(tree.getChildCount(top));
for (int i = 0; i < tree.getChildCount(top); i++) {
NodeRef child = tree.getChild(top, i);
if (excludeBelow == null) {
collectAllTimes(tree, child, excludeBelow, times, childs);
} else {
// check if this subtree is included in the coalescent density
boolean include = true;
for (NodeRef anExcludeBelow : excludeBelow) {
if (anExcludeBelow.getNumber() == child.getNumber()) {
include = false;
break;
}
}
if (include)
collectAllTimes(tree, child, excludeBelow, times, childs);
}
}
}
private class XTreeIntervals {
public XTreeIntervals(double[] intervals, int[] lineageCounts) {
this.intervals = intervals;
this.lineagesCount = lineageCounts;
}
int nIntervals;
final int[] lineagesCount;
final double[] intervals;
}
private static void getTreeIntervals(Tree tree, NodeRef root, NodeRef[] exclude, XTreeIntervals ti) {
double MULTIFURCATION_LIMIT = 1e-9;
ArrayList<ComparableDouble> times = new ArrayList<ComparableDouble>();
ArrayList<Integer> childs = new ArrayList<Integer>();
collectAllTimes(tree, root, exclude, times, childs);
int[] indices = new int[times.size()];
HeapSort.sort(times, indices);
final double[] intervals = ti.intervals;
final int[] lineageCounts = ti.lineagesCount;
// start is the time of the first tip
double start = times.get(indices[0]).doubleValue();
int numLines = 0;
int i = 0;
int intervalCount = 0;
while (i < times.size()) {
int lineagesRemoved = 0;
int lineagesAdded = 0;
final double finish = times.get(indices[i]).doubleValue();
double next = finish;
while (Math.abs(next - finish) < MULTIFURCATION_LIMIT) {
final int children = childs.get(indices[i]);
if (children == 0) {
lineagesAdded += 1;
} else {
lineagesRemoved += (children - 1);
}
i += 1;
if (i == times.size()) break;
next = times.get(indices[i]).doubleValue();
}
//System.out.println("time = " + finish + " removed = " + lineagesRemoved + " added = " + lineagesAdded);
if (lineagesAdded > 0) {
if (intervalCount > 0 || ((finish - start) > MULTIFURCATION_LIMIT)) {
intervals[intervalCount] = finish - start;
lineageCounts[intervalCount] = numLines;
intervalCount += 1;
}
start = finish;
}
// add sample event
numLines += lineagesAdded;
if (lineagesRemoved > 0) {
intervals[intervalCount] = finish - start;
lineageCounts[intervalCount] = numLines;
intervalCount += 1;
start = finish;
}
// coalescent event
numLines -= lineagesRemoved;
}
ti.nIntervals = intervalCount;
}
public final int getIntervalCount() {
return intervalCount;
}
public final double getInterval(int i) {
if (i >= intervalCount) throw new IllegalArgumentException();
return intervals[i];
}
public final int getLineageCount(int i) {
if (i >= intervalCount) throw new IllegalArgumentException();
return lineageCounts[i];
}
public final int getCoalescentEvents(int i) {
if (i >= intervalCount) throw new IllegalArgumentException();
if (i < intervalCount - 1) {
return lineageCounts[i] - lineageCounts[i + 1];
} else {
return lineageCounts[i] - 1;
}
}
public final CoalescentEventType getIntervalType(int i) {
if (i >= intervalCount) throw new IllegalArgumentException();
int numEvents = getCoalescentEvents(i);
if (numEvents > 0) return CoalescentEventType.COALESCENT;
else if (numEvents < 0) return CoalescentEventType.NEW_SAMPLE;
else return CoalescentEventType.NOTHING;
}
public final double getTotalHeight() {
double height = 0.0;
for (int j = 0; j < intervalCount; j++) {
height += intervals[j];
}
return height;
}
public final boolean isBinaryCoalescent() {
for (int i = 0; i < intervalCount; i++) {
if (getCoalescentEvents(i) != 1) return false;
}
return true;
}
public final boolean isCoalescentOnly() {
for (int i = 0; i < intervalCount; i++) {
if (getCoalescentEvents(i) < 1) return false;
}
return true;
}
public String toString() {
return getId(); // Double.toString(getLogLikelihood());
}
// **************************************************************
// Units IMPLEMENTATION
// **************************************************************
public final void setUnits(Type u) {
demoModel.setUnits(u);
}
public final Type getUnits() {
return demoModel.getUnits();
}
public final boolean getIntervalsKnown() {
return intervalsKnown;
}
// ****************************************************************
// Inner classes
// ****************************************************************
public class DeltaStatistic extends Statistic.Abstract {
public DeltaStatistic() {
super("delta");
}
public int getDimension() {
return 1;
}
public double getStatisticValue(int i) {
throw new RuntimeException("Not implemented");
//			return IntervalList.Utils.getDelta(intervals);
}
}
// ****************************************************************
// Private and protected stuff
// ****************************************************************
//    public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
//
//        public String getParserName() {
//            return COALESCENT_LIKELIHOOD;
//        }
//
//		public Object parseXMLObject(XMLObject xo) throws XMLParseException {
//
//            XMLObject cxo = (XMLObject) xo.getChild(MODEL);
//            DemographicModel demoModel = (DemographicModel) cxo.getChild(DemographicModel.class);
//
//            List<TreeModel> trees = new ArrayList<TreeModel>();
//            List<Double> popFactors = new ArrayList<Double>();
//            MultiLociTreeSet treesSet = demoModel instanceof MultiLociTreeSet ? (MultiLociTreeSet)demoModel : null;
//
//            for(int k = 0; k < xo.getChildCount(); ++k) {
//                final Object child = xo.getChild(k);
//                if( child instanceof XMLObject ) {
//                    cxo = (XMLObject)child;
//                    if( cxo.getName().equals(POPULATION_TREE) ) {
//                        final TreeModel treeModel = (TreeModel) cxo.getChild(TreeModel.class);
//                        if( treeModel == null ) {
//                            // xml check not done yet?
//                            throw new XMLParseException("Expecting a tree model.");
//                        }
//                        trees.add(treeModel);
//
//                        try {
//                            double v = cxo.hasAttribute(POPULATION_FACTOR) ?
//                                    cxo.getDoubleAttribute(POPULATION_FACTOR) : 1.0;
//                            popFactors.add(v);
//                        } catch (XMLParseException e) {
//                            throw new XMLParseException(e.getMessage());
//                        }
//                    }
//                } else if( child instanceof MultiLociTreeSet )  {
//                    treesSet = (MultiLociTreeSet)child;
//                }
//            }
//
//            TreeModel treeModel = null;
//            if( trees.size() == 1 && popFactors.get(0) == 1.0 ) {
//                treeModel = trees.get(0);
//            } else if( trees.size() > 1 ) {
//               treesSet = new MultiLociTreeSet.Default(trees, popFactors);
//            } else if( !(trees.size() == 0 && treesSet != null) ) {
//               throw new XMLParseException("error");
//            }
//
//            if( treeModel != null ) {
//                return new OldAbstractCoalescentLikelihood(treeModel, demoModel);
//            }
//            return new OldAbstractCoalescentLikelihood(treesSet, demoModel);
//        }
//
//
//        //************************************************************************
//		// AbstractXMLObjectParser implementation
//		//************************************************************************
//
//        public String getParserDescription() {
//            return "This element represents the likelihood of the tree given the demographic function.";
//        }
//
//        public Class getReturnType() {
//            return Likelihood.class;
//        }
//
//        public XMLSyntaxRule[] getSyntaxRules() {
//            return rules;
//        }
//
//		private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
//			new ElementRule(MODEL, new XMLSyntaxRule[] {
//				new ElementRule(DemographicModel.class)
//			}),
//			new ElementRule(POPULATION_TREE, new XMLSyntaxRule[] {
//                    AttributeRule.newDoubleRule(POPULATION_FACTOR, true),
//                new ElementRule(TreeModel.class)
//			}, 0, Integer.MAX_VALUE),
//		};
//	};
// ****************************************************************
// Private and protected stuff
// ****************************************************************
public String getParserName() { return COALESCENT_LIKELIHOOD; }
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
DemographicModel demoModel = null;
if (xo.hasAttribute(MODEL)) {
demoModel = (DemographicModel)xo.getAttribute(MODEL);
}
TreeModel treeModel = (TreeModel)xo.getAttribute(TREE);
return new CoalescentLikelihood(treeModel, demoModel);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "This element represents the likelihood of the tree given the demographic function.";
}
public Class getReturnType() { return Likelihood.class; }
public XMLSyntaxRule[] getSyntaxRules() { return rules; }
private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
new XORRule(
new EnumAttributeRule(ANALYTICAL, new String[] { "constant" }),
new AttributeRule(MODEL, DemographicModel.class)
),
new AttributeRule(TREE, TreeModel.class)
};
};*/
private DemographicModel demoModel = null;
Tree tree = null;
double[] intervals;
private double[] storedIntervals;
int[] lineageCounts;
private int[] storedLineageCounts;
boolean intervalsKnown = false;
private boolean storedIntervalsKnown = false;
double logLikelihood;
private double storedLogLikelihood;
boolean likelihoodKnown = false;
private boolean storedLikelihoodKnown = false;
int intervalCount = 0;
private int storedIntervalCount = 0;
}
