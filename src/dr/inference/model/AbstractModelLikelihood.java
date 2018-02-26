
package dr.inference.model;

import dr.inference.loggers.LogColumn;
import dr.inference.loggers.NumberColumn;

public abstract class AbstractModelLikelihood extends AbstractModel implements Likelihood {
    public AbstractModelLikelihood(String name) {
        super(name);
    }

    public String prettyName() {
        return Likelihood.Abstract.getPrettyName(this);
    }

    @Override
    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed() {
        isUsed = true;
    }

    public boolean evaluateEarly() {
        return false;
    }

    private boolean isUsed = false;

    // **************************************************************
    // Loggable IMPLEMENTATION
    // **************************************************************

    public LogColumn[] getColumns() {
        return new LogColumn[]{
                new LikelihoodColumn(getId())
        };
    }

    protected class LikelihoodColumn extends NumberColumn {
        public LikelihoodColumn(String label) {
            super(label);
        }

        public double getDoubleValue() {
            return getLogLikelihood();
        }
    }
}
