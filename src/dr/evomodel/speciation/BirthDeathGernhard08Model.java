package dr.evomodel.speciation;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodelxml.speciation.BirthDeathModelParser;
import dr.inference.model.Parameter;
import static org.apache.commons.math.special.Gamma.logGamma;
public class BirthDeathGernhard08Model extends UltrametricSpeciationModel {
public enum TreeType {
UNSCALED,     // no coefficient 
TIMESONLY,    // n!
ORIENTED,     // n
LABELED,      // 2^(n-1)/(n-1)!  (conditional on root: 2^(n-1)/n!(n-1) )
}
public static final String BIRTH_DEATH_MODEL = BirthDeathModelParser.BIRTH_DEATH_MODEL;
private Parameter relativeDeathRateParameter;
private Parameter birthDiffRateParameter;
private Parameter sampleProbability;
private TreeType type;
private boolean conditionalOnRoot;
public BirthDeathGernhard08Model(Parameter birthDiffRateParameter,
Parameter relativeDeathRateParameter,
Parameter sampleProbability,
TreeType type,
Type units) {
this(BIRTH_DEATH_MODEL, birthDiffRateParameter, relativeDeathRateParameter, sampleProbability, type, units, false);
}
public BirthDeathGernhard08Model(String modelName,
Parameter birthDiffRateParameter,
Parameter relativeDeathRateParameter,
Parameter sampleProbability,
TreeType type,
Type units, boolean conditionalOnRoot) {
super(modelName, units);
this.birthDiffRateParameter = birthDiffRateParameter;
addVariable(birthDiffRateParameter);
birthDiffRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.relativeDeathRateParameter = relativeDeathRateParameter;
if( relativeDeathRateParameter != null ) {
addVariable(relativeDeathRateParameter);
relativeDeathRateParameter.addBounds(new Parameter.DefaultBounds(1.0, 0.0, 1));
}
this.sampleProbability = sampleProbability;
if (sampleProbability != null) {
addVariable(sampleProbability);
sampleProbability.addBounds(new Parameter.DefaultBounds(1.0, 0.0, 1));
}
this.conditionalOnRoot = conditionalOnRoot;
if ( conditionalOnRoot && sampleProbability != null) {
throw new IllegalArgumentException("Not supported: birth death prior conditional on root with sampling probability.");
}
this.type = type;
}
@Override
public boolean isYule() {
// Yule only
return (relativeDeathRateParameter == null && sampleProbability == null && !conditionalOnRoot);
}
@Override
public double getMarginal(Tree tree, CalibrationPoints calibration) {
// Yule only
return calibration.getCorrection(tree, getR());
}
public double getR() {
return birthDiffRateParameter.getParameterValue(0);
}
public double getA() {
return relativeDeathRateParameter != null ? relativeDeathRateParameter.getParameterValue(0) : 0;
}
public double getRho() {
return sampleProbability != null ? sampleProbability.getParameterValue(0) : 1.0;
}
private double logCoeff(int taxonCount) {
switch( type ) {
case UNSCALED: break;
case TIMESONLY: return logGamma(taxonCount + 1);
case ORIENTED:  return Math.log(taxonCount);
case LABELED:   {
final double two2nm1 = (taxonCount - 1) * Math.log(2.0);
if( ! conditionalOnRoot ) {
return two2nm1 - logGamma(taxonCount);
} else {
return two2nm1 - Math.log(taxonCount-1) - logGamma(taxonCount+1);
}
}
}
return 0.0;
}
public double logTreeProbability(int taxonCount) {
double c1 = logCoeff(taxonCount);
if( ! conditionalOnRoot ) {
c1 += (taxonCount - 1) * Math.log(getR() * getRho()) + taxonCount * Math.log(1 - getA());
}
return c1;
}
public double logNodeProbability(Tree tree, NodeRef node) {
final double height = tree.getNodeHeight(node);
final double r = getR();
final double mrh = -r * height;
final double a = getA();
if( ! conditionalOnRoot ) {
final double rho = getRho();
final double z = Math.log(rho + ((1 - rho) - a) * Math.exp(mrh));
double l = -2 * z + mrh;
if( tree.getRoot() == node ) {
l += mrh - z;
}
return l;
} else {
double l;
if( tree.getRoot() != node ) {
final double z = Math.log(1 - a * Math.exp(mrh));
l = -2 * z + mrh;
} else {
// Root dependent coefficient from each internal node
final double ca = 1 - a;
final double emrh = Math.exp(-mrh);
if( emrh != 1.0 ) {
l = (tree.getTaxonCount() - 2) * Math.log(r * ca * (1 + ca /(emrh - 1)));
} else {  // use exp(x)-1 = x for x near 0
l = (tree.getTaxonCount() - 2) * Math.log(ca * (r + ca/height));
}
}
return l;
}
}
public boolean includeExternalNodesInLikelihoodCalculation() {
return false;
}
}