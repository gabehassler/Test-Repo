package dr.inferencexml.model;
import dr.app.beagle.evomodel.branchmodel.lineagespecific.BeagleBranchLikelihood;
import dr.inference.model.CompoundLikelihood;
import dr.inference.model.Likelihood;
import dr.xml.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
public class CompoundLikelihoodParser extends AbstractXMLObjectParser {
public static final String COMPOUND_LIKELIHOOD = "compoundLikelihood";
public static final String THREADS = "threads";
public static final String POSTERIOR = "posterior";
public static final String PRIOR = "prior";
public static final String LIKELIHOOD = "likelihood";
public static final String PSEUDO_PRIOR = "pseudoPrior";
public static final String WORKING_PRIOR = "referencePrior";
public String getParserName() {
return COMPOUND_LIKELIHOOD;
}
public String[] getParserNames() {
return new String[]{getParserName(), POSTERIOR, PRIOR, LIKELIHOOD, PSEUDO_PRIOR, WORKING_PRIOR};
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
// the default is -1 threads (automatic thread pool size) but an XML attribute can override it
int threads = xo.getAttribute(THREADS, -1);
// both the XML attribute and a system property can override it
if (System.getProperty("thread.count") != null) {
threads = Integer.parseInt(System.getProperty("thread.count"));
if (threads < -1 || threads > 1000) {
// put an upper limit here - may be unnecessary?
threads = -1;
}
}
//        }
List<Likelihood> likelihoods = new ArrayList<Likelihood>();
for (int i = 0; i < xo.getChildCount(); i++) {
final Object child = xo.getChild(i);
if (child instanceof Likelihood) {
likelihoods.add((Likelihood) child);
//            } else if (child instanceof BeagleBranchLikelihoods){
//                
//            	//TODO
//            	likelihoods.addAll( ((BeagleBranchLikelihoods)child).getBranchLikelihoods());
} else {
throw new XMLParseException("An element (" + child + ") which is not a likelihood has been added to a "
+ COMPOUND_LIKELIHOOD + " element");
}
}
CompoundLikelihood compoundLikelihood;
if (xo.getName().equalsIgnoreCase(LIKELIHOOD)) {
compoundLikelihood = new CompoundLikelihood(threads, likelihoods);
switch (threads) {
case -1:
Logger.getLogger("dr.evomodel").info("Likelihood computation is using an auto sizing thread pool.");
break;
case 0:
Logger.getLogger("dr.evomodel").info("Likelihood computation is using a single thread.");
break;
default:
Logger.getLogger("dr.evomodel").info("Likelihood computation is using a pool of " + threads + " threads.");
break;
}
} else {
compoundLikelihood = new CompoundLikelihood(likelihoods);
}
//		TODO
//        System.err.println("CompundLikelihood consists of " + compoundLikelihood.getLikelihoodCount() + " likelihood element(s)");
return compoundLikelihood;
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "A likelihood function which is simply the product of its component likelihood functions.";
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
AttributeRule.newIntegerRule(THREADS, true),
new ElementRule(Likelihood.class, -1, Integer.MAX_VALUE)
};
public Class getReturnType() {
return CompoundLikelihood.class;
}
}