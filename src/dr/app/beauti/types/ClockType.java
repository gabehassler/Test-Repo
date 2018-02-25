package dr.app.beauti.types;
public enum ClockType {
STRICT_CLOCK("Strict clock"),
UNCORRELATED("Uncorrelated relaxed clock"),
RANDOM_LOCAL_CLOCK("Random local clock"),
FIXED_LOCAL_CLOCK("Fixed local clock"),
AUTOCORRELATED("Autocorrelated relaxed clock");
ClockType(String displayName) {
this.displayName = displayName;
}
public String toString() {
return displayName;
}
private final String displayName;
final public static String LOCAL_CLOCK = "localClock";
final public static String UCED_MEAN = "uced.mean";
final public static String UCLD_MEAN = "ucld.mean";
final public static String UCLD_STDEV = "ucld.stdev";
final public static String UCGD_SCALE = "ucgd.scale";
final public static String UCGD_SHAPE = "ucgd.shape";
final public static String ACLD_MEAN = "acld.mean";
final public static String ACLD_STDEV = "acld.stdev";
}