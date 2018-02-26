
package dr.app.beauti.types;

public enum FrequencyPolicyType {

    ESTIMATED("Estimated"),
    EMPIRICAL("Empirical"),
    ALLEQUAL("All equal");

    FrequencyPolicyType (String displayName) {
        this.displayName = displayName;
    }

    public String toString() {
        return displayName;
    }

    private String displayName;
}
