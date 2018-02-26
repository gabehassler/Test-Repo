
package dr.evolution.tree;

import dr.evolution.util.MutableTaxonListListener;
import dr.evolution.util.Taxon;
import dr.util.Attributable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FlexibleTree implements MutableTree {

    public FlexibleTree() {
        root = null;
    }

    public FlexibleTree(Tree tree) {
        this(tree, false);
    }

    public FlexibleTree(Tree tree, boolean copyAttributes) {

        setUnits(tree.getUnits());

        root = new FlexibleNode(tree, tree.getRoot(), copyAttributes);

        nodeCount = tree.getNodeCount();
        internalNodeCount = tree.getInternalNodeCount();
        externalNodeCount = tree.getExternalNodeCount();


        nodes = new FlexibleNode[nodeCount];

        FlexibleNode node = root;
        do {
            node = (FlexibleNode) Tree.Utils.postorderSuccessor(this, node);
            if ((node.getNumber() >= externalNodeCount && node.isExternal()) ||
                    (node.getNumber() < externalNodeCount && !node.isExternal())) {
                throw new RuntimeException("Error cloning tree: node numbers are incompatible");
            }
            nodes[node.getNumber()] = node;
        } while (node != root);

        heightsKnown = tree.hasNodeHeights();
        lengthsKnown = tree.hasBranchLengths();
    }

    public FlexibleTree(FlexibleNode root) {
        this(root, true, true, null);
    }

    public FlexibleTree(FlexibleNode root, boolean heightsKnown, boolean lengthsKnown) {

        this(root, heightsKnown, lengthsKnown, null);
    }

    public FlexibleTree(FlexibleNode root, Map<Taxon, Integer> taxonNumberMap) {
        this(root, true, true, taxonNumberMap);
    }

    public FlexibleTree(FlexibleNode root, boolean heightsKnown, boolean lengthsKnown, Map<Taxon, Integer> taxonNumberMap) {

        adoptNodes(root, taxonNumberMap);

        this.heightsKnown = heightsKnown;
        this.lengthsKnown = lengthsKnown;

    }

    public Tree getCopy() {
        return new FlexibleTree(this);
    }

    public void adoptTreeModelOrdering() {
        resolveTree();
        int i = 0;
        int j = externalNodeCount;

        FlexibleNode node = (FlexibleNode) getRoot();

        do {
            node = (FlexibleNode) Tree.Utils.postorderSuccessor(this, node);

            if (node.isExternal()) {
                node.setNumber(i);
                nodes[i] = node;
                i++;
            } else {
                node.setNumber(j);
                nodes[j] = node;
                j++;
            }
        } while (node != root);
    }

    protected void adoptNodes(FlexibleNode node, Map<Taxon, Integer> taxonNumberMap) {

        if (inEdit) throw new RuntimeException("Mustn't be in an edit transaction to call this method!");

        internalNodeCount = 0;
        externalNodeCount = 0;

        root = node;

        do {
            node = (FlexibleNode) Tree.Utils.postorderSuccessor(this, node);
            if (node.isExternal()) {
                externalNodeCount++;
            } else
                internalNodeCount++;

        } while (node != root);

        nodeCount = internalNodeCount + externalNodeCount;
        //System.out.println("internal count = " + internalNodeCount);
        //System.out.println("external count = " + externalNodeCount);

        nodes = new FlexibleNode[nodeCount];

        node = root;
        int i = 0;
        int j = externalNodeCount;

        do {
            node = (FlexibleNode) Tree.Utils.postorderSuccessor(this, node);
            //System.out.print("node = " + node.getId() + " ");
            if (node.isExternal()) {
                if (taxonNumberMap != null && taxonNumberMap.size() > 0) {
                    i = taxonNumberMap.get(node.getTaxon());
                }

                node.setNumber(i);
                //System.out.println("  leaf number " + i);
                nodes[i] = node;

                if (taxonNumberMap == null || taxonNumberMap.size() == 0) {
                    i++;
                }
            } else {
                node.setNumber(j);
                //System.out.println("  ancestor number " + j);
                nodes[j] = node;
                j++;
            }
        } while (node != root);
    }

    public final Type getUnits() {
        return units;
    }

    public final void setUnits(Type units) {
        this.units = units;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public boolean hasNodeHeights() {
        return heightsKnown;
    }

    public double getNodeHeight(NodeRef node) {
            if (!heightsKnown) {
                calculateNodeHeights();
            }
            return ((FlexibleNode) node).getHeight();
    }

    public boolean hasBranchLengths() {
        return lengthsKnown;
    }

    public double getBranchLength(NodeRef node) {
            if (!lengthsKnown) {
                calculateBranchLengths();
            }
            return ((FlexibleNode) node).getLength();
    }

    public double getNodeRate(NodeRef node) {

        Object rateAttr = getNodeAttribute(node, "rate");

        if (rateAttr != null) {
            if (rateAttr instanceof Number) return (Double) rateAttr;
            if (rateAttr instanceof String) return Double.parseDouble((String) rateAttr);
        }

        return ((FlexibleNode) node).getRate();
    }

    public Taxon getNodeTaxon(NodeRef node) {
        return ((FlexibleNode) node).getTaxon();
    }

    public void setNodeTaxon(NodeRef node, Taxon taxon) {
        ((FlexibleNode) node).setTaxon(taxon);
    }

    public int getChildCount(NodeRef node) {
        return ((FlexibleNode) node).getChildCount();
    }

    public boolean isExternal(NodeRef node) {
        return ((FlexibleNode) node).getChildCount() == 0;
    }

    public boolean isRoot(NodeRef node) {
        return (node == root);
    }

    public NodeRef getChild(NodeRef node, int i) {
        return ((FlexibleNode) node).getChild(i);
    }

    public NodeRef getParent(NodeRef node) {
        return ((FlexibleNode) node).getParent();
    }


    public final NodeRef getExternalNode(int i) {
        return nodes[i];
    }

    public final NodeRef getInternalNode(int i) {
        return nodes[i + externalNodeCount];
    }

    public final NodeRef getNode(int i) {
        return nodes[i];
    }

    public final int getExternalNodeCount() {
        return externalNodeCount;
    }

    public final int getInternalNodeCount() {
        return internalNodeCount;
    }

    public final NodeRef getRoot() {
        return root;
    }

    public final void setRoot(NodeRef r) {

        if (!inEdit) throw new RuntimeException("Must be in edit transaction to call this method!");

        if (!(r instanceof FlexibleNode)) {
            throw new IllegalArgumentException();
        }
        root = (FlexibleNode) r;
    }


    public final double getRootHeight() {
        return getNodeHeight(root);
    }

    public final void setRootHeight(double height) {
        setNodeHeight(root, height);

        fireTreeChanged();
    }


    public void addChild(NodeRef p, NodeRef c) {
        if (!inEdit) throw new RuntimeException("Must be in edit transaction to call this method!");
        FlexibleNode parent = (FlexibleNode) p;
        FlexibleNode child = (FlexibleNode) c;
        if (parent.hasChild(child)) throw new IllegalArgumentException("Child already existists in parent");

        parent.addChild(child);
    }

    public void removeChild(NodeRef p, NodeRef c) {

        if (!inEdit) throw new RuntimeException("Must be in edit transaction to call this method!");

        FlexibleNode parent = (FlexibleNode) p;
        FlexibleNode child = (FlexibleNode) c;

        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChild(i) == child) {
                parent.removeChild(i);
                return;
            }
        }
    }

    public void replaceChild(NodeRef node, NodeRef child, NodeRef newChild) {
        throw new RuntimeException("Unimplemented");
    }

    public boolean beginTreeEdit() {
        boolean r = inEdit;
        inEdit = true;
        return r;
    }

    public void endTreeEdit() {
        inEdit = false;

        fireTreeChanged();
    }

    public void setNodeHeight(NodeRef n, double height) {
        if (!heightsKnown) {
            calculateNodeHeights();
        }
        FlexibleNode node = (FlexibleNode) n;
        node.setHeight(height);

        lengthsKnown = false;

        fireTreeChanged();
    }

    public void setBranchLength(NodeRef n, double length) {
        if (!lengthsKnown) {
            calculateBranchLengths();
        }

        FlexibleNode node = (FlexibleNode) n;
        node.setLength(length);

        heightsKnown = false;

        fireTreeChanged();
    }

    public void setHeightsKnown(boolean heightsKnown) {
        this.heightsKnown = heightsKnown;
    }

    public void setLengthsKnown(boolean lengthsKnown) {
        this.lengthsKnown = lengthsKnown;
    }

    public void setNodeRate(NodeRef n, double rate) {
        FlexibleNode node = (FlexibleNode) n;
        node.setRate(rate);

        fireTreeChanged();
    }

    protected void calculateNodeHeights() {

        if (!lengthsKnown) {
            throw new IllegalArgumentException("Branch lengths not known");
        }

        nodeLengthsToHeights((FlexibleNode) getRoot(), 0.0);

        double maxHeight = 0.0;
        FlexibleNode node;
        for (int i = 0; i < getExternalNodeCount(); i++) {
            node = (FlexibleNode) getExternalNode(i);
            if (node.getHeight() > maxHeight) {
                maxHeight = node.getHeight();
            }
        }

        for (int i = 0; i < getNodeCount(); i++) {
            node = (FlexibleNode) getNode(i);
            node.setHeight(maxHeight - node.getHeight());
        }

        heightsKnown = true;
    }

    private void nodeLengthsToHeights(FlexibleNode node, double height) {

        double newHeight = height;

        if (node.getLength() > 0.0) {
            newHeight += node.getLength();
        }

        node.setHeight(newHeight);

        for (int i = 0; i < node.getChildCount(); i++) {
            nodeLengthsToHeights(node.getChild(i), newHeight);
        }
    }

    protected void calculateBranchLengths() {

        nodeHeightsToLengths((FlexibleNode) getRoot(), getRootHeight());

        lengthsKnown = true;
    }

    private void nodeHeightsToLengths(FlexibleNode node, double height) {

        node.setLength(height - node.getHeight());

        for (int i = 0; i < node.getChildCount(); i++) {
            nodeHeightsToLengths(node.getChild(i), node.getHeight());
        }

    }

    public void changeRoot(NodeRef node, double height) {
        FlexibleNode node1 = (FlexibleNode) node;
        FlexibleNode parent = node1.getParent();

        double l1 = height - getNodeHeight(node);
        if (l1 < 0.0) {
            throw new IllegalArgumentException("New root height less than the node's height");
        }

        double l2 = getNodeHeight(parent) - height;
        if (l2 < 0.0) {
            throw new IllegalArgumentException("New root height above the node's parent's height");
        }

        changeRoot(node, l1, l2);
    }

    public void changeRoot(NodeRef node, double l1, double l2) {

        FlexibleNode node1 = (FlexibleNode) node;
        FlexibleNode parent = node1.getParent();
        if (parent == null || parent == root) {
            // the node is already the root so nothing to do...
            return;
        }

        beginTreeEdit();

        if (!lengthsKnown) {
            calculateBranchLengths();
        }

        FlexibleNode parent2 = parent.getParent();

        swapParentNode(parent, parent2, null);

        // the root is now free so use it as the root again
        parent.removeChild(node1);
        root.addChild(node1);
        root.addChild(parent);

        node1.setLength(l1);
        parent.setLength(l2);

        heightsKnown = false;
        String t = toString();

        endTreeEdit();
    }

    private void swapParentNode(FlexibleNode node, FlexibleNode parent, FlexibleNode child) {

        if (parent != null) {
            FlexibleNode parent2 = parent.getParent();

            swapParentNode(parent, parent2, node);

            if (child != null) {
                node.removeChild(child);
                child.addChild(node);
                node.setLength(child.getLength());
            }

        } else {
            // First remove child from the root
            node.removeChild(child);
            if (node.getChildCount() > 1) {
                throw new IllegalArgumentException("Trees must be binary");
            }

            FlexibleNode tmp = node.getChild(0);
            node.removeChild(tmp);
            child.addChild(tmp);
            tmp.setLength(tmp.getLength() + child.getLength());
        }

    }

    public void resolveTree() {

        for (int i = 0; i < getInternalNodeCount(); i++) {

            FlexibleNode node = ((FlexibleNode) getInternalNode(i));

            if (node.getChildCount() > 2) {
                resolveNode(node);

            }
        }

        adoptNodes(root, null);

        fireTreeChanged();
    }

    private void resolveNode(FlexibleNode node) {

        while (node.getChildCount() > 2) {
            FlexibleNode node0 = node.getChild(0);
            FlexibleNode node1 = node.getChild(1);

            node.removeChild(node0);
            node.removeChild(node1);

            FlexibleNode node2 = node.getShallowCopy();
            node2.addChild(node0);
            node2.addChild(node1);
            node2.setLength(0.0);

            node.addChild(node2);
        }
    }

    public void setNodeAttribute(NodeRef node, String name, Object value) {
        ((FlexibleNode) node).setAttribute(name, value);

        fireTreeChanged();
    }

    public Object getNodeAttribute(NodeRef node, String name) {
        return ((FlexibleNode) node).getAttribute(name);
    }

    public Iterator getNodeAttributeNames(NodeRef node) {
        return ((FlexibleNode) node).getAttributeNames();
    }

    // **************************************************************
    // TaxonList IMPLEMENTATION
    // **************************************************************

    public int getTaxonCount() {
        return getExternalNodeCount();
    }

    public Taxon getTaxon(int taxonIndex) {
        return ((FlexibleNode) getExternalNode(taxonIndex)).getTaxon();
    }

    public String getTaxonId(int taxonIndex) {
        Taxon taxon = getTaxon(taxonIndex);
        if (taxon != null)
            return taxon.getId();
        else
            return ((FlexibleNode) getExternalNode(taxonIndex)).getId();
    }

    public int getTaxonIndex(String id) {
        for (int i = 0, n = getTaxonCount(); i < n; i++) {
            if (getTaxonId(i).equals(id)) return i;
        }
        return -1;
    }

    public int getTaxonIndex(Taxon taxon) {
        for (int i = 0, n = getTaxonCount(); i < n; i++) {
            if (getTaxon(i) == taxon) return i;
        }
        return -1;
    }

    public List<Taxon> asList() {
        List<Taxon> taxa = new ArrayList<Taxon>();
        for (int i = 0, n = getTaxonCount(); i < n; i++) {
            taxa.add(getTaxon(i));
        }
        return taxa;
    }

    public Iterator<Taxon> iterator() {
        return new Iterator<Taxon>() {
            private int index = -1;

            public boolean hasNext() {
                return index < getTaxonCount() - 1;
            }

            public Taxon next() {
                index++;
                return getTaxon(index);
            }

            public void remove() { /* do nothing */ }
        };
    }

    public Object getTaxonAttribute(int taxonIndex, String name) {
        Taxon taxon = getTaxon(taxonIndex);
        if (taxon != null)
            return taxon.getAttribute(name);
        else
            return ((FlexibleNode) getExternalNode(taxonIndex)).getAttribute(name);
    }

    // **************************************************************
    // MutableTaxonList IMPLEMENTATION
    // **************************************************************

    public int addTaxon(Taxon taxon) {
        throw new IllegalArgumentException("Cannot add taxon to a MutableTree");
    }

    public boolean removeTaxon(Taxon taxon) {
        throw new IllegalArgumentException("Cannot add taxon to a MutableTree");
    }

    public void setTaxonId(int taxonIndex, String id) {
        Taxon taxon = getTaxon(taxonIndex);
        if (taxon != null)
            taxon.setId(id);
        else
            ((FlexibleNode) getExternalNode(taxonIndex)).setId(id);

        fireTreeChanged();
        fireTaxaChanged();
    }

    public void setTaxonAttribute(int taxonIndex, String name, Object value) {
        Taxon taxon = getTaxon(taxonIndex);
        if (taxon != null)
            taxon.setAttribute(name, value);
        else
            ((FlexibleNode) getExternalNode(taxonIndex)).setAttribute(name, value);

        fireTreeChanged();
        fireTaxaChanged();
    }

    // **************************************************************
    // Identifiable IMPLEMENTATION
    // **************************************************************

    protected String id = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;

        fireTreeChanged();
    }

    // **************************************************************
    // Attributable IMPLEMENTATION
    // **************************************************************

    private Attributable.AttributeHelper attributes = null;

    public void setAttribute(String name, Object value) {
        if (attributes == null)
            attributes = new Attributable.AttributeHelper();
        attributes.setAttribute(name, value);

        fireTreeChanged();
    }

    public Object getAttribute(String name) {
        if (attributes == null)
            return null;
        else
            return attributes.getAttribute(name);
    }

    public Iterator<String> getAttributeNames() {
        if (attributes == null)
            return null;
        else
            return attributes.getAttributeNames();
    }

    public void addMutableTreeListener(MutableTreeListener listener) {
        mutableTreeListeners.add(listener);
    }

    private void fireTreeChanged() {
        for (MutableTreeListener mutableTreeListener : mutableTreeListeners) {
            (mutableTreeListener).treeChanged(this);
        }
    }

    private final ArrayList<MutableTreeListener> mutableTreeListeners = new ArrayList<MutableTreeListener>();

    public void addMutableTaxonListListener(MutableTaxonListListener listener) {
        mutableTaxonListListeners.add(listener);
    }

    private void fireTaxaChanged() {
        for (MutableTaxonListListener mutableTaxonListListener : mutableTaxonListListeners) {
            (mutableTaxonListListener).taxaChanged(this);
        }
    }

    private final ArrayList<MutableTaxonListListener> mutableTaxonListListeners = new ArrayList<MutableTaxonListListener>();

    public String toString() {
        return Tree.Utils.newick(this);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Tree)) {
            throw new IllegalArgumentException("FlexibleTree.equals can only compare instances of Tree");
        }
        return Tree.Utils.equal(this, (Tree) obj);
    }

    // **************************************************************
    // Private Stuff
    // **************************************************************

    FlexibleNode root;

    FlexibleNode[] nodes = null;

    int nodeCount;

    int externalNodeCount;

    int internalNodeCount;

    private Type units = Type.SUBSTITUTIONS;

    boolean inEdit = false;

    boolean heightsKnown = false;
    boolean lengthsKnown = false;
}
