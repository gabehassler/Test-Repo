package dr.inferencexml.distribution;
import dr.inference.distribution.EmpiricalDistributionLikelihood;
import dr.inference.distribution.SplineInterpolatedLikelihood;
import dr.inference.model.Likelihood;
import dr.inference.model.Statistic;
import dr.xml.*;
public class EmpiricalDistributionLikelihoodParser extends AbstractXMLObjectParser {
    public static final String FILE_NAME = "fileName";
    public static final String DATA = "data";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String SPLINE_INTERPOLATION = "splineInterpolation";
    public static final String DEGREE = "degree";
    public static final String INVERSE = "inverse";
    public static final String READ_BY_COLUMN = "readByColumn";
    public static final String OFFSET="offset";
    public static final String LOWER = "lower";
    public static final String UPPER = "upper";
    public String getParserName() {
        return EmpiricalDistributionLikelihood.EMPIRICAL_DISTRIBUTION_LIKELIHOOD;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        String fileName = xo.getStringAttribute(FILE_NAME);
        boolean splineInterpolation = xo.getAttribute(SPLINE_INTERPOLATION,false);
        int degree = xo.getAttribute(DEGREE,3); // Default is cubic-spline
        boolean inverse = xo.getAttribute(INVERSE,false);
        boolean byColumn = xo.getAttribute(READ_BY_COLUMN,true);
        EmpiricalDistributionLikelihood likelihood;
        if (splineInterpolation) {
            if( degree < 1 )
                throw new XMLParseException("Spline degree must be greater than zero!");
            likelihood = new SplineInterpolatedLikelihood(fileName,degree,inverse,byColumn);
        } else
            //likelihood = new EmpiricalDistributionLikelihood(fileName,inverse,byColumn);
            throw new XMLParseException("Only spline-interpolated empirical distributions are currently support");
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
        double offset = cxo1.getAttribute(OFFSET,0); 
        likelihood.setOffset(offset);
        if (cxo1.hasAttribute(LOWER) || cxo1.hasAttribute(UPPER)) {
            likelihood.setBounds(
                    cxo1.getAttribute(LOWER, Double.NEGATIVE_INFINITY),
                    cxo1.getAttribute(UPPER, Double.POSITIVE_INFINITY)
            );
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
            AttributeRule.newStringRule(FILE_NAME),
            AttributeRule.newBooleanRule(SPLINE_INTERPOLATION,true),
            AttributeRule.newIntegerRule(DEGREE,true),
            AttributeRule.newBooleanRule(INVERSE,true),
            AttributeRule.newBooleanRule(READ_BY_COLUMN,true),
            new ElementRule(DATA, new XMLSyntaxRule[]{
                    AttributeRule.newIntegerRule(FROM, true),
                    AttributeRule.newIntegerRule(TO, true),
                    AttributeRule.newDoubleRule(OFFSET,true),
                    AttributeRule.newDoubleRule(LOWER, true),
                    AttributeRule.newDoubleRule(UPPER, true),
                    new ElementRule(Statistic.class, 1, Integer.MAX_VALUE)
            })
    };
    public String getParserDescription() {
        return "Calculates the likelihood of some data given some empirically-generated distribution.";
    }
    public Class getReturnType() {
        return Likelihood.class;
    }
}