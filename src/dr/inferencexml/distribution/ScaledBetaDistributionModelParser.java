package dr.inferencexml.distribution;
import dr.inference.distribution.BetaDistributionModel;
import dr.inference.distribution.ParametricDistributionModel;
import dr.inference.model.Parameter;
public class ScaledBetaDistributionModelParser extends BetaDistributionModelParser {
    public static final String LENGTH = "length";
    public static final String SCALED_BETA_DISTRIBUTION_MODEL = "scaledBetaDistributionModel";
    public String getParserName() {
        return SCALED_BETA_DISTRIBUTION_MODEL;
    }
    ParametricDistributionModel parseDistributionModel(Parameter[] parameters, double offset) {
        return new BetaDistributionModel(parameters[0], parameters[1], offset, parameters[2].getParameterValue(0));
    }
    public String[] getParameterNames() {
        return new String[]{ALPHA, BETA, LENGTH};
    }
    public String getParserDescription() {
        return "A model of a beta distribution allowing offset and scale.";
    }
    public boolean allowOffset() {
        return true;
    }
    public Class getReturnType() {
        return BetaDistributionModel.class;
    }
}
