
package dr.evomodel.coalescent;

import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.ExponentialGrowth;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.coalescent.EmergingEpidemicModelParser;
import dr.evomodelxml.coalescent.ExponentialGrowthModelParser;
import dr.inference.model.Parameter;
import dr.inference.model.Statistic;

public class EmergingEpidemicModel extends DemographicModel {

    //
    // Public stuff
    //
    public EmergingEpidemicModel(Parameter growthRateParameter,
                                 Parameter generationTimeParameter,
                                 Parameter generationShapeParameter,
                                 Parameter offspringDispersionParameter,
                                 TreeModel treeModel,
                                 Type units) {

        this(EmergingEpidemicModelParser.EMERGING_EPIDEMIC_MODEL, growthRateParameter, generationTimeParameter, generationShapeParameter, offspringDispersionParameter, treeModel, units);
    }

    public EmergingEpidemicModel(String name,
                                 Parameter growthRateParameter,
                                 Parameter generationTimeParameter,
                                 Parameter generationShapeParameter,
                                 Parameter offspringDispersionParameter,
                                 TreeModel treeModel,
                                 Type units) {

        super(name);

        exponentialGrowth = new ExponentialGrowth(units);

        this.growthRateParameter = growthRateParameter;
        addVariable(growthRateParameter);
        growthRateParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 0.0, 1));

        this.generationTimeParameter = generationTimeParameter;
        addVariable(generationTimeParameter);
        generationTimeParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 0.0, 1));

        this.generationShapeParameter = generationShapeParameter;
        addVariable(generationShapeParameter);
        generationShapeParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 0.0, 1));

        this.offspringDispersionParameter = offspringDispersionParameter;
        addVariable(offspringDispersionParameter);
        offspringDispersionParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 0.0, 1));

        this.treeModel = treeModel;
        addModel(treeModel);

        addStatistic(new N0Statistic("N0"));
        addStatistic(new RStatistic("R"));

        setUnits(units);
    }

    // general functions

    public DemographicFunction getDemographicFunction() {
        exponentialGrowth.setN0(getN0());
        exponentialGrowth.setGrowthRate(growthRateParameter.getParameterValue(0));

        return exponentialGrowth;
    }

    public double getR() {
        double r = growthRateParameter.getParameterValue(0);
        double Tg = generationTimeParameter.getParameterValue(0);
        double alpha = generationShapeParameter.getParameterValue(0);

        double R = Math.pow(1.0 + ((r * Tg) / alpha), alpha);

        return R;
    }

    public double getN0() {
        double R = getR();

        double t0 = treeModel.getNodeHeight(treeModel.getRoot());

        double r = growthRateParameter.getParameterValue(0);
        double Tg = generationTimeParameter.getParameterValue(0);
        double k = offspringDispersionParameter.getParameterValue(0);

        double N0 = (k * Tg * Math.exp(r * t0)) / (R * (k + R));

        return N0;
    }

    public class N0Statistic extends Statistic.Abstract {

        public N0Statistic(String name) {
            super(name);
        }

        public int getDimension() {
            return 1;
        }

        public double getStatisticValue(final int i) {
            return getN0();
        }
    }

    public class RStatistic extends Statistic.Abstract {

        public RStatistic(String name) {
            super(name);
        }

        public int getDimension() {
            return 1;
        }

        public double getStatisticValue(final int i) {
            return getR();
        }
    }

    //
    // protected stuff
    //

    private final Parameter growthRateParameter;
    private final Parameter generationTimeParameter;
    private final Parameter generationShapeParameter;
    private final Parameter offspringDispersionParameter;
    private final TreeModel treeModel;

    private final ExponentialGrowth exponentialGrowth;
}
