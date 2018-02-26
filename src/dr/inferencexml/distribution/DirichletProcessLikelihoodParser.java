package dr.inferencexml.distribution;
import dr.inference.distribution.DirichletProcessLikelihood;
import dr.inference.model.*;
import dr.xml.*;
public class DirichletProcessLikelihoodParser extends AbstractXMLObjectParser {
    public static final String ETA = "eta";
    public static final String CHI = "chi";
    public String getParserName() {
        return DirichletProcessLikelihood.DIRICHLET_PROCESS_LIKELIHOOD;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        XMLObject cxo = xo.getChild(ETA);
        Statistic etaParam = (Statistic) cxo.getChild(Statistic.class);
        cxo = xo.getChild(CHI);
        Parameter chiParameter = (Parameter) cxo.getChild(Parameter.class);
        return new DirichletProcessLikelihood(etaParam, chiParameter);
    }
    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private final XMLSyntaxRule[] rules = {
            new ElementRule(ETA,
                    new XMLSyntaxRule[]{new ElementRule(Statistic.class)}, "Counts of N items distributed amongst K classes"),
            new ElementRule(CHI,
                    new XMLSyntaxRule[]{new ElementRule(Parameter.class)}, "Aggregation parameter"),
    };
    public String getParserDescription() {
        return "Calculates the likelihood of some items distributed into a number of classes under a Dirichlet drocess.";
    }
    public Class getReturnType() {
        return Likelihood.class;
    }
}
