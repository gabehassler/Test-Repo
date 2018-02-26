package dr.evolution.tree;
import dr.evolution.util.Taxon;
import dr.util.Attributable;
import java.util.Iterator;
public class SimpleNode implements NodeRef, Attributable {
	private SimpleNode parent;
	private int nodeNumber;
	private double height;
	private double rate;
	private Taxon taxon = null;
	//
	// Private stuff
	//
	private SimpleNode[] child;
	public SimpleNode()
	{
		parent = null;
		child =null;
		height = 0.0;
		rate = 1.0;
		nodeNumber = 0;
	}
	public SimpleNode(SimpleNode node) {
		parent = null;
		setHeight(node.getHeight());
		setRate(node.getRate());
		setId(node.getId());		
		setNumber(node.getNumber());
		setTaxon(node.getTaxon());
		child = null;
		for (int i = 0; i < node.getChildCount(); i++) {
			addChild(new SimpleNode(node.getChild(i)));
		}
	}
	public SimpleNode(Tree tree, NodeRef node)
	{
		parent = null;
		setHeight(tree.getNodeHeight(node));
		setRate(tree.getNodeRate(node));
        final int nodeNumber = node.getNumber();
        setId(tree.getTaxonId(nodeNumber));
		setNumber(nodeNumber);
		setTaxon(tree.getNodeTaxon(node));
		child = null;
		for (int i = 0; i < tree.getChildCount(node); i++) {
			addChild(new SimpleNode(tree, tree.getChild(node, i)));
		}
	}
	public SimpleNode getDeepCopy() {
        return new SimpleNode(this);
	}
	public final SimpleNode getParent() {
		return parent;
	}
	public void setParent(SimpleNode node) { parent = node; }
	public final double getHeight() {
		return height;
	}
	public final void setHeight(double value) {
		height = value;
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
	public SimpleNode getChild(int n)
	{
        assert 0 <= n && n < child.length;
		return child[n];
	}
	public boolean hasChild(SimpleNode node) { 
		for (int i = 0, n = getChildCount(); i < n; i++) {
			if (node == child[i]) return true;
		}
		return false;
	}
	public void addChild(SimpleNode n)
	{
		insertChild(n, getChildCount());
	}
	 + @param pos position
	public void insertChild(SimpleNode n, int pos)
	{
		int numChildren = getChildCount();
		SimpleNode[] newChild = new SimpleNode[numChildren + 1];
		for (int i = 0; i < pos; i++)
		{
			newChild[i] = child[i];
		}
		newChild[pos] = n;
		for (int i = pos; i < numChildren; i++)
		{
			newChild[i+1] = child[i];
		}
		child = newChild;
		n.setParent(this);
	}
	public SimpleNode removeChild(SimpleNode n)
	{
		int numChildren = getChildCount();
		SimpleNode[] newChild = new SimpleNode[numChildren-1];
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
	public SimpleNode removeChild(int n)
	{
		int numChildren = getChildCount();
		if (n >= numChildren)
		{
			throw new IllegalArgumentException("Nonexistent child");
		}
		return removeChild(child[n]);
	}
    public void replaceChild(SimpleNode childNode, SimpleNode replacment) {
         for(int nc = 0; nc < child.length; ++nc) {
             if( child[nc] == childNode ) {
                 replacment.setParent(this);
                 child[nc] = replacment;
                 break;
             }
         }
    }
	public boolean hasChildren()
	{
		return (getChildCount() != 0);
	}
	public boolean isExternal()	{
		return !hasChildren();
	}
	public boolean isRoot()
	{
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
}
