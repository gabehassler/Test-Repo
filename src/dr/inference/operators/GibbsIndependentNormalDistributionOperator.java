package dr.inference.operators;
import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import dr.inference.distribution.NormalDistributionModel;
import dr.inference.model.Bounds;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.MathUtils;
import dr.xml.AttributeRule;
import dr.xml.ElementRule;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;
public class GibbsIndependentNormalDistributionOperator extends SimpleMCMCOperator implements GibbsOperator {
	public static final String OPERATOR_NAME = "GibbsIndependentNormalDistributionOperator";
	private Variable<Double> variable = null;
	private NormalDistributionModel model = null;
	private boolean updateAllIndependently = true;
	private static final boolean TRY_COLT = true;
    private static RandomEngine randomEngine;
    private static Normal coltNormal;
	public GibbsIndependentNormalDistributionOperator(Variable variable, NormalDistributionModel model) {
		this(variable, model, 1.0);
	}
	public GibbsIndependentNormalDistributionOperator(Variable variable, NormalDistributionModel model, double weight) {
		this(variable, model, weight, true);
	}
	public GibbsIndependentNormalDistributionOperator(Variable variable, NormalDistributionModel model, double weight, boolean updateAllIndependently) {
		this.variable = variable;
		this.model = model;
		this.updateAllIndependently = updateAllIndependently;
		setWeight(weight);
		if (TRY_COLT) {
            randomEngine = new MersenneTwister(MathUtils.nextInt());
            //create standard normal distribution, internal states will be bypassed anyway
            //takes mean and standard deviation
            coltNormal = new Normal(0.0, 1.0, randomEngine);
        } else {
        	//no random draw with specified mean and stdev implemented in the normal distribution in BEAST (as far as I know)
        	throw new RuntimeException("Normal distribution in BEAST still needs a random sampler.");
        }
	}
	public String getPerformanceSuggestion() {
		return "";
	}
	public String getOperatorName() {
		return "GibbsIndependentNormalDistribution(" + variable.getVariableName() + ")";
	}
	public int getStepCount() {
        return 1;
    }
	public double doOperation() throws OperatorFailedException {
		//double logq = 0;
	    //double currentValue;
		double newValue;
		final Bounds<Double> bounds = variable.getBounds();
		final int dim = variable.getSize();
		if (updateAllIndependently) {
			for (int i = 0; i < dim; i++) {
				//both current and new value of the variable needed for the hastings ratio
				//currentValue = variable.getValue(i);
				//use the current mean and precision (standard deviation)
				newValue = coltNormal.nextDouble(model.getMean().getValue(i), 1.0 / Math.sqrt(model.getPrecision().getValue(i)));
				//newValue = (double)model.nextRandom();
				//System.out.print("normal distribution model: N(" + model.getMean().getValue(i) + "," + model.getPrecision().getValue(i) + ")   ");
				//System.out.println(1.0 / Math.sqrt(model.getPrecision().getValue(i)));
				//System.out.println("current value: " + currentValue + " -- new value: " + newValue);
				//logq += (model.logPdf(currentValue) - model.logPdf(newValue));
				if (newValue < bounds.getLowerLimit(i) || newValue > bounds.getUpperLimit(i)) {
                    throw new OperatorFailedException("proposed value outside boundaries");
                }
				variable.setValue(i, newValue);
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
			NormalDistributionModel model = (NormalDistributionModel) xo.getChild(NormalDistributionModel.class);
			Parameter parameter = (Parameter) xo.getChild(Parameter.class);
			return new GibbsIndependentNormalDistributionOperator(parameter, model, weight);
		}
		//************************************************************************
        // AbstractXMLObjectParser implementation
        //************************************************************************
		public XMLSyntaxRule[] getSyntaxRules() {
			return rules;
		}
		private final XMLSyntaxRule[] rules = {
                AttributeRule.newDoubleRule(WEIGHT),
                new ElementRule(NormalDistributionModel.class),
                new ElementRule(Parameter.class)
        };
		public String getParserDescription() {
			return "This element returns an independence sampler, disguised as a Gibbs operator, from a provided normal distribution model.";
		}
		public Class getReturnType() {
			return MCMCOperator.class;
		}
	};
}
