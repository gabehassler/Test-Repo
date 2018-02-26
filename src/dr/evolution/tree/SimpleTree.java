
package dr.evolution.tree;

import dr.evolution.util.MutableTaxonListListener;
import dr.evolution.util.Taxon;
import dr.util.Attributable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleTree implements MutableTree {

    public SimpleTree() {
        root = null;
    }

    public SimpleTree(Tree tree) {

        setUnits(tree.getUnits());

        root = new SimpleNode(tree, tree.getRoot());

        nodeCount = tree.getNodeCount();
        internalNodeCount = tree.getInternalNodeCount();
        externalNodeCount = tree.getExternalNodeCount();

        nodes = new SimpleNode[nodeCount];

        SimpleNode node = root;
        do {
            node = (SimpleNode)Tree.Utils.postorderSuccessor(this, node);
            if ((node.getNumber() >= externalNodeCount && node.isExternal()) ||
                (node.getNumber() < externalNodeCount && !node.isExternal())) {
                throw new RuntimeException("Error cloning tree: node numbers are incompatible");
            }
            nodes[node.getNumber()] = node;
        } while (node != root);
    }

    public SimpleTree(SimpleNode root) {
        adoptNodes(root);
    }

    public Tree getCopy() {
        return new SimpleTree(this);
    }

    protected void adoptNodes(SimpleNode node) {

        if (inEdit) throw new RuntimeException("Mustn't be in an edit transaction to call this method!");

        internalNodeCount = 0;
        externalNodeCount = 0;

        root = node;

        do {
            node = (SimpleNode)Tree.Utils.postorderSuccessor(this, node);
            if (node.isExternal()) {
                externalNodeCount++;
            } else
                internalNodeCount++;

        } while(node != root);

        nodeCount = internalNodeCount + externalNodeCount;

        nodes = new SimpleNode[nodeCount];

        node = root;
        int i = 0;
        int j = externalNodeCount;

        do {
            node = (SimpleNode)Tree.Utils.postorderSuccessor(this, node);
            if (node.isExternal()) {
                node.setNumber(i);
                nodes[i] = node;
                i++;
            } else {
                node.setNumber(j);
                nodes[j] = node;
                j++;
            }
        } while(node != root);
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

    public boolean hasNodeHeights() { return true; }
    public double getNodeHeight(NodeRef node) { return ((SimpleNode)node).getHeight(); }
    public double getNodeRate(NodeRef node) { return ((SimpleNode)node).getRate(); }
    public Taxon getNodeTaxon(NodeRef node) { return ((SimpleNode)node).getTaxon(); }
    public int getChildCount(NodeRef node) { return ((SimpleNode)node).getChildCount(); }
    public boolean isExternal(NodeRef node) { return ((SimpleNode)node).getChildCount() == 0; }
    public boolean isRoot(NodeRef node) { return (node == root); }
    public NodeRef getChild(NodeRef node, int i) { return ((SimpleNode)node).getChild(i); }
    public NodeRef getParent(NodeRef node) { return ((SimpleNode)node).getParent(); }

    public boolean hasBranchLengths() { return true; }
    public double getBranchLength(NodeRef node) {
        NodeRef parent = getParent(node);
        if (parent == null) {
            return 0.0;
        }

        return getNodeHeight(parent) - getNodeHeight(node);
    }
    public void setBranchLength(NodeRef node, double length) {
        throw new UnsupportedOperationException("SimpleTree cannot have branch lengths set... use FlexibleTree");
    }


    public final SimpleNode getExternalNode(int i) {
        return nodes[i];
    }

    public final SimpleNode getInternalNode(int i) {
        return nodes[i+externalNodeCount];
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

        if (!(r instanceof SimpleNode)) { throw new IllegalArgumentException(); }
        root = (SimpleNode)r;
        root.setParent(null);
    }


    public final double getRootHeight() {
        return root.getHeight();
    }

    public final void setRootHeight(double height) {
        root.setHeight(height);
        fireTreeChanged();
    }


    public void addChild(NodeRef p, NodeRef c) {
        if (!inEdit) throw new RuntimeException("Must be in edit transaction to call this method!");
        SimpleNode parent = (SimpleNode)p;
        SimpleNode child = (SimpleNode)c;
        if (parent.hasChild(child)) throw new IllegalArgumentException("Child already existists in parent");

        parent.addChild(child);
    }

    public void removeChild(NodeRef p, NodeRef c) {

        if (!inEdit) throw new RuntimeException("Must be in edit transaction to call this method!");

        SimpleNode parent = (SimpleNode)p;
        SimpleNode child = (SimpleNode)c;

        parent.removeChild(child);
    }

    public void replaceChild(NodeRef node, NodeRef child, NodeRef newChild) {
        if (!inEdit) throw new RuntimeException("Must be in edit transaction to call this method!");
        SimpleNode parent = (SimpleNode)node;
        parent.replaceChild((SimpleNode)child, (SimpleNode)newChild);
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
        SimpleNode node = (SimpleNode)n;
        node.setHeight(height);
        fireTreeChanged();
    }

    public void setNodeRate(NodeRef n, double rate) {
        SimpleNode node = (SimpleNode)n;
        node.setRate(rate);
        fireTreeChanged();
    }

    public void setNodeAttribute(NodeRef node, String name, Object value) {
        ((SimpleNode)node).setAttribute(name, value);
        fireTreeChanged();
    }

    public Object getNodeAttribute(NodeRef node, String name) {
        return ((SimpleNode)node).getAttribute(name);
    }

    public Iterator getNodeAttributeNames(NodeRef node) {
        return ((SimpleNode)node).getAttributeNames();
    }

    // **************************************************************
    // TaxonList IMPLEMENTATION
    // **************************************************************

    public int getTaxonCount() {
        return getExternalNodeCount();
    }

    public Taxon getTaxon(int taxonIndex) {
        return getExternalNode(taxonIndex).getTaxon();
    }

    public String getTaxonId(int taxonIndex) {
        Taxon taxon = getTaxon(taxonIndex);
        if (taxon != null)
            return taxon.getId();
        else
            return getExternalNode(taxonIndex).getId();
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
                index ++;
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
            return getExternalNode(taxonIndex).getAttribute(name);
    }

    // **************************************************************
    // MutableTaxonList IMPLEMENTATION
    // **************************************************************

    public int addTaxon(Taxon taxon) { throw new IllegalArgumentException("Cannot add taxon to a MutableTree"); }
    public boolean removeTaxon(Taxon taxon) { throw new IllegalArgumentException("Cannot add taxon to a MutableTree"); }

    public void setTaxonId(int taxonIndex, String id) {
        Taxon taxon = getTaxon(taxonIndex);
        if (taxon != null)
            taxon.setId(id);
        else
            getExternalNode(taxonIndex).setId(id);

        fireTreeChanged();
        fireTaxaChanged();
    }

    public void setTaxonAttribute(int taxonIndex, String name, Object value) {
        Taxon taxon = getTaxon(taxonIndex);
        if (taxon != null)
            taxon.setAttribute(name, value);
        else
            getExternalNode(taxonIndex).setAttribute(name, value);

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
    }

    // **************************************************************
    // Attributable IMPLEMENTATION
    // **************************************************************

    private Attributable.AttributeHelper attributes = null;

    public void setAttribute(String name, Object value) {
        if (attributes == null)
            attributes = new Attributable.AttributeHelper();
        attributes.setAttribute(name, value);
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
            mutableTreeListener.treeChanged(this);
        }
    }

    private final ArrayList<MutableTreeListener> mutableTreeListeners = new ArrayList<MutableTreeListener>();

    public void addMutableTaxonListListener(MutableTaxonListListener listener) {
        mutableTaxonListListeners.add(listener);
    }

    private void fireTaxaChanged() {
        for (MutableTaxonListListener mutableTaxonListListener : mutableTaxonListListeners) {
            mutableTaxonListListener.taxaChanged(this);
        }
    }

    private final ArrayList<MutableTaxonListListener> mutableTaxonListListeners = new ArrayList<MutableTaxonListListener>();

    public String toString() {
        return Tree.Utils.newick(this);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Tree)) {
            throw new IllegalArgumentException("SimpleTree.equals can only compare instances of Tree");
        }
        return Tree.Utils.equal(this, (Tree)obj);
    }

    // **************************************************************
    // Private members
    // **************************************************************

    SimpleNode root;

    SimpleNode[] nodes = null;

    int nodeCount;

    int externalNodeCount;

    int internalNodeCount;

    private Type units = Type.SUBSTITUTIONS;

    boolean inEdit = false;
}
