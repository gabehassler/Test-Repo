
package dr.evomodel.epidemiology.casetocase.periodpriors;

import dr.inference.loggers.LogColumn;
import dr.inference.loggers.Loggable;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;

import java.util.ArrayList;



public abstract class AbstractPeriodPriorDistribution extends AbstractModel implements Loggable {

    // are we working on the logarithms of the values?
    protected boolean log;

    protected double logL;
    protected double storedLogL;

    public AbstractPeriodPriorDistribution(String name, boolean log) {
        super(name);
        this.log = log;
    }

    public double getLogLikelihood(double[] values){
        if(!log){
            return calculateLogLikelihood(values);
        } else {
            double[] logValues = new double[values.length];
            for(int i=0; i<values.length; i++){
                logValues[i] = Math.log(values[i]);
            }
            return calculateLogLikelihood(logValues);
        }
    }

    public double getLogPosteriorProbability(double newValue, double minValue){
        if(!log){
            return calculateLogPosteriorProbability(newValue, minValue);
        } else {
            return calculateLogPosteriorProbability(Math.log(newValue), Math.log(minValue));
        }
    }

    public double getLogPosteriorCDF(double limit, boolean upper){
        if(!log){
            return calculateLogPosteriorCDF(limit, upper);
        } else {
            return calculateLogPosteriorCDF(Math.log(limit), upper);
        }
    }

    protected void handleModelChangedEvent(Model model, Object object, int index) {
        //generally nothing to do
    }

    protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        //generally nothing to do
    }

    protected void storeState() {
        storedLogL = logL;

    }

    protected void restoreState() {
        logL = storedLogL;
    }

    protected void acceptState() {
        //generally nothing to do
    }


    public LogColumn[] getColumns() {
        ArrayList<LogColumn> columns = new ArrayList<LogColumn>();

        columns.add(new LogColumn.Abstract(getModelName()+"_LL"){
            protected String getFormattedValue() {
                return String.valueOf(logL);
            }
        });
        return columns.toArray(new LogColumn[columns.size()]);
    }

    public abstract void reset();

    public abstract double calculateLogPosteriorProbability(double newValue, double minValue);

    public abstract double calculateLogPosteriorCDF(double limit, boolean upper);

    public abstract double calculateLogLikelihood(double[] values);




}
