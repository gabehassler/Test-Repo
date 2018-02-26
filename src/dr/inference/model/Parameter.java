package dr.inference.model;
import dr.inference.parallel.MPIServices;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.*;
public interface Parameter extends Statistic, Variable<Double> {
    double getParameterValue(int dim);
    double[] getParameterValues();
    void setParameterValue(int dim, double value);
    void setParameterValueQuietly(int dim, double value);
    void setParameterValueNotifyChangedAll(int dim, double value);
    String getParameterName();
    void addParameterListener(VariableListener listener);
    void removeParameterListener(VariableListener listener);
    void storeParameterValues();
    void restoreParameterValues();
    void acceptParameterValues();
    void adoptParameterValues(Parameter source);
    boolean isWithinBounds();
    void setDimension(int dim);
    void addBounds(Bounds<Double> bounds);
    Bounds<Double> getBounds();
    public void addDimension(int index, double value);
    public double removeDimension(int index);
    public void fireParameterChangedEvent();
    boolean isUsed();
    public final static Set<Parameter> FULL_PARAMETER_SET = new LinkedHashSet<Parameter>();
    public abstract class Abstract extends Statistic.Abstract implements Parameter {
        protected Abstract() {
            FULL_PARAMETER_SET.add(this);
        }
        protected Abstract(final String name) {
            super(name);
            FULL_PARAMETER_SET.add(this);
        }
        // **************************************************************
        // MPI IMPLEMENTATION
        // **************************************************************
        public void sendState(int toRank) {
            double[] value = getParameterValues();
            MPIServices.sendDoubleArray(value, toRank);
        }
        public void receiveState(int fromRank) {
            final int length = getDimension();
            double[] values = MPIServices.receiveDoubleArray(fromRank, length);
            for (int i = 0; i < length; i++)
                setParameterValueQuietly(i, values[i]);
            this.fireParameterChangedEvent();
        }
        public int getDimension() {
            return 1;
        }
        public void fireParameterChangedEvent() {
            fireParameterChangedEvent(-1, Parameter.ChangeType.VALUE_CHANGED);
        }
        public void fireParameterChangedEvent(int index, Parameter.ChangeType type) {
            if (listeners != null) {
                for (VariableListener listener : listeners) {
                    listener.variableChangedEvent(this, index, type);
                }
            }
        }
        public final void addParameterListener(VariableListener listener) {
            if (listeners == null) {
                listeners = new ArrayList<VariableListener>();
            }
            listeners.add(listener);
        }
        public final void removeParameterListener(VariableListener listener) {
            if (listeners != null) {
                listeners.remove(listener);
            }
        }
        public final String getStatisticName() {
            return getParameterName();
        }
        public final double getStatisticValue(int dim) {
            return getParameterValue(dim);
        }
        @Override
        public String getDimensionName(int dim) {
            if (dimensionNames == null) {
                return super.getDimensionName(dim);
            }
            return dimensionNames[dim];
        }
        public final void setDimensionNames(String[] names) {
            if (names != null && names.length != getDimension()) {
                throw new IllegalArgumentException("Length of dimension name array doesn't match the number of dimensions");
            }
            dimensionNames = names;
        }
        public void setDimension(int dim) {
            throw new UnsupportedOperationException();
        }
        public double[] getParameterValues() {
            double[] copyOfValues = new double[getDimension()];
            for (int i = 0; i < copyOfValues.length; i++) {
                copyOfValues[i] = getParameterValue(i);
            }
            return copyOfValues;
        }
        public final void storeParameterValues() {
            if (isValid) {
                storeValues();
                isValid = false;
            }
        }
        public final void restoreParameterValues() {
            if (!isValid) {
                restoreValues();
                isValid = true;
            }
        }
        public final void acceptParameterValues() {
            if (!isValid) {
                acceptValues();
                isValid = true;
            }
        }
        public final void adoptParameterValues(Parameter source) {
            adoptValues(source);
            isValid = true;
        }
        public boolean isWithinBounds() {
            Bounds<Double> bounds = getBounds();
            for (int i = 0; i < getDimension(); i++) {
                final double value = getParameterValue(i);
                if (value < bounds.getLowerLimit(i) || value > bounds.getUpperLimit(i)) {
                    return false;
                }
            }
            return true;
        }
        // --------------------------------------------------------------------
        // IMPLEMENT VARIABLE
        // --------------------------------------------------------------------
        public final String getVariableName() {
            return getParameterName();
        }
        public final Double getValue(int index) {
            return getParameterValue(index);
        }
        public final void setValue(int index, Double value) {
            setParameterValue(index, value);
        }
        public Double[] getValues() {
            Double[] copyOfValues = new Double[getDimension()];
            for (int i = 0; i < getDimension(); i++) {
                copyOfValues[i] = getValue(i);
            }
            return copyOfValues;
        }
        public int getSize() {
            return getDimension();
        }
        public final void addVariableListener(VariableListener listener) {
            addParameterListener(listener);
        }
        public final void removeVariableListener(VariableListener listener) {
            removeParameterListener(listener);
        }
        public void storeVariableValues() {
            storeParameterValues();
        }
        public void restoreVariableValues() {
            restoreParameterValues();
        }
        public void acceptVariableValues() {
            acceptParameterValues();
        }
        public boolean isUsed() {
            return listeners != null && listeners.size() > 0;
        }
// --------------------------------------------------------------------
        protected abstract void storeValues();
        protected abstract void restoreValues();
        protected abstract void acceptValues();
        protected abstract void adoptValues(Parameter source);
        public String toString() {
            StringBuffer buffer = new StringBuffer(String.valueOf(getParameterValue(0)));
            Bounds bounds = null;
            try {
                bounds = getBounds();
            } catch (NullPointerException e) {
                //
            }
            final String id = getId();
            if (id != null) buffer.append(", ").append(id);
            if (bounds != null) {
                buffer.append("=[").append(String.valueOf(bounds.getLowerLimit(0)));
                buffer.append(", ").append(String.valueOf(bounds.getUpperLimit(0))).append("]");
            }
            for (int i = 1; i < getDimension(); i++) {
                buffer.append(", ").append(String.valueOf(getParameterValue(i)));
                if (bounds != null) {
                    buffer.append("[").append(String.valueOf(bounds.getLowerLimit(i)));
                    buffer.append(", ").append(String.valueOf(bounds.getUpperLimit(i))).append("]");
                }
            }
            return buffer.toString();
        }
        public Element createElement(Document document) {
            throw new IllegalArgumentException();
        }
        private boolean isValid = true;
        private ArrayList<VariableListener> listeners;
        private String[] dimensionNames = null;
    }
    class Default extends Abstract {
        public Default(String id, int dimension) {
            this(dimension);
            setId(id);
        }
        public Default(String id) {
            this(1); // dimension
            setId(id);
        }
        public Default(int dimension) {
            this(dimension, 1.0);
        }
        public Default(double initialValue) {
            values = new double[1];
            values[0] = initialValue;
            this.bounds = null;
        }
        public Default(String id, double initialValue, double lower, double upper) {
            this(initialValue);
            setId(id);
            addBounds(new DefaultBounds(upper, lower, 1));
        }
        public Default(int dimension, double initialValue) {
            values = new double[dimension];
            for (int i = 0; i < dimension; i++) {
                values[i] = initialValue;
            }
            this.bounds = null;
        }
        public Default(String id, double[] values) {
            this(values);
            setId(id);
        }
        public Default(double[] values) {
            this.values = new double[values.length];
            System.arraycopy(values, 0, this.values, 0, values.length);
        }
        public Default(String id, int dimension, double initialValue) {
            this(dimension, initialValue);
            setId(id);
        }
        public void addBounds(Bounds<Double> boundary) {
            if (bounds == null) {
                bounds = boundary;
            } else {
                if (!(bounds instanceof IntersectionBounds)) {
                    IntersectionBounds newBounds = new IntersectionBounds(getDimension());
                    newBounds.addBounds(bounds);
                    bounds = newBounds;
                }
                ((IntersectionBounds) bounds).addBounds(boundary);
            }
            // can't change dimension after bounds are added!
            //hasBeenStored = true;
        }
        //********************************************************************
        // GETTERS
        //********************************************************************
        public final int getDimension() {
            return values.length;
        }
        public final int getSize() {
            return getDimension();
        }
        public final double getParameterValue(int i) {
            return values[i];
        }
        public final double[] getParameterValues() {
            double[] copyOfValues = new double[values.length];
            System.arraycopy(values, 0, copyOfValues, 0, copyOfValues.length);
            return copyOfValues;
        }
        public final double[] inspectParameterValues() {
            return values;
        }
        public Bounds<Double> getBounds() {
            if (bounds == null) {
                throw new NullPointerException(getParameterName() + " parameter: Bounds not set");
            }
            return bounds;
        }
        public String getParameterName() {
            return getId();
        }
        //********************************************************************
        // SETTERS
        //********************************************************************
        public void setDimension(int dim) {
            final int oldDim = getDimension();
            if (oldDim == dim) {
                return;
            }
            assert storedValues == null :
                    "Can't change dimension after store has been called! storedValues=" +
                            Arrays.toString(storedValues) + " bounds=" + bounds;
            double[] newValues = new double[dim];
            // copy over new values, min in case new dim is smaller
            System.arraycopy(values, 0, newValues, 0, Math.min(oldDim, dim));
            // fill new values with first item
            for (int i = oldDim; i < dim; i++) {
                newValues[i] = values[0];
            }
            values = newValues;
            if (bounds != null) {
                //assert oldDim < dim :  "Can't decrease dimension when bounds are set";
                for (int k = 1; k < oldDim; ++k) {
                    assert ((double) bounds.getLowerLimit(k) == bounds.getLowerLimit(0)) &&
                            ((double) bounds.getUpperLimit(k) == bounds.getUpperLimit(0)) :
                            "Can't change dimension when bounds are not all equal";
                }
                final double low = bounds.getLowerLimit(0);
                final double high = bounds.getUpperLimit(0);
                bounds = null;
                addBounds(low, high);
            }
        }
        public void addDimension(int index, double value) {
            assert bounds == null;
            final int n = values.length;
            double[] newValues = new double[n + 1];
            System.arraycopy(values, 0, newValues, 0, index);
            newValues[index] = value;
            System.arraycopy(values, index, newValues, index + 1, n - index);
            values = newValues;
            fireParameterChangedEvent(index, Parameter.ChangeType.ADDED);
        }
        public double removeDimension(int index) {
            assert bounds == null;
            final int n = values.length;
            final double value = values[index];
            final double[] newValues = new double[n - 1];
            System.arraycopy(values, 0, newValues, 0, index);
            System.arraycopy(values, index, newValues, index - 1, n - index);
            values = newValues;
            fireParameterChangedEvent(index, Parameter.ChangeType.REMOVED);
            return value;
        }
        public void setParameterValue(int i, double val) {
            values[i] = val;
            fireParameterChangedEvent(i, Parameter.ChangeType.VALUE_CHANGED);
        }
        public void setParameterValueQuietly(int dim, double value) {
            values[dim] = value;
        }
        public void setParameterValueNotifyChangedAll(int i, double val) {
            values[i] = val;
            fireParameterChangedEvent(i, Parameter.ChangeType.ALL_VALUES_CHANGED);
        }
        protected final void storeValues() {
            // no need to pay a price in a very common call for one-time rare usage
            //hasBeenStored = true;
            if (storedValues == null || storedValues.length != values.length) {
                storedValues = new double[values.length];
            }
            System.arraycopy(values, 0, storedValues, 0, storedValues.length);
        }
        protected final void restoreValues() {
            //swap the arrays
            double[] temp = storedValues;
            storedValues = values;
            values = temp;
            //if (storedValues != null) {
            //	System.arraycopy(storedValues, 0, values, 0, values.length);
            //} else throw new RuntimeException("restore called before store!");
        }
        protected final void acceptValues() {
        }
        protected final void adoptValues(Parameter source) {
            // todo bug ? bounds not adopted?
            if (getDimension() != source.getDimension()) {
                throw new RuntimeException("The two parameters don't have the same number of dimensions");
            }
            for (int i = 0, n = getDimension(); i < n; i++) {
                values[i] = source.getParameterValue(i);
            }
        }
        private double[] values;
        private double[] storedValues;
        // same as !storedValues && !bounds
        //private boolean hasBeenStored = false;
        private Bounds<Double> bounds = null;
        public void addBounds(double lower, double upper) {
            addBounds(new DefaultBounds(upper, lower, getDimension()));
        }
    }
    class DefaultBounds implements Bounds<Double> {
        public DefaultBounds(double upper, double lower, int dimension) {
            this.uppers = new double[dimension];
            this.lowers = new double[dimension];
            for (int i = 0; i < dimension; i++) {
                uppers[i] = upper;
                lowers[i] = lower;
            }
        }
//
//		public DefaultBounds(ArrayList<java.lang.Double> upperList, ArrayList<java.lang.Double> lowerList) {
//
//            final int length = upperList.size();
//            if (length != lowerList.size()) {
//				throw new IllegalArgumentException("upper and lower limits must be defined on the same number of dimensions.");
//			}
//			uppers = new double[length];
//			lowers = new double[length];
//			for (int i = 0; i < uppers.length; i++) {
//				uppers[i] = upperList.get(i);
//				lowers[i] = lowerList.get(i);
//			}
//		}
        public DefaultBounds(double[] uppers, double[] lowers) {
            if (uppers.length != lowers.length) {
                throw new IllegalArgumentException("upper and lower limits must be defined on the same number of dimensions.");
            }
            this.uppers = uppers;
            this.lowers = lowers;
        }
        public Double getUpperLimit(int i) {
            return uppers[i];
        }
        public Double getLowerLimit(int i) {
            return lowers[i];
        }
        public int getBoundsDimension() {
            return uppers.length;
        }
        public boolean isConstant() {
            return true;
        }
        private final double[] uppers, lowers;
    }
}
