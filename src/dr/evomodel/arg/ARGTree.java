package dr.evomodel.arg;

import dr.evolution.tree.MutableTreeListener;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.MutableTaxonListListener;
import dr.evolution.util.Taxon;
import dr.evomodel.arg.ARGModel.Node;
import dr.util.Attributable;

import java.util.*;

public class ARGTree implements Tree {

	//NodeRef root;

	protected Taxon[] taxaList;
	protected int taxaCount;

	private final Node initialRoot;

    public ARGModel argModel;
//    private Map<NodeRef,Integer> mapARGNodesToInts;

    private final Map<NodeRef,NodeRef> mapARGNodesToTreeNodes;

//    public Map<NodeRef,Integer> getMappingInts() { return mapARGNodesToInts; }

//    public Map<ARGModel.Node, NodeRef> getMappingNodes() { return mapARGNodesToTreeNodes; }

	private int partition = -9;

//	/**
//	 * Constructor to represent complete ARG as a tree
//	 *
//	 * @param arg
//	 */
//	public ARGTree(ARGModel arg) {
//              this.argModel = arg;
//              mapNodesARGToTree = new HashMap<Node,Node>(arg.getNodeCount());
//              root = arg.new Node((Node) arg.getRoot());
//	}


//	/**
//	 * Constructor for specific partition tree
//	 *
//	 * @param arg
//	 * @param partition
//	 */

//	ArrayList<Node> nodeList;

	public boolean wasRootTrimmed() {
		return (root != initialRoot);
	}


	public String toGraphString() {
		StringBuffer sb = new StringBuffer();
		for (Node node : nodes) {
			sb.append(node.number);
			if (node.leftParent != null)
				sb.append(" " + node.leftParent.number);
			else
				sb.append(" 0");
			if (node.rightParent != null)
				sb.append(" " + node.rightParent.number);
			else
				sb.append(" 0");
			if (node.leftChild != null)
				sb.append(" " + node.leftChild.number);
			else
				sb.append(" 0");
			if (node.rightChild != null)
				sb.append(" " + node.rightChild.number);
			else
				sb.append(" 0");
			if (node.taxon != null)
				sb.append(" " + node.taxon.toString());
			sb.append("\n");
		}
		sb.append("Root = " + ((Node) getRoot()).number + "\n");
		return new String(sb);
	}

	public ARGTree(ARGModel arg, int partition) {
              this.argModel = arg;
              mapARGNodesToTreeNodes = new HashMap<NodeRef,NodeRef>(arg.getNodeCount());

		this.partition = partition;
		ARGModel.Node node = arg.new Node(((Node) arg.getRoot()), partition);
		initialRoot = node;

		int j = arg.externalNodeCount;
		node.stripOutDeadEnds();
		root = node.stripOutSingleChildNodes(node);
		node = root;
		nodeCount = 2 * j - 1;
		externalNodeCount = j;
		internalNodeCount = j - 1;
		nodes = new Node[nodeCount];

		do {
			node = (Node) Tree.Utils.postorderSuccessor(this, node);
			if (node.isExternal()) {
                                  // keep same order as ARG, so do not need to reload tipStates/Partials
                                  nodes[node.number] = node;
                                  mapARGNodesToTreeNodes.put(node.mirrorNode,node);
			} else {
                                  // Reorder in new post-order succession

				nodes[j] = node;
                                        node.number = j;
				j++;
                                  mapARGNodesToTreeNodes.put(node.mirrorNode, node);
			}
		} while (node != root);
        hasRates = false;
    }


    public Map<NodeRef,NodeRef> getMapping() {
        return mapARGNodesToTreeNodes;
    }

//    public Map<NodeRef,Integer> getMapARGNodesToInts() {
//        // Only need to map internal nodes
//        Map<NodeRef,Integer> map = new HashMap<NodeRef,Integer>(getInternalNodeCount());
//        for(int i=0; i<getInternalNodeCount(); i++) {
//            ARGModel.Node node = (ARGModel.Node) getInternalNode(i);
//            map.put(node.mirrorNode,node.number);
//        }
//        return map;
//    }
	public boolean checkForNullRights(Node node) {
		return node.checkForNullRights();
	}

	// *****************************************************************
	// Interface Tree
	// *****************************************************************


	public final Type getUnits() {
		return units;
	}

	public void setUnits(Type units) {
		this.units = units;

	}



	public final int getNodeCount() {
		return nodeCount;
	}

	public final boolean hasNodeHeights() {
		return true;
	}

	public final double getNodeHeight(NodeRef node) {

		//System.err.println(Tree.Utils.uniqueNewick(this, node));
		//((Node)node))

		return ((Node) node).getHeight();
	}

	public final double getNodeHeightUpper(NodeRef node) {
		return ((Node) node).heightParameter.getBounds().getUpperLimit(0);
	}

	public final double getNodeHeightLower(NodeRef node) {
		return ((Node) node).heightParameter.getBounds().getLowerLimit(0);
	}


	public final double getNodeRate(NodeRef node) {


		if (!hasRates) {
			return 1.0;
		}

		return ((Node) node).getRate(partition);
	}

	public Object getNodeAttribute(NodeRef node, String name) {
		throw new UnsupportedOperationException("TreeModel does not use NodeAttributes");
	}

	public Iterator getNodeAttributeNames(NodeRef node) {
		throw new UnsupportedOperationException("TreeModel does not use NodeAttributes");
	}

	public double getNodeTrait(NodeRef node) {
		if (!hasTraits) throw new IllegalArgumentException("Trait parameters have not been created");
		return ((Node) node).getTrait();
	}

	public final Taxon getNodeTaxon(NodeRef node) {
		return ((Node) node).taxon;
	}

	public final boolean isExternal(NodeRef node) {
		return ((Node) node).isExternal();
	}

	public final boolean isRoot(NodeRef node) {
		return (node == root);
	}

	public final int getChildCount(NodeRef node) {
		//System.err.println("Cn for "+((Node)node).number);
		return ((Node) node).getChildCount();
	}

	public final NodeRef getChild(NodeRef node, int i) {
		return ((Node) node).getChild(i);
	}

	public final NodeRef getParent(NodeRef node) {
		//System.err.println("Gimme!");
		return ((Node) node).leftParent;
	}

	public final boolean hasBranchLengths() {
		return true;
	}

	public final double getBranchLength(NodeRef node) {
		NodeRef parent = getParent(node);
		if (parent == null) {
			return 0.0;
		}

		return getNodeHeight(parent) - getNodeHeight(node);
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

	// **************************************************************
	// TaxonList IMPLEMENTATION
	// **************************************************************

	public int getTaxonCount() {
		return getExternalNodeCount();
	}

//    public Taxon getTaxon(int taxonIndex) {
//        return ((Node)getExternalNode(taxonIndex)).taxon;
//    }

//    public Taxon getTaxon(int taxonIndex) {
//    	if( taxonIndex >= taxaCount )
//    		return null;
//        return taxaList[taxonIndex];
//    }
	public Taxon getTaxon(int taxonIndex) {
		return ((Node) getExternalNode(taxonIndex)).taxon;
	}


	public String getTaxonId(int taxonIndex) {
		Taxon taxon = getTaxon(taxonIndex);
		if (taxon != null) {
			return taxon.getId();
		} else {
			return null;
		}
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

	public final Object getTaxonAttribute(int taxonIndex, String name) {
		Taxon taxon = getTaxon(taxonIndex);
		if (taxon != null) {
			return taxon.getAttribute(name);
		}
		return null;
	}

	// **************************************************************
	// MutableTaxonList IMPLEMENTATION
	// **************************************************************

	public int addTaxon(Taxon taxon) {
		throw new IllegalArgumentException("Cannot add taxon to a TreeModel");
	}

	public boolean removeTaxon(Taxon taxon) {
		throw new IllegalArgumentException("Cannot add taxon to a TreeModel");
	}

	public void setTaxonId(int taxonIndex, String id) {
		throw new IllegalArgumentException("Cannot set taxon id in a TreeModel");
	}

	public void setTaxonAttribute(int taxonIndex, String name, Object value) {
		throw new IllegalArgumentException("Cannot set taxon attribute in a TreeModel");
	}

	public void addMutableTreeListener(MutableTreeListener listener) {
	} // Do nothing at the moment

	public void addMutableTaxonListListener(MutableTaxonListListener listener) {
	} // Do nothing at the moment

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

	private Attributable.AttributeHelper treeAttributes = null;

	public void setAttribute(String name, Object value) {
		if (treeAttributes == null)
			treeAttributes = new Attributable.AttributeHelper();
		treeAttributes.setAttribute(name, value);
	}

	public Object getAttribute(String name) {
		if (treeAttributes == null)
			return null;
		else
			return treeAttributes.getAttribute(name);
	}

	public Iterator<String> getAttributeNames() {
		if (treeAttributes == null)
			return null;
		else
			return treeAttributes.getAttributeNames();
	}

	public final String getNewick() {
		return Tree.Utils.newick(this);
	}

	public final String getUniqueNewick(){
		return Tree.Utils.uniqueNewick(this,this.getRoot());
	}

	public String toString() {
		return getNewick();
	}

	public Tree getCopy() {
		throw new UnsupportedOperationException("please don't call this function");
	}


	// ***********************************************************************
	// Private members
	// ***********************************************************************


	protected Node root = null;
	protected int storedRootNumber;

	protected Node[] nodes = null;
	protected Node[] storedNodes = null;

	protected int nodeCount;

	protected int externalNodeCount;

	protected int internalNodeCount;




//	private int units = SUBSTITUTIONS;
	private Type units;

	protected boolean inEdit = false;

	private final boolean hasRates;
	private final boolean hasTraits = false;


}
