package dr.inference.operators;
import dr.inference.distribution.DistributionLikelihood;
import dr.inference.distribution.GammaDistributionModel;
import dr.inference.distribution.LogNormalDistributionModel;
import dr.inference.distribution.NormalDistributionModel;
import dr.inference.model.Parameter;
import dr.math.MathUtils;
import dr.math.distributions.Distribution;
import dr.math.distributions.GammaDistribution;
import dr.util.Attribute;
import dr.xml.*;
import java.util.List;
public class NormalGammaPrecisionGibbsOperator extends SimpleMCMCOperator implements GibbsOperator {
public static final String OPERATOR_NAME = "normalGammaPrecisionGibbsOperator";
public static final String LIKELIHOOD = "likelihood";
public static final String PRIOR = "prior";
public NormalGammaPrecisionGibbsOperator(DistributionLikelihood inLikelihood, Distribution prior,
double weight) {
if (!(prior instanceof GammaDistribution || prior instanceof GammaDistributionModel))
throw new RuntimeException("Precision prior must be Gamma");
Distribution likelihood = inLikelihood.getDistribution();
this.dataList = inLikelihood.getDataList();
if (likelihood instanceof NormalDistributionModel) {
this.precisionParameter = (Parameter) ((NormalDistributionModel) likelihood).getPrecision();
this.meanParameter = (Parameter) ((NormalDistributionModel) likelihood).getMean();
} else if (likelihood instanceof LogNormalDistributionModel) {
this.precisionParameter = ((LogNormalDistributionModel) likelihood).getPrecisionParameter();
this.meanParameter = ((LogNormalDistributionModel) likelihood).getMeanParameter();
isLog = true;
} else
throw new RuntimeException("Likelihood must be Normal or log Normal");
if (precisionParameter == null)
throw new RuntimeException("Must characterize likelihood in terms of a precision parameter");
this.prior = prior;
setWeight(weight);
}
public String getPerformanceSuggestion() {
return null;
}
public String getOperatorName() {
return OPERATOR_NAME;
}
public double doOperation() throws OperatorFailedException {
final double priorMean = prior.mean();
final double priorVariance = prior.variance();
double priorRate;
double priorShape;
if (priorMean == 0) {
priorRate = 0;
priorShape = -0.5; // Uninformative prior
} else {
priorRate = priorMean / priorVariance;
priorShape = priorMean * priorRate;
}
// Calculate weighted sum-of-squares
final double mu = meanParameter.getParameterValue(0);
double SSE = 0;
int n = 0;
for (Attribute<double[]> statistic : dataList) {
for (double x : statistic.getAttributeValue()) {
if (isLog) {
final double logX = Math.log(x);
SSE += (logX - mu) * (logX - mu);
} else {
SSE += (x - mu) * (x - mu);
}
n++;
}
}
final double shape = priorShape + n / 2.0;
final double rate = priorRate + 0.5 * SSE;
final double draw = MathUtils.nextGamma(shape, rate); // Gamma( \alpha + n/2 , \beta + (1/2)*SSE )
precisionParameter.setParameterValue(0, draw);
return 0;
}
public int getStepCount() {
return 1;
}
public static dr.xml.XMLObjectParser PARSER = new dr.xml.AbstractXMLObjectParser() {
public String getParserName() {
return OPERATOR_NAME;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
double weight = xo.getDoubleAttribute(WEIGHT);
DistributionLikelihood likelihood = (DistributionLikelihood) ((XMLObject) xo.getChild(LIKELIHOOD)).getChild(DistributionLikelihood.class);
DistributionLikelihood prior = (DistributionLikelihood) ((XMLObject) xo.getChild(PRIOR)).getChild(DistributionLikelihood.class);
//            System.err.println("class: " + prior.getDistribution().getClass());
if (!((prior.getDistribution() instanceof GammaDistribution) ||
(prior.getDistribution() instanceof GammaDistributionModel)
) ||
!((likelihood.getDistribution() instanceof NormalDistributionModel) ||
(likelihood.getDistribution() instanceof LogNormalDistributionModel)
))
throw new XMLParseException("Gibbs operator assumes normal-gamma model");
return new NormalGammaPrecisionGibbsOperator(likelihood, prior.getDistribution(), weight);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "This element returns a operator on the precision parameter of a normal model with gamma prior.";
}
public Class getReturnType() {
return MCMCOperator.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
AttributeRule.newDoubleRule(WEIGHT),
new ElementRule(LIKELIHOOD,
new XMLSyntaxRule[]{
new ElementRule(DistributionLikelihood.class)
}),
new ElementRule(PRIOR,
new XMLSyntaxRule[]{
new ElementRule(DistributionLikelihood.class)
}),
};
};
private final Distribution prior;
private boolean isLog = false;
private final List<Attribute<double[]>> dataList;
private final Parameter meanParameter;
private final Parameter precisionParameter;
}
