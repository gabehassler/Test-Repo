
package dr.inference.distribution;

import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.UnivariateFunction;
import dr.math.distributions.GammaDistribution;
import dr.math.distributions.InverseGammaDistribution;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class InverseGammaDistributionModel extends AbstractModel implements ParametricDistributionModel {

    public static final String INVERSE_GAMMA_DISTRIBUTION_MODEL = "inverseGammaDistributionModel";

    public InverseGammaDistributionModel(Variable<Double> shape, Variable<Double> scale) {

        super(INVERSE_GAMMA_DISTRIBUTION_MODEL);

        this.shape = shape;
        this.scale = scale;
        addVariable(shape);
        shape.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        addVariable(scale);
        scale.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
    }

    public InverseGammaDistributionModel(Variable<Double> shape) {

        super(INVERSE_GAMMA_DISTRIBUTION_MODEL);

        this.shape = shape;
        addVariable(shape);
        shape.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
    }

    // *****************************************************************
    // Interface Distribution
    // *****************************************************************

    public double pdf(double x) {
        return InverseGammaDistribution.pdf(x, getShape(), getScale(), 1.0);
    }

    public double logPdf(double x) {
        return InverseGammaDistribution.logPdf(x, getShape(), getScale(), 1);
    }

    public double cdf(double x) {
        return InverseGammaDistribution.cdf(x, getShape(), getScale());
    }

    public double quantile(double y) {
        return (InverseGammaDistribution.quantile(y, getShape(), getScale()));
    }

    public double mean() {
        return InverseGammaDistribution.mean(getShape(), getScale());
    }

    public double variance() {
        return InverseGammaDistribution.variance(getShape(), getScale());
    }

    public final UnivariateFunction getProbabilityDensityFunction() {
        return pdfFunction;
    }

    private final UnivariateFunction pdfFunction = new UnivariateFunction() {
        public final double evaluate(double x) {
            return pdf(x);
        }

        public final double getLowerBound() {
            return 0.0;
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

    // **************************************************************
    // XMLElement IMPLEMENTATION
    // **************************************************************

    public Element createElement(Document document) {
        throw new RuntimeException("Not implemented!");
    }

    public double getShape() {
        return shape.getValue(0);
    }

    public double getScale() {
        return scale.getValue(0);
    }

    // **************************************************************
    // Private instance variables
    // **************************************************************

    private Variable<Double> shape = null;
    private Variable<Double> scale = null;
}

