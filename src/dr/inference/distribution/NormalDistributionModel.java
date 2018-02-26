package dr.inference.distribution;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.inferencexml.distribution.NormalDistributionModelParser;
import dr.math.MathUtils;
import dr.math.UnivariateFunction;
import dr.math.distributions.NormalDistribution;
import dr.math.distributions.RandomGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class NormalDistributionModel extends AbstractModel implements ParametricDistributionModel, RandomGenerator {
    public NormalDistributionModel(Variable<Double> mean, Variable<Double> stdev) {
        super(NormalDistributionModelParser.NORMAL_DISTRIBUTION_MODEL);
        this.mean = mean;
        this.stdev = stdev;
        addVariable(mean);
        mean.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        addVariable(stdev);
        stdev.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
    }
    public NormalDistributionModel(Parameter meanParameter, Parameter scale, boolean isPrecision) {
        super(NormalDistributionModelParser.NORMAL_DISTRIBUTION_MODEL);
        this.hasPrecision = isPrecision;
        this.mean = meanParameter;
        addVariable(meanParameter);
        meanParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        if (isPrecision) {
            this.precision = scale;
            this.stdev = null;  // todo why not keep the name scale to avoid confusion??
        } else {
            this.stdev = scale;
        }
        addVariable(scale);
        scale.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
    }
    public double getStdev() {
        if (hasPrecision)
            return 1.0 / Math.sqrt(precision.getValue(0));
        return stdev.getValue(0);
    }
    public Variable<Double> getMean() {
        return mean;
    }
    public Variable<Double> getPrecision() {
        if (hasPrecision)
            return precision;
        return null;
    }
    // *****************************************************************
    // Interface Distribution
    // *****************************************************************
    public double pdf(double x) {
        return NormalDistribution.pdf(x, mean(), getStdev());
    }
    public double logPdf(double x) {
        return NormalDistribution.logPdf(x, mean(), getStdev());
    }
    public double cdf(double x) {
        return NormalDistribution.cdf(x, mean(), getStdev());
    }
    public double quantile(double y) {
        return NormalDistribution.quantile(y, mean(), getStdev());
    }
    public double mean() {
        return mean.getValue(0);
    }
    public double variance() {
        if (hasPrecision)
            return 1.0 / precision.getValue(0);
        double sd = stdev.getValue(0);
        return sd * sd;
    }
    public final UnivariateFunction getProbabilityDensityFunction() {
        return pdfFunction;
    }
    private final UnivariateFunction pdfFunction = new UnivariateFunction() {
        public final double evaluate(double x) {
            return pdf(x);
        }
        public final double getLowerBound() {
            return Double.NEGATIVE_INFINITY;
        }
        public final double getUpperBound() {
            return Double.POSITIVE_INFINITY;
        }
    };
    // *****************************************************************
    // Interface Model
    // *****************************************************************
    public void handleModelChangedEvent(Model model, Object object, int index) {
        // no intermediates need to be recalculated...
    }
    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        // no intermediates need to be recalculated...
    }
    protected void storeState() {
    } // no additional state needs storing
    protected void restoreState() {
    } // no additional state needs restoring
    protected void acceptState() {
    } // no additional state needs accepting
    public Element createElement(Document document) {
        throw new RuntimeException("Not implemented!");
    }
    // **************************************************************
    // Private instance variables
    // **************************************************************
    private final Variable<Double> mean;
    private final Variable<Double> stdev;
    private Variable<Double> precision;
    private boolean hasPrecision = false;
    public Object nextRandom() {
        double eps = MathUtils.nextGaussian();
        eps *= getStdev();
        eps += mean();
        return eps;
    }
    public double logPdf(Object x) {
        double v = (Double) x;
        return logPdf(v);
    }
}
