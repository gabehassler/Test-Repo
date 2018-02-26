
package dr.evomodel.branchratemodel;

import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodelxml.branchratemodel.StrictClockBranchRatesParser;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;

public class StrictClockBranchRates extends AbstractBranchRateModel {

    private final Parameter rateParameter;

    public StrictClockBranchRates(Parameter rateParameter) {

        super(StrictClockBranchRatesParser.STRICT_CLOCK_BRANCH_RATES);

        this.rateParameter = rateParameter;

        addVariable(rateParameter);
    }

    public void handleModelChangedEvent(Model model, Object object, int index) {
        // nothing to do
    }

    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        fireModelChanged();
    }

    protected void storeState() {
        // nothing to do
    }

    protected void restoreState() {
        // nothing to do
    }

    protected void acceptState() {
        // nothing to do
    }

    public double getBranchRate(final Tree tree, final NodeRef node) {
        return rateParameter.getParameterValue(0);
    }

}