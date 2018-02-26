
package dr.evomodel.continuous;

import dr.evolution.tree.FlexibleTree;
import dr.evolution.tree.MutableTree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.xml.*;


public class SimulateTrait {	
	
	public static final String SIMULATE_TRAIT = "traitTree";
	public static final String TRAIT_NAME = "traitName";
	public static final String CLONE = "clone";
	public static final String INITIAL_VALUE = "initialValue";
	public static final String MODEL = "model";
	public static final String TREE = "tree";
	
	public SimulateTrait(DiffusionModel diffusionModel, String traitName) {

		this.diffusionModel = diffusionModel;
		this.traitName = traitName;
	}
	
	public Tree simulate(Tree tree, double value, boolean clone) {
		
		Tree binaryTree = null;
		
		if (clone) {
			binaryTree = new FlexibleTree(tree);
			((FlexibleTree)binaryTree).resolveTree();
		} else {
			binaryTree = tree;	
		}
		simulate((MutableTree)binaryTree, binaryTree.getRoot(), value);
		
		return binaryTree;
	}
	
	private void simulate(MutableTree tree, NodeRef node, double value) {
			
		tree.setNodeAttribute(node, traitName, new Double(value));
		int childCount = tree.getChildCount(node);
		double height = tree.getNodeHeight(node);
		for (int i = 0; i < childCount; i++) {
			NodeRef child = tree.getChild(node, i);
			
			simulate(tree, child, diffusionModel.simulateForward(value, height - tree.getNodeHeight(child)));
		}	
	}
	
	// **************************************************************
    // XMLObjectParser
    // **************************************************************

	public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
		
		public String getParserName() { return SIMULATE_TRAIT; }
			
		public Object parseXMLObject(XMLObject xo) throws XMLParseException {
			
			String traitName = xo.getStringAttribute(TRAIT_NAME);	
			boolean clone = xo.getBooleanAttribute(CLONE);	
			double initialValue = xo.getDoubleAttribute(INITIAL_VALUE);	
				
			DiffusionModel diffusionModel = (DiffusionModel)xo.getChild(DiffusionModel.class);	
			Tree tree = (Tree)xo.getChild(Tree.class);	
			
			SimulateTrait simulateTrait = new SimulateTrait(diffusionModel, traitName);
			Tree simTree = simulateTrait.simulate(tree, initialValue, clone);
			return simTree;
		}
		
		//************************************************************************
		// AbstractXMLObjectParser implementation
		//************************************************************************

		public XMLSyntaxRule[] getSyntaxRules() { return rules; }
		
		private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
			new StringAttributeRule(TRAIT_NAME, "The name to be given to the trait to be simulated"),
			AttributeRule.newBooleanRule(CLONE),
			AttributeRule.newDoubleRule(INITIAL_VALUE),
			new ElementRule(DiffusionModel.class),
			new ElementRule(Tree.class)
		};

		public String getParserDescription() { 
			return "Simulates a trait on a tree";
		}
	
		public Class getReturnType() { return Tree.class; }
	};
	
	DiffusionModel diffusionModel = null;
	String traitName = null;
}

