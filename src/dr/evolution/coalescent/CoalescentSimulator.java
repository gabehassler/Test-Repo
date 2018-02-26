package dr.evolution.coalescent;
import dr.evolution.tree.*;
import dr.evolution.util.*;
import dr.evolution.util.Date;
import dr.math.MathUtils;
import dr.util.HeapSort;
import java.util.*;
public class CoalescentSimulator {
    public CoalescentSimulator() {}
	public SimpleTree simulateTree(TaxonList taxa, DemographicFunction demoFunction) {
        if( taxa.getTaxonCount() == 0 ) return new SimpleTree();
        SimpleNode[] nodes = new SimpleNode[taxa.getTaxonCount()];
		for (int i = 0; i < taxa.getTaxonCount(); i++) {
			nodes[i] = new SimpleNode();
			nodes[i].setTaxon(taxa.getTaxon(i));
		}
		boolean usingDates = Taxon.getMostRecentDate() != null;
        for (int i = 0; i < taxa.getTaxonCount(); i++) {
            Taxon taxon = taxa.getTaxon(i);
            if (usingDates) {
                nodes[i].setHeight(taxon.getHeight());
			} else {
				// assume contemporaneous tips
				nodes[i].setHeight(0.0);
			}
		}
		return new SimpleTree(simulateCoalescent(nodes, demoFunction));
	}
	public SimpleNode simulateCoalescent(SimpleNode[] nodes, DemographicFunction demographic) {
        // sanity check - disjoint trees
        if( ! Tree.Utils.allDisjoint(nodes) ) {
            throw new RuntimeException("subtrees' taxa overlap");
        }
        if( nodes.length == 0 ) {
             throw new IllegalArgumentException("empty nodes set") ;
        }
        for(int attempts = 0; attempts < 1000; ++attempts) {
            SimpleNode[] rootNode = simulateCoalescent(nodes, demographic, 0.0, Double.POSITIVE_INFINITY);
            if( rootNode.length == 1 ) {
                return rootNode[0];
            }
        }
        throw new RuntimeException("failed to merge trees after 1000 tries.");
	}
    public SimpleNode[] simulateCoalescent(SimpleNode[] nodes, DemographicFunction demographic, double currentHeight,
                                           double maxHeight){
        return simulateCoalescent(nodes, demographic, currentHeight, maxHeight, false);
    }
    // if enforceMaxHeight is true, all heights will be drawn from a normalised distribution such that maxHeight really
    // is the maximum height
    public SimpleNode[] simulateCoalescent(SimpleNode[] nodes, DemographicFunction demographic, double currentHeight,
                                           double maxHeight, boolean enforceMaxHeight) {
        // If only one node, return it
        // continuing results in an infinite loop
        if( nodes.length == 1 ) return nodes;
        double[] heights = new double[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			heights[i] = nodes[i].getHeight();
		}
		int[] indices = new int[nodes.length];
		HeapSort.sort(heights, indices);
		// node list
		nodeList.clear();
		activeNodeCount = 0;
		for (int i = 0; i < nodes.length; i++) {
			nodeList.add(nodes[indices[i]]);
		}
		setCurrentHeight(currentHeight);
		// get at least two tips
		while (getActiveNodeCount() < 2) {
			currentHeight = getMinimumInactiveHeight();
			setCurrentHeight(currentHeight);
		}
        double nextCoalescentHeight;
		// simulate coalescent events
        if(!enforceMaxHeight){
		    nextCoalescentHeight = currentHeight + DemographicFunction.Utils.getSimulatedInterval(demographic,
                    getActiveNodeCount(), currentHeight);
        } else {
            nextCoalescentHeight = currentHeight + DemographicFunction.Utils.getSimulatedInterval(demographic,
                    getActiveNodeCount(), currentHeight, maxHeight);
        }
		while (nextCoalescentHeight < maxHeight && (getNodeCount() > 1)) {
			if (nextCoalescentHeight >= getMinimumInactiveHeight()) {
				currentHeight = getMinimumInactiveHeight();
				setCurrentHeight(currentHeight);
			} else {
				currentHeight = nextCoalescentHeight;
				coalesceTwoActiveNodes(currentHeight);
			}
			if (getNodeCount() > 1) {
				// get at least two tips
				while (getActiveNodeCount() < 2) {
					currentHeight = getMinimumInactiveHeight();
					setCurrentHeight(currentHeight);
				}
                if(!enforceMaxHeight){
	//			nextCoalescentHeight = currentHeight + DemographicFunction.Utils.getMedianInterval(demographic, getActiveNodeCount(), currentHeight);
				    nextCoalescentHeight = currentHeight + DemographicFunction.Utils.getSimulatedInterval(demographic,
                            getActiveNodeCount(), currentHeight);
                } else {
                    nextCoalescentHeight = currentHeight + DemographicFunction.Utils.getSimulatedInterval(demographic,
                            getActiveNodeCount(), currentHeight, maxHeight);
                }
			}
		}
		SimpleNode[] nodesLeft = new SimpleNode[nodeList.size()];
		for (int i = 0; i < nodesLeft.length; i++) {
			nodesLeft[i] = nodeList.get(i);
		}
		return nodesLeft;
	}
	private double getMinimumInactiveHeight() {
		if (activeNodeCount < nodeList.size()) {
			return (nodeList.get(activeNodeCount)).getHeight();
		} else return Double.POSITIVE_INFINITY;
	}
	private void setCurrentHeight(double height) {
		while (getMinimumInactiveHeight() <= height) {
			activeNodeCount += 1;
		}
	}
	private int getActiveNodeCount() {
		return activeNodeCount;
	}
	private int getNodeCount() {
		return nodeList.size();
	}
	private void coalesceTwoActiveNodes(double height) {
		int node1 = MathUtils.nextInt(activeNodeCount);
		int node2 = node1;
		while (node2 == node1) {
			node2 = MathUtils.nextInt(activeNodeCount);
		}
		SimpleNode left = nodeList.get(node1);
		SimpleNode right = nodeList.get(node2);
		SimpleNode newNode = new SimpleNode();
		newNode.setHeight(height);
		newNode.addChild(left);
		newNode.addChild(right);
		nodeList.remove(left);
		nodeList.remove(right);
		activeNodeCount -= 2;
		nodeList.add(activeNodeCount, newNode);
		activeNodeCount += 1;
		if (getMinimumInactiveHeight() < height) {
			throw new RuntimeException("This should never happen! Somehow the current active node is older than the next inactive node!");
		}
	}
	private final ArrayList<SimpleNode> nodeList = new ArrayList<SimpleNode>();
	private int activeNodeCount = 0;
	public static void main(String[] args) {
		double[] samplingTimes = {
				0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0
		};
		ExponentialGrowth exponentialGrowth = new ExponentialGrowth(Units.Type.YEARS);
		exponentialGrowth.setN0(10);
		exponentialGrowth.setGrowthRate(0.5);
		ConstantPopulation constantPopulation = new ConstantPopulation(Units.Type.YEARS);
		constantPopulation.setN0(10);
		Taxa taxa = new Taxa();
		int i = 1;
		for (double time : samplingTimes) {
			Taxon taxon = new Taxon("tip" + i);
			taxon.setAttribute("date", new Date(time, Units.Type.YEARS, true));
			i++;
			taxa.addTaxon(taxon);
		}
		CoalescentSimulator simulator = new CoalescentSimulator();
		Tree tree = simulator.simulateTree(taxa, exponentialGrowth);
		List<Double> heights = new ArrayList<Double>();
		for (int j = 0; j < tree.getInternalNodeCount(); j++) {
			heights.add(tree.getNodeHeight(tree.getInternalNode(j)));
		}
		Collections.sort(heights);
		for (int j = 0; j < heights.size(); j++) {
			System.out.println(j + "\t" + heights.get(j));
		}
	}
}