package dr.evomodel.tree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.tree.TreeDoubleTraitProvider;
import dr.evolution.tree.TreeTrait;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
public class TreeParameterModel extends AbstractModel implements TreeTrait<Double>, TreeDoubleTraitProvider {
    protected final TreeModel tree;
    // The tree parameter;
    private final Parameter parameter;
    // the index of the root node.
    private int rootNodeNumber;
    private int storedRootNodeNumber;
    private boolean includeRoot = false;
    private Intent intent;
    public TreeParameterModel(TreeModel tree, Parameter parameter, boolean includeRoot) {
        this(tree, parameter, includeRoot, Intent.NODE);
    }
    public TreeParameterModel(TreeModel tree, Parameter parameter, boolean includeRoot, Intent intent) {
        super("treeParameterModel");
        this.tree = tree;
        this.parameter = parameter;
        this.includeRoot = includeRoot;
        this.intent = intent;
        int dim = parameter.getDimension();
        int treeSize = getParameterSize();
        if (dim != treeSize) {
//            System.err.println("WARNING: setting dimension of parameter to match tree branch count ("
//                    + dim + " != " + treeSize + ")"); // http://code.google.com/p/beast-mcmc/issues/detail?id=385
            parameter.setDimension(treeSize);
        }
        addModel(tree);
        addVariable(parameter);
        rootNodeNumber = tree.getRoot().getNumber();
        storedRootNodeNumber = rootNodeNumber;
    }
    public int getParameterSize() {
        int treeSize = tree.getNodeCount();
        if (!includeRoot) {
            treeSize -= 1;
        }
        return treeSize;
    }
    public void handleModelChangedEvent(Model model, Object object, int index) {
        if (model == tree) {
            handleRootMove();
        }
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
    public double getNodeDoubleValue(Tree tree, NodeRef node) {
        return getNodeValue(tree, node);
    }
    public double getNodeValue(Tree tree, NodeRef node) {
        assert (!tree.isRoot(node) || includeRoot) : "root node doesn't have a parameter value!";
        assert tree.getRoot().getNumber() == rootNodeNumber :
                "INTERNAL ERROR! node with number " + rootNodeNumber + " should be the root node.";
        int nodeNumber = node.getNumber();
        int index = getParameterIndexFromNodeNumber(nodeNumber);
        return parameter.getParameterValue(index);
    }
    public void setNodeValue(Tree tree, NodeRef node, double value) {
        assert (!tree.isRoot(node) && !includeRoot) : "root node doesn't have a parameter value!";
        assert tree.getRoot().getNumber() == rootNodeNumber :
                "INTERNAL ERROR! node with number " + rootNodeNumber + " should be the root node.";
        int nodeNumber = node.getNumber();
        int index = getParameterIndexFromNodeNumber(nodeNumber);
        parameter.setParameterValue(index, value);
    }
    public int getNodeNumberFromParameterIndex(int parameterIndex) {
        if (!includeRoot && parameterIndex >= tree.getRoot().getNumber()) return parameterIndex + 1;
        return parameterIndex;
    }
    public int getParameterIndexFromNodeNumber(int nodeNumber) {
        if (!includeRoot && nodeNumber > tree.getRoot().getNumber()) return nodeNumber - 1;
        return nodeNumber;
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
    public String[] getNodeAttributeLabel() {
        return new String[]{};
    }
    public String[] getAttributeForNode(Tree tree, NodeRef node) {
        return new String[]{};
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
    public Double getTrait(Tree tree, NodeRef node) {
        return getNodeValue(tree, node);
    }
    public String getTraitString(Tree tree, NodeRef node) {
        return Double.toString(getNodeValue(tree, node));
    }
}
