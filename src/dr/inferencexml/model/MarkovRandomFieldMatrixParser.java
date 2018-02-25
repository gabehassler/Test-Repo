package dr.inferencexml.model;
import dr.inference.model.MarkovRandomFieldMatrix;
import dr.inference.model.Parameter;
import dr.util.Transform;
import dr.xml.*;
public class MarkovRandomFieldMatrixParser extends AbstractXMLObjectParser {
public final static String MATRIX_PARAMETER = "markovRandomFieldMatrix";
public static final String DIAGONAL = "diagonal";
public static final String OFF_DIAGONAL = "offDiagonal";
public static final String AS_CORRELATION = "asCorrelation";
public String getParserName() {
return MATRIX_PARAMETER;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
XMLObject cxo = xo.getChild(DIAGONAL);
Parameter diagonalParameter = (Parameter) cxo.getChild(Parameter.class);
Transform.ParsedTransform tmp = (Transform.ParsedTransform) cxo.getChild(Transform.ParsedTransform.class);
Transform diagonalTransform = (tmp != null) ? tmp.transform : null;
cxo = xo.getChild(OFF_DIAGONAL);
Parameter offDiagonalParameter = (Parameter) cxo.getChild(Parameter.class);
tmp = (Transform.ParsedTransform) cxo.getChild(Transform.ParsedTransform.class);
Transform offDiagonalTransform = (tmp != null) ? tmp.transform : null;
boolean asCorrelation = xo.getAttribute(AS_CORRELATION, false);
return new MarkovRandomFieldMatrix(diagonalParameter, offDiagonalParameter, asCorrelation,
diagonalTransform, offDiagonalTransform);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "A MRF matrix parameter constructed from its diagonals and first-order off diagonal.";
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
new ElementRule(DIAGONAL,
new XMLSyntaxRule[]{
new ElementRule(Parameter.class),
new ElementRule(Transform.ParsedTransform.class, true),
}),
new ElementRule(OFF_DIAGONAL,
new XMLSyntaxRule[]{
new ElementRule(Parameter.class),
new ElementRule(Transform.ParsedTransform.class, true),
}),
AttributeRule.newBooleanRule(AS_CORRELATION, true)
};
public Class getReturnType() {
return MarkovRandomFieldMatrix.class;
}
}
