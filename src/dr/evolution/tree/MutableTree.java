package dr.evolution.tree;
import dr.evolution.util.MutableTaxonList;
import dr.math.MathUtils;
public interface MutableTree extends Tree, MutableTaxonList {
    public class InvalidTreeException extends Exception {
		private static final long serialVersionUID = 1955744780140327882L;
		public InvalidTreeException(String message) { super(message); }
	}
    // return true if tree already in edit mode
    boolean beginTreeEdit();
    void endTreeEdit();
	void addChild(NodeRef parent, NodeRef child);
	void removeChild(NodeRef parent, NodeRef child);
    void replaceChild(NodeRef node, NodeRef child, NodeRef newChild);
	void setRoot(NodeRef root);
	void setNodeHeight(NodeRef node, double height);
	void setNodeRate(NodeRef node, double height);
	void setBranchLength(NodeRef node, double length);
	public void setNodeAttribute(NodeRef node, String name, Object value);
	void addMutableTreeListener(MutableTreeListener listener);
	public class Utils {
		public static int order(MutableTree tree, NodeRef node) {
			if (tree.isExternal(node)) {
				return node.getNumber();
			} else {
				NodeRef child1 = tree.getChild(node, 0);
				NodeRef child2 = tree.getChild(node, 1);
				int num1 = order(tree, child1);
				int num2 = order(tree, child2);
				if (num1 > num2) {
					// swap child order
					tree.removeChild(node, child1);
					tree.removeChild(node, child2);
					tree.addChild(node, child2);
					tree.addChild(node, child1);
				}
				return Math.min(num1, num2);
			}
		}
		public static void scaleNodeHeights(MutableTree tree, double scale) {
			for (int i = 0; i < tree.getExternalNodeCount(); i++) {
				NodeRef node = tree.getExternalNode(i);
				tree.setNodeHeight(node, tree.getNodeHeight(node)*scale);
			}
			for (int i = 0; i < tree.getInternalNodeCount(); i++) {
				NodeRef node = tree.getInternalNode(i);
				tree.setNodeHeight(node, tree.getNodeHeight(node)*scale);
			}
		}
		public static void correctHeightsForTips(MutableTree tree) {
			correctHeightsForTips(tree, tree.getRoot());
		}
        private static void correctHeightsForTips(MutableTree tree, NodeRef node) {
            if( !tree.isExternal(node) ) {
                // pre-order recursion
                for(int i = 0; i < tree.getChildCount(node); i++) {
                    correctHeightsForTips(tree, tree.getChild(node, i));
                }
            }
            if( !tree.isRoot(node) ) {
                final double parentHeight = tree.getNodeHeight(tree.getParent(node));
                if( parentHeight <= tree.getNodeHeight(node) ) {
                    // set the parent height to be slightly above this node's height
                    // picks
                    double height = tree.getNodeHeight(node);
                    height += tree.getNodeHeight(tree.getRoot()) * (MathUtils.nextDouble() * 0.001);                  
                    tree.setNodeHeight(tree.getParent(node), height);
                }
            }
        }
	}
}
