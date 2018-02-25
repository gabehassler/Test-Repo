package dr.evomodel.coalescent.structure;
import dr.evolution.colouring.ColourChangeMatrix;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
public abstract class MigrationModel extends AbstractModel
{
public MigrationModel(String name) { super(name); }
// general functions
public abstract ColourChangeMatrix getMigrationMatrix();
public abstract double[] getMigrationRates(double time);
// **************************************************************
// Model IMPLEMENTATION
// **************************************************************
protected void handleModelChangedEvent(Model model, Object object, int index) {
// no intermediates need to be recalculated...
}
protected void storeState() {} // no additional state needs storing
protected void restoreState() {} // no additional state needs restoring
protected void acceptState() {} // no additional state needs accepting
}