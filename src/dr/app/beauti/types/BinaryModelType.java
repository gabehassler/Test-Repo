package dr.app.beauti.types;
public enum BinaryModelType {
    BIN_SIMPLE("Simple model"),
    BIN_COVARION("Covarion model"),
    BIN_DOLLO("Binary stochastic Dollo");
    BinaryModelType (String displayName) {
        this.displayName = displayName;
    }
    public String toString() {
        return displayName;
    }
    private String displayName;
}
