package dr.evomodel.coalescent;

import dr.evolution.tree.Tree;
import dr.evolution.coalescent.TreeIntervals;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.Model;
import dr.inference.model.ModelListener;

import java.util.Arrays;
import java.util.List;

public interface MultiLociTreeSet {
    int nLoci();

    Tree getTree(int nt);

    TreeIntervals getTreeIntervals(int nt);

    double getPopulationFactor(int nt);

    void storeTheState();

    void restoreTheState();

    public class Default implements MultiLociTreeSet, ModelListener {
        private final List<TreeModel> trees;
        private final List<Double> factors;
        private final boolean[] dirty;
        private final boolean[] gotDirty;

        private final TreeIntervals[] intervals;

        public Default(List<TreeModel> trees, List<Double> popFactors) {
            this.trees = trees;
            this.factors = popFactors;

            for(TreeModel t : trees ) {
                t.addModelListener(this);
            }

            final int nt = trees.size();
            dirty = new boolean[nt];
            gotDirty = new boolean[nt];
            intervals = new TreeIntervals[nt];
            Arrays.fill(dirty, true);
            Arrays.fill(intervals, null);
        }

        public int nLoci() {
            return trees.size();
        }

        public Tree getTree(int nt) {
            return trees.get(nt);
        }

        public TreeIntervals getTreeIntervals(int nt) {
            if( dirty[nt] ) {
                intervals[nt] = new TreeIntervals(trees.get(nt));
                intervals[nt].setMultifurcationLimit(0);
                dirty[nt] = false;
            }
            return intervals[nt];
        }

        public double getPopulationFactor(int nt) {
            return factors.get(nt);
        }

        public void storeTheState() {
            Arrays.fill(gotDirty, false);
        }

        public void restoreTheState() {
            for(int nt = 0; nt < gotDirty.length; ++nt) {
                if( gotDirty[nt] ) {
                    dirty[nt] = true;
                }
            }
        }

        public void modelChangedEvent(Model model, Object object, int index) {
            assert model instanceof TreeModel;
            final int k = trees.indexOf((TreeModel)model);
            dirty[k] = gotDirty[k] = true;
        }

        public void modelRestored(Model model) {}
    }
}
