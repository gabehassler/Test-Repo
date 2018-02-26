
package dr.app.beauti.types;

public enum FixRateType {
	FIX_MEAN("Estimate relative clock rates (fixed mean)"), //
    RELATIVE_TO("Estimate clock rates relative to"),
	TIP_CALIBRATED("Tip times calibrated"), //
	NODE_CALIBRATED("Internal node(s) calibrated"), //
	RATE_CALIBRATED("Rate is calibrated"), //
    CUSTOMIZED("Customize clock rates");

	FixRateType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    private final String name;
}
