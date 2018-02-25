package dr.evomodelxml.tree;
import dr.evomodel.tree.*;
import dr.inference.model.Parameter;
import dr.xml.*;
import java.util.logging.Logger;
public class TransformedTreeModelParser extends AbstractXMLObjectParser {
public static final String TRANSFORMED_TREE_MODEL = "transformedTreeModel";
public static final String VERSION = "version";
public String getParserName() {
return TRANSFORMED_TREE_MODEL;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
TreeModel tree = (TreeModel) xo.getChild(TreeModel.class);
Parameter scale = (Parameter) xo.getChild(Parameter.class);
String id = tree.getId();
if (!xo.hasId()) {
//            System.err.println("No check!");
id = "transformed." + id;
} else {
//            System.err.println("Why am I here?");
id = xo.getId();
}
Logger.getLogger("dr.evomodel").info("Creating a transformed tree model, '" + id + "'");
TreeTransform transform;
String version = xo.getAttribute(VERSION, "generic");
if (version.compareTo("new") == 0) {
transform = new ProgressiveScalarTreeTransform(scale);
} else if (version.compareTo("branch") == 0) {
transform = new ProgressiveScalarTreeTransform(tree, scale);
} else {
transform = new SingleScalarTreeTransform(scale);
}
return new TransformedTreeModel(id, tree, transform);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "This element represents a transformed model of the tree.";
}
public Class getReturnType() {
return TransformedTreeModel.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules =
new XMLSyntaxRule[]{
new ElementRule(TreeModel.class),
new ElementRule(Parameter.class),
AttributeRule.newStringRule(VERSION, true),
};
}
