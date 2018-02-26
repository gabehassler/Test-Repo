
package dr.evomodel.substmodel;

import dr.evolution.datatype.Microsatellite;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;

import java.util.ArrayList;


public abstract class OnePhaseModel extends MicrosatelliteModel{
    protected ArrayList<Variable<Double>> nestedParams = null;

    public OnePhaseModel(String name, Microsatellite microsatellite, FrequencyModel freqModel, Parameter parameter){
        super(name, microsatellite, freqModel, parameter);
        nestedParams=new ArrayList<Variable<Double>>();
    }

    protected void addParam(Variable<Double> param){
        if(isNested){
            nestedParams.add(param);
        }else{
            super.addVariable(param);
        }
    }

    public Variable<Double> getNestedParameter(int i){
        return nestedParams.get(i);
    }

    public int getNestedParameterCount(){
        return nestedParams.size();
    }

    public void computeStationaryDistribution(){
        if(useStationaryFreqs){
            computeOnePhaseStationaryDistribution();
        }
        super.computeStationaryDistribution();

    }


}
