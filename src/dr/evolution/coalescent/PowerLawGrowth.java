package dr.evolution.coalescent;
public class PowerLawGrowth extends LinearGrowth
{
//
// Public stuff
//
public PowerLawGrowth(Type units) {
super(units);
}
public double getR(){
return r;
}
public void setR(double r) {
this.r = r;
}
// Implementation of abstract methods
public double getDemographic(double t) {
if(t>0){
throw new RuntimeException("Negative times only!");
}
return getN0()*Math.pow(-t,r);
}
public double getIntensity(double t) {
throw new RuntimeException("getIntensity is not implemented (and not finite); use getIntegral instead");
}
public double getInverseIntensity(double x) {
throw new RuntimeException("Not implemented");
}
public double getInverseIntegral(double x, double start) {
throw new RuntimeException("Not implemented");
}
public double getIntegral(double start, double finish) {
return (Math.pow(-finish, -r+1) - Math.pow(-start, -r+1))/(getN0()*(r-1));
}
public int getNumArguments() {
return 2;
}
public String getArgumentName(int n) {
if (n == 0) {
return "N0";
} else {
return "r";
}
}
public double getArgument(int n) {
if (n == 0) {
return getN0();
} else {
return getR();
}
}
public void setArgument(int n, double value) {
if (n == 0) {
setN0(value);
} else {
setR(value);
}
}
public double getLowerBound(int n) {
if (n == 0) {
return 0;
} else {
return 1;
}
}
public double getUpperBound(int n) {
return Double.POSITIVE_INFINITY;
}
public DemographicFunction getCopy() {
PowerLawGrowth df = new PowerLawGrowth(getUnits());
df.setN0(getN0());
df.r=r;
return df;
}
//
// private stuff
//
private double r;
}
