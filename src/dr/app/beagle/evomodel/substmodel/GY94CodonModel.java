
package dr.app.beagle.evomodel.substmodel;

import dr.evolution.datatype.Codons;
import dr.inference.model.Parameter;
import dr.inference.model.Statistic;

public class GY94CodonModel extends AbstractCodonModel {
    protected Parameter kappaParameter;

    protected Parameter omegaParameter;

    public GY94CodonModel(Codons codonDataType, Parameter omegaParameter, Parameter kappaParameter,
                          FrequencyModel freqModel) {
        this(codonDataType, omegaParameter, kappaParameter, freqModel,
                new DefaultEigenSystem(codonDataType.getStateCount()));
    }

    public GY94CodonModel(Codons codonDataType,
                          Parameter omegaParameter,
                          Parameter kappaParameter,
                          FrequencyModel freqModel, EigenSystem eigenSystem) {

        super("GY94", codonDataType, freqModel, eigenSystem);

        this.omegaParameter = omegaParameter;

        int dim = omegaParameter.getDimension();
        double value = omegaParameter.getParameterValue(dim - 1); 
        if(value < 0) {
        	throw new RuntimeException("Negative Omega parameter value " + value);
        }//END: negative check
        
        addVariable(omegaParameter);
        omegaParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0,
                omegaParameter.getDimension()));

        this.kappaParameter = kappaParameter;
        
        dim = kappaParameter.getDimension();
        value = kappaParameter.getParameterValue(dim - 1);
        if(value < 0) {
        	throw new RuntimeException("Negative kappa parameter value value " + value);
        }//END: negative check
        
        addVariable(kappaParameter);
        kappaParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0,
                kappaParameter.getDimension()));

        // Assuming it's always the same dim
        
        
        addStatistic(synonymousRateStatistic);
    }

    public void setKappa(double kappa) {
        kappaParameter.setParameterValue(0, kappa);
        updateMatrix = true;
    }

    public double getKappa() {
        return kappaParameter.getParameterValue(0);
    }

    public void setOmega(double omega) {
        omegaParameter.setParameterValue(0, omega);
        updateMatrix = true;
    }

    public double getOmega() {
        return omegaParameter.getParameterValue(0);
    }

    public double getSynonymousRate() {
        double k = getKappa();
        double o = getOmega();
        return ((31.0 * k) + 36.0) / ((31.0 * k) + 36.0 + (138.0 * o) + (58.0 * o * k));
    }

    public double getNonSynonymousRate() {
        return 0;
    }

    protected void setupRelativeRates(double[] rates) {

        double kappa = getKappa();
        double omega = getOmega();
        for (int i = 0; i < rateCount; i++) {
            switch (rateMap[i]) {
                case 0:
                    rates[i] = 0.0;
                    break;            // codon changes in more than one codon position
                case 1:
                    rates[i] = kappa;
                    break;        // synonymous transition
                case 2:
                    rates[i] = 1.0;
                    break;            // synonymous transversion
                case 3:
                    rates[i] = kappa * omega;
                    break;// non-synonymous transition
                case 4:
                    rates[i] = omega;
                    break;        // non-synonymous transversion
            }
        }
    }

    // **************************************************************
    // XHTMLable IMPLEMENTATION
    // **************************************************************

    public String toXHTML() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("<em>Goldman Yang 94 Codon Model</em> kappa = ");
        buffer.append(getKappa());
        buffer.append(", omega = ");
        buffer.append(getOmega());

        return buffer.toString();
    }

    public Statistic synonymousRateStatistic = new Statistic.Abstract() {

        public String getStatisticName() {
            return "synonymousRate";
        }

        public int getDimension() {
            return 1;
        }

        public double getStatisticValue(int dim) {
            return getSynonymousRate();
        }

    };


        public String getStatisticName() {
            return "nonSynonymousRate";
        }

        public int getDimension() { return 1; }

        public double getStatisticValue(int dim) {
            return getNonSynonymousRate();
        }

    };*/

}