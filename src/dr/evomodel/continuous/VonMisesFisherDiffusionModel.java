package dr.evomodel.continuous;
import dr.inference.model.Parameter;
import dr.geo.math.Space;
import dr.geo.distributions.VonMisesFisherDistribution;
import dr.xml.*;
public class VonMisesFisherDiffusionModel extends MultivariateDiffusionModel {
public static final String VON_MISES_FISHER_MODEL = "vonMisesFisherDiffusionModel";
public static final String KAPPA = "kappa";
public VonMisesFisherDiffusionModel(Parameter concentrationParameter) {
this(3, concentrationParameter); // Default is distribution on a sphere
}
public VonMisesFisherDiffusionModel(int p, Parameter concentrationParameter) {
super();
this.p = p;
this.concentrationParameter = concentrationParameter;
calculatePrecisionInfo();
addVariable(concentrationParameter);
}
protected void calculatePrecisionInfo() {
if (p == 1 || p > 3) {
throw new RuntimeException("Von Mises-Fisher distribution only implemented for circles and spheres");
}
// Nothing gets stored
}
private boolean isInBounds(double[] x) {
final double latitude = x[0];
final double longitude = x[1];
return (latitude >= -90 && latitude < 90 && longitude > -180 && longitude < 180);
}
protected double calculateLogDensity(double[] start, double[] stop, double time) {
// TODO Write transition kernel that wraps these parameters
if (!isInBounds(start) || !isInBounds(stop)) return Double.NEGATIVE_INFINITY;
final double kappa = concentrationParameter.getParameterValue(0) / time;
return VonMisesFisherDistribution.logPdf(start, stop, kappa, Space.LAT_LONG);
}
private Parameter concentrationParameter;
private int p;
public static void main(String[] arg) {
Parameter kappa = new Parameter.Default(2.0);
VonMisesFisherDiffusionModel model = new VonMisesFisherDiffusionModel(kappa);
double[] start = {90,0}; // North-pole
double[] stop  = {0,90}; // Somewhere in the East
double time = 0.1;
System.err.println("logPDF = "+model.calculateLogDensity(start,stop,time)+" ?= -18.84214");
// R code check
// north = c(0,0,1)
// east  = c(0,1,0)
// log(vmf(north,east,2/0.1,3))
}
public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
public String getParserName() {
return VON_MISES_FISHER_MODEL;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
XMLObject cxo = xo.getChild(KAPPA);
Parameter kappa = (Parameter) cxo.getChild(Parameter.class);
return new VonMisesFisherDiffusionModel(kappa);
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "Describes a von Mises-Fisher distributed diffusion process on a sphere.";
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
new ElementRule(KAPPA,
new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
};
public Class getReturnType() {
return MultivariateDiffusionModel.class;
}
};
}