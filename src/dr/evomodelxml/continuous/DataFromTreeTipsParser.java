package dr.evomodelxml.continuous;
import dr.evolution.tree.MultivariateTraitTree;
import dr.evomodelxml.treelikelihood.TreeTraitParserUtilities;
import dr.inference.model.MatrixParameter;
import dr.inference.model.Parameter;
import dr.xml.*;
public class DataFromTreeTipsParser extends AbstractXMLObjectParser {
public final static String DATA_FROM_TREE_TIPS = "dataFromTreeTips";
public final static String DATA = "data";
public static final String CONTINUOUS = "continuous";
public String getParserName() {
return DATA_FROM_TREE_TIPS;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
TreeTraitParserUtilities utilities = new TreeTraitParserUtilities();
String traitName = (String) xo.getAttribute(TreeTraitParserUtilities.TRAIT_NAME);
MultivariateTraitTree treeModel = (MultivariateTraitTree) xo.getChild(MultivariateTraitTree.class);
TreeTraitParserUtilities.TraitsAndMissingIndices returnValue =
utilities.parseTraitsFromTaxonAttributes(xo, traitName, treeModel, true);
MatrixParameter dataParameter = MatrixParameter.recast(returnValue.traitParameter.getId(),
returnValue.traitParameter);
return dataParameter;
}
private static final XMLSyntaxRule[] rules = {
new ElementRule(MultivariateTraitTree.class),
AttributeRule.newStringRule(TreeTraitParserUtilities.TRAIT_NAME),
new ElementRule(TreeTraitParserUtilities.TRAIT_PARAMETER, new XMLSyntaxRule[]{
new ElementRule(Parameter.class)
}),
};
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
@Override
public String getParserDescription() {
return "Takes the data from the tips of a tree and puts it into a MatrixParameter";
}
@Override
public Class getReturnType() {
return MatrixParameter.class;
}
}
