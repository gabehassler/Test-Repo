package dr.evolution.tree;
import dr.evolution.util.Taxon;
import dr.math.MathUtils;
The aim is to simplify code by not having to use a MutableTree with its long list of
methods. The users of SlidableTree are
1. AlloppLeggedTree, for moves within tetraploid subtrees.
2. AlloppDiploidHistory. A SimpleTree is made for testing purposes.
3. PopsIOSpeciesTreeModel. Needs to be a Tree for logging. So I
make and update a SimpleTree and delegate to that.
4. MulSpeciesTreeModel does not use it yet, but could do. Currently a MutableTree and
uses near-copy of JH's code for MCMC moves. Same issue as PopsIOSpeciesTreeModel if use
SlidableTree.
It uses NodeRefs cause that's what SimpleTree uses. This means that clients have
to implement get/setNumber when they have no use for node numbers.
public interface SlidableTree {
    NodeRef getSlidableRoot();
    int getSlidableNodeCount();
    Taxon getSlidableNodeTaxon(NodeRef node);
    double getSlidableNodeHeight(NodeRef node);
    void setSlidableNodeHeight(NodeRef node, double height);
    boolean isExternalSlidable(NodeRef node);
    NodeRef getSlidableChild(NodeRef node, int i);
    void replaceSlidableChildren(NodeRef node, NodeRef lft, NodeRef rgt);
    void replaceSlidableRoot(NodeRef root);
    public class Utils {
        static public NodeRef[] mnlCanonical(SlidableTree tree) {
            final int count = tree.getSlidableNodeCount();
            NodeRef[] order = new NodeRef[count];
            mnlCanonicalSub(tree, tree.getSlidableRoot(), 0, order);
            return order;
        }
        static public void mnlReconstruct(SlidableTree tree, NodeRef[] order) {
            final NodeRef root = mnlReconstructSub(tree, 0, (order.length - 1) / 2, order);
            tree.replaceSlidableRoot(root);
        }
        static private int mnlCanonicalSub(SlidableTree tree, NodeRef node,
                                           int nextloc, NodeRef[] order) {
            if( tree.isExternalSlidable(node) ) {
                order[nextloc] = node;     assert (nextloc & 0x1) == 0;
                nextloc++;
                return nextloc;
            }
            final boolean swap = MathUtils.nextBoolean();
            nextloc = mnlCanonicalSub(tree, tree.getSlidableChild(node, swap ? 1 : 0), nextloc, order);
            order[nextloc] = node;   assert (nextloc & 0x1) == 1;
            nextloc++;
            nextloc = mnlCanonicalSub(tree, tree.getSlidableChild(node, swap ? 0 : 1), nextloc, order);
            return nextloc;
        }
        static private NodeRef mnlReconstructSub(SlidableTree tree, int from, int to,
                                                 NodeRef[] order) {
            if (from == to) {
                return order[2*from];
            }
            int rootIndex = highestNode(tree, order, from, to);
            NodeRef root = order[2 * rootIndex + 1];
            NodeRef lft = mnlReconstructSub(tree, from, rootIndex, order);
            NodeRef rgt = mnlReconstructSub(tree, rootIndex+1, to, order);
            tree.replaceSlidableChildren(root, lft, rgt);
            return root;
        }
        static private int highestNode(SlidableTree tree, NodeRef[] order, int from, int to) {
            int rootIndex = -1;
            double maxh = -1.0;
            for (int i = from; i < to; ++i) {
                final double h = tree.getSlidableNodeHeight(order[2 * i + 1]);
                if (h > maxh) {
                    maxh = h;
                    rootIndex = i;
                }
            }
            return rootIndex;
        }
    }
}
