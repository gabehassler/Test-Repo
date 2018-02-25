package dr.evolution.coalescent;
public class CataclysmicDemographic extends ExponentialGrowth {
//
// Public stuff
//
public CataclysmicDemographic(Type units) {
super(units);
}
public final double getDeclineRate() { return d; }
public void setDeclineRate(double d) { 
if (d <= 0) throw new IllegalArgumentException();
this.d = d; 
}
public final double getCataclysmTime() { return catTime; }
public final void setCataclysmTime(double t) { 
if (t <= 0) throw new IllegalArgumentException();
catTime = t; 
}
public final void setSpikeFactor(double f) {
setDeclineRate( Math.log(f) / catTime );
}
// Implementation of abstract methods
public double getDemographic(double t) {
double d = getDeclineRate();
if (t < catTime) {
return getN0() * Math.exp(t * d);
} else {
double spikeHeight = getN0() * Math.exp(catTime * d);
//System.out.println("Spike height = " + spikeHeight);
t -= catTime;
double r = getGrowthRate();
if (r == 0) {
return spikeHeight;
} else {
return spikeHeight * Math.exp(-t * r);
}
}
}
public double getIntensity(double t) {
double d = getDeclineRate();
double r = getGrowthRate();
if (t < catTime) {
return (Math.exp(t*-d)-1.0)/getN0()/-d;
} else {
double intensityUpToSpike = (Math.exp(catTime*-d)-1.0)/getN0()/-d;
double spikeHeight = getN0() * Math.exp(catTime * d);
t -= catTime;
//System.out.println("Spike height = " + spikeHeight);
if (r == 0) {
return t/spikeHeight + intensityUpToSpike;
} else {
return (Math.exp(t*r)-1.0)/spikeHeight/r + intensityUpToSpike;
}
}
}
public double getInverseIntensity(double x) {
double d = getDeclineRate();
double r = getGrowthRate();
double intensityUpToSpike = (Math.exp(catTime*-d)-1.0)/getN0()/-d;
if(x < intensityUpToSpike){
return -Math.log(1.0 - getN0() * d * x)/d;
}
double spikeHeight = getN0() * Math.exp(catTime * d);
x -= intensityUpToSpike;
if(r == 0){
return spikeHeight*x + catTime;
}
return catTime + Math.log(1.0 + spikeHeight * x * r)/r;
//throw new UnsupportedOperationException();
}
public int getNumArguments() {
return 4;
}
public String getArgumentName(int n) {
switch (n) {
case 0: return "N0";
case 1: return "r";
case 2: return "d";
case 3: return "t";
default: throw new IllegalArgumentException();
}
}
public double getArgument(int n) {
switch (n) {
case 0: return getN0();
case 1: return getGrowthRate();
case 2: return getDeclineRate();
case 3: return getCataclysmTime();
default: throw new IllegalArgumentException();
}
}
public void setArgument(int n, double value) {
switch (n) {
case 0: setN0(value); break;
case 1: setGrowthRate(value); break;
case 2: setDeclineRate(value); break;
case 3: setCataclysmTime(value); break;
default: throw new IllegalArgumentException();
}
}
public DemographicFunction getCopy() {
CataclysmicDemographic df = new CataclysmicDemographic(getUnits());
df.setN0(getN0());
df.setGrowthRate(getGrowthRate());
df.d = d;
df.catTime = catTime;
return df;
}
//
// private stuff
//
private double d;
private double catTime;	
}
