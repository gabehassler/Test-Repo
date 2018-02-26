
package dr.evomodel.operators;

import dr.evomodel.coalescent.structure.ColourSamplerModel;
import dr.inference.operators.CoercableMCMCOperator;
import dr.inference.operators.CoercionMode;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.OperatorFailedException;
import dr.xml.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FixedColouredOperator implements CoercableMCMCOperator {

    public static final double ACCEPTANCE_FACTOR = 0.5;

    public static final String FIXED_COLOURED_OPERATOR = "fixedColouredOperator";

    private ColourSamplerModel colouringModel;

    private MCMCOperator innerOperator;

    public FixedColouredOperator(ColourSamplerModel colouringModel, MCMCOperator operator) {

        this.colouringModel = colouringModel;
        this.innerOperator = operator;
    }

    public final double operate() throws OperatorFailedException {

        colouringModel.invalidateProposalProbability();

        return innerOperator.operate();
    }

    public double getCoercableParameter() {
        if (innerOperator instanceof CoercableMCMCOperator) {
            return ((CoercableMCMCOperator) innerOperator).getCoercableParameter();
        }
        throw new IllegalArgumentException();
    }

    public void setCoercableParameter(double value) {
        if (innerOperator instanceof CoercableMCMCOperator) {
            ((CoercableMCMCOperator) innerOperator).setCoercableParameter(value);
            return;
        }
        throw new IllegalArgumentException();
    }

    public double getRawParameter() {

        if (innerOperator instanceof CoercableMCMCOperator) {
            return ((CoercableMCMCOperator) innerOperator).getRawParameter();
        }
        throw new IllegalArgumentException();
    }

    public CoercionMode getMode() {
        if (innerOperator instanceof CoercableMCMCOperator) {
            return ((CoercableMCMCOperator) innerOperator).getMode();
        }
        return CoercionMode.COERCION_OFF;
    }

    public String getOperatorName() {
        return "Coloured(" + innerOperator.getOperatorName() + ")";
    }

    public Element createOperatorElement(Document d) {
        throw new RuntimeException("not implemented");
    }

    public double getTargetAcceptanceProbability() {
        return innerOperator.getTargetAcceptanceProbability();
    }

    public double getMinimumAcceptanceLevel() {
        return innerOperator.getMinimumAcceptanceLevel();
    }

    public double getMaximumAcceptanceLevel() {
        return innerOperator.getMaximumAcceptanceLevel();
    }

    public double getMinimumGoodAcceptanceLevel() {
        return innerOperator.getMinimumGoodAcceptanceLevel();
    }

    public double getMaximumGoodAcceptanceLevel() {
        return innerOperator.getMaximumGoodAcceptanceLevel();
    }

    // All of this is copied and modified from SimpleMCMCOperator
    public final double getWeight() {
        return innerOperator.getWeight();
    }

    public final void setWeight(double w) {
        innerOperator.setWeight(w);
    }

    public final void accept(double deviation) {
        innerOperator.accept(deviation);
    }

    public final void reject() {
        innerOperator.reject();
    }

    public final void reset() {
        innerOperator.reset();
    }

    public final int getCount() {
        return innerOperator.getCount();
    }

    public final int getAcceptCount() {
        return innerOperator.getAcceptCount();
    }

    public final void setAcceptCount(int accepted) {
        innerOperator.setAcceptCount(accepted);
    }

    public final int getRejectCount() {
        return innerOperator.getRejectCount();
    }

    public final void setRejectCount(int rejected) {
        innerOperator.setRejectCount(rejected);
    }

    public final double getMeanDeviation() {
        return innerOperator.getMeanDeviation();
    }

    public final double getSumDeviation() {
        return innerOperator.getSumDeviation();
    }

    public double getSpan(boolean reset) {
        return 0;
    }

    public final void setSumDeviation(double sumDeviation) {
        innerOperator.setSumDeviation(sumDeviation);
    }

    public String getPerformanceSuggestion() {
        return innerOperator.getPerformanceSuggestion();
    }

    public double getMeanEvaluationTime() {
        return innerOperator.getMeanEvaluationTime();
    }

    public long getTotalEvaluationTime() {
        return innerOperator.getTotalEvaluationTime();
    }

    public void addEvaluationTime(long time) {
        innerOperator.addEvaluationTime(time);
    }

    public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {

        public String getParserName() {
            return FIXED_COLOURED_OPERATOR;
        }

        public Object parseXMLObject(XMLObject xo) {

            MCMCOperator operator = (MCMCOperator) xo.getChild(MCMCOperator.class);
            ColourSamplerModel colourSamplerModel = (ColourSamplerModel) xo.getChild(ColourSamplerModel.class);

            return new ColouredOperator(colourSamplerModel, operator);
        }

        //************************************************************************
        // AbstractXMLObjectParser implementation
        //************************************************************************

        public String getParserDescription() {
            return "This element (or a ColouredOperator) must wrap any operator that changes a parameter upon which the colouring proposal distribution depends";
        }

        public Class getReturnType() {
            return ColouredOperator.class;
        }

        public XMLSyntaxRule[] getSyntaxRules() {
            return rules;
        }

        private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
                new ElementRule(MCMCOperator.class),
                new ElementRule(ColourSamplerModel.class)
        };

    };
}
