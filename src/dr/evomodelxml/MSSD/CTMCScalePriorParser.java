package dr.evomodelxml.MSSD;
import dr.app.beagle.evomodel.substmodel.SubstitutionModel;
import dr.evomodel.MSSD.CTMCScalePrior;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.Parameter;
import dr.xml.*;
import java.util.logging.Logger;
public class CTMCScalePriorParser extends AbstractXMLObjectParser {
public static final String MODEL_NAME = "ctmcScalePrior";
public static final String SCALEPARAMETER = "ctmcScale";
public static final String RECIPROCAL = "reciprocal";
public static final String TRIAL = "trial";
public String getParserName() {
return MODEL_NAME;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
TreeModel treeModel = (TreeModel) xo.getChild(TreeModel.class);
Parameter ctmcScale = (Parameter) xo.getElementFirstChild(SCALEPARAMETER);
boolean reciprocal = xo.getAttribute(RECIPROCAL, false);
boolean trial = xo.getAttribute(TRIAL, false);
SubstitutionModel substitutionModel = (SubstitutionModel) xo.getChild(SubstitutionModel.class);
Logger.getLogger("dr.evolution").info("\n ---------------------------------\nCreating ctmcScalePrior model.");
Logger.getLogger("dr.evolution").info("\tIf you publish results using this prior, please reference:");
Logger.getLogger("dr.evolution").info("\t\t 1. Ferreira and Suchard (2008) for the conditional reference prior on CTMC scale parameter prior;");
return new CTMCScalePrior(MODEL_NAME, ctmcScale, treeModel, reciprocal, substitutionModel, trial);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "This element represents the prior for CTMC scale parameter.";
}
public Class getReturnType() {
return CTMCScalePrior.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
new ElementRule(TreeModel.class),
new ElementRule(SCALEPARAMETER, new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
AttributeRule.newBooleanRule(RECIPROCAL, true),
new ElementRule(SubstitutionModel.class, true),
AttributeRule.newBooleanRule(TRIAL, true),
};
}
