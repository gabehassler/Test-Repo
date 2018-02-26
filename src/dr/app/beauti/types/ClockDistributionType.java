
package dr.app.beauti.types;

public enum ClockDistributionType {

    LOGNORMAL("Lognormal"),
    GAMMA("Gamma"),
    CAUCHY("Cauchy"),
    EXPONENTIAL ("Exponential");

    ClockDistributionType(String displayName) {
        this.displayName = displayName;
    }

    public String toString() {
        return displayName;
    }

    private final String displayName;
}
