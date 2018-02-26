package dr.inference.operators;
import dr.inference.model.Bounds;
import dr.inference.model.Parameter;
public interface Scalable {
    int scale(double factor, int nDims) throws OperatorFailedException;
    String getName();
    public class Default implements Scalable {
        private final Parameter parameter;
        public Default(Parameter p) {
            this.parameter = p;
        }
        public int scale(double factor, int nDims) throws OperatorFailedException {
            assert nDims <= 0;
            final int dimension = parameter.getDimension();
            for (int i = 0; i < dimension; ++i) {
                parameter.setParameterValue(i, parameter.getParameterValue(i) * factor);
            }
            final Bounds<Double> bounds = parameter.getBounds();
            for (int i = 0; i < dimension; i++) {
                final double value = parameter.getParameterValue(i);
                if (value < bounds.getLowerLimit(i) || value > bounds.getUpperLimit(i)) {
                    throw new OperatorFailedException("proposed value outside boundaries");
                }
            }
            return dimension;
        }
        public String getName() {
            return parameter.getParameterName();
        }
        public int scaleAllAndNotify(double factor, int nDims) throws OperatorFailedException {
            assert nDims <= 0;
            final int dimension = parameter.getDimension();
            final int dimMinusOne = dimension-1;
            for(int i = 0; i < dimMinusOne; ++i) {
                parameter.setParameterValueQuietly(i, parameter.getParameterValue(i) * factor);
            }
            parameter.setParameterValueNotifyChangedAll(dimMinusOne, parameter.getParameterValue(dimMinusOne) * factor);
            final Bounds<Double> bounds = parameter.getBounds();
            for(int i = 0; i < dimension; i++) {
                final double value = parameter.getParameterValue(i);
                if( value < bounds.getLowerLimit(i) || value > bounds.getUpperLimit(i) ) {
                    throw new OperatorFailedException("proposed value outside boundaries");
                }
            }
            return dimension;
        }
    }
}
