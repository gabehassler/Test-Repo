
package dr.inference.loggers;

import java.io.Serializable;


public interface LogColumn extends Serializable {

    void setLabel(String label);

    String getLabel();

    void setMinimumWidth(int minimumWidth);

    int getMinimumWidth();

    String getFormatted();

    public abstract class Abstract implements LogColumn {

        private String label;
        private int minimumWidth;

        public Abstract(String label) {

            setLabel(label);
            minimumWidth = -1;
        }

        public void setLabel(String label) {
            if (label == null) throw new IllegalArgumentException("column label is null");
            this.label = label;
        }

        public String getLabel() {
            StringBuffer buffer = new StringBuffer(label);

            if (minimumWidth > 0) {
                while (buffer.length() < minimumWidth) {
                    buffer.append(' ');
                }
            }

            return buffer.toString();
        }

        public void setMinimumWidth(int minimumWidth) {
            this.minimumWidth = minimumWidth;
        }

        public int getMinimumWidth() {
            return minimumWidth;
        }

        public final String getFormatted() {
            StringBuffer buffer = new StringBuffer(getFormattedValue());

            if (minimumWidth > 0) {
                while (buffer.length() < minimumWidth) {
                    buffer.append(' ');
                }
            }

            return buffer.toString();
        }

        protected abstract String getFormattedValue();
    }

    public class Default extends Abstract {

        private Object object;

        public Default(String label, Object object) {
            super(label);
            this.object = object;
        }

        protected String getFormattedValue() {
            return object.toString();
        }
    }

}
