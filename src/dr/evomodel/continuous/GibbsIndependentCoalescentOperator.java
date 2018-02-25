package dr.evomodel.continuous;
import java.util.ArrayList;
import java.util.List;
import dr.evolution.tree.SimpleTree;
import dr.evolution.tree.Tree;
import dr.evolution.util.Taxa;
import dr.evolution.util.TaxonList;
import dr.evomodel.coalescent.CoalescentLikelihood;
import dr.evomodel.coalescent.CoalescentSimulator;
import dr.evomodel.coalescent.DemographicModel;
import dr.evomodel.tree.TreeModel;
import dr.inference.operators.GibbsOperator;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.OperatorFailedException;
import dr.inference.operators.SimpleMCMCOperator;
import dr.xml.AttributeRule;
import dr.xml.ElementRule;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;
public class GibbsIndependentCoalescentOperator extends SimpleMCMCOperator implements GibbsOperator {
public static final String OPERATOR_NAME = "GibbsIndependentCoalescentOperator";
private TreeModel treeModel;
private DemographicModel demoModel;
private CoalescentLikelihood coalescent;
private XMLObject xo;
public GibbsIndependentCoalescentOperator(XMLObject xo, TreeModel treeModel, DemographicModel demoModel, CoalescentLikelihood coalescent, double weight) {
this.xo = xo;
this.treeModel = treeModel;
this.demoModel = demoModel;
this.coalescent = coalescent;
setWeight(weight);
}
public String getPerformanceSuggestion() {
return "";
}
public String getOperatorName() {
return "GibbsIndependentCoalescent(" + treeModel.getModelName() + ")";
}
public int getStepCount() {
return 1;
}
public double doOperation() throws OperatorFailedException {
CoalescentSimulator simulator = new CoalescentSimulator();
List<TaxonList> taxonLists = new ArrayList<TaxonList>();
double rootHeight = -1.0;
double oldLikelihood = 0.0;
double newLikelihood = 0.0;
// should have one child that is node
for (int i = 0; i < xo.getChildCount(); i++) {
final Object child = xo.getChild(i);
//careful: Trees are TaxonLists ... (AER); see OldCoalescentSimulatorParser
if (child instanceof Tree) {
//do nothing
} else if (child instanceof TaxonList) {
//taxonLists.add((TaxonList) child);
taxonLists.add((Taxa) child);
//taxa added
break;
} 
}
try {
Tree[] trees = new Tree[taxonLists.size()];
// simulate each taxonList separately
for (int i = 0; i < taxonLists.size(); i++) {
trees[i] = simulator.simulateTree(taxonLists.get(i), demoModel);
}
oldLikelihood = coalescent.getLogLikelihood();
SimpleTree simTree = simulator.simulateTree(trees, demoModel, rootHeight, trees.length != 1);
//this would be the normal way to do it
treeModel.beginTreeEdit();
//now it's allowed to adjust the tree structure
treeModel.adoptTreeStructure(simTree);
//endTreeEdit() would then fire the events
treeModel.endTreeEdit();
newLikelihood = coalescent.getLogLikelihood();
} catch (IllegalArgumentException iae) {
try {
throw new XMLParseException(iae.getMessage());
} catch (XMLParseException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
}
//return oldLikelihood - newLikelihood;
return 0;
}
public static dr.xml.XMLObjectParser PARSER = new dr.xml.AbstractXMLObjectParser() {
public String getParserName() {
return OPERATOR_NAME;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
TreeModel treeModel = (TreeModel) xo.getChild(TreeModel.class);
double weight = xo.getDoubleAttribute(MCMCOperator.WEIGHT);
DemographicModel demoModel = (DemographicModel) xo.getChild(DemographicModel.class);
CoalescentLikelihood coalescent = (CoalescentLikelihood) xo.getChild(CoalescentLikelihood.class);
return new GibbsIndependentCoalescentOperator(xo, treeModel, demoModel, coalescent, weight);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
AttributeRule.newDoubleRule(MCMCOperator.WEIGHT),
new ElementRule(Taxa.class),
new ElementRule(TreeModel.class),
new ElementRule(DemographicModel.class),
new ElementRule(CoalescentLikelihood.class)
};
public String getParserDescription() {
return "This element returns an independence coalescent sampler, disguised as a Gibbs operator, from a demographic model.";
}
public Class getReturnType() {
return MCMCOperator.class;
}
};
}
