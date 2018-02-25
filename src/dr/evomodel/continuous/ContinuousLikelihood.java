package dr.evomodel.continuous;
import dr.evolution.continuous.ContinuousTraitLikelihood;
import dr.evolution.continuous.Contrastable;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.Likelihood;
import dr.xml.*;
import java.util.ArrayList;
public class ContinuousLikelihood extends Likelihood.Abstract {
public static final String CONTINUOUS_LIKELIHOOD = "continuousLikelihood";	
public static final String TRAIT = "trait";	
public static final String NAME = "name";	
public ContinuousLikelihood(TreeModel treeModel, String[] traitNames) {
super(treeModel);
this.treeModel = treeModel;
this.traitNames = traitNames;
mles = new Contrastable[traitNames.length];
}
public double calculateLogLikelihood() {
return contTraitLikelihood.calculateLikelihood(treeModel, traitNames, mles, 1.0);
}
public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
public String getParserName() { return CONTINUOUS_LIKELIHOOD; }
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
TreeModel treeModel = (TreeModel)xo.getChild(TreeModel.class);
ArrayList<String> traits = new ArrayList<String>();
for (int i = 0; i < xo.getChildCount(); i++) {
Object child = xo.getChild(i);
if  (child instanceof XMLObject) {
XMLObject cxo = (XMLObject)child;
if (cxo.getName().equals(TRAIT)) {
traits.add(cxo.getStringAttribute(NAME));
}
} else {
throw new XMLParseException("unknown child element found in continuousLikelihood");
}
}
if (treeModel == null)
throw new XMLParseException("tree model element missing from continuousLikelihood element");
String[] traitNames = new String[traits.size()];
for (int i =0; i < traitNames.length; i++) { traitNames[i] = traits.get(i); }
ContinuousLikelihood cl = new ContinuousLikelihood(treeModel, traitNames);
return cl;
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "A likelihood for continuous traits.";
}
public Class getReturnType() { return ContinuousLikelihood.class; }
public XMLSyntaxRule[] getSyntaxRules() { return rules; }
private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
new ElementRule(TreeModel.class )
};
};
private String[] traitNames = null;
private Contrastable[] mles = null;
private TreeModel treeModel = null;
private ContinuousTraitLikelihood contTraitLikelihood = new ContinuousTraitLikelihood();
}