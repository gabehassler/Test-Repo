package dr.evomodel.substmodel;
import dr.evolution.datatype.DataType;
import dr.inference.distribution.LogLinearModel;
import dr.inference.loggers.LogColumn;
import dr.inference.model.BayesianStochasticSearchVariableSelection;
import dr.inference.model.Model;
public class GLMSubstitutionModel extends ComplexSubstitutionModel {
public GLMSubstitutionModel(String name, DataType dataType, FrequencyModel rootFreqModel,
LogLinearModel glm) {
super(name, dataType, rootFreqModel, null);
this.glm = glm;
addModel(glm);
testProbabilities = new double[stateCount*stateCount];
}
public double[] getRates() {
return glm.getXBeta();
}
protected void handleModelChangedEvent(Model model, Object object, int index) {
if (model == glm) {
updateMatrix = true;
fireModelChanged();
}
else
super.handleModelChangedEvent(model,object,index);       
}
public LogColumn[] getColumns() {
return glm.getColumns();
}
public double getLogLikelihood() {
double logL = super.getLogLikelihood();
if (logL == 0 &&
BayesianStochasticSearchVariableSelection.Utils.connectedAndWellConditioned(testProbabilities,this)) { // Also check that graph is connected
return 0;
}
return Double.NEGATIVE_INFINITY;
}   
private LogLinearModel glm;
private double[] testProbabilities;    
}