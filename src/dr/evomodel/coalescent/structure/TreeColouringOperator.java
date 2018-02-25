package dr.evomodel.coalescent.structure;
import dr.evolution.colouring.ColourSampler;
import dr.inference.operators.OperatorFailedException;
import dr.inference.operators.SimpleMCMCOperator;
import dr.xml.*;
public class TreeColouringOperator extends SimpleMCMCOperator {
public static final String TREE_COLOURING_OPERATOR = "treeColouringOperator";
ColourSamplerModel colouringModel;
ColourSampler colourSampler;
public TreeColouringOperator(ColourSamplerModel colouringModel) {
this.colouringModel = colouringModel;
}
public String getPerformanceSuggestion() {
return "This operator cannot be optimized";
}
public String getOperatorName() {
return "twoColourTree";
}
public double doOperation() throws OperatorFailedException {
double logP = colouringModel.getTreeColouringWithProbability().getLogProbabilityDensity();
colouringModel.resample();
double logQ = colouringModel.getTreeColouringWithProbability().getLogProbabilityDensity();
return logP - logQ;
}
public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
public String getParserName() { return TREE_COLOURING_OPERATOR; }
public Object parseXMLObject(XMLObject xo) {
ColourSamplerModel colourSamplerModel = (ColourSamplerModel)xo.getChild(ColourSamplerModel.class);
return new TreeColouringOperator(colourSamplerModel);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public XMLSyntaxRule[] getSyntaxRules() { return rules; }
private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
new ElementRule(ColourSamplerModel.class),
};
public String getParserDescription() {
return "A tree colouring model.";
}
public Class getReturnType() { return TreeColouringOperator.class; }
};
}
