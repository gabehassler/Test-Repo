package dr.inference.distribution;/*
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.inferencexml.distribution.NormalDistributionModelParser;
import dr.inferencexml.distribution.TruncatedNormalDistributionModelParser;
import dr.math.UnivariateFunction;
import dr.math.distributions.TruncatedNormalDistribution;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class TruncatedNormalDistributionModel extends AbstractModel implements ParametricDistributionModel {
    public TruncatedNormalDistributionModel(Variable<Double> mean, Variable<Double> stdev, Variable<Double> minimum,
                                            Variable<Double> maximum) {
        super(TruncatedNormalDistributionModelParser.TRUNCATED_NORMAL_DISTRIBUTION_MODEL);
        this.mean = mean;
        this.stdev = stdev;
        this.minimum = minimum;
        this.maximum = maximum;
        this.
        addVariable(mean);
        mean.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        addVariable(stdev);
        stdev.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        addVariable(minimum);
        minimum.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        addVariable(maximum);
        maximum.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        recomputeTruncatedNormalDistribution();
    }
    public TruncatedNormalDistributionModel(Parameter meanParameter, Parameter scale, Parameter minParameter, Parameter
            maxParameter, boolean isPrecision) {
        super(NormalDistributionModelParser.NORMAL_DISTRIBUTION_MODEL);
        this.minimum = minParameter;
        this.maximum = maxParameter;
        this.hasPrecision = isPrecision;
        this.mean = meanParameter;
        addVariable(meanParameter);
        meanParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        addVariable(maxParameter);
        maxParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        addVariable(minParameter);
        minParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        if (isPrecision) {
            this.precision = scale;
            this.stdev = null;
        } else {
            this.stdev = scale;
        }
        addVariable(scale);
        scale.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        recomputeTruncatedNormalDistribution();
    }
    public double getStdev() {
        if (hasPrecision)
            return 1.0 / Math.sqrt(precision.getValue(0));
        return stdev.getValue(0);
    }
    public Variable<Double> getMean() {
        return mean;
    }
    public Variable<Double> getMaximum() {
        return maximum;
    }
    public Variable<Double> getMinimum() {
        return minimum;
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
        return distribution.pdf(x);
    }
    public double logPdf(double x) {
        return distribution.logPdf(x);
    }
    public double cdf(double x) {
        return distribution.cdf(x);
    }
    public double quantile(double y) {
        return distribution.quantile(y);
    }
    public double mean() {
        return mean.getValue(0);
    }
    public double minimum() {
        return minimum.getValue(0);
    }
    public double maximum() {
        return maximum.getValue(0);
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
    private void recomputeTruncatedNormalDistribution(){
        distribution = new TruncatedNormalDistribution(mean.getValue(0), stdev.getValue(0), minimum.getValue(0),
                maximum.getValue(0));
    };
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
        recomputeTruncatedNormalDistribution();
    }
    protected void storeState() {
        storedDistribution = distribution;
    }
    protected void restoreState() {
        distribution = storedDistribution;
    }
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
    private final Variable<Double> minimum;
    private final Variable<Double> maximum;
    private Variable<Double> precision;
    private boolean hasPrecision = false;
    private TruncatedNormalDistribution distribution = null;
    private TruncatedNormalDistribution storedDistribution = null;
}
