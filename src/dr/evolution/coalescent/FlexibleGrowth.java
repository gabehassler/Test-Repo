package dr.evolution.coalescent;
public class FlexibleGrowth extends PowerLawGrowth
{
//
// Public stuff
//
public FlexibleGrowth(Type units) {
super(units);
}
public double getK(){
return K;
}
public void setK(double K) {
this.K = K;
}
// Implementation of abstract methods
public double getDemographic(double t) {
if(t>0){
throw new RuntimeException("Negative times only! t="+t);
}
return getN0()*K*Math.pow(-t,getR())/(1+K*Math.pow(-t, getR()-1));
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
return 1/getN0() * (1/((getR()-1)*K) * (Math.pow(-finish, -getR() + 1) - Math.pow(-start, -getR() +1 )) + Math.log((-start)/(-finish)));
}
public int getNumArguments() {
return 3;
}
public String getArgumentName(int n) {
switch (n) {
case 0:
return "N0";
case 1:
return "r";
case 2:
return "K";
}
throw new IllegalArgumentException("Argument " + n + " does not exist");
}
public double getArgument(int n) {
switch (n) {
case 0:
return getN0();
case 1:
return getR();
case 2:
return getK();
}
throw new IllegalArgumentException("Argument " + n + " does not exist");
}
public void setArgument(int n, double value) {
switch (n) {
case 0:
setN0(value);
break;
case 1:
setR(value);
break;
case 2:
setK(value);
break;
default:
throw new IllegalArgumentException("Argument " + n + " does not exist");
}
}
public double getLowerBound(int n) {
switch (n) {
case 0:
return 0;
case 1:
return Double.NEGATIVE_INFINITY;
case 2:
return 0;
default:
throw new IllegalArgumentException("Argument " + n + " does not exist");
}
}
public double getUpperBound(int n) {
return Double.POSITIVE_INFINITY;
}
public DemographicFunction getCopy() {
FlexibleGrowth df = new FlexibleGrowth(getUnits());
df.setN0(getN0());
df.setR(getR());
df.K = K;
return df;
}
//
// private stuff
//
private double K;
}
