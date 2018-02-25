package dr.inference.operators;
import dr.inference.model.Parameter;
import java.util.List;
public class SwapParameterGibbsOperator extends SwapParameterOperator implements GibbsOperator {
public SwapParameterGibbsOperator(List<Parameter> parameterList, double weight) {
super(parameterList, weight);
}
public int getStepCount() {
return 1;
}
public String getOperatorName() {
return "GIBBS." + super.getOperatorName();
}
}
