
package dr.evomodel.coalescent;

import dr.evolution.coalescent.IntervalList;
import dr.evolution.coalescent.IntervalType;
import dr.evolution.coalescent.Intervals;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;
import dr.evolution.util.Units;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class AbstractCoalescentLikelihood extends AbstractModelLikelihood implements Units, CoalescentIntervalProvider {

    // PUBLIC STUFF

    public AbstractCoalescentLikelihood(
            String name,
            Tree tree,
            TaxonList includeSubtree,
            List<TaxonList> excludeSubtrees) throws Tree.MissingTaxonException {

        super(name);

        this.tree = tree;

        if (includeSubtree != null) {
            includedLeafSet = Tree.Utils.getLeavesForTaxa(tree, includeSubtree);
        } else {
            includedLeafSet = null;
        }

        if (excludeSubtrees != null) {
            excludedLeafSets = new Set[excludeSubtrees.size()];
            for (int i = 0; i < excludeSubtrees.size(); i++) {
                excludedLeafSets[i] = Tree.Utils.getLeavesForTaxa(tree, excludeSubtrees.get(i));
            }
        } else {
            excludedLeafSets = new Set[0];
        }

        if (tree instanceof TreeModel) {
            addModel((TreeModel) tree);
        }

        intervals = new Intervals(tree.getNodeCount());
        storedIntervals = new Intervals(tree.getNodeCount());
        eventsKnown = false;

        addStatistic(new DeltaStatistic());

        likelihoodKnown = false;
    }

    // **************************************************************
    // ModelListener IMPLEMENTATION
    // **************************************************************

    protected final void handleModelChangedEvent(Model model, Object object, int index) {
        if (model == tree) {
            // treeModel has changed so recalculate the intervals
            eventsKnown = false;
        }

        likelihoodKnown = false;
    }

    // **************************************************************
    // VariableListener IMPLEMENTATION
    // **************************************************************

    protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
    } // No parameters to respond to

    // **************************************************************
    // Model IMPLEMENTATION
    // **************************************************************

    protected final void storeState() {
        // copy the intervals into the storedIntervals
        storedIntervals.copyIntervals(intervals);

        storedEventsKnown = eventsKnown;
        storedLikelihoodKnown = likelihoodKnown;
        storedLogLikelihood = logLikelihood;
    }

    protected final void restoreState() {
        // swap the intervals back
        Intervals tmp = storedIntervals;
        storedIntervals = intervals;
        intervals = tmp;

        eventsKnown = storedEventsKnown;
        likelihoodKnown = storedLikelihoodKnown;
        logLikelihood = storedLogLikelihood;
    }

    protected final void acceptState() {
    } // nothing to do

    // **************************************************************
    // Likelihood IMPLEMENTATION
    // **************************************************************

    public final Model getModel() {
        return this;
    }

    public final double getLogLikelihood() {
        if (!eventsKnown) {
            setupIntervals();
        }

        if (!likelihoodKnown) {
            logLikelihood = calculateLogLikelihood();
            likelihoodKnown = true;
        }

        return logLikelihood;
    }

    public final void makeDirty() {
        likelihoodKnown = false;
        eventsKnown = false;
    }

    public abstract double calculateLogLikelihood();

    protected NodeRef getIncludedMRCA(Tree tree) {
        if (includedLeafSet != null) {
            return Tree.Utils.getCommonAncestorNode(tree, includedLeafSet);
        } else {
            return tree.getRoot();
        }
    }

    protected Set<NodeRef> getExcludedMRCAs(Tree tree) {

        if (excludedLeafSets.length == 0) return null;

        Set<NodeRef> excludeNodesBelow = new HashSet<NodeRef>();
        for (Set<String> excludedLeafSet : excludedLeafSets) {
            excludeNodesBelow.add(Tree.Utils.getCommonAncestorNode(tree, excludedLeafSet));
        }
        return excludeNodesBelow;
    }

    public Tree getTree() {
        return tree;
    }

    public IntervalList getIntervals() {
        return intervals;
    }

    protected final void setupIntervals() {

        intervals.resetEvents();
        collectTimes(tree, getIncludedMRCA(tree), getExcludedMRCAs(tree), intervals);
        // force a calculation of the intervals...
        intervals.getIntervalCount();

        eventsKnown = true;
        likelihoodKnown = false;
    }


    private void collectTimes(Tree tree, NodeRef node, Set<NodeRef> excludeNodesBelow, Intervals intervals) {

        intervals.addCoalescentEvent(tree.getNodeHeight(node));

        for (int i = 0; i < tree.getChildCount(node); i++) {
            NodeRef child = tree.getChild(node, i);

            // check if this subtree is included in the coalescent density
            boolean include = true;

            if (excludeNodesBelow != null && excludeNodesBelow.contains(child)) {
                include = false;
            }

            if (!include || tree.isExternal(child)) {
                intervals.addSampleEvent(tree.getNodeHeight(child));
            } else {
                collectTimes(tree, child, excludeNodesBelow, intervals);
            }
        }

    }

    public double getCoalescentInterval(int i) {
        if (!eventsKnown) {
            setupIntervals();
        }
        return intervals.getInterval(i);
    }

    public int getCoalescentIntervalDimension() {
        if (!eventsKnown) {
            setupIntervals();
        }
        return intervals.getIntervalCount();
    }

    public int getCoalescentIntervalLineageCount(int i) {
        if (!eventsKnown) {
            setupIntervals();
        }
        return intervals.getLineageCount(i);
    }

    public IntervalType getCoalescentIntervalType(int i) {
        if (!eventsKnown) {
            setupIntervals();
        }
        return intervals.getIntervalType(i);
    }

    public String toString() {
        return Double.toString(logLikelihood);

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

    private Tree tree = null;
    private final Set<String> includedLeafSet;
    private final Set[] excludedLeafSets;

    private Intervals intervals = null;

    private Intervals storedIntervals = null;

    private boolean eventsKnown = false;
    private boolean storedEventsKnown = false;

    private double logLikelihood;
    private double storedLogLikelihood;
    protected boolean likelihoodKnown = false;
    private boolean storedLikelihoodKnown = false;
}