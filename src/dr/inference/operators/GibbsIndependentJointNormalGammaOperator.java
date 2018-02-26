package dr.inference.operators;
import cern.jet.random.Gamma;
import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import dr.inference.distribution.NormalDistributionModel;
import dr.inference.model.Bounds;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.MathUtils;
import dr.math.distributions.GammaDistribution;
import dr.xml.*;
public class GibbsIndependentJointNormalGammaOperator extends SimpleMCMCOperator implements GibbsOperator {
	public static final String OPERATOR_NAME = "GibbsIndependentJointNormalGammaOperator";
    public static final String MEAN = "mean";
    public static final String PREC = "precision";
    public static final String SHAPE = "shape";
    public static final String SCALE = "scale";
	private Variable<Double> mean = null;
    private Variable<Double> precision = null;
	private NormalDistributionModel model = null;
    private GammaDistribution gamma = null;
	private boolean updateAllIndependently = true;
	private static final boolean TRY_COLT = true;
    private static final boolean DEBUG = false;
    private static RandomEngine randomEngine;
    private static Normal coltNormal;
    //private static Gamma coltGamma;
	public GibbsIndependentJointNormalGammaOperator(Variable mean, Variable precision, NormalDistributionModel model, GammaDistribution gamma) {
		this(mean, precision, model, gamma, 1.0);
	}
	public GibbsIndependentJointNormalGammaOperator(Variable mean, Variable precision, NormalDistributionModel model, GammaDistribution gamma, double weight) {
		this(mean, precision, model, gamma, weight, true);
	}
	public GibbsIndependentJointNormalGammaOperator(Variable mean, Variable precision, NormalDistributionModel model, GammaDistribution gamma, double weight, boolean updateAllIndependently) {
		this.mean = mean;
        this.precision = precision;
		this.model = model;
        this.gamma = gamma;
		this.updateAllIndependently = updateAllIndependently;
		setWeight(weight);
		if (TRY_COLT) {
            randomEngine = new MersenneTwister(MathUtils.nextInt());
            //create standard normal distribution, internal states will be bypassed anyway
            //takes mean and standard deviation
            coltNormal = new Normal(0.0, 1.0, randomEngine);
            //coltGamma = new Gamma(gamma.getShape(), 1.0/gamma.getScale(), randomEngine);
        } else {
        	//no random draw with specified mean and stdev implemented in the normal distribution in BEAST (as far as I know)
        	throw new RuntimeException("Normal distribution in BEAST still needs a random sampler.");
        }
	}
	public String getPerformanceSuggestion() {
		return "";
	}
	public String getOperatorName() {
		return "GibbsIndependentJointNormalGamma(" + mean.getVariableName() + "," + precision.getVariableName() + ")";
	}
	public int getStepCount() {
        return 1;
    }
	public double doOperation() throws OperatorFailedException {
		//double logq = 0;
	    //double currentValue;
		double newValue;
		final Bounds<Double> meanBounds = mean.getBounds();
        final Bounds<Double> precBounds = precision.getBounds();
		final int dim = mean.getSize();
		if (updateAllIndependently) {
			for (int i = 0; i < dim; i++) {
                if (DEBUG) {
                    System.out.println("old precision value: " + precision.getValue(i));
                    System.out.println("old mean value: " + mean.getValue(i));
                    System.out.println("model mean check: " + model.getMean().getValue(i));
                    System.out.println("model precision check: " + model.getPrecision().getValue(i) + "\n");
                }
                //start with updating the precision
                newValue = gamma.nextGamma();
                //newValue = coltGamma.nextDouble();
                while (newValue == 0.0) {
                    newValue = gamma.nextGamma();
                    //newValue = coltGamma.nextDouble();
                }
                if (newValue < precBounds.getLowerLimit(i) || newValue > precBounds.getUpperLimit(i)) {
                    throw new OperatorFailedException("proposed value from gamma distribution outside boundaries");
                }
                precision.setValue(i, newValue);
                if (DEBUG) {
                    System.out.println("new precision value: " + precision.getValue(i));
                    System.out.println("old mean value: " + mean.getValue(i));
                    System.out.println("model mean check: " + model.getMean().getValue(i));
                    System.out.println("model precision check: " + model.getPrecision().getValue(i) + "\n");
                }
				//use the current mean and precision (standard deviation)
				newValue = coltNormal.nextDouble(model.getMean().getValue(i), 1.0 / Math.sqrt(model.getPrecision().getValue(i)));
				//newValue = (double)model.nextRandom();
				//System.out.print("normal distribution model: N(" + model.getMean().getValue(i) + "," + model.getPrecision().getValue(i) + ")   ");
				//System.out.println(1.0 / Math.sqrt(model.getPrecision().getValue(i)));
				//System.out.println("current value: " + currentValue + " -- new value: " + newValue);
				//logq += (model.logPdf(currentValue) - model.logPdf(newValue));
				if (newValue < meanBounds.getLowerLimit(i) || newValue > meanBounds.getUpperLimit(i)) {
                    throw new OperatorFailedException("proposed value from normal distribution outside boundaries");
                }
				mean.setValue(i, newValue);
                if (DEBUG) {
                    System.out.println("new precision value: " + precision.getValue(i));
                    System.out.println("new mean value: " + mean.getValue(i));
                    System.out.println("model mean check: " + model.getMean().getValue(i));
                    System.out.println("model precision check: " + model.getPrecision().getValue(i) + "\n");
                }
			}
		}
		//return logq;
		return 0;
	}
	public static dr.xml.XMLObjectParser PARSER = new dr.xml.AbstractXMLObjectParser() {
		public String getParserName() {
            return OPERATOR_NAME;
        }
		public Object parseXMLObject(XMLObject xo) throws XMLParseException {
			double weight = xo.getDoubleAttribute(WEIGHT);
            double shape = xo.getDoubleAttribute(SHAPE);
            double scale = xo.getDoubleAttribute(SCALE);
            NormalDistributionModel model = (NormalDistributionModel) xo.getChild(NormalDistributionModel.class);
            XMLObject cxo = xo.getChild(MEAN);
            Parameter mean = (Parameter) cxo.getChild(Parameter.class);
            cxo = xo.getChild(PREC);
            Parameter precision = (Parameter) cxo.getChild(Parameter.class);
			return new GibbsIndependentJointNormalGammaOperator(mean, precision, model, new GammaDistribution(shape, scale), weight);
		}
		//************************************************************************
        // AbstractXMLObjectParser implementation
        //************************************************************************
		public XMLSyntaxRule[] getSyntaxRules() {
			return rules;
		}
		private final XMLSyntaxRule[] rules = {
                AttributeRule.newDoubleRule(WEIGHT),
                AttributeRule.newDoubleRule(SHAPE),
                AttributeRule.newDoubleRule(SCALE),
                new ElementRule(NormalDistributionModel.class),
                new ElementRule(MEAN, new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
                new ElementRule(PREC, new XMLSyntaxRule[]{new ElementRule(Parameter.class)})
        };
		public String getParserDescription() {
			return "This element returns an independence sampler, disguised as a Gibbs operator, from provided normal and gamma distributions.";
		}
		public Class getReturnType() {
			return MCMCOperator.class;
		}
	};
}
