package dr.evomodel.antigenic;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.tree.TreeTrait;
import dr.evolution.tree.TreeTraitProvider;
import dr.evolution.util.Taxon;
import dr.inference.model.*;
import dr.evomodel.continuous.AbstractMultivariateTraitLikelihood;
import dr.xml.*;
public class DriftedTraitsLogger implements TreeTraitProvider {
public static final String DRIFTED_TRAITS_LOGGER = "driftedTraits";
private final AbstractMultivariateTraitLikelihood multivariateTraits;
private final Parameter locationDriftParameter;
private TreeTrait[] treeTraits = null;
private double maxHeight = -1.0;
public DriftedTraitsLogger(AbstractMultivariateTraitLikelihood multivariateTraits, Parameter locationDriftParameter) {
this.multivariateTraits = multivariateTraits;
this.locationDriftParameter = locationDriftParameter;
}
@Override
public TreeTrait[] getTreeTraits() {
if (treeTraits == null) {
treeTraits = new TreeTrait[] {
new TreeTrait.DA() {
public String getTraitName() {
return multivariateTraits.getTraitName();
}
public Intent getIntent() {
return Intent.NODE;
}
public Class getTraitClass() {
return Double.class;
}
public double[] getTrait(Tree tree, NodeRef node) {
double t[] = multivariateTraits.getTraitForNode(tree, node, multivariateTraits.getTraitName());
computeMaxHeight(tree);
// drift first dimension
double nodeHeight = tree.getNodeHeight(node);
double offset = locationDriftParameter.getParameterValue(0) * (maxHeight-nodeHeight);
t[0] = t[0] + offset;
return t;
}
}
};
}
return treeTraits;
}
@Override
public TreeTrait getTreeTrait(String key) {
TreeTrait[] tts = getTreeTraits();
for (TreeTrait tt : tts) {
if (tt.getTraitName().equals(key)) {
return tt;
}
}
return null;
}
private void computeMaxHeight(Tree tree) {
if (maxHeight < 0) {
int m = tree.getTaxonCount();
for (int i = 0; i < m; i++) {
Taxon taxon = tree.getTaxon(i);
double height = taxon.getHeight();
if (height > maxHeight) {
maxHeight = height;
}
}
}
}
public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
public String getParserName() {
return DRIFTED_TRAITS_LOGGER;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
AbstractMultivariateTraitLikelihood multivariateTraits = (AbstractMultivariateTraitLikelihood) xo.getChild(AbstractMultivariateTraitLikelihood.class);
Parameter locationDrift = (Parameter) xo.getChild(Parameter.class);
return new DriftedTraitsLogger(multivariateTraits, locationDrift);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
new ElementRule(AbstractMultivariateTraitLikelihood.class, "The tree trait provider which is to be drifted."),
new ElementRule(Parameter.class, "The parameter specifying location drift rate.")
};
public String getParserDescription() {
return null;
}
public String getExample() {
return null;
}
public Class getReturnType() {
return TreeTraitProvider.class;
}
};
}
