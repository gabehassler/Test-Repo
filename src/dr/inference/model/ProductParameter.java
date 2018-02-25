package dr.inference.model;
import java.util.List;
public class ProductParameter extends Parameter.Abstract implements VariableListener {
public ProductParameter(List<Parameter> parameter) {
this.paramList = parameter;
for (Parameter p : paramList) {
p.addVariableListener(this);
}
}
public int getDimension() {
return paramList.get(0).getDimension();
}
protected void storeValues() {
for (Parameter p : paramList) {
p.storeParameterValues();
}
}
protected void restoreValues() {
for (Parameter p : paramList) {
p.restoreParameterValues();
}
}
protected void acceptValues() {
for (Parameter p : paramList) {
p.acceptParameterValues();
}
}
protected void adoptValues(Parameter source) {
throw new RuntimeException("Not implemented");
}
public double getParameterValue(int dim) {
double value = 1.0;
for (Parameter p : paramList) {
value *= p.getParameterValue(dim);
}
return value;
}
public void setParameterValue(int dim, double value) {
throw new RuntimeException("Not implemented");
}
public void setParameterValueQuietly(int dim, double value) {
throw new RuntimeException("Not implemented");
}
public void setParameterValueNotifyChangedAll(int dim, double value){
throw new RuntimeException("Not implemented");
}
public String getParameterName() {
if (getId() == null) {
StringBuilder sb = new StringBuilder("product");
for (Parameter p : paramList) {
sb.append(".").append(p.getId());
}
setId(sb.toString());
}
return getId();
}
public void addBounds(Bounds bounds) {
this.bounds = bounds;
}
public Bounds<Double> getBounds() {
if (bounds == null) {
return paramList.get(0).getBounds(); // TODO
} else {
return bounds;
}
}
public void addDimension(int index, double value) {
throw new RuntimeException("Not yet implemented.");
}
public double removeDimension(int index) {
throw new RuntimeException("Not yet implemented.");
}
public void variableChangedEvent(Variable variable, int index, ChangeType type) {
fireParameterChangedEvent(index,type);
}
private final List<Parameter> paramList;
private Bounds bounds = null;
}
