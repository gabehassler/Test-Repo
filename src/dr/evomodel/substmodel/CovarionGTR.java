package dr.evomodel.substmodel;

import dr.evolution.datatype.HiddenNucleotides;
import dr.evomodelxml.substmodel.CovarionGTRParser;
import dr.inference.model.Parameter;

public class CovarionGTR extends AbstractCovarionDNAModel {
    public CovarionGTR(
            HiddenNucleotides dataType,
            Parameter hiddenRates,
            Parameter switchingRates,
            Parameter rateACParameter,
            Parameter rateAGParameter,
            Parameter rateATParameter,
            Parameter rateCGParameter,
            Parameter rateCTParameter,
            Parameter rateGTParameter,
            FrequencyModel freqModel) {

        super(CovarionGTRParser.GTR_COVARION_MODEL, dataType, hiddenRates, switchingRates, freqModel);
        if (rateACParameter != null) {
            addVariable(rateACParameter);
            rateACParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
            this.rateACParameter = rateACParameter;
        }

        if (rateAGParameter != null) {
            addVariable(rateAGParameter);
            rateAGParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
            this.rateAGParameter = rateAGParameter;
        }

        if (rateATParameter != null) {
            addVariable(rateATParameter);
            rateATParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
            this.rateATParameter = rateATParameter;
        }

        if (rateCGParameter != null) {
            addVariable(rateCGParameter);
            rateCGParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
            this.rateCGParameter = rateCGParameter;
        }

        if (rateCTParameter != null) {
            addVariable(rateCTParameter);
            rateCTParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
            this.rateCTParameter = rateCTParameter;
        }

        if (rateGTParameter != null) {
            addVariable(rateGTParameter);
            rateGTParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
            this.rateGTParameter = rateGTParameter;
        }
    }

    double[] getRelativeDNARates() {
        double[] rr = new double[6];
        for (int i = 0; i < rr.length; i++) {
            rr[i] = 1.0;
        }

        if (rateACParameter != null) {
            rr[0] = rateACParameter.getParameterValue(0);
        }
        if (rateAGParameter != null) {
            rr[1] = rateAGParameter.getParameterValue(0);
        }
        if (rateATParameter != null) {
            rr[2] = rateATParameter.getParameterValue(0);
        }
        if (rateCGParameter != null) {
            rr[3] = rateCGParameter.getParameterValue(0);
        }
        if (rateCTParameter != null) {
            rr[4] = rateCTParameter.getParameterValue(0);
        }
        if (rateGTParameter != null) {
            rr[5] = rateGTParameter.getParameterValue(0);
        }
        return rr;
    }

    private Parameter rateACParameter = null;
    private Parameter rateAGParameter = null;
    private Parameter rateATParameter = null;
    private Parameter rateCGParameter = null;
    private Parameter rateCTParameter = null;
    private Parameter rateGTParameter = null;
}
