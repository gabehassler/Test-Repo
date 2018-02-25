package dr.app.beauti.types;
public enum PopulationSizeModelType {
CONTINUOUS_CONSTANT("Piecewise linear & constant root"),
CONTINUOUS("Piecewise linear"),
CONSTANT("Piecewise constant");
PopulationSizeModelType(String name) {
this.name = name;
}
public String toString() {
return name;
}
private final String name;
}