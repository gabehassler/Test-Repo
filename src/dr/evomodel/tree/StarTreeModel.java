package dr.evomodel.tree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.inference.model.Bounds;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
public class StarTreeModel extends TreeModel {
    public StarTreeModel(String id, Tree tree) {
        super(id, tree);
        maxTipHeightKnown = false;
        rootHeightParameter = null;
        sharedRoot = null;
    }
    @Override
    public void setupHeightBounds() {
        if (heightBoundsSetup) {
            throw new IllegalArgumentException("Node height bounds set up twice");
        }
        for (int i = 0; i < getNodeCount(); i++) {
            setupHeightBounds((Node) getNode(i));
        }
        heightBoundsSetup = true;
    }
//    private void fixInternalNodeHeightToRoot() {
//        double rootHeight = getNodeHeight(getRoot());
//        for (int i = 0; i < getInternalNodeCount(); ++i) {
//            Node node = (Node) getInternalNode(i);
//            if (node != getRoot()) {
//                node.heightParameter.setParameterValueQuietly(0, rootHeight);
//            }
//        }
//        fixedInternalNodes = true;
//    }
//    public void setRootHeightParameter(Parameter p) {
//        addVariable(p);
//        rootHeightParameter = p;
//    }
    private void setupHeightBounds(Node node) {
        node.heightParameter.addBounds(new StarTreeNodeHeightBounds(node.heightParameter));
    }
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        if (model == sharedRoot) {
            if (object instanceof TreeChangedEvent) {
                TreeChangedEvent event = (TreeChangedEvent) object;
                if (event.getParameter() == sharedRoot.getRootHeightParameter()) {
                    pushTreeChangedEvent();
                }
            }
        }
    }
    public void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        if (variable == rootHeightParameter) {
            pushTreeChangedEvent();
        } else {
            final Node node = getNodeOfParameter((Parameter) variable);
            if (node.isRoot()) {
                pushTreeChangedEvent();
            } else if (node.isExternal()) {
                maxTipHeightKnown = false;
                pushTreeChangedEvent();
            } else {
                throw new IllegalArgumentException("Can not sample internal nodes in StarTree");
            }
            super.handleVariableChangedEvent(variable, index, type);
        }
    }
    public double getNodeHeight(final NodeRef nr) {
        Node node = (Node) nr;
        if (!node.isExternal()) {
            if (rootHeightParameter != null) {
                return rootHeightParameter.getParameterValue(0);
            } else if (sharedRoot != null) {
                return sharedRoot.getNodeHeight(sharedRoot.getRoot());
            } else {
                return ((Node) getRoot()).getHeight();
            }
        }
        return node.getHeight();
    }
    public void setSharedRootHeightParameter(TreeModel sharedRoot) {
        this.sharedRoot = sharedRoot;
        addModel(sharedRoot);
    }
    private class StarTreeNodeHeightBounds implements Bounds<Double> {
        public StarTreeNodeHeightBounds(Parameter parameter) {
            nodeHeightParameter = parameter;
        }
        public Double getUpperLimit(int i) {
            Node node = getNodeOfParameter(nodeHeightParameter);
            if (node.isRoot()) {
                return Double.POSITIVE_INFINITY;
            } else {
                return getNodeHeight(getRoot());
            }
        }
        public Double getLowerLimit(int i) {
            Node node = getNodeOfParameter(nodeHeightParameter);
            if (node.isExternal()) {
                return 0.0;
            } else {
                return getMaxTipHeight();
            }
        }
        public int getBoundsDimension() {
            return 1;
        }
        private Parameter nodeHeightParameter = null;
    }
    public void storeState() {
        super.storeState();
        savedMaxTipHeight = maxTipHeight;
        savedMaxTipHeightKnown = maxTipHeightKnown;
    }
    public void restoreState() {
        super.restoreState();
        maxTipHeight = savedMaxTipHeight;
        maxTipHeightKnown = savedMaxTipHeightKnown;
    }
    private double getMaxTipHeight() {
        if (!maxTipHeightKnown) {
            maxTipHeight = getNodeHeight(getExternalNode(0));
            for (int i = 1; i < getExternalNodeCount(); ++i) {
                double height = getNodeHeight(getExternalNode(i));
                if (height > maxTipHeight) {
                    maxTipHeight = height;
                }
            }
            maxTipHeightKnown = true;
        }
        return maxTipHeight;
    }
    private boolean maxTipHeightKnown = false;
    private boolean savedMaxTipHeightKnown;
    private double maxTipHeight = 5;
    private double savedMaxTipHeight;
    private Parameter rootHeightParameter = null;
    private TreeModel sharedRoot = null;
}
