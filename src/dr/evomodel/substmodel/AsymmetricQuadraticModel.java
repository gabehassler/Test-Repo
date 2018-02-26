package dr.evomodel.substmodel;


import dr.evolution.datatype.Microsatellite;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;

public class AsymmetricQuadraticModel extends OnePhaseModel{

    public static final String ASYMQUAD_MODEL = "ASYMQUADModel";

    private Variable<Double> expanConst;
    private Variable<Double> expanLin;
    private Variable<Double> expanQuad;
    private Variable<Double> contractConst;
    private Variable<Double> contractLin;
    private Variable<Double> contractQuad;


    public AsymmetricQuadraticModel(Microsatellite microsatellite, FrequencyModel freqModel){
        this(
                microsatellite,
                freqModel,
                null, null, null, null, null, null,
                false);

    }


    public AsymmetricQuadraticModel(Microsatellite microsatellite, FrequencyModel freqModel, boolean isNested){
        this(
                microsatellite,
                freqModel,
                null, null, null, null, null, null,
                isNested
        );

    }


    public AsymmetricQuadraticModel(
            Microsatellite microsatellite,
            FrequencyModel freqModel,
            Variable<Double> expanConst,
            Variable<Double> expanLin,
            Variable<Double> expanQuad,
            Variable<Double> contractConst,
            Variable<Double> contractLin,
            Variable<Double> contractQuad,
            boolean isNested){

        super(ASYMQUAD_MODEL, microsatellite, freqModel,null);


        //The default setting of the parameters gives the same infinitesimal rates
        // as the StepwiseMutaionalModel class.
        this.expanConst = overrideDefault(new Parameter.Default(1.0), expanConst);
        this.expanLin = overrideDefault(new Parameter.Default(0.0), expanLin);
        this.expanQuad = overrideDefault(new Parameter.Default(0.0), expanQuad);
        this.contractConst = overrideDefault(this.expanConst, contractConst);
        this.contractLin = overrideDefault(this.expanLin, contractLin);
        this.contractQuad = overrideDefault(this.expanQuad, contractQuad);
        this.isNested = isNested;
        addParameters();

        //printDetails();

        setupInfinitesimalRates();

        //calculate the default frequencies when not provieded by the user.
        if(freqModel == null){
            useStationaryFreqs = true;
            computeStationaryDistribution();
        }else{
            this.freqModel = freqModel;
        }

        addModel(this.freqModel);

    }

    private void addParameters(){
        addParam(this.expanConst);
        addParam(this.expanLin);
        addParam(this.expanQuad);
        if(this.contractConst != this.expanConst)
            addParam(this.contractConst);
        if(this.contractLin != this.expanLin)
            addParam(this.contractLin);
        if(this.contractQuad != this.expanQuad)
            addParam(this.contractQuad);
    }




    private Variable<Double> overrideDefault(Variable<Double> defaultParam, Variable<Double> providedParam){
        if(providedParam != null && providedParam != defaultParam)
            return providedParam;
        return defaultParam;
    }


    public void setupInfinitesimalRates(){


        double u0 = expanConst.getValue(0);
        double u1 = expanLin.getValue(0);
        double u2 = expanQuad.getValue(0);

        double d0 = contractConst.getValue(0);
        double d1 = contractLin.getValue(0);
        double d2 = contractQuad.getValue(0);

        double rowSum;
        for(int i = 0; i < stateCount;i++){
            rowSum = 0.0;
            if(i - 1 > -1){
                infinitesimalRateMatrix[i][i - 1] =d0+d1*i+d2*i*i;
                rowSum = rowSum + infinitesimalRateMatrix[i][i - 1];

            }

            if(i + 1 < stateCount){
                infinitesimalRateMatrix[i][i + 1] = u0+u1*i+u2*i*i;
                rowSum = rowSum + infinitesimalRateMatrix[i][i + 1];

            }

            infinitesimalRateMatrix[i][i] = rowSum*-1;

        }


    }


    public Variable<Double> getExpansionConstant(){
        return expanConst;
    }

    public Variable<Double> getExpansionLinear(){
        return expanLin;
    }

    public Variable<Double> getExpansionQuad(){
        return expanQuad;
    }

    public Variable<Double> getContractionConstant(){
        return contractConst;
    }

    public Variable<Double> getContractionLinear(){
        return contractLin;
    }

    public Variable<Double> getContractionQuad(){
        return contractQuad;
    }



    public void printDetails(){
        System.out.println("\n");
        System.out.println("Details of the asymmetric quadratic model and its parameters:");
        System.out.println("expansion constant:   "+expanConst.getValue(0));
        System.out.println("expansion linear:     "+ expanLin.getValue(0));
        System.out.println("expansion quadratic:  "+expanQuad.getValue(0));
        System.out.println("contraction constant: "+contractConst.getValue(0));
        System.out.println("contraction linear:   "+contractLin.getValue(0));
        System.out.println("contraction quadratc: "+contractQuad.getValue(0));
        System.out.println("a submodel:           "+isNested);
        System.out.println("\n");
    }

}
