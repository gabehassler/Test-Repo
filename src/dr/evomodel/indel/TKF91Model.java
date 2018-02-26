package dr.evomodel.indel;
import dr.evomodelxml.indel.TKF91ModelParser;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class TKF91Model extends IndelModel {
    private final Parameter lengthDistParameter, deathRateParameter;
    public TKF91Model(Parameter lengthDistParameter, Parameter deathRateParameter, Type units) {
        super(TKF91ModelParser.TKF91_MODEL);
        this.lengthDistParameter = lengthDistParameter;
        addVariable(lengthDistParameter);
        this.deathRateParameter = deathRateParameter;
        addVariable(deathRateParameter);
        setUnits(units);
    }
    public final double getLengthDistributionValue() {
        return lengthDistParameter.getParameterValue(0);
    }
    public final double getBirthRate(int length) {
        throw new RuntimeException("Not implemented");
        //return birthRateParameter.getParameterValue(0);
    }
    public final double getDeathRate(int length) {
        return deathRateParameter.getParameterValue(0);
    }
    // *****************************************************************
    // Interface ModelComponent
    // *****************************************************************
    public String getModelComponentName() {
        return TKF91ModelParser.TKF91_MODEL;
    }
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        // Substitution model has changed so fire model changed event
        listenerHelper.fireModelChanged(this, object, index);
    }
    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        // no intermediates need to be recalculated...
    }
    protected void storeState() {
    } // no extra state apart from parameters
    protected void acceptState() {
    } // no extra state apart from parameters
    protected void restoreState() {
    } // no extra state apart from parameters
    // **************************************************************
    // XMLElement IMPLEMENTATION
    // **************************************************************
    public Element createElement(Document doc) {
        throw new RuntimeException("Not implemented!");
    }
}
