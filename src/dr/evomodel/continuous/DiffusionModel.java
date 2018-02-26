
package dr.evomodel.continuous;

import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.MathUtils;
import dr.xml.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class DiffusionModel extends AbstractModel {

    public static final String DIFFUSION_PROCESS = "diffusionProcess";
    public static final String DIFFUSION_CONSTANT = "D";
    public static final String BIAS = "mu";

    public DiffusionModel(Parameter diffusionRateParameter) {

        super(DIFFUSION_PROCESS);

        this.diffusionRateParameter = diffusionRateParameter;
        addVariable(diffusionRateParameter);
        diffusionRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
    }

    public DiffusionModel(Parameter diffusionRateParameter, Parameter biasParameter) {

        super(DIFFUSION_PROCESS);

        this.diffusionRateParameter = diffusionRateParameter;
        this.biasParameter = biasParameter;
        addVariable(diffusionRateParameter);
        addVariable(biasParameter);
        diffusionRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
    }

    public double getD() {
        return diffusionRateParameter.getParameterValue(0);
    }

    public double getLogLikelihood(double start, double stop, double time) {

        double D = diffusionRateParameter.getParameterValue(0);
        double bias = getBias();

        // expected variance of distances of given time
        double Dtime = D * time;

        double unbiasedDistance = (stop - start) - (bias * time);

        //System.out.println("distance=" + unbiasedDistance + " time=" + time);

        // the log likelihood of travelling distance d, in time t given diffusion rate D
        return -0.5 * Math.log(Dtime) - ((unbiasedDistance * unbiasedDistance) / (Dtime));
    }

    public double simulateForward(double value, double time) {
        double D = diffusionRateParameter.getParameterValue(0);
        double delta = MathUtils.nextGaussian();

        delta *= Math.sqrt(D * time);
        delta += getBias() * time;

        return value + delta;
    }


    private double getBias() {
        if (biasParameter == null) return 0.0;
        return biasParameter.getParameterValue(0);
    }

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
    // XMLObjectParser
    // **************************************************************

    public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {

        public String getParserName() {
            return DIFFUSION_PROCESS;
        }

        public Object parseXMLObject(XMLObject xo) throws XMLParseException {

            XMLObject cxo = xo.getChild(DIFFUSION_CONSTANT);
            Parameter diffusionParam = (Parameter) cxo.getChild(Parameter.class);

            Parameter biasParam = null;
            if (xo.hasAttribute(BIAS)) {
                cxo = xo.getChild(BIAS);
                biasParam = (Parameter) cxo.getChild(Parameter.class);
            }

            if (biasParam == null) {
                return new DiffusionModel(diffusionParam);
            }
            return new DiffusionModel(diffusionParam, biasParam);
        }

        //************************************************************************
        // AbstractXMLObjectParser implementation
        //************************************************************************

        public String getParserDescription() {
            return "Describes a diffusion process.";
        }

        public XMLSyntaxRule[] getSyntaxRules() {
            return rules;
        }

        private final XMLSyntaxRule[] rules = {
                new ElementRule(DIFFUSION_CONSTANT,
                        new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
                new ElementRule(BIAS,
                        new XMLSyntaxRule[]{new ElementRule(Parameter.class)})
        };

        public Class getReturnType() {
            return DiffusionModel.class;
        }
    };

    // **************************************************************
    // Private instance variables
    // **************************************************************

    private final Parameter diffusionRateParameter;
    private Parameter biasParameter;
}
