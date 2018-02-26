
package dr.inference.model;


public class FiniteSetParameter extends Parameter.Abstract implements VariableListener {

    public void variableChangedEvent(Variable variable, int index, ChangeType type) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void storeValues() {
        indicator.storeParameterValues();
    }

    protected void restoreValues() {
        indicator.restoreParameterValues();
    }

    protected void acceptValues() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void adoptValues(Parameter source) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public double getParameterValue(int dim) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setParameterValue(int dim, double value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setParameterValueQuietly(int dim, double value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setParameterValueNotifyChangedAll(int dim, double value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getParameterName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addBounds(Bounds<Double> bounds) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Bounds<Double> getBounds() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addDimension(int index, double value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public double removeDimension(int index) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private Parameter indicator;

}
