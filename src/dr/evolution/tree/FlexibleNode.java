
package dr.evolution.tree;

import dr.evolution.util.Taxon;
import dr.util.Attributable;

import java.util.Iterator;

public class FlexibleNode implements NodeRef, Attributable {

	private FlexibleNode parent;

	private int nodeNumber;

	private double height;

	private double length;

	private double rate;

	private Taxon taxon = null;

	//
	// Private stuff
	//

	private FlexibleNode[] child;

	public FlexibleNode() {
		parent = null;
		child = null;
		height = 0.0;
		rate = 1.0;

		nodeNumber = 0;
	}

	public FlexibleNode(Taxon taxon) {
		parent = null;
		child = null;
		height = 0.0;
		rate = 1.0;

		nodeNumber = 0;
		this.taxon = taxon;
	}

	public FlexibleNode(FlexibleNode node) {
		parent = null;

		setHeight(node.getHeight());
		setLength(node.getLength());
		setRate(node.getRate());
		setId(node.getId());
		setNumber(node.getNumber());
		setTaxon(node.getTaxon());

		child = null;

		for (int i = 0; i < node.getChildCount(); i++) {
			addChild(new FlexibleNode(node.getChild(i)));
		}
	}

	public FlexibleNode(Tree tree, NodeRef node) {
        this(tree, node, false);
        // Unbelievable code duplication

//        parent = null;
//		setHeight(tree.getNodeHeight(node));
//		setLength(tree.getBranchLength(node));
//		setRate(tree.getNodeRate(node));
//		setId(tree.getTaxonId(node.getNumber()));
//		setNumber(node.getNumber());
//		setTaxon(tree.getNodeTaxon(node));
//
//		child = null;
//
//		for (int i = 0; i < tree.getChildCount(node); i++) {
//			addChild(new FlexibleNode(tree, tree.getChild(node, i)));
//		}
	}

	public FlexibleNode(Tree tree, NodeRef node, boolean copyAttributes) {
		parent = null;
		setHeight(tree.getNodeHeight(node));
		setLength(tree.getBranchLength(node));
		setRate(tree.getNodeRate(node));
		setId(tree.getTaxonId(node.getNumber()));
		setNumber(node.getNumber());
		setTaxon(tree.getNodeTaxon(node));

		child = null;

		for (int i = 0; i < tree.getChildCount(node); i++) {
			addChild(new FlexibleNode(tree, tree.getChild(node, i), copyAttributes));
		}
		if (copyAttributes) {
			Iterator iter = tree.getNodeAttributeNames(node);
			if (iter != null) {
				while (iter.hasNext()) {
					String name = (String) iter.next();
					this.setAttribute(name, tree.getNodeAttribute(node, name));
				}
			}
		}
	}

	public FlexibleNode getDeepCopy() {
        return new FlexibleNode(this);
	}

	public FlexibleNode getShallowCopy() {
		FlexibleNode copy = new FlexibleNode();

		copy.setHeight(getHeight());
		copy.setLength(getLength());
		copy.setRate(getRate());
		copy.setId(getId());
		copy.setNumber(getNumber());
		copy.setTaxon(getTaxon());

		return copy;
	}

	public final FlexibleNode getParent() {
		return parent;
	}

	public void setParent(FlexibleNode node) {
		parent = node;
	}

	public final double getHeight() {
		return height;
	}

	public final void setHeight(double value) {
		height = value;
	}

	public final double getLength() {
		return length;
	}

	public final void setLength(double value) {
		length = value;
	}

	public final double getRate() {
		return rate;
	}

	public final void setRate(double value) {
		rate = value;
	}

	public void setNumber(int n) {
		nodeNumber = n;
	}

	public int getNumber() {
		return nodeNumber;
	}

	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public FlexibleNode getChild(int n) {
		return child[n];
	}

	public boolean hasChild(FlexibleNode node) {

		for (int i = 0, n = getChildCount(); i < n; i++) {
			if (node == child[i]) return true;
		}
		return false;
	}

	public void addChild(FlexibleNode n) {
		insertChild(n, getChildCount());
	}

	public void insertChild(FlexibleNode n, int pos) {
		int numChildren = getChildCount();

		FlexibleNode[] newChild = new FlexibleNode[numChildren + 1];

// AR 28/05/08
// This doesn't work because pos can be zero and (numChildren - pos) can be zero in which case an NullPointerException is thrown.
// We could check for these special cases but we are generally only moving one or two nodes so it is unlikely to be an efficiency
// gain for using arraycopy.
//        System.arraycopy(child, 0, newChild, 0, pos);
//		newChild[pos] = n;
//        System.arraycopy(child, pos, newChild, pos + 1, numChildren - pos);

        for (int i = 0; i < pos; i++) {
            newChild[i] = child[i];
        }
        newChild[pos] = n;
        for (int i = pos; i < numChildren; i++) {
            newChild[i + 1] = child[i];
        }

		child = newChild;

		n.setParent(this);
	}

	public FlexibleNode removeChild(FlexibleNode n) {
		int numChildren = getChildCount();
		FlexibleNode[] newChild = new FlexibleNode[numChildren - 1];

		int j = 0;
		boolean found = false;
		for (int i = 0; i < numChildren; i++) {
			if (child[i] != n) {
				newChild[j] = child[i];
				j++;
			} else
				found = true;
		}

		if (!found)
			throw new IllegalArgumentException("Nonexistent child");

		//remove parent link from removed child!
		n.setParent(null);

		child = newChild;

		return n;
	}

	public FlexibleNode removeChild(int n) {
		int numChildren = getChildCount();

		if (n >= numChildren) {
			throw new IllegalArgumentException("Nonexistent child");
		}

		return removeChild(child[n]);
	}


	public boolean hasChildren() {
		return (getChildCount() != 0);
	}

	public boolean isExternal() {
		return !hasChildren();
	}

	public boolean isRoot() {
		return (getParent() == null);
	}


	public final int getChildCount() {
		if (child == null) return 0;
		return child.length;
	}

	// **************************************************************
	// Identifiable IMPLEMENTATION
	// **************************************************************

	private String id = null;

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

		if (attributes == null) {
			return null;
		} else {
			return attributes.getAttribute(name);
		}
	}

	public Iterator<String> getAttributeNames() {
		if (attributes == null)
			return null;
		else
			return attributes.getAttributeNames();
	}
}
