
package dr.evomodel.coalescent;

import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.ExpConstExpDemographic;
import dr.evomodelxml.coalescent.ExpConstExpDemographicModelParser;
import dr.inference.model.Parameter;

public class ExpConstExpDemographicModel extends DemographicModel {

    //
    // Public stuff
    //

    public ExpConstExpDemographicModel(
            Parameter N0Parameter,
            Parameter N1Parameter,
            Parameter growthRateParameter,
            Parameter timeParameter,
            Parameter relTimeParameter,
            Type units) {

        this(ExpConstExpDemographicModelParser.EXP_CONST_EXP_MODEL, N0Parameter, N1Parameter, growthRateParameter, timeParameter, relTimeParameter, units);
    }

    public ExpConstExpDemographicModel(String name, Parameter N0Parameter, Parameter N1Parameter, Parameter growthRateParameter, Parameter timeParameter, Parameter relTimeParameter, Type units) {

        super(name);

        expConstExp = new ExpConstExpDemographic(units);

        this.N0Parameter = N0Parameter;
        addVariable(N0Parameter);
        N0Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));

        this.N1Parameter = N1Parameter;
        addVariable(N1Parameter);
        N1Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));

        this.growthRateParameter = growthRateParameter;
        addVariable(growthRateParameter);
        growthRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.MIN_VALUE, 1));

        this.timeParameter = timeParameter;
        addVariable(timeParameter);
        timeParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.MIN_VALUE, 1));

        this.relTimeParameter = relTimeParameter;
        addVariable(relTimeParameter);
        relTimeParameter.addBounds(new Parameter.DefaultBounds(1.0, Double.MIN_VALUE, 1));

        setUnits(units);
    }


    // general functions

    public DemographicFunction getDemographicFunction() {

        expConstExp.setN0(N0Parameter.getParameterValue(0));

        double relTime = relTimeParameter.getParameterValue(0);
        double time2 = timeParameter.getParameterValue(0);

        //System.out.println("relTime=" + relTime);
        //System.out.println("time2=" + (time2));


        double timeInModernGrowthPhase = time2 * relTime;

        double r = -Math.log(N1Parameter.getParameterValue(0)) / timeInModernGrowthPhase;

        //System.out.println("N0=" + N0Parameter.getParameterValue(0));
        //System.out.println("r=" + r);
        //System.out.println("r2=" + growthRateParameter.getParameterValue(0));
        //System.out.println("time1=" + timeInModernGrowthPhase);
        //System.out.println("plateauTime=" + (time2-timeInModernGrowthPhase));

        expConstExp.setGrowthRate(r);
        expConstExp.setGrowthRate2(growthRateParameter.getParameterValue(0));

        expConstExp.setTime1(timeInModernGrowthPhase);
        expConstExp.setPlateauTime(time2 - timeInModernGrowthPhase);

        return expConstExp;
    }

    //
    // protected stuff
    //

    Parameter N0Parameter = null;
    Parameter N1Parameter = null;
    Parameter growthRateParameter = null;
    Parameter timeParameter = null;
    Parameter relTimeParameter = null;
    ExpConstExpDemographic expConstExp = null;
}
