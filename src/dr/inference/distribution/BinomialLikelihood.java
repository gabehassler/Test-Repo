package dr.inference.distribution;
import dr.inference.model.AbstractModelLikelihood;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.Binomial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class BinomialLikelihood extends AbstractModelLikelihood {
    public static final String BINOMIAL_LIKELIHOOD = "binomialLikelihood";
    public BinomialLikelihood(Parameter trialsParameter, Parameter proportionParameter, Parameter countsParameter) {
        super(BINOMIAL_LIKELIHOOD);
        this.trialsParameter = trialsParameter;
        this.proportionParameter = proportionParameter;
        this.countsParameter = countsParameter;
        addVariable(trialsParameter);
        addVariable(proportionParameter);
        addVariable(countsParameter);
    }
    // **************************************************************
    // Likelihood IMPLEMENTATION
    // **************************************************************
    public Model getModel() {
        return this;
    }
    public double getLogLikelihood() {
        double p = proportionParameter.getParameterValue(0);
        if (p <= 0 || p >= 1) return Double.NEGATIVE_INFINITY;
        double logP = Math.log(p);
        double log1MinusP = Math.log(1.0 - p);
        double logL = 0.0;
        for (int i = 0; i < trialsParameter.getDimension(); i++) {
            int trials = (int) Math.round(trialsParameter.getParameterValue(i));
            int counts = (int) Math.round(countsParameter.getParameterValue(i));
            if (counts > trials) return Double.NEGATIVE_INFINITY;
            logL += binomialLogLikelihood(trials, counts, logP, log1MinusP);
        }
        return logL;
    }
    public void makeDirty() {
    }
    public void acceptState() {
        // DO NOTHING
    }
    public void restoreState() {
        // DO NOTHING
    }
    public void storeState() {
        // DO NOTHING
    }
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        // DO NOTHING
    }
    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        // DO NOTHING
    }
    private double binomialLogLikelihood(int trials, int count, double logP, double log1MinusP) {
        return Math.log(Binomial.choose(trials, count)) + (logP * count) + (log1MinusP * (trials - count));
    }
    // **************************************************************
    // XMLElement IMPLEMENTATION
    // **************************************************************
    public Element createElement(Document d) {
        throw new RuntimeException("Not implemented yet!");
    }
    Parameter trialsParameter;
    Parameter proportionParameter;
    Parameter countsParameter;
}
