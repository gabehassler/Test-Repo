package dr.inference.operators;
import dr.inference.model.Bounds;
import dr.inference.model.Parameter;
import dr.inferencexml.operators.UniformOperatorParser;
import dr.math.MathUtils;
public class UniformOperator extends SimpleMCMCOperator {
    public UniformOperator(Parameter parameter, double weight) {
        this(parameter, weight, null, null);
    }
    public UniformOperator(Parameter parameter, double weight, Double lowerBound, Double upperBound) {
        this.parameter = parameter;
        setWeight(weight);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    public Parameter getParameter() {
        return parameter;
    }
    public final double doOperation() {
        final int index = MathUtils.nextInt(parameter.getDimension());
        final Bounds<Double> bounds = parameter.getBounds();
        final double lower = (lowerBound == null ? bounds.getLowerLimit(index) : Math.max(bounds.getLowerLimit(index), lowerBound));
        final double upper = (upperBound == null ? bounds.getUpperLimit(index) : Math.min(bounds.getUpperLimit(index), upperBound));
        final double newValue = (MathUtils.nextDouble() * (upper - lower)) + lower;
        parameter.setParameterValue(index, newValue);
//		System.out.println(newValue + "[" + lower + "," + upper + "]");
        return 0.0;
    }
    //MCMCOperator INTERFACE
    public final String getOperatorName() {
        return "uniform(" + parameter.getParameterName() + ")";
    }
    public final void optimize(double targetProb) {
        throw new RuntimeException("This operator cannot be optimized!");
    }
    public boolean isOptimizing() {
        return false;
    }
    public void setOptimizing(boolean opt) {
        throw new RuntimeException("This operator cannot be optimized!");
    }
    public String getPerformanceSuggestion() {
        return "";
//        final double acceptance = Utils.getAcceptanceProbability(this);
//        if ( acceptance < getMinimumAcceptanceLevel()) {
//            return "";
//        } else if ( acceptance > getMaximumAcceptanceLevel() ) {
//            return "";
//        } else {
//            return "";
//        }
    }
    public String toString() {
        return UniformOperatorParser.UNIFORM_OPERATOR + "(" + parameter.getParameterName() + ")";
    }
    //PRIVATE STUFF
    private Parameter parameter = null;
    private final Double lowerBound;
    private final Double upperBound;
}
