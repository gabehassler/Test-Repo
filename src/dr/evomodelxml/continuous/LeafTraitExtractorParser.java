package dr.evomodelxml.continuous;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.tree.TreeModelParser;
import dr.inference.model.CompoundParameter;
import dr.inference.model.Parameter;
import dr.xml.*;
public class LeafTraitExtractorParser extends AbstractXMLObjectParser {
public static final String NAME = "leafTraitParameter";
public static final String SET_BOUNDS = "setBounds";
@Override
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
TreeModel model = (TreeModel) xo.getChild(TreeModel.class);
final CompoundParameter allTraits = (CompoundParameter) xo.getChild(CompoundParameter.class);
String taxonString = (String) xo.getAttribute(TreeModelParser.TAXON);
final int leafIndex = model.getTaxonIndex(taxonString);
if (leafIndex == -1) {
throw new XMLParseException("Unable to find taxon '" + taxonString + "' in trees.");
}
final Parameter leafTrait = allTraits.getParameter(leafIndex);
boolean setBounds = xo.getAttribute(SET_BOUNDS, true);
if (setBounds) {
Parameter.DefaultBounds bound = new Parameter.DefaultBounds(Double.MAX_VALUE, -Double.MAX_VALUE,
leafTrait.getDimension());
leafTrait.addBounds(bound);
}
return leafTrait;
}
@Override
public XMLSyntaxRule[] getSyntaxRules() {
return new XMLSyntaxRule[]{
AttributeRule.newStringRule(TreeModelParser.TAXON),
AttributeRule.newBooleanRule(SET_BOUNDS, true),
new ElementRule(TreeModel.class),
new ElementRule(CompoundParameter.class),
};
}
@Override
public String getParserDescription() {
return "Parses the leaf trait parameter out of the compound parameter of an integrated trait likelihood";
}
@Override
public Class getReturnType() {
return Parameter.class;
}
public String getParserName() {
return NAME;
}
}
