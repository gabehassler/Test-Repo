package dr.inference.operators;
import dr.inference.model.Parameter;
import dr.math.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class SetOperator extends SimpleMCMCOperator {
    public SetOperator(Parameter parameter, double[] values) {
        this.parameter = parameter;
        this.values = values;
    }
    public Parameter getParameter() {
        return parameter;
    }
    public final double doOperation() throws OperatorFailedException {
        int index = MathUtils.nextInt(values.length);
        double newValue = values[index];
        if (newValue < parameter.getBounds().getLowerLimit(index) || newValue > parameter.getBounds().getUpperLimit(index)) {
            throw new OperatorFailedException("proposed value outside boundaries");
        }
        parameter.setParameterValue(index, newValue);
        return 0.0;
    }
    public Element createOperatorElement(Document document) {
        throw new RuntimeException("Not implememented!");
    }
    public String getOperatorName() {
        return "setOperator(" + parameter.getParameterName() + ")";
    }
    public String getPerformanceSuggestion() {
        return "No suggestions";
    }
    //PRIVATE STUFF
    private Parameter parameter = null;
    private double[] values;
}
