
package dr.evomodel.coalescent;

import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.coalescent.EmpiricalPiecewiseConstant;
import dr.evomodelxml.coalescent.EmpiricalPiecewiseModelParser;
import dr.inference.model.Model;
import dr.inference.model.Parameter;

public class EmpiricalPiecewiseModel extends DemographicModel {
    //
    // Public stuff
    //
    public EmpiricalPiecewiseModel(double[] intervalWidths, Parameter populationSizesParameter, Parameter tauParameter, Parameter bParameter, Parameter lagParameter, Type units) {

        this(EmpiricalPiecewiseModelParser.EMPIRICAL_PIECEWISE, intervalWidths, populationSizesParameter, tauParameter, bParameter, lagParameter, units);

    }

    public EmpiricalPiecewiseModel(String name, double[] intervalWidths, Parameter populationSizesParameter, Parameter tauParameter, Parameter bParameter, Parameter lagParameter, Type units) {

        super(name);

        //System.out.println("intervalWidths.length=" + intervalWidths.length);
        //System.out.println("populationSizes.dimension=" + populationSizesParameter.getDimension());

        if (intervalWidths.length == 1) {
            double[] newIntervalWidths = new double[populationSizesParameter.getDimension() - 1];
            for (int i = 0; i < newIntervalWidths.length; i++) {
                newIntervalWidths[i] = intervalWidths[0];
            }
            intervalWidths = newIntervalWidths;
        }
        //System.out.println("new intervalWidths.length=" + intervalWidths.length);

        if (populationSizesParameter.getDimension() != (intervalWidths.length + 1)) {
            throw new IllegalArgumentException(
                    "interval widths array must have either 1 or " + (populationSizesParameter.getDimension() - 1) +
                            " elements, but instead it has " + intervalWidths.length + "."
            );
        }

        this.tauParameter = tauParameter;
        this.lagParameter = lagParameter;
        this.bParameter = bParameter;

        this.intervalWidths = intervalWidths;
        this.populationSizesParameter = populationSizesParameter;

        addVariable(tauParameter);
        addVariable(lagParameter);
        addVariable(bParameter);
        addVariable(populationSizesParameter);
        tauParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 0.0, tauParameter.getDimension()));
        lagParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 0.0, lagParameter.getDimension()));
        bParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 0.0, bParameter.getDimension()));
        populationSizesParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 0.0, populationSizesParameter.getDimension()));

        setUnits(units);

        piecewiseFunction = new EmpiricalPiecewiseConstant(intervalWidths, calculatePopSizes(),
                lagParameter.getParameterValue(0), units);
    }

    public DemographicFunction getDemographicFunction() {
        piecewiseFunction.setLag(lagParameter.getParameterValue(0));
        piecewiseFunction.setPopulationSizes(calculatePopSizes());

        return piecewiseFunction;
    }

    private double[] calculatePopSizes() {
        double m = tauParameter.getParameterValue(0);
        double c = bParameter.getParameterValue(0);

        double[] popSizes = new double[populationSizesParameter.getDimension()];
        for (int i = 0; i < popSizes.length; i++) {
            popSizes[i] = m * populationSizesParameter.getParameterValue(i) + c;
        }
        return popSizes;
    }

    // **************************************************************
    // Model IMPLEMENTATION
    // **************************************************************

    protected void handleModelChangedEvent(Model model, Object object, int index) {
        // no intermediates need to be recalculated...
    }

//	protected void handleVariableChangedEvent(Parameter parameter, int index) {
//
//		// no intermediates need to be recalculated...
//	}
//

    // todo: why override?

    protected void storeState() {
    } // no additional state needs storing

    protected void restoreState() {
    } // no additional state needs restoring

    protected void acceptState() {
    } // no additional state needs accepting

    //
    // protected stuff
    //

    Parameter tauParameter;
    Parameter lagParameter;
    Parameter bParameter;
    Parameter populationSizesParameter;
    double[] intervalWidths;
    EmpiricalPiecewiseConstant piecewiseFunction = null;
}
