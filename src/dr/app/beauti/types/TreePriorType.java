
package dr.app.beauti.types;

public enum TreePriorType {

    CONSTANT("Coalescent: Constant Size"),
    EXPONENTIAL("Coalescent: Exponential Growth"),
    LOGISTIC("Coalescent: Logistic Growth"),
    EXPANSION("Coalescent: Expansion Growth"),
    SKYLINE("Coalescent: Bayesian Skyline"),
    EXTENDED_SKYLINE("Coalescent: Extended Bayesian Skyline Plot"),
    GMRF_SKYRIDE("Coalescent: GMRF Bayesian Skyride"),
    SKYGRID("Coalescent: Bayesian SkyGrid"),
    YULE("Speciation: Yule Process"),
    YULE_CALIBRATION("Speciation: Calibrated Yule"),
    BIRTH_DEATH("Speciation: Birth-Death Process"),
    BIRTH_DEATH_INCOMPLETE_SAMPLING("Speciation: Birth-Death Incomplete Sampling"),
    BIRTH_DEATH_SERIAL_SAMPLING("Speciation: Birth-Death Serially Sampled"),
    BIRTH_DEATH_BASIC_REPRODUCTIVE_NUMBER("Epidemiology: Birth-Death Basic Reproductive Number"),
    SPECIES_YULE("Species Tree: Yule Process"),
    SPECIES_YULE_CALIBRATION("Species Tree: Calibrated Yule"),
    SPECIES_BIRTH_DEATH("Species Tree: Birth-Death Process");

    TreePriorType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    private final String name;
}
