package dr.evomodel.speciation;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.Taxon;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import java.util.Set;
public class BirthDeathSerialSamplingModel extends MaskableSpeciationModel {
// R0
Variable<Double> R0;
// recovery rate
Variable<Double> recoveryRate;
// sampling probability
Variable<Double> samplingProbability;
// birth rate
Variable<Double> lambda;
// death rate
Variable<Double> mu;
// serial sampling rate
Variable<Double> psi;
// extant sampling proportion
Variable<Double> p;
//boolean death rate is relative?
boolean relativeDeath = false;
// boolean stating whether sampled individuals remain infectious, or become non-infectious
//    boolean sampledIndividualsRemainInfectious = false; // replaced by r
//    the additional parameter 0 <= r <= 1 has to be estimated.
//    for r=1, this is sampledRemainInfectiousProb=0
//    for r=0, this is sampledRemainInfectiousProb=1
Variable<Double> r;
//Variable<Double> finalTimeInterval;
boolean hasFinalSample = false;
// the origin of the infection, x0 > tree.getRoot();
Variable<Double> origin;
public BirthDeathSerialSamplingModel(
Variable<Double> lambda,
Variable<Double> mu,
Variable<Double> psi,
Variable<Double> p,
boolean relativeDeath,
Variable<Double> r,
boolean hasFinalSample,
Variable<Double> origin,
Type units) {
this("birthDeathSerialSamplingModel", lambda, mu, psi, p, relativeDeath, r, hasFinalSample, origin, units);
}
public BirthDeathSerialSamplingModel(
String modelName,
Variable<Double> lambda,
Variable<Double> mu,
Variable<Double> psi,
Variable<Double> p,
boolean relativeDeath,
Variable<Double> r,
boolean hasFinalSample,
Variable<Double> origin,
Type units) {
super(modelName, units);
this.relativeDeath = relativeDeath;
this.lambda = lambda;
addVariable(lambda);
lambda.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.mu = mu;
addVariable(mu);
mu.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.psi = psi;
addVariable(psi);
psi.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.p = p;
addVariable(p);
p.addBounds(new Parameter.DefaultBounds(1.0, 0.0, 1));
this.hasFinalSample = hasFinalSample;
this.r = r;
addVariable(r);
r.addBounds(new Parameter.DefaultBounds(1.0, 0.0, 1));
this.origin = origin;
if (origin != null) {
addVariable(origin);
origin.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
}
}
public BirthDeathSerialSamplingModel(
String modelName,
Variable<Double> R0,
Variable<Double> recoveryRate,
Variable<Double> samplingProbability,
Variable<Double> origin,
Type units) {
super(modelName, units);
this.relativeDeath = false;
this.hasFinalSample = false;
this.R0 = R0;
addVariable(R0);
R0.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.recoveryRate = recoveryRate;
addVariable(recoveryRate);
recoveryRate.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.samplingProbability = samplingProbability;
addVariable(samplingProbability);
samplingProbability.addBounds(new Parameter.DefaultBounds(1.0, 0.0, 1));
this.origin = origin;
if (origin != null) {
addVariable(origin);
origin.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
}
}
public static double p0(double b, double d, double p, double psi, double t) {
double c1 = c1(b, d, psi);
double c2 = c2(b, d, p, psi);
double expc1trc2 = Math.exp(-c1 * t) * (1.0 - c2);
return (b + d + psi + c1 * ((expc1trc2 - (1.0 + c2)) / (expc1trc2 + (1.0 + c2)))) / (2.0 * b);
}
public static double q(double b, double d, double p, double psi, double t) {
double c1 = c1(b, d, psi);
double c2 = c2(b, d, p, psi);
//        double res = 2.0 * (1.0 - c2 * c2) + Math.exp(-c1 * t) * (1.0 - c2) * (1.0 - c2) + Math.exp(c1 * t) * (1.0 + c2) * (1.0 + c2);
double res = c1 * t + 2.0 * Math.log( Math.exp(-c1 * t) * (1.0 - c2) + (1.0 + c2) ); // operate directly in logspace, c1 * t too big
return res;
}
private static double c1(double b, double d, double psi) {
return Math.abs(Math.sqrt(Math.pow(b - d - psi, 2.0) + 4.0 * b * psi));
}
private static double c2(double b, double d, double p, double psi) {
return -(b - d - 2.0 * b * p - psi) / c1(b, d, psi);
}
public double p0(double t) {
return p0(birth(), death(), p(), psi(), t);
}
public double q(double t) {
return q(birth(), death(), p(), psi(), t);
}
private double c1() {
return c1(birth(), death(), psi());
}
private double c2() {
return c2(birth(), death(), p(), psi());
}
public double birth() {
if (mask != null) return mask.birth();
if (lambda != null) {
return lambda.getValue(0);
} else {
double r0 = R0.getValue(0);
double rr = recoveryRate.getValue(0);
return r0 * rr;
}
}
public double death() {
if (mask != null) return mask.death();
if (mu != null) {
return relativeDeath ? mu.getValue(0) * birth() : mu.getValue(0);
} else {
double rr = recoveryRate.getValue(0);
double sp = samplingProbability.getValue(0);
return rr * (1.0 - sp);
}
}
public double psi() {
if (mask != null) return mask.psi();
if (psi != null) {
return psi.getValue(0);
} else {
double rr = recoveryRate.getValue(0);
double sp = samplingProbability.getValue(0);
return rr * sp;
}
}
public double p() {
if (mask != null) return mask.p.getValue(0);
return hasFinalSample ? p.getValue(0) : 0;
}
// The mask does not affect the following three methods
public boolean isSamplingOrigin() {
return origin != null;
}
public double x0() {
return origin.getValue(0);
}
public final double calculateTreeLogLikelihood(Tree tree) {
if (isSamplingOrigin() && x0() < tree.getNodeHeight(tree.getRoot())) {
return Double.NEGATIVE_INFINITY;
//            throw new RuntimeException("Orign value (" + x0() + ") cannot < tree root height (" + tree.getNodeHeight(tree.getRoot()) + ")");
}
//System.out.println("calculating tree log likelihood");
//double time = finalTimeInterval();
// extant leaves
int n = 0;
// extinct leaves
int m = 0;
for (int i = 0; i < tree.getExternalNodeCount(); i++) {
NodeRef node = tree.getExternalNode(i);
if (tree.getNodeHeight(node) == 0.0) {
n += 1;
} else {
m += 1;
}
}
if (!hasFinalSample && n < 1) {
throw new RuntimeException(
"For sampling-through-time model there must be at least one tip at time zero.");
}
double b = birth();
double p = p();
double logL;
if (isSamplingOrigin()) {
//            logL = Math.log(1.0 / q(x0()));
logL = - q(x0());
//System.out.println("originLogL=" + logL + " x0");
} else {
throw new RuntimeException(
"The origin must be sampled, as integrating it out is not implemented!");
// integrating out the time between the origin and the root of the tree
//double bottom = c1 * (c2 + 1) * (1 - c2 + (1 + c2) * Math.exp(c1 * x1));
//logL = Math.log(1 / bottom);
}
if (hasFinalSample) {
logL += n * Math.log(4.0 * p);
}
for (int i = 0; i < tree.getInternalNodeCount(); i++) {
double x = tree.getNodeHeight(tree.getInternalNode(i));
logL += Math.log(b) - q(x);
//System.out.println("internalNodeLogL=" + Math.log(b / q(x)));
}
for (int i = 0; i < tree.getExternalNodeCount(); i++) {
double y = tree.getNodeHeight(tree.getExternalNode(i));
if (y > 0.0) {
logL += Math.log(psi()) + q(y);
//System.out.println("externalNodeLogL=" + Math.log(psi() * (r() + (1.0 - r()) * p0(y)) * q(y)));
} else if (!hasFinalSample) {
//handle condition ending on final tip in sampling-through-time-only situation
logL += Math.log(psi()) + q(y);
//                System.out.println("externalNodeLogL=" + Math.log(psi() * q(y)));
}
}
return logL;
}
public double calculateTreeLogLikelihood(Tree tree, Set<Taxon> exclude) {
if (exclude.size() == 0) return calculateTreeLogLikelihood(tree);
throw new RuntimeException("Not implemented!");
}
public void mask(SpeciationModel mask) {
if (mask instanceof BirthDeathSerialSamplingModel) {
this.mask = (BirthDeathSerialSamplingModel) mask;
} else {
throw new IllegalArgumentException();
}
}
public void unmask() {
mask = null;
}
// if a mask exists then use the mask's parameters instead (except for origin and finalTimeInterval)
BirthDeathSerialSamplingModel mask = null;
}