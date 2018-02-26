package dr.evomodel.tree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.tree.TreeTrait;
import dr.evolution.tree.TreeTraitProvider;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Variable;
import dr.util.Author;
import dr.util.Citable;
import dr.util.Citation;
import java.util.ArrayList;
import java.util.List;
public abstract class TreeTransform extends AbstractModel implements TreeTraitProvider, Citable {
    public static final String TREE_TRANSFORM_PREFIX = "treeTransform";
    public TreeTransform(String name) {
        super(name);
        setupTraits();
    }
    private void setupTraits() {
        TreeTrait baseTrait = new TreeTrait.D() {
            public String getTraitName() {
                return TREE_TRANSFORM_PREFIX;
            }
            public Intent getIntent() {
                return Intent.BRANCH;
            }
            public Double getTrait(Tree tree, NodeRef node) {
                return getScaleForNode(tree, node);
            }
            public boolean getLoggable() {
                return true;
            }
        };
        treeTraits.addTrait(baseTrait);
    }
    // TODO originalHeight is not necessary to pass anymore; remove
    public abstract double transform(Tree tree, NodeRef node, double originalHeight);
    protected abstract double getScaleForNode(Tree tree, NodeRef node);
    public abstract String getInfo();
    protected void handleModelChangedEvent(Model model, Object object, int index) {
    }
    protected void handleVariableChangedEvent(Variable variable, int index, Variable.ChangeType type) {
    }
    protected void storeState() {
    }
    protected void restoreState() {
    }
    protected void acceptState() {
    }
    public TreeTrait[] getTreeTraits() {
        return treeTraits.getTreeTraits();
    }
    public TreeTrait getTreeTrait(String key) {
        return treeTraits.getTreeTrait(key);
    }
    private final Helper treeTraits = new Helper();
    public List<Citation> getCitations() {
        List<Citation> citations = new ArrayList<Citation>();
        citations.add(
                new Citation(
                        new Author[]{
                                new Author("P", "Lemey"),
                                new Author("MA", "Suchard"),
                        },
                        Citation.Status.IN_PREPARATION
                ));
        return citations;
    }
}
