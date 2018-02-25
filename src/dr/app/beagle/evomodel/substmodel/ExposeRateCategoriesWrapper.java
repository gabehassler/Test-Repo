package dr.app.beagle.evomodel.substmodel;
import dr.inference.model.Bounds;
import dr.inference.model.Model;
import dr.inference.model.ModelListener;
import dr.inference.model.Parameter;
public class ExposeRateCategoriesWrapper extends Parameter.Abstract implements ModelListener {
public ExposeRateCategoriesWrapper(MarkovModulatedSubstitutionModel mmSubstModel) {
super();
this.mmSubstModel = mmSubstModel;
}
public int getDimension() {
return mmSubstModel.getNumBaseModel();
}
@Override
protected void storeValues() {
// Do nothing
}
@Override
protected void restoreValues() {
// Do nothing
}
@Override
protected void acceptValues() {
// Do nothing
}
@Override
protected void adoptValues(Parameter source) {
// Do nothing
}
@Override
public double getParameterValue(int dim) {
return mmSubstModel.getModelRateScalar(dim);
}
@Override
public void setParameterValue(int dim, double value) {
throw new RuntimeException("Not implemented for wrapper.");
}
@Override
public void setParameterValueQuietly(int dim, double value) {
throw new RuntimeException("Not implemented for wrapper.");
}
@Override
public void setParameterValueNotifyChangedAll(int dim, double value) {
throw new RuntimeException("Not implemented for wrapper.");
}
@Override
public String getParameterName() {
return "useful name";
}
@Override
public void addBounds(Bounds<Double> bounds) {
throw new RuntimeException("Not implemented for wrapper.");
}
@Override
public Bounds<Double> getBounds() {
throw new RuntimeException("Not implemented for wrapper.");
}
@Override
public void addDimension(int index, double value) {
throw new RuntimeException("Not implemented for wrapper.");
}
@Override
public double removeDimension(int index) {
throw new RuntimeException("Not implemented for wrapper.");
}
private final MarkovModulatedSubstitutionModel mmSubstModel;
@Override
public void modelChangedEvent(Model model, Object object, int index) {
if (model == mmSubstModel) { // TODO limit to passing along only exposed value changes
fireParameterChangedEvent();
}
}
@Override
public void modelRestored(Model model) {
// Do nothing
}
}
