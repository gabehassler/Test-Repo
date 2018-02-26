package dr.inference.model;
import dr.util.Transform;
public class TransformedParameter extends Parameter.Abstract implements VariableListener {
    public TransformedParameter(Parameter parameter, Transform transform) {
        this(parameter, transform, false);
    }
    public TransformedParameter(Parameter parameter, Transform transform, boolean inverse) {
        this.parameter = parameter;
        this.transform = transform;
        this.inverse = inverse;
    }
    public int getDimension() {
        return parameter.getDimension();
    }
    protected void storeValues() {
        parameter.storeParameterValues();
    }
    protected void restoreValues() {
        parameter.restoreParameterValues();
    }
    protected void acceptValues() {
        parameter.acceptParameterValues();
    }
    protected void adoptValues(Parameter source) {
        parameter.adoptParameterValues(source);
    }
    private double transform(double value) {
        if (inverse) {
            return transform.inverse(value);
        } else {
            return transform.transform(value);
        }
    }
    private double inverse(double value) {
        return inverse ? transform.transform(value) : transform.inverse(value);
    }
    public double getParameterValue(int dim) {
        return transform(parameter.getParameterValue(dim));
    }
    public void setParameterValue(int dim, double value) {
        parameter.setParameterValue(dim, inverse(value));
    }
    public void setParameterValueQuietly(int dim, double value) {
        parameter.setParameterValueQuietly(dim, inverse(value));
    }
    public void setParameterValueNotifyChangedAll(int dim, double value) {
        parameter.setParameterValueNotifyChangedAll(dim, inverse(value));
    }
    public String getParameterName() {
        if (getId() == null)
            return "transformed." + parameter.getParameterName();
        return getId();
    }
    public void addBounds(Bounds<Double> bounds) {
        final int dim = bounds.getBoundsDimension();
        final double[] lower = new double[dim];
        final double[] upper = new double[dim];
        for (int i = 0; i < dim; ++i) {
            lower[i] = inverse(bounds.getLowerLimit(i));
            upper[i] = inverse(bounds.getUpperLimit(i));
        }
        transformedBounds = new DefaultBounds(upper, lower);
//        System.err.println("Started with:");
//        for (int i = 0; i < dim; ++i) {
//            System.err.print("\t" + bounds.getLowerLimit(i));
//        }
//        System.err.println("");
//        for (int i = 0; i < dim; ++i) {
//            System.err.print("\t" + bounds.getUpperLimit(i));
//        }
//        System.err.println("\n");
//
//        System.err.println("Ended with:");
//        for (int i = 0; i < dim; ++i) {
//            System.err.print("\t" + transformedBounds.getLowerLimit(i));
//        }
//        System.err.println("");
//        for (int i = 0; i < dim; ++i) {
//            System.err.print("\t" + transformedBounds.getUpperLimit(i));
//        }
//        System.err.println("\n");
//
        parameter.addBounds(transformedBounds);
//        throw new RuntimeException("Should not call addBounds() on transformed parameter");
    }
    public Bounds<Double> getBounds() {
        return transformedBounds;
//        throw new RuntimeException("Should not call getBounds() on transformed parameter");
    }
    public void addDimension(int index, double value) {
        throw new RuntimeException("Not yet implemented.");
    }
    public double removeDimension(int index) {
        throw new RuntimeException("Not yet implemented.");
    }
    public void variableChangedEvent(Variable variable, int index, ChangeType type) {
        throw new RuntimeException("Should not call variableChangedEvent() on transformed parameter");
    }
    private final Parameter parameter;
    private final Transform transform;
    private final boolean inverse;
    private Bounds<Double> transformedBounds = null;
}
