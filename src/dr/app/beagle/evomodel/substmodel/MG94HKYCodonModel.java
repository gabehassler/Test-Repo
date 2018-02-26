package dr.app.beagle.evomodel.substmodel;
import dr.evolution.datatype.Codons;
import dr.inference.model.Parameter;
public class MG94HKYCodonModel extends MG94CodonModel {
    protected Parameter kappaParameter;
//    protected Parameter AtoC_Parameter;
//    protected Parameter AtoG_Parameter;
//    protected Parameter AtoT_Parameter;
//    protected Parameter CtoG_Parameter;
//    protected Parameter CtoT_Parameter;
//    protected Parameter GtoT_Parameter;
    public MG94HKYCodonModel(Codons codonDataType, Parameter alphaParameter, Parameter betaParameter, Parameter kappaParameter,
                             FrequencyModel freqModel) {
        this(codonDataType, alphaParameter, betaParameter, kappaParameter, freqModel,
                new DefaultEigenSystem(codonDataType.getStateCount()));
    }
    public MG94HKYCodonModel(Codons codonDataType,
                             Parameter alphaParameter,
                             Parameter betaParameter,
                             Parameter kappaParameter,
//                          Parameter AtoC_Parameter,
//                          Parameter AtoG_Parameter,
//                          Parameter AtoT_Parameter,
//                          Parameter CtoG_Parameter,
//                          Parameter CtoT_Parameter,
//                          Parameter GtoT_Parameter,
                             FrequencyModel freqModel, EigenSystem eigenSystem) {
        super(codonDataType, alphaParameter, betaParameter, freqModel, eigenSystem);
        this.kappaParameter = kappaParameter;
        addVariable(kappaParameter);
        kappaParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0,
        kappaParameter.getDimension()));
    }
    public double getKappa() {
        return kappaParameter.getParameterValue(0);
    }
    protected void setupRelativeRates(double[] rates) {
        double alpha = getAlpha();
        double beta = getBeta();
        double kappa = getKappa();
        for (int i = 0; i < rateCount; i++) {
            switch (rateMap[i]) {
                case 0:
                    rates[i] = 0.0;
                    break;            // codon changes in more than one codon position
                case 1:
                    rates[i] = alpha  * kappa;
                    break;        // synonymous transition
                case 2:
                    rates[i] = alpha;
                    break;        // synonymous transversion
                case 3:
                    rates[i] = beta  * kappa;
                    break;         // non-synonymous transition
                case 4:
                    rates[i] = beta;
                    break;            // non-synonymous transversion
            }
        }
    }
}