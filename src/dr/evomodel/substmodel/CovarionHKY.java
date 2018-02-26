
package dr.evomodel.substmodel;

import dr.evolution.datatype.HiddenNucleotides;
import dr.evomodelxml.substmodel.CovarionHKYParser;
import dr.inference.model.Parameter;

public class CovarionHKY extends AbstractCovarionDNAModel {
    
    private Parameter kappaParameter;

    public CovarionHKY(HiddenNucleotides dataType, Parameter kappaParameter, Parameter hiddenClassRates, Parameter switchingRates, FrequencyModel freqModel) {

        super(CovarionHKYParser.COVARION_HKY, dataType, hiddenClassRates, switchingRates, freqModel);

        this.kappaParameter = kappaParameter;
        addVariable(kappaParameter);
        setupRelativeRates();
    }

    double[] getRelativeDNARates() {
        double kappa = kappaParameter.getParameterValue(0);
        return new double[]{1.0, kappa, 1.0, 1.0, kappa, 1.0};
    }

    public void setKappa(double kappa) {
        kappaParameter.setParameterValue(0, kappa);
    }

    public double getKappa() {
        return kappaParameter.getParameterValue(0);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Covarion HKY model with ");
        builder.append(getHiddenClassCount()).append(" rate classes.\n");
        builder.append("Relative rates: \n");
        builder.append(SubstitutionModelUtils.toString(relativeRates, dataType, true, 2));
        return builder.toString();

    }

}