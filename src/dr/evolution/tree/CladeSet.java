package dr.evolution.tree;
import dr.evolution.util.TaxonList;
import dr.util.FrequencySet;
import java.util.*;
public class CladeSet extends FrequencySet<BitSet> {
    //
    // Public stuff
    //
    public CladeSet() {}
    public CladeSet(Tree tree)
    {
        this(tree, tree);
    }
    public CladeSet(Tree tree, TaxonList taxonList)
    {
        this.taxonList = taxonList;
        add(tree);
    }
    public int getCladeCount()
    {
        return size();
    }
    public String getClade(int index)
    {
        BitSet bits = get(index);
        StringBuffer buffer = new StringBuffer("{");
        boolean first = true;
        for (String taxonId : getTaxaSet(bits)) {
            if (!first) {
                buffer.append(", ");
            } else {
                first = false;
            }
            buffer.append(taxonId);
        }
        buffer.append("}");
        return buffer.toString();
    }
    private SortedSet<String> getTaxaSet(BitSet bits) {
        SortedSet<String> taxaSet = new TreeSet<String>();
        for (int i = 0; i < bits.length(); i++) {
            if (bits.get(i)) {
                taxaSet.add(taxonList.getTaxonId(i));
            }
        }
        return taxaSet;
    }
    int getCladeFrequency(int index)
    {
        return getFrequency(index);
    }
    public void add(Tree tree)
    {
        if (taxonList == null) {
            taxonList = tree;
        }
        totalTrees += 1;
        // Recurse over the tree and add all the clades (or increment their
        // frequency if already present). The root clade is not added.
        addClades(tree, tree.getRoot(), null);
    }
    private void addClades(Tree tree, NodeRef node, BitSet bits) {
        if (tree.isExternal(node)) {
            if (taxonList != null) {
                int index = taxonList.getTaxonIndex(tree.getNodeTaxon(node).getId());
                bits.set(index);
            } else {
                bits.set(node.getNumber());
            }
        } else {
            BitSet bits2 = new BitSet();
            for (int i = 0; i < tree.getChildCount(node); i++) {
                NodeRef node1 = tree.getChild(node, i);
                addClades(tree, node1, bits2);
            }
            add(bits2, 1);
            addNodeHeight(bits2, tree.getNodeHeight(node));
            if (bits != null) {
                bits.or(bits2);
            }
        }
    }
    public double getMeanNodeHeight(int i) {
        BitSet bits = get(i);
        return getTotalNodeHeight(bits) / getFrequency(i);
    }
    private double getTotalNodeHeight(BitSet bits) {
        Double tnh = totalNodeHeight.get(bits);
        if (tnh == null) return 0.0;
        return tnh;
    }
    private void addNodeHeight(BitSet bits, double height) {
        totalNodeHeight.put(bits, (getTotalNodeHeight(bits) + height));
    }
    // Generifying found that this code was buggy. Kuckily it is not used anymore.
//    /** adds all the clades in the CladeSet */
//    public void add(CladeSet cladeSet)
//    {
//        for (int i = 0, n = cladeSet.getCladeCount(); i < n; i++) {
//            add(cladeSet.getClade(i), cladeSet.getCladeFrequency(i));
//        }
//    }
    private BitSet annotate(MutableTree tree, NodeRef node, String freqAttrName) {
        BitSet b = null;
        if (tree.isExternal(node)) {
            int index;
            if (taxonList != null) {
                index = taxonList.getTaxonIndex(tree.getNodeTaxon(node).getId());
            } else {
                index = node.getNumber();
            }
            b = new BitSet(tree.getExternalNodeCount());
            b.set(index);
        } else {
            for (int i = 0; i < tree.getChildCount(node); i++) {
                NodeRef child = tree.getChild(node, i);
                BitSet b1 = annotate(tree, child, freqAttrName);
                if( i == 0 ) {
                    b = b1;
                } else {
                    b.or(b1);
                }
            }
            final int total = getFrequency(b);
            if( total >= 0 ) {
                tree.setNodeAttribute(node, freqAttrName, total / (double)totalTrees );
            }
        }
        return b;
    }
    public double annotate(MutableTree tree, String freqAttrName) {
        annotate(tree, tree.getRoot(), freqAttrName);
        double logClade = 0.0;
        for(int n = 0; n < tree.getInternalNodeCount(); ++n) {
            final double f = (Double)tree.getNodeAttribute(tree.getInternalNode(n), freqAttrName);
            logClade += Math.log(f);
        }
        return logClade;
    }
    public boolean hasClade(int index, Tree tree) {
        BitSet bits = get(index);
        NodeRef[] mrca = new NodeRef[1];
        findClade(bits, tree, tree.getRoot(), mrca);
        return (mrca[0] != null);
    }
    private int findClade(BitSet bitSet, Tree tree, NodeRef node, NodeRef[] cladeMRCA) {
        if (tree.isExternal(node)) {
            if (taxonList != null) {
                int index = taxonList.getTaxonIndex(tree.getNodeTaxon(node).getId());
                if (bitSet.get(index)) return 1;
            } else {
                if (bitSet.get(node.getNumber())) return 1;
            }
            return -1;
        } else {
            int count = 0;
            for (int i = 0; i < tree.getChildCount(node); i++) {
                NodeRef node1 = tree.getChild(node, i);
                int childCount = findClade(bitSet, tree, node1, cladeMRCA);
                if (childCount != -1 && count != -1) {
                    count += childCount;
                } else count = -1;
            }
            if (count == bitSet.cardinality()) cladeMRCA[0] = node;
            return count;
        }
    }
    //
    // Private stuff
    //
    private TaxonList taxonList = null;
    private final Map<BitSet, Double> totalNodeHeight = new HashMap<BitSet, Double>();
    private int totalTrees = 0;
}
