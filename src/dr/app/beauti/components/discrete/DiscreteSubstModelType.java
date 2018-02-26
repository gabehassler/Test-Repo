
package dr.app.beauti.components.discrete;

public enum DiscreteSubstModelType {
    SYM_SUBST("Symmetric substitution model"),
    ASYM_SUBST("Asymmetric substitution model");

    DiscreteSubstModelType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    private final String name;
}
