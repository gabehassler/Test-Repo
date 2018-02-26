
package dr.inference.distribution;

import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.UnivariateFunction;
import dr.math.distributions.ExponentialDistribution;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ExponentialDistributionModel extends AbstractModel implements ParametricDistributionModel {

    public static final String EXPONENTIAL_DISTRIBUTION_MODEL = "exponentialDistributionModel";

    public ExponentialDistributionModel(Variable<Double> mean) {

        this(mean, 0.0);
    }


    public ExponentialDistributionModel(Variable<Double> mean, double offset) {

        super(EXPONENTIAL_DISTRIBUTION_MODEL);

        this.mean = mean;
        this.offset = offset;

        addVariable(mean);
        mean.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
    }

    // *****************************************************************
    // Interface Distribution
    // *****************************************************************

    public double pdf(double x) {
        if (x < offset) return 0.0;
        return ExponentialDistribution.pdf(x - offset, 1.0 / getMean());
    }

    public double logPdf(double x) {
        if (x < offset) return Double.NEGATIVE_INFINITY;
        return ExponentialDistribution.logPdf(x - offset, 1.0 / getMean());
    }

    public double cdf(double x) {
        if (x < offset) return 0.0;
        return ExponentialDistribution.cdf(x - offset, 1.0 / getMean());
    }

    public double quantile(double y) {
        return ExponentialDistribution.quantile(y, 1.0 / getMean()) + offset;
    }

    public double mean() {
        return ExponentialDistribution.mean(1.0 / getMean()) + offset;
    }

    public double variance() {
        return ExponentialDistribution.variance(1.0 / getMean());
    }

    public final UnivariateFunction getProbabilityDensityFunction() {
        return pdfFunction;
    }

    private final UnivariateFunction pdfFunction = new UnivariateFunction() {
        public final double evaluate(double x) {
            return pdf(x);
        }

        public final double getLowerBound() {
            return offset;
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

    public void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        // no intermediates need to be recalculated...
    }

    protected void storeState() {
    } // no additional state needs storing

    protected void restoreState() {
    } // no additional state needs restoring

    protected void acceptState() {
    } // no additional state needs accepting

    // **************************************************************
    // XMLElement IMPLEMENTATION
    // **************************************************************

    public Element createElement(Document document) {
        throw new RuntimeException("Not implemented!");
    }

    // **************************************************************
    // Private methods
    // **************************************************************

    private double getMean() {
        return mean.getValue(0);
    }

    // **************************************************************
    // Private instance variables
    // **************************************************************

    private Variable<Double> mean = null;
    private double offset = 0.0;

}

