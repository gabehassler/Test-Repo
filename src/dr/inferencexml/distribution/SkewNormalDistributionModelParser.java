package dr.inferencexml.distribution;
import dr.inference.distribution.ParametricDistributionModel;
import dr.inference.distribution.SkewNormalDistributionModel;
import dr.inference.model.Parameter;
public class SkewNormalDistributionModelParser extends DistributionModelParser {
public static final String LOCATION = "location";
public static final String SCALE = "scale";
public static final String SHAPE = "shape";
public String getParserName() {
return SkewNormalDistributionModel.SKEW_NORMAL_DISTRIBUTION_MODEL;
}
ParametricDistributionModel parseDistributionModel(Parameter[] parameters, double offset) {
return new SkewNormalDistributionModel(parameters[0], parameters[1], parameters[2]);
}
public String[] getParameterNames() {
return new String[]{LOCATION, SCALE, SHAPE};
}
public String getParserDescription() {
return "A model of a skew normal distribution.";
}
public boolean allowOffset() {
return false;
}
public Class getReturnType() {
return SkewNormalDistributionModel.class;
}
}
