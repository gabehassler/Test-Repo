package dr.inference.distribution;

import dr.math.distributions.Distribution;
import dr.inference.model.Parameter;

public class TwoPartsDistributionLikelihood extends DistributionLikelihood{
    public static final int PRESENT = 1;
    public static final int ABSENT = 0;
    protected Distribution prior;
    protected Distribution pseudoPrior;

    protected Parameter bitVector;
    protected int paramIndex;

    public TwoPartsDistributionLikelihood(
            Distribution prior,
            Distribution pseudoPrior,
            Parameter bitVector,
            int paramIndex){
        super(prior);
        this.prior = distribution;
        this.pseudoPrior = pseudoPrior;
        this.bitVector = bitVector;
        this.paramIndex = paramIndex;
    }

    // **************************************************************
    // Likelihood IMPLEMENTATION
    // **************************************************************

    public double calculateLogLikelihood() {

        int paramStatus = (int)bitVector.getParameterValue(paramIndex);
        //System.out.println(paramStatus);
        if(paramStatus == PRESENT){
            distribution = prior;
        }else if(paramStatus == ABSENT){
            distribution = pseudoPrior;
        }
        double logL = super.calculateLogLikelihood();
        //System.out.println(paramStatus+" "+logL);
        return logL;
    }
}
