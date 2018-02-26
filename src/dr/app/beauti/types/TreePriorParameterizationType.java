package dr.app.beauti.types;
public enum TreePriorParameterizationType {
    GROWTH_RATE("Growth Rate"),
    DOUBLING_TIME("Doubling Time"),
    CONSTANT_SKYLINE("Piecewise-constant"),
    LINEAR_SKYLINE("Piecewise-linear"),
    UNIFORM_SKYRIDE("Uniform"),
    TIME_AWARE_SKYRIDE("Time-aware");
    TreePriorParameterizationType(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
    private final String name;
}