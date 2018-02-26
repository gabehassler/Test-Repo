
package dr.evomodel.clock;

import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodel.branchratemodel.AbstractBranchRateModel;
import dr.evomodel.branchratemodel.BranchRateModel;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;


public class UniversalClock extends AbstractBranchRateModel {

    public static final String UNIVERSAL_CLOCK = "universalClock";

    public UniversalClock(
            Parameter rateParameter,
            Parameter massParameter,
            Parameter temperatureParameter,
            Parameter scaleParameter
    ) {

        super(UNIVERSAL_CLOCK);

        this.rateParameter = rateParameter;
        this.massParameter = massParameter;
        this.temperatureParameter = temperatureParameter;
        this.scaleParameter = scaleParameter;

        // don't add rate parameter, cause that is what you are changing!
        // you don't care if it changes
        addVariable(massParameter);
        addVariable(temperatureParameter);
        addVariable(scaleParameter);

    }

    // *****************************************************************
    // Interface Model
    // *****************************************************************

    protected void handleModelChangedEvent(Model model, Object object, int index) {
        // no submodels so nothing to do
    }

    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {

        if ((variable == massParameter) || (variable == temperatureParameter)) {
            if (index == -1) {
                calculateAllRates();
            } else calculateRate(index);
        } else if (variable == scaleParameter) {
            calculateAllRates();
        } else {
            throw new RuntimeException("unknown parameter changed in " + UNIVERSAL_CLOCK);
        }
    }

    protected void storeState() {
    } // no additional state needs storing

    protected void restoreState() {
    } // no additional state needs restoring

    protected void acceptState() {
    } // no additional state needs accepting

    // **************************************************************
    // Private methods
    // **************************************************************

    private void calculateAllRates() {
        int numNodes = massParameter.getDimension();
        for (int i = 0; i < numNodes; i++) {
            calculateRate(i);
        }
    }

    private void calculateRate(int index) {

        double mass = massParameter.getParameterValue(index);
        double temperature = temperatureParameter.getParameterValue(index);

        double scale = scaleParameter.getParameterValue(0);

        // replace this with the real equation!!
        double substitutionRate = scale * Math.pow(mass, -0.25) * Math.exp(temperature);

        rateParameter.setParameterValue(index, substitutionRate);
    }

    public double getBranchRate(Tree tree, NodeRef node) {
        throw new RuntimeException("Look at code before running this class!");
    }

    Parameter rateParameter = null;
    Parameter massParameter = null;
    Parameter temperatureParameter = null;
    Parameter scaleParameter = null;

}

