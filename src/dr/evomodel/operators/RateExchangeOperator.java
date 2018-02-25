package dr.evomodel.operators;
import dr.evolution.tree.NodeRef;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.operators.RateExchangeOperatorParser;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.OperatorFailedException;
import dr.inference.operators.SimpleMCMCOperator;
import dr.math.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class RateExchangeOperator extends SimpleMCMCOperator {
private static final String TRAIT = "trait";
private final TreeModel tree;
private final boolean swapRates;
private final boolean swapTraits;
private final boolean swapAtRoot;
private final boolean moveHeight;
public RateExchangeOperator(TreeModel tree, double weight, boolean swapRates, boolean swapTraits, boolean swapAtRoot, boolean moveHeight) {
this.tree = tree;
setWeight(weight);
this.swapRates = swapRates;
this.swapTraits = swapTraits;
this.swapAtRoot = swapAtRoot;
this.moveHeight = moveHeight;
}
public double doOperation() throws OperatorFailedException {
NodeRef node0 = tree.getInternalNode(MathUtils.nextInt(tree.getInternalNodeCount()));
NodeRef node1 = tree.getChild(node0, 0);
NodeRef node2 = tree.getChild(node0, 1);
if (swapRates) {
if (swapAtRoot) {
double[] rates = new double[]{tree.getNodeRate(node0), tree.getNodeRate(node1), tree.getNodeRate(node2)};
int r1 = MathUtils.nextInt(3);
tree.setNodeRate(node0, rates[r1]);
// swap down the top trait
rates[r1] = rates[2];
int r2 = MathUtils.nextInt(2);
tree.setNodeRate(node1, rates[r2]);
// swap down the top trait
rates[r2] = rates[1];
tree.setNodeRate(node2, rates[0]);
} else {
// just swap the two child rates...
double tmp = tree.getNodeRate(node1);
tree.setNodeRate(node1, tree.getNodeRate(node2));
tree.setNodeRate(node2, tmp);
}
}
if (swapTraits) {
if (swapAtRoot) {
double[] traits = new double[]{tree.getNodeTrait(node0, TRAIT), tree.getNodeTrait(node1, TRAIT), tree.getNodeTrait(node2, TRAIT)};
int r1 = MathUtils.nextInt(3);
tree.setNodeTrait(node0, TRAIT, traits[r1]);
// swap down the top trait
traits[r1] = traits[2];
int r2 = MathUtils.nextInt(2);
tree.setNodeTrait(node1, TRAIT, traits[r2]);
// swap down the top trait
traits[r2] = traits[1];
tree.setNodeTrait(node2, TRAIT, traits[0]);
} else {
// just swap the two child traits...
double tmp = tree.getNodeTrait(node1, TRAIT);
tree.setNodeTrait(node1, TRAIT, tree.getNodeTrait(node2, TRAIT));
tree.setNodeTrait(node2, TRAIT, tmp);
}
}
// If the node is not the root, do a uniform pick of its height
if (!tree.isRoot(node0) && moveHeight) {
double lower = tree.getNodeHeightLower(node0);
double upper = tree.getNodeHeightUpper(node0);
double newValue = (MathUtils.nextDouble() * (upper - lower)) + lower;
tree.setNodeHeight(node0, newValue);
}
return 0.0;
}
public double getTargetAcceptanceProbability() {
return 0.234;
}
public String getPerformanceSuggestion() {
if (MCMCOperator.Utils.getAcceptanceProbability(this) < getMinimumAcceptanceLevel()) {
return "";
} else if (MCMCOperator.Utils.getAcceptanceProbability(this) > getMaximumAcceptanceLevel()) {
return "";
} else {
return "";
}
}
public String getOperatorName() {
return RateExchangeOperatorParser.RATE_EXCHANGE;
}
public Element createOperatorElement(Document d) {
return d.createElement(RateExchangeOperatorParser.RATE_EXCHANGE);
}
}
