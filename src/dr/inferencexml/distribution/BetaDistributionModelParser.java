package dr.inferencexml.distribution;
import dr.inference.distribution.BetaDistributionModel;
import dr.inference.distribution.ParametricDistributionModel;
import dr.inference.model.Parameter;
public class BetaDistributionModelParser extends DistributionModelParser {
    public static final String ALPHA = "alpha";
    public static final String BETA = "beta";
    public String getParserName() {
        return BetaDistributionModel.BETA_DISTRIBUTION_MODEL;
    }
    ParametricDistributionModel parseDistributionModel(Parameter[] parameters, double offset) {
        return new BetaDistributionModel(parameters[0], parameters[1]);
    }
    public String[] getParameterNames() {
        return new String[]{ALPHA, BETA};
    }
    public String getParserDescription() {
        return "A model of a beta distribution.";
    }
    public boolean allowOffset() {
        return false;
    }
    public Class getReturnType() {
        return BetaDistributionModel.class;
    }
}
