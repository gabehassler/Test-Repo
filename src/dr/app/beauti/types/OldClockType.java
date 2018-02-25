package dr.app.beauti.types;
public enum OldClockType {
STRICT_CLOCK("Strict clock", ClockType.STRICT_CLOCK),
UNCORRELATED_LOGNORMAL("Lognormal relaxed clock (Uncorrelated)", ClockType.UNCORRELATED, ClockDistributionType.LOGNORMAL),
UNCORRELATED_GAMMA("Gamma relaxed clock (Uncorrelated)", ClockType.UNCORRELATED, ClockDistributionType.GAMMA),
UNCORRELATED_CAUCHY("Cauchy relaxed clock (Uncorrelated)", ClockType.UNCORRELATED, ClockDistributionType.CAUCHY),
UNCORRELATED_EXPONENTIAL("Exponential relaxed clock (Uncorrelated)", ClockType.UNCORRELATED, ClockDistributionType.EXPONENTIAL),
RANDOM_LOCAL_CLOCK("Random local clock", ClockType.RANDOM_LOCAL_CLOCK),
FIXED_LOCAL_CLOCK("Fixed local clock", ClockType.FIXED_LOCAL_CLOCK),
AUTOCORRELATED("Autocorrelated relaxed clock", ClockType.AUTOCORRELATED, ClockDistributionType.LOGNORMAL);
OldClockType(String displayName, ClockType clockType) {
this(displayName, clockType, null);
}
OldClockType(String displayName, ClockType clockType, ClockDistributionType clockDistributionType) {
this.displayName = displayName;
this.clockType = clockType;
this.clockDistributionType = clockDistributionType;
}
public ClockType getClockType() {
return clockType;
}
public ClockDistributionType getClockDistributionType() {
return clockDistributionType;
}
public String toString() {
return displayName;
}
public static OldClockType getType(final ClockType clockType, final ClockDistributionType clockDistributionType) {
switch (clockType) {
case STRICT_CLOCK:
return STRICT_CLOCK;
case RANDOM_LOCAL_CLOCK:
return RANDOM_LOCAL_CLOCK;
case FIXED_LOCAL_CLOCK:
return FIXED_LOCAL_CLOCK;
case UNCORRELATED:
switch (clockDistributionType) {
case LOGNORMAL:
return UNCORRELATED_LOGNORMAL;
case GAMMA:
return UNCORRELATED_GAMMA;
case CAUCHY:
return UNCORRELATED_CAUCHY;
case EXPONENTIAL:
return UNCORRELATED_EXPONENTIAL;
default:
throw new IllegalArgumentException("Unknown clock distribution model");
}
case AUTOCORRELATED:
return AUTOCORRELATED;
default:
throw new IllegalArgumentException("Unknown clock model");
}
}
private final String displayName;
private final ClockType clockType;
private final ClockDistributionType clockDistributionType;
}