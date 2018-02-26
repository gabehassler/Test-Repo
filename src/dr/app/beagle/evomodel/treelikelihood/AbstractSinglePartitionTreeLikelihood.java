package dr.app.beagle.evomodel.treelikelihood;
import dr.evolution.alignment.PatternList;
import dr.evolution.datatype.DataType;
import dr.evomodel.tree.TreeModel;
public abstract class AbstractSinglePartitionTreeLikelihood extends AbstractTreeLikelihood {
    public AbstractSinglePartitionTreeLikelihood(String name, PatternList patternList, TreeModel treeModel) {
        super(name, treeModel);
        this.patternList = patternList;
        this.dataType = patternList.getDataType();
        patternCount = patternList.getPatternCount();
        stateCount = dataType.getStateCount();
        patternWeights = patternList.getPatternWeights();
    }
    protected void updatePattern(int i) {
        if (updatePattern != null) {
            updatePattern[i] = true;
        }
        likelihoodKnown = false;
    }
    protected void updateAllPatterns() {
        if (updatePattern != null) {
            for (int i = 0; i < patternCount; i++) {
                updatePattern[i] = true;
            }
        }
        likelihoodKnown = false;
    }
    public final double[] getPatternWeights() {
        return patternWeights;
    }
    public void makeDirty() {
        super.makeDirty();
        updateAllPatterns();
    }
    protected PatternList patternList = null;
    protected DataType dataType = null;
    protected double[] patternWeights;
    protected int patternCount;
    protected int stateCount;
    protected boolean[] updatePattern = null;
}
