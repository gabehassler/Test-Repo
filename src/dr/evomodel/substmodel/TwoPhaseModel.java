
package dr.evomodel.substmodel;


import dr.evolution.datatype.Microsatellite;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;

import java.util.ArrayList;

public class TwoPhaseModel extends MicrosatelliteModel{


    private Parameter geoParam;
    private Parameter onePhasePrParam;
    private Parameter transformParam;
    private boolean estimateSubmodelParams = false;
    private ArrayList<Variable<Double>> submodelParameters = null;
    private boolean updateSubmodelRates = false;

    public static final String TWO_PHASE_MODEL = "TWOPHASEModel";


    public TwoPhaseModel(
            Microsatellite microsatellite,
            FrequencyModel freqModel,
            OnePhaseModel submodel,
            Parameter onePhasePrParam,
            Parameter geoParam,
            Parameter transParam,
            boolean estimateSubmodelParams){

        super(TWO_PHASE_MODEL, microsatellite, freqModel, null);

        this.subModel = submodel;
        this.estimateSubmodelParams = estimateSubmodelParams;

        if(this.estimateSubmodelParams){
            submodelParameters = new ArrayList<Variable<Double>>();
            for(int i = 0; i < subModel.getNestedParameterCount(); i++){
                addVariable(subModel.getNestedParameter(i));
                submodelParameters.add(subModel.getNestedParameter(i));
            }
            updateSubmodelRates = true;
        }

        this.geoParam = geoParam;
        this.onePhasePrParam = onePhasePrParam;

        addVariable(this.geoParam);
        addVariable(this.onePhasePrParam);

        this.estimateSubmodelParams = estimateSubmodelParams;

        if(transParam != null){
            this.transformParam = transParam;
        }else{
            this.transformParam = new Parameter.Default(0.0);
        }

        //printDetails();

        setupInfinitesimalRates();

        if(freqModel == null){
            useStationaryFreqs = true;
            computeStationaryDistribution();
        }else{
            useStationaryFreqs = false;
        }


    }

    private Parameter transOnePhase;
    private Parameter transGeo;
    private void transform(){
        double e = transformParam.getParameterValue(0);
        double m = geoParam.getParameterValue(0);
        double p = onePhasePrParam.getParameterValue(0);
        if(p < 1 -  e && m < 1 - e || p ==m || e ==0){
            transOnePhase = onePhasePrParam;
            transGeo = geoParam;
        }else if(m > Math.max(1 - e,p)){
            p = p*(m-(m+e-1)/e)/m+(m+e-1)/e;
            transOnePhase = new Parameter.Default(p);
        }else if(p > Math.max(1 - e,m)){
            m = m*(p-(p+e-1)/e)/p+(p+e-1)/e;
            transGeo = new Parameter.Default(m);
        }
    }


    protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        if(submodelParameters !=null && submodelParameters.indexOf(variable) != -1){
            updateSubmodelRates = true;
        }
        updateMatrix = true;

    }

    public void setupInfinitesimalRates(){
        if(updateSubmodelRates){
            subModel.setupInfinitesimalRates();
            updateSubmodelRates = false;
        }
        transform();

        double geoParameter = transGeo.getParameterValue(0);
        double p = transOnePhase.getParameterValue(0);

        for(int i = 1; i < stateCount; i++){
            condProbNum[i] = geoParameter*Math.pow((1.0 - geoParameter),i-1);
        }*/

        setupInfinitesimalRates(
            stateCount,
            geoParameter,
            p,
            infinitesimalRateMatrix,
            subModel.getInfinitesimalRates()
        );


    }

    public static void setupInfinitesimalRates(
            int stateCount,
            double geoParameter,
            double p,
            double[][] rates,
            double[][] subRates){

        double[] condProbNum = new double[stateCount];

        for(int i = 1; i < stateCount; i++){
            condProbNum[i] = (1.0-geoParameter)*Math.pow(geoParameter,i-1);
        }
        double condGeo = 0.0;

        for(int i = 0; i < stateCount; i++){
            double expansionGeoDenom = 1-Math.pow(geoParameter,stateCount - 1 - i);
            double contractionGeoDenom = 1 - Math.pow(geoParameter, i);
            double rowSum = 0.0;
            double submodelRate = 0.0;

            for(int j = 0; j < stateCount; j++){
                if(j < i){
                    condGeo = condProbNum[Math.abs(i-j)]/contractionGeoDenom;
                    submodelRate = subRates[i][i-1];
                }else if(j > i){
                    submodelRate = subRates[i][i+1];
                    condGeo = condProbNum[Math.abs(i-j)]/expansionGeoDenom;
                }

                if(i != j){
                    if(i == j + 1 || i == j - 1){
                        rates[i][j]= submodelRate*(p + (1 - p)*condGeo);
                    }else {
                        rates[i][j] = submodelRate*(1 - p)*condGeo;
                    }
                    rowSum = rowSum+rates[i][j];
                }

            }

            rates[i][i] = 0.0-rowSum;
        }
    }


    public void computeStationaryDistribution() {

        if(useStationaryFreqs){
            computeTwoPhaseStationaryDistribution();
        }
        super.computeStationaryDistribution();
    }

    public MicrosatelliteModel getSubModel(){
        return subModel;
    }

    public Parameter getGeometricParamter(){
        return geoParam;
    }

    public Parameter getOnePhasePrParamter(){
        return onePhasePrParam;

    }

    public Parameter getTransGeometricParamter(){
        return transGeo;
    }

    public Parameter getTransOnePhasePrParamter(){
        return transOnePhase;
    }

    public Parameter getTransformParam(){
        return transformParam;
    }

    public boolean isEstimatingSubmodelParams(){
        return estimateSubmodelParams;
    }

    public void printDetails(){
        System.out.println("Details of the TwoPhase Model and its paramters:");
        System.out.println("a submodel:                     "+isNested);
        System.out.println("has submodel:                   "+hasSubmodel());
        if(hasSubmodel()){
            System.out.println("submodel class:                 "+subModel.getClass());
        }
        System.out.println("esitmating submodel parameters: "+estimateSubmodelParams);
        System.out.println("one phase probability:          "+onePhasePrParam.getParameterValue(0));
        System.out.println("geometric parameter:            "+geoParam.getParameterValue(0));
        System.out.println("transformation parameter:       "+transformParam.getParameterValue(0));
    }

}
