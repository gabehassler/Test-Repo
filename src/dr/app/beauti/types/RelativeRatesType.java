
package dr.app.beauti.types;

public enum RelativeRatesType {
	MU_RELATIVE_RATES("Codon relative rates"),
    CLOCK_RELATIVE_RATES("Clock relative rates");

	RelativeRatesType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    private final String name;
}
