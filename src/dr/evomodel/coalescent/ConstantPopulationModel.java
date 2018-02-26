package dr.evomodel.coalescent;
import dr.evolution.coalescent.ConstantPopulation;
import dr.evolution.coalescent.DemographicFunction;
import dr.evomodelxml.coalescent.ConstantPopulationModelParser;
import dr.inference.model.Parameter;
public class ConstantPopulationModel extends DemographicModel {
    //
    // Public stuff
    //
    public ConstantPopulationModel(Parameter N0Parameter, Type units) {
        this(ConstantPopulationModelParser.CONSTANT_POPULATION_MODEL, N0Parameter, units);
    }
    public ConstantPopulationModel(String name, Parameter N0Parameter, Type units) {
        super(name);
        constantPopulation = new ConstantPopulation(units);
        this.N0Parameter = N0Parameter;
        addVariable(N0Parameter);
        N0Parameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        setUnits(units);
    }
    // general functions
    public DemographicFunction getDemographicFunction() {
        constantPopulation.setN0(N0Parameter.getParameterValue(0));
        return constantPopulation;
    }
    //
    // protected stuff
    //
    private Parameter N0Parameter;
    private ConstantPopulation constantPopulation = null;
}
