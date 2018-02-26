
package dr.evomodel.substmodel;

import dr.evolution.datatype.TwoStateCovarion;
import dr.evomodelxml.substmodel.TwoStateCovarionModelParser;
import dr.inference.model.Parameter;

public class TwoStateCovarionModel extends AbstractSubstitutionModel {

    public TwoStateCovarionModel(TwoStateCovarion dataType, FrequencyModel freqModel,
                                 Parameter alphaParameter,
                                 Parameter switchingParameter) {
        super(TwoStateCovarionModelParser.COVARION_MODEL, dataType, freqModel);

        alpha = alphaParameter;
        this.switchingParameter = switchingParameter;

        addVariable(alpha);
        addVariable(switchingParameter);
        setupRelativeRates();
    }

    protected void frequenciesChanged() {
        // DO NOTHING
    }

    protected void ratesChanged() {
        setupRelativeRates();
    }

    protected void setupRelativeRates() {

        relativeRates[0] = alpha.getParameterValue(0);
        relativeRates[1] = switchingParameter.getParameterValue(0);
        relativeRates[2] = 0.0;
        relativeRates[3] = 0.0;
        relativeRates[4] = switchingParameter.getParameterValue(0);
        relativeRates[5] = 1.0;
    }

    public String toString() {

        return SubstitutionModelUtils.toString(relativeRates, dataType, true, 2);
    }

    void normalize(double[][] matrix, double[] pi) {

        if (isNormalized) {

            double subst = 0.0;
            int dimension = pi.length;

            for (int i = 0; i < dimension; i++) {
                subst += -matrix[i][i] * pi[i];
            }

            // normalize, including switches
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    matrix[i][j] = matrix[i][j] / subst;
                }
            }

            double switchingProportion = 0.0;
            switchingProportion += matrix[0][2] * pi[2];
            switchingProportion += matrix[2][0] * pi[0];
            switchingProportion += matrix[1][3] * pi[3];
            switchingProportion += matrix[3][1] * pi[1];

            //System.out.println("switchingProportion=" + switchingProportion);

            // normalize, removing switches
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    matrix[i][j] = matrix[i][j] / (1.0 - switchingProportion);
                }
            }
        }
    }

    private Parameter alpha;
    private Parameter switchingParameter;

    // if true then matrix will be normalized to output 1 substitution per unit time
    private boolean isNormalized = true;

}
