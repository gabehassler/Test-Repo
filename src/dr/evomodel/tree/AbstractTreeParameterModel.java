
package dr.evomodel.tree;

import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.tree.TreeTrait;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;

public abstract class AbstractTreeParameterModel<T> extends AbstractModel implements TreeTrait<T> {

    protected final TreeModel tree;

    // The tree parameter;
    protected final Parameter parameter;

    // the index of the root node.
    private int rootNodeNumber;
    private int storedRootNodeNumber;

    private boolean includeRoot = false;
    private boolean includeTips = true;

    private final Intent intent;

    public AbstractTreeParameterModel(TreeModel tree, Parameter parameter, boolean includeRoot, boolean includeTips) {
        this(tree, parameter, includeRoot, includeTips, Intent.BRANCH);
    }

    public AbstractTreeParameterModel(TreeModel tree, Parameter parameter, boolean includeRoot, boolean includeTips,
                                      Intent intent) {

        super("treeParameterModel");
        this.tree = tree;
        this.parameter = parameter;

        this.includeRoot = includeRoot;
        this.includeTips = includeTips;

        this.intent = intent;

        int dim = parameter.getDimension();
        int treeSize = getParameterSize();
        if (dim != treeSize) {
            parameter.setDimension(treeSize);
        }

        addModel(tree);
        addVariable(parameter);

        rootNodeNumber = tree.getRoot().getNumber();
        storedRootNodeNumber = rootNodeNumber;
    }

    public abstract int getParameterSize();

    protected Parameter getParameter() {
        return parameter;
    }

    protected boolean checkNode(Tree tree, NodeRef node) {
        assert (!tree.isRoot(node) && !doesIncludeRoot()) : "root node doesn't have a parameter value!";

        assert tree.getRoot().getNumber() == rootNodeNumber :
                "INTERNAL ERROR! node with number " + rootNodeNumber + " should be the root node.";

        assert (!tree.isExternal(node) && !includeTips) : "tip nodes do not have parameter values!";

        return true;
    }

    public void handleModelChangedEvent(Model model, Object object, int index) {
        if (model == tree) {
            handleRootMove();
        }
    }

    protected boolean doesIncludeRoot() {
        return includeRoot;
    }

    protected boolean doesIncludeTips() {
        return includeTips;
    }

    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        int nodeNumber = getNodeNumberFromParameterIndex(index);

        assert (tree.getNode(nodeNumber).getNumber() == nodeNumber);

        fireModelChanged(variable, nodeNumber);
    }

    protected void storeState() {
        storedRootNodeNumber = rootNodeNumber;
    }

    protected void restoreState() {
        rootNodeNumber = storedRootNodeNumber;
    }

    protected void acceptState() {
    }

    public abstract T getNodeValue(Tree tree, NodeRef node);

    public abstract void setNodeValue(Tree tree, NodeRef node, T value);

    protected int getNodeNumberFromParameterIndex(int parameterIndex) {
        int number = parameterIndex;
        if (!includeRoot && parameterIndex >= tree.getRoot().getNumber()) {
            number += 1;
        }
        if (!includeTips) {
            number += tree.getExternalNodeCount();
        }
        return number;
    }

    protected int getParameterIndexFromNodeNumber(int nodeNumber) {
        int index = nodeNumber;
        if (!includeRoot && nodeNumber > tree.getRoot().getNumber()) {
            index -= 1;
        }

        if (!includeTips) {
            index -= tree.getExternalNodeCount();
        }
        return index;

    }

    private void handleRootMove() {

        if (!includeRoot) {

            final int newRootNodeNumber = tree.getRoot().getNumber();

            if (rootNodeNumber > newRootNodeNumber) {

                final double oldValue = parameter.getParameterValue(newRootNodeNumber);

                final int end = Math.min(parameter.getDimension() - 1, rootNodeNumber);
                for (int i = newRootNodeNumber; i < end; i++) {
                    parameter.setParameterValue(i, parameter.getParameterValue(i + 1));
                }

                parameter.setParameterValue(end, oldValue);

            } else if (rootNodeNumber < newRootNodeNumber) {

                final int end = Math.min(parameter.getDimension() - 1, newRootNodeNumber);

                final double oldValue = parameter.getParameterValue(end);

                for (int i = end; i > rootNodeNumber; i--) {
                    parameter.setParameterValue(i, parameter.getParameterValue(i - 1));
                }

                parameter.setParameterValue(rootNodeNumber, oldValue);
            }
            rootNodeNumber = newRootNodeNumber;
        }
    }

    public TreeModel getTreeModel() {
        return tree;
    }

    public String getTraitName() {
        return parameter.getId();
    }

    public Intent getIntent() {
        return intent;
    }

    public Class getTraitClass() {
        return Double.class;
    }

    public boolean getLoggable() {
        return true;
    }

    public abstract T getTrait(Tree tree, NodeRef node);

    public abstract String getTraitString(Tree tree, NodeRef node);

}
