
package dr.evomodel.tree;

import dr.evolution.tree.Clade;
import dr.evolution.tree.ImportanceDistribution;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.inference.model.Likelihood;
import dr.inference.prior.Prior;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractCladeImportanceDistribution implements
        ImportanceDistribution {

    public AbstractCladeImportanceDistribution() {
        // TODO Auto-generated constructor stub
    }

    protected Clade getNonComplementaryClades(Tree tree, NodeRef node,
                                              List<Clade> parentClades, List<Clade> childClade,
                                              HashMap<String, Integer> taxonMap) {

        // create a new bit set for this clade
        BitSet bits = new BitSet();
        Clade c = null;

        // check if the node is external
        if (tree.isExternal(node)) {

            // if so, the only taxon in the clade is I
            // int index = node.getNumber();
            String taxon = tree.getTaxon(node.getNumber()).getId();
            int index = taxonMap.get(taxon);
            bits.set(index);

            c = new Clade(bits, tree.getNodeHeight(node));

        } else {
            // otherwise, call all children and add its taxon together to one
            // clade

            NodeRef childNode = tree.getChild(node, 0);
            // add just my first child to the list
            // the second child is complementary to the first
            Clade leftChild = getNonComplementaryClades(tree, childNode,
                    parentClades, childClade, taxonMap);
            bits.or(leftChild.getBits());

            childNode = tree.getChild(node, 1);
            // add just my first child to the list
            // the second child is complementary to the first
            Clade rightChild = getNonComplementaryClades(tree, childNode,
                    parentClades, childClade, taxonMap);
            bits.or(rightChild.getBits());

            c = new Clade(bits, tree.getNodeHeight(node));

            if (leftChild.getSize() >= 2) {
                parentClades.add(c);
                childClade.add(leftChild);
            } else if (rightChild.getSize() >= 2) {
                parentClades.add(c);
                childClade.add(rightChild);
            }
        }

        return c;
    }

    protected Clade getNonComplementaryClades(Tree tree, NodeRef node,
                                              List<Clade> parentClades, List<Clade> childClade) {

        // create a new bit set for this clade
        BitSet bits = new BitSet();
        Clade c = null;

        // check if the node is external
        if (tree.isExternal(node)) {

            // if so, the only taxon in the clade is I
            int index = node.getNumber();
            bits.set(index);

            c = new Clade(bits, tree.getNodeHeight(node));

        } else {
            // otherwise, call all children and add its taxon together to one
            // clade

            NodeRef childNode = tree.getChild(node, 0);
            // add just my first child to the list
            // the second child is complementary to the first
            Clade leftChild = getNonComplementaryClades(tree, childNode,
                    parentClades, childClade);
            bits.or(leftChild.getBits());

            childNode = tree.getChild(node, 1);
            // add just my first child to the list
            // the second child is complementary to the first
            Clade rightChild = getNonComplementaryClades(tree, childNode,
                    parentClades, childClade);
            bits.or(rightChild.getBits());

            c = new Clade(bits, tree.getNodeHeight(node));

            if (leftChild.getSize() >= 2) {
                parentClades.add(c);
                childClade.add(leftChild);
            } else if (rightChild.getSize() >= 2) {
                parentClades.add(c);
                childClade.add(rightChild);
            }
        }

        return c;
    }

    protected Clade getClades(Tree tree, NodeRef node,
                              List<Clade> parentClades, List<Clade> childClade) {

        // create a new bit set for this clade
        BitSet bits = new BitSet();
        Clade c = null;

        // check if the node is external
        if (tree.isExternal(node)) {

            // if so, the only taxon in the clade is I
            int index = node.getNumber();
            bits.set(index);

            c = new Clade(bits, tree.getNodeHeight(node));

        } else {
            // otherwise, call all children and add its taxon together to one
            // clade

            NodeRef childNode = tree.getChild(node, 0);
            // add just my first child to the list
            // the second child is complementary to the first
            Clade leftChild = getClades(tree, childNode, parentClades,
                    childClade);
            bits.or(leftChild.getBits());

            childNode = tree.getChild(node, 1);
            // add just my first child to the list
            // the second child is complementary to the first
            Clade rightChild = getClades(tree, childNode, parentClades,
                    childClade);
            bits.or(rightChild.getBits());

            c = new Clade(bits, tree.getNodeHeight(node));

            if (leftChild.getSize() >= 2) {
                parentClades.add(c);
                childClade.add(leftChild);
            }
            if (rightChild.getSize() >= 2) {
                parentClades.add(c);
                childClade.add(rightChild);
            }
        }

        return c;
    }

    protected Clade getClades(Tree tree, NodeRef node,
                              List<Clade> parentClades, List<Clade> childClade,
                              HashMap<String, Integer> taxonMap) {

        // create a new bit set for this clade
        BitSet bits = new BitSet();
        Clade c = null;

        // check if the node is external
        if (tree.isExternal(node)) {

            // if so, the only taxon in the clade is I
            // int index = node.getNumber();
            String taxon = tree.getTaxon(node.getNumber()).getId();
            int index = taxonMap.get(taxon);
            bits.set(index);

            c = new Clade(bits, tree.getNodeHeight(node));

        } else {
            // otherwise, call all children and add its taxon together to one
            // clade

            NodeRef childNode = tree.getChild(node, 0);
            // add just my first child to the list
            // the second child is complementary to the first
            Clade leftChild = getClades(tree, childNode, parentClades,
                    childClade, taxonMap);
            bits.or(leftChild.getBits());

            childNode = tree.getChild(node, 1);
            // add just my first child to the list
            // the second child is complementary to the first
            Clade rightChild = getClades(tree, childNode, parentClades,
                    childClade, taxonMap);
            bits.or(rightChild.getBits());

            c = new Clade(bits, tree.getNodeHeight(node));

            if (leftChild.getSize() >= 2) {
                parentClades.add(c);
                childClade.add(leftChild);
            }
            if (rightChild.getSize() >= 2) {
                parentClades.add(c);
                childClade.add(rightChild);
            }
        }

        return c;
    }

    public void getClades(Tree tree, NodeRef node, List<Clade> clades,
                          BitSet bits) {

        // create a new bit set for this clade
        BitSet bits2 = new BitSet();

        // check if the node is external
        if (tree.isExternal(node)) {

            // if so, the only taxon in the clade is I
            int index = node.getNumber();
            bits2.set(index);

        } else {

            // otherwise, call all children and add its taxon together to one
            // clade
            for (int i = 0; i < tree.getChildCount(node); i++) {
                NodeRef child = tree.getChild(node, i);
                getClades(tree, child, clades, bits2);
            }
            // add my bit set to the list
            clades.add(new Clade(bits2, tree.getNodeHeight(node)));
        }

        // add my bit set to the bit set I was given
        // this is needed for adding all children clades together
        if (bits != null) {
            bits.or(bits2);
        }
    }

    public void getCladesHeights(Tree tree, NodeRef node, List<Double> heights) {

        // check if the node is external
        if (tree.isExternal(node)) {

            // if so, do nothing

        } else {

            // otherwise, call all children and add its taxon together to one
            // clade
            for (int i = 0; i < tree.getChildCount(node); i++) {
                NodeRef child = tree.getChild(node, i);
                getCladesHeights(tree, child, heights);
            }
            // add my bit set to the list
            heights.add(tree.getNodeHeight(node));
        }
    }

    public void getRelativeCladesHeights(Tree tree, NodeRef node,
                                         List<Double> heights) {

        // check if the node is external
        if (tree.isExternal(node)) {

            // if so, do nothing

        } else {

            // otherwise, call all children and add its taxon together to one
            // clade
            for (int i = 0; i < tree.getChildCount(node); i++) {
                NodeRef child = tree.getChild(node, i);
                getRelativeCladesHeights(tree, child, heights);
            }
            // add my bit set to the list
            if (node != tree.getRoot()) {
                NodeRef parent = tree.getParent(node);
                heights.add(tree.getNodeHeight(node)
                        / tree.getNodeHeight(parent));
            } else {
                heights.add(1.0);
            }
        }
    }

    protected Clade getClade(Tree tree, NodeRef node) {

        // create a new bit set for this clade
        BitSet bits = new BitSet();

        // check if the node is external
        if (tree.isExternal(node)) {

            // if so, the only taxon in the clade am I
            int index = node.getNumber();
            bits.set(index);

        } else {

            // otherwise, call all children and add its taxon together to one
            // clade
            for (int i = 0; i < tree.getChildCount(node); i++) {
                NodeRef child = tree.getChild(node, i);
                Clade c = getClade(tree, child);
                bits.or(c.getBits());
            }
        }

        Clade c = new Clade(bits, tree.getNodeHeight(node));

        return c;
    }

    protected Clade getParentClade(List<Clade> clades, Clade child) {
        Clade parent = null;
        BitSet childBits = child.getBits();
        int parentSize = Integer.MAX_VALUE;

        // look in all clades of the list which contains the child and has the
        // minimum cardinality (least taxa) -> that's the parent :-)
        for (int i = 0; i < clades.size(); i++) {
            Clade tmp = clades.get(i);
            if (!child.equals(tmp) && containsClade(tmp.getBits(), childBits)) {
                if (parent == null || parentSize > tmp.getSize()) {
                    parent = tmp;
                    parentSize = parent.getSize();
                }
            }
        }
        // if there isn't a parent, then you probably asked for the whole tree
        if (parent == null) {
            parent = child;
        }

        return parent;
    }

    protected boolean containsClade(Clade i, Clade j) {
        return containsClade(i.getBits(), j.getBits());
    }

    protected boolean containsClade(BitSet i, BitSet j) {
        BitSet tmpI = (BitSet) i.clone();

        // just set the bits which are either in j but not in i or in i but not
        // in j
        tmpI.xor(j);
        int numberOfBitsInEither = tmpI.cardinality();
        // which bits are just in i
        tmpI.and(i);
        int numberIfBitJustInContaining = tmpI.cardinality();

        // if the number of bits just in i is equal to the number of bits just
        // in one of i or j
        // then i contains j
        return numberOfBitsInEither == numberIfBitJustInContaining;
    }

    public abstract void addTree(Tree tree);

    public abstract double getTreeProbability(Tree tree);

    public abstract double splitClade(Clade parent, Clade[] children);

    public abstract double setNodeHeights(TreeModel tree,
                                          Likelihood likelihood, Prior prior);

    public abstract double getChanceForNodeHeights(TreeModel tree,
                                                   Likelihood likelihood, Prior prior);

}
