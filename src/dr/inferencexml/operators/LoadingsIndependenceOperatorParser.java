package dr.inferencexml.operators;
import dr.inference.distribution.DistributionLikelihood;
import dr.inference.model.LatentFactorModel;
import dr.inference.operators.CoercionMode;
import dr.inference.operators.LoadingsIndependenceOperator;
import dr.xml.*;
public class LoadingsIndependenceOperatorParser extends AbstractXMLObjectParser {
    public static final String LOADINGS_GIBBS_OPERATOR = "loadingsIndependenceOperator";
    public static final String WEIGHT = "weight";
    private final String RANDOM_SCAN = "randomScan";
    private final String SCALE_FACTOR = "scaleFactor";
    @Override
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        CoercionMode mode = CoercionMode.parseMode(xo);
        String scaleFactorTemp = (String) xo.getAttribute(SCALE_FACTOR);
        double scaleFactor = Double.parseDouble(scaleFactorTemp);
        String weightTemp = (String) xo.getAttribute(WEIGHT);
        double weight = Double.parseDouble(weightTemp);
        LatentFactorModel LFM = (LatentFactorModel) xo.getChild(LatentFactorModel.class);
        DistributionLikelihood prior = (DistributionLikelihood) xo.getChild(DistributionLikelihood.class);
        boolean randomScan = xo.getAttribute(RANDOM_SCAN, true);
        return new LoadingsIndependenceOperator(LFM, prior, weight, randomScan, scaleFactor, mode);  //To change body of implemented methods use File | Settings | File Templates.
    }
    @Override
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
            new ElementRule(LatentFactorModel.class),
            new ElementRule(DistributionLikelihood.class),
//            new ElementRule(CompoundParameter.class),
            AttributeRule.newDoubleRule(WEIGHT),
    };
    @Override
    public String getParserDescription() {
        return "Gibbs sampler for the loadings matrix of a latent factor model";  //To change body of implemented methods use File | Settings | File Templates.
    }
    @Override
    public Class getReturnType() {
        return LoadingsIndependenceOperator.class;  //To change body of implemented methods use File | Settings | File Templates.
    }
    @Override
    public String getParserName() {
        return LOADINGS_GIBBS_OPERATOR;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
