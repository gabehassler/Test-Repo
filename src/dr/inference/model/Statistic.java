
package dr.inference.model;

import dr.inference.loggers.LogColumn;
import dr.inference.loggers.Loggable;
import dr.inference.loggers.NumberColumn;
import dr.util.Attribute;
import dr.util.Identifiable;

public interface Statistic extends Attribute<double[]>, Identifiable, Loggable {

    public static final String NAME = "name";

    String getStatisticName();

    String getDimensionName(int dim);

    void setDimensionNames(String[] names) ;

    int getDimension();

    double getStatisticValue(int dim);


    public abstract class Abstract implements Statistic {

        private String name = null;

        public Abstract() {
            this.name = null;
        }

        public Abstract(String name) {
            this.name = name;
        }

        public String getStatisticName() {
            if (name != null) {
                return name;
            } else if (id != null) {
                return id;
            } else {
                return getClass().toString();
            }
        }

        public String getDimensionName(int dim) {
            if (getDimension() == 1) {
                return getStatisticName();
            } else {
                return getStatisticName() + Integer.toString(dim + 1);
            }
        }

        public void setDimensionNames(String[] names) {
            // do nothing
        }

      public String toString() {
            StringBuffer buffer = new StringBuffer(String.valueOf(getStatisticValue(0)));

            for (int i = 1; i < getDimension(); i++) {
                buffer.append(", ").append(String.valueOf(getStatisticValue(i)));
            }
            return buffer.toString();
        }

        // **************************************************************
        // Attribute IMPLEMENTATION
        // **************************************************************

        public final String getAttributeName() {
            return getStatisticName();
        }

        public double[] getAttributeValue() {
            double[] stats = new double[getDimension()];
            for (int i = 0; i < stats.length; i++) {
                stats[i] = getStatisticValue(i);
            }

            return stats;
        }

        // **************************************************************
        // Identifiable IMPLEMENTATION
        // **************************************************************

        protected String id = null;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        // **************************************************************
        // Loggable IMPLEMENTATION
        // **************************************************************

        public LogColumn[] getColumns() {
            LogColumn[] columns = new LogColumn[getDimension()];
            for (int i = 0; i < getDimension(); i++) {
                columns[i] = new StatisticColumn(getDimensionName(i), i);
            }
            return columns;
        }

        private class StatisticColumn extends NumberColumn {
            private final int dim;

            public StatisticColumn(String label, int dim) {
                super(label);
                this.dim = dim;
            }

            public double getDoubleValue() {
                return getStatisticValue(dim); }
		}
	}
}
