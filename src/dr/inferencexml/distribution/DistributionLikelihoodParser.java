package dr.inferencexml.distribution;
import dr.inference.distribution.DistributionLikelihood;
import dr.inference.distribution.ParametricDistributionModel;
import dr.inference.model.Statistic;
import dr.xml.*;
public class DistributionLikelihoodParser extends AbstractXMLObjectParser {
public static final String DISTRIBUTION = "distribution";
public static final String DATA = "data";
public static final String FROM = "from";
public static final String TO = "to";
public String getParserName() {
return DistributionLikelihood.DISTRIBUTION_LIKELIHOOD;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
final XMLObject cxo = xo.getChild(DISTRIBUTION);
ParametricDistributionModel model = (ParametricDistributionModel) cxo.getChild(ParametricDistributionModel.class);
DistributionLikelihood likelihood = new DistributionLikelihood(model);
XMLObject cxo1 = xo.getChild(DATA);
final int from = cxo1.getAttribute(FROM, -1);
int to = cxo1.getAttribute(TO, -1);
if (from >= 0 || to >= 0) {
if (to < 0) {
to = Integer.MAX_VALUE;
}
if (!(from >= 0 && to >= 0 && from < to)) {
throw new XMLParseException("ill formed from-to");
}
likelihood.setRange(from, to);
}
for (int j = 0; j < cxo1.getChildCount(); j++) {
if (cxo1.getChild(j) instanceof Statistic) {
likelihood.addData((Statistic) cxo1.getChild(j));
} else {
throw new XMLParseException("illegal element in " + cxo1.getName() + " element");
}
}
return likelihood;
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
new ElementRule(DISTRIBUTION,
new XMLSyntaxRule[]{new ElementRule(ParametricDistributionModel.class)}),
new ElementRule(DATA, new XMLSyntaxRule[]{
AttributeRule.newIntegerRule(FROM, true),
AttributeRule.newIntegerRule(TO, true),
new ElementRule(Statistic.class, 1, Integer.MAX_VALUE)
})
};
public String getParserDescription() {
return "Calculates the likelihood of some data given some parametric or empirical distribution.";
}
public Class getReturnType() {
return DistributionLikelihood.class;
}
}