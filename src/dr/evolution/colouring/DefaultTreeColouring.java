package dr.evolution.colouring;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
public class DefaultTreeColouring implements TreeColouring {
    public DefaultTreeColouring(int colourCount, Tree tree) {
        this.colourCount = colourCount;
        this.tree = tree;
        branchColourings = new DefaultBranchColouring[tree.getNodeCount()];
        colourChangeCount = 0;
    }
    public DefaultTreeColouring( DefaultTreeColouring treeColouring ) {
    	if (!treeColouring.immutable) {
    		throw new RuntimeException("Attempted to create a copy of a mutable colouring");
    	}
    	this.colourCount = treeColouring.colourCount;
    	this.tree = treeColouring.tree;                                 // NOT a copy, to allow tree editing after creation
    	colourChangeCount = treeColouring.colourChangeCount;
    	branchColourings = (DefaultBranchColouring[])treeColouring.branchColourings.clone();      // Shallow copy
    	for (int i=0; i<branchColourings.length; i++) {
    		branchColourings[i] = branchColourings[i].getCopy();        // Turn into a deep copy
    	}
    }
    public int getColourCount() {
        return colourCount;
    }
    public Tree getTree() {
        return tree;
    }
    public void setBranchColouring(NodeRef node, DefaultBranchColouring branchColouring) {
    	if (immutable) throw new RuntimeException("Attempted to setBranchColouring of an immutable colouring");
        if (tree.isRoot(node)) throw new IllegalArgumentException("Can't set a BranchColouring for the root node");
        if (branchColourings[node.getNumber()] != null) {
        	colourChangeCount -= branchColourings[node.getNumber()].getColourChanges().size();
        }
        branchColourings[node.getNumber()] = branchColouring;
        colourChangeCount += branchColouring.getColourChanges().size();
    }
    public BranchColouring getBranchColouring(NodeRef node) {
    	if (tree.isRoot(node)) throw new IllegalArgumentException("Can't get a BranchColouring for the root node");
    	if (mutableBranches) {
    		return branchColourings[node.getNumber()];
    	}
    	if (branchColourings[node.getNumber()] == null) return null;
    	return branchColourings[node.getNumber()].getCopy();
    }
    public void setLogProbabilityDensity(double logP) {
        if (hasProbability) throw new RuntimeException("Attempted to set probability twice");
        makeImmutable();
        hasProbability = true;
        this.logP = logP;
    }
    public void makeImmutable() {
        immutable = true;
    }
    public boolean isImmutable() {
    	return immutable;
    }
    public boolean hasProbability() {
    	return hasProbability;
    }
    public void checkColouring() {
        checkColouringAtNode(tree.getRoot());
    }
    private void checkColouringAtNode(NodeRef node) {
        int colour = getNodeColour(node);
        if (!tree.isExternal(node)) {
            for(int i = 0; i < tree.getChildCount(node); i++) {
                NodeRef child = tree.getChild(node, i);
                if (branchColourings[child.getNumber()].getParentColour() != colour) {
                    throw new RuntimeException("Colour mismatch at node " + node.toString());
                }
                checkColouringAtNode(child);
            }
        }
    }
    public int getNodeColour(NodeRef node) {
        if (tree.isRoot(node)) {
            NodeRef child = tree.getChild(node, 0);
            return branchColourings[child.getNumber()].getParentColour();
        } else {
            return branchColourings[node.getNumber()].getChildColour();
        }
    }
        double nodeHeight = tree.getNodeHeight(node);
        if (time < nodeHeight) throw new IllegalArgumentException("Specified time is younger than node!");
        while ((!tree.isRoot(node)) && (tree.getNodeHeight(tree.getParent(node)) < time)) {
        	node = tree.getParent(node);
        }
        if (tree.isRoot(node)) throw new IllegalArgumentException("Can't ascertain lineage colour above root!");
        int colour = getColour(node);
        List changes = getColourChanges(node);
        int size = changes.size();
        int index = 0;
        while (index < size && ((ColourChange)changes.get(index)).getTime() < time) {
            index += 1;
        }
        if (index == 0) return colour;
        return ((ColourChange)changes.get(index-1)).getColourAbove();
    }
    public BranchColouring getBranchColouring(NodeRef node) {
        return branchColourings[node.getNumber()];
    }
    public int getColourChangeCount() {
        return colourChangeCount;
    }
    public double getLogProbabilityDensity() {
    	if (!hasProbability) {
    		throw new RuntimeException("Tree colouring has no probability density; use colouringModel.getTreeColouringWithProbability()");
    	}
        return logP;
    }
    private final Tree tree;
    private final int colourCount;
    private final DefaultBranchColouring[] branchColourings;
    private int colourChangeCount;
    private double logP = Double.NEGATIVE_INFINITY;
    private boolean immutable = false;             // true if colouring cannot be changed
    private boolean hasProbability = false;        // true if probability has been assigned (implies immutable)
}
