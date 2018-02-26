package dr.evomodel.coalescent;
import dr.evolution.coalescent.ConstantPopulation;
import dr.evolution.coalescent.ExponentialBSPGrowth;
import dr.evolution.tree.Tree;
import dr.evolution.util.Units;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.coalescent.BayesianSkylineLikelihoodParser;
import dr.inference.model.Parameter;
import dr.inference.model.Statistic;
import dr.math.MathUtils;
import java.util.Date;
public class BayesianSkylineLikelihood extends OldAbstractCoalescentLikelihood {
    // PUBLIC STUFF
    public static final int STEPWISE_TYPE = 0;
    public static final int LINEAR_TYPE = 1;
    public static final int EXPONENTIAL_TYPE = 2;
    public BayesianSkylineLikelihood(Tree tree,
                                     Parameter popSizeParameter,
                                     Parameter groupSizeParameter,
                                     int type) {
        super(BayesianSkylineLikelihoodParser.SKYLINE_LIKELIHOOD);
        this.groupSizeParameter = groupSizeParameter;
        this.popSizeParameter = popSizeParameter;
        int events = tree.getExternalNodeCount() - 1;
        int paramDim1 = popSizeParameter.getDimension();
        int paramDim2 = groupSizeParameter.getDimension();
        this.type = type;
        if (type == EXPONENTIAL_TYPE) {
            if (paramDim1 != (paramDim2+1)) {
                throw new IllegalArgumentException("Dimension of population parameter must be one greater than dimension of group size parameter.");
            }
        } else if (type == LINEAR_TYPE) {
            if (paramDim1 != (paramDim2+1)) {
                throw new IllegalArgumentException("Dimension of population parameter must be one greater than dimension of group size parameter.");
            }
        } else { // STEPWISE_TYPE
            if (paramDim1 != paramDim2) {
                throw new IllegalArgumentException("Dimension of population parameter and group size parameters should be the same.");
            }
        }
        if (paramDim2 > events) {
            throw new IllegalArgumentException("There are more groups than coalescent nodes in the tree.");
        }
        int eventsCovered = 0;
        for (int i = 0; i < getGroupCount(); i++) {
            eventsCovered += getGroupSize(i);
        }
        if (eventsCovered != events) {
            if (eventsCovered == 0 || eventsCovered == paramDim2) {
                double[] uppers = new double[paramDim2];
                double[] lowers = new double[paramDim2];
                // For these special cases we assume that the XML has not specified initial group sizes
                // or has set all to 1 and we set them here automatically...
                int eventsEach = events / paramDim2;
                int eventsExtras = events % paramDim2;
                for (int i = 0; i < paramDim2; i++) {
                    if (i < eventsExtras) {
                        groupSizeParameter.setParameterValue(i, eventsEach + 1);
                    } else {
                        groupSizeParameter.setParameterValue(i, eventsEach);
                    }
                    uppers[i] = Double.MAX_VALUE;
                    lowers[i] = 1.0;
                }
                if (type == EXPONENTIAL_TYPE || type == LINEAR_TYPE) {
                    lowers[0] = 2.0;
                }
                groupSizeParameter.addBounds(new Parameter.DefaultBounds(uppers, lowers));
            } else {
                // ... otherwise assume the user has made a mistake setting initial group sizes.
                throw new IllegalArgumentException("The sum of the initial group sizes does not match the number of coalescent events in the tree.");
            }
        }
        if ((type == EXPONENTIAL_TYPE || type == LINEAR_TYPE) && groupSizeParameter.getParameterValue(0) < 2.0) {
            throw new IllegalArgumentException("For linear or exponential model first group size must be >= 2.");
        }
        this.tree = tree;
        if (tree instanceof TreeModel) {
            addModel((TreeModel)tree);
        }
        addVariable(popSizeParameter);
        addVariable(groupSizeParameter);
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
        if (type == EXPONENTIAL_TYPE) {
            ExponentialBSPGrowth eg = new ExponentialBSPGrowth(Units.Type.YEARS);
            for (int j = 0; j < intervalCount; j++) {
                double startGroupPopSize = popSizeParameter.getParameterValue(groupIndex);
                double endGroupPopSize = popSizeParameter.getParameterValue(groupIndex+1);
                double startTime = currentTime;
                double endTime = currentTime + intervals[j];
                eg.setup(startGroupPopSize, endGroupPopSize, endTime - startTime);
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
        } else {
            ConstantPopulation cp = new ConstantPopulation(Units.Type.YEARS);
            for (int j = 0; j < intervalCount; j++) {
                // set the population size to the size of the middle of the current interval
                final double ps = getPopSize(groupIndex, currentTime + (intervals[j]/2.0), groupEnds);
                cp.setN0(ps);
                if (getIntervalType(j) == CoalescentEventType.COALESCENT) {
                    subIndex += 1;
                    if (subIndex >= groupSizes[groupIndex]) {
                        groupIndex += 1;
                        subIndex = 0;
                    }
                }
                logL += calculateIntervalLikelihood(cp, intervals[j], currentTime, lineageCounts[j], getIntervalType(j));
                // insert zero-length coalescent intervals
                int diff = getCoalescentEvents(j)-1;
                for (int k = 0; k < diff; k++) {
                    cp.setN0(getPopSize(groupIndex, currentTime, groupEnds));
                    logL += calculateIntervalLikelihood(cp, 0.0, currentTime, lineageCounts[j]-k-1,
                            CoalescentEventType.COALESCENT);
                    subIndex += 1;
                    if (subIndex >= groupSizes[groupIndex]) {
                        groupIndex += 1;
                        subIndex = 0;
                    }
                }
                currentTime += intervals[j];
            }
        }
        return logL;
    }
    public final double getPopSize(int groupIndex, double midTime, double[] groupHeights) {
        if (type == LINEAR_TYPE) {
            double startGroupPopSize = popSizeParameter.getParameterValue(groupIndex);
            double endGroupPopSize = popSizeParameter.getParameterValue(groupIndex+1);
            double startGroupTime = 0.0;
            if (groupIndex > 0) {
                startGroupTime = groupHeights[groupIndex-1];
            }
            double endGroupTime = groupHeights[groupIndex];
            // calculate the gradient
            double m = (endGroupPopSize-startGroupPopSize)/(endGroupTime-startGroupTime);
            // calculate the population size at midTime using linear interpolation
            final double midPopSize = (m * (midTime-startGroupTime)) + startGroupPopSize;
            return midPopSize;
        } else {
            return popSizeParameter.getParameterValue(groupIndex);
        }
    }
    public final int[] getGroupSizes() {
        if ((type == EXPONENTIAL_TYPE || type == LINEAR_TYPE) && groupSizeParameter.getParameterValue(0) < 2.0) {
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
        final String title = "Bayesian Skyline (" + (type == STEPWISE_TYPE ? "stepwise" : "linear") + ")\n" +
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
    private final Parameter popSizeParameter;
    private final Parameter groupSizeParameter;
    private final int type;
}
