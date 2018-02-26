package dr.evomodel.treelikelihood;
import dr.evolution.alignment.PatternList;
import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import java.util.HashMap;
import java.util.Map;
public abstract class TipStatesModel extends AbstractModel {
    // an enum which specifies if the model emits tip states or partials
    public enum Type {
        PARTIALS,
        STATES
    };
    public TipStatesModel(String name, TaxonList includeTaxa, TaxonList excludeTaxa) {
        super(name);
        this.includeTaxa = includeTaxa;
        this.excludeTaxa = excludeTaxa;
    }
    public final void setTree(Tree tree) {
        this.tree = tree;
        int extNodeCount = tree.getExternalNodeCount();
        excluded = new boolean[extNodeCount];
        if (includeTaxa != null) {
            for (int i = 0; i < extNodeCount; i++) {
                if (includeTaxa.getTaxonIndex(tree.getNodeTaxon(tree.getExternalNode(i))) == -1) {
                    excluded[i] = true;
                }
            }
        }
        if (excludeTaxa != null) {
            for (int i = 0; i < extNodeCount; i++) {
                if (excludeTaxa.getTaxonIndex(tree.getNodeTaxon(tree.getExternalNode(i))) != -1) {
                    excluded[i] = true;
                }
            }
        }
        states = new int[extNodeCount][];
        taxaChanged();
    }
    protected abstract void taxaChanged();
    public final void setStates(PatternList patternList, int sequenceIndex, int nodeIndex, String taxonId) {
        if (this.patternList == null) {
            this.patternList = patternList;
            patternCount = patternList.getPatternCount();
            stateCount = patternList.getDataType().getStateCount();
        } else if (patternList != this.patternList) {
            throw new RuntimeException("The TipStatesModel with id, " + getId() + ", has already been associated with a patternList.");
        }
        if (this.states[nodeIndex] == null) {
            this.states[nodeIndex] = new int[patternCount];
        }
        for (int i = 0; i < patternCount; i++) {
            this.states[nodeIndex][i] = patternList.getPatternState(sequenceIndex, i);
        }
        taxonMap.put(nodeIndex, taxonId);
    }
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        fireModelChanged();
    }
    protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        fireModelChanged();
    }
    protected void storeState() {
    }
    protected void restoreState() {
    }
    protected void acceptState() {
    }
    public PatternList getPatternList() {
        return patternList;
    }
    public abstract Type getModelType();
    public abstract void getTipPartials(int nodeIndex, double[] tipPartials);
    public abstract void getTipStates(int nodeIndex, int[] tipStates);
    protected int[][] states;
    protected boolean[] excluded;
    protected int patternCount = 0;
    protected int stateCount;
    protected TaxonList includeTaxa;
    protected TaxonList excludeTaxa;
    protected Tree tree;
    private PatternList patternList = null;
    protected Map<Integer, String> taxonMap = new HashMap<Integer, String>();
}
