package dr.app.beauti.components.linkedparameters;
import dr.app.beauti.options.Operator;
import dr.app.beauti.options.Parameter;
import dr.app.beauti.types.PriorType;
import java.util.List;
public class LinkedParameter {
    private String name;
    final private Parameter argumentParameter;
    final private Operator argumentOperator;
    final private LinkedParameterComponentOptions options;
    public LinkedParameter(String name, Parameter argumentParameter, Operator argumentOperator, LinkedParameterComponentOptions options) {
        this.name = name;
        this.argumentParameter = argumentParameter;
        this.argumentOperator = argumentOperator;
        this.options = options;
    }
    public Parameter getArgumentParameter() {
        return argumentParameter;
    }
    public Operator getArgumentOperator() {
        return argumentOperator;
    }
    public List<Parameter> getDependentParameterList() {
        return options.getDependentParameters(this);
    }
    public void linkDependentParameters(List<Parameter> parameterList) {
        options.setDependentParameters(this, parameterList);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        argumentParameter.setName(name);
        argumentOperator.setName(name);
        this.name = name;
    }
}
