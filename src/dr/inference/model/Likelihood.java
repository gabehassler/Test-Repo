
package dr.inference.model;

import dr.inference.loggers.Loggable;
import dr.util.Identifiable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public interface Likelihood extends Loggable, Identifiable {

	Model getModel();

	double getLogLikelihood();

	void makeDirty();

    String prettyName();

    boolean isUsed();

    void setUsed();

    boolean evaluateEarly();


	public abstract class Abstract implements Likelihood, ModelListener {

		public Abstract(Model model) {

			this.model = model;
			if (model != null) model.addModelListener(this);
   		}

        public void modelChangedEvent(Model model, Object object, int index) {
            makeDirty();
        }

        // by default restore is the same as changed
        public void modelRestored(Model model) {
            makeDirty();
        }

        // **************************************************************
	    // Likelihood IMPLEMENTATION
	    // **************************************************************

		public Model getModel() { return model; }

		public final double getLogLikelihood() {
			if (!getLikelihoodKnown()) {
				logLikelihood = calculateLogLikelihood();
				likelihoodKnown = true;
			}
			return logLikelihood;
		}

		public void makeDirty() {
			likelihoodKnown = false;
		}

		protected boolean getLikelihoodKnown() {
			return likelihoodKnown;
		}

		protected abstract double calculateLogLikelihood();

		public String toString() {
            // don't call any "recalculating" stuff like getLogLikelihood() in toString -
            // this interferes with the debugger.

            //return getClass().getName() + "(" + getLogLikelihood() + ")";
            return getClass().getName() + "(" + (getLikelihoodKnown() ? logLikelihood : "??") + ")";
		}

        static public String getPrettyName(Likelihood l) {
            final Model m = l.getModel();
            String s = l.getClass().getName();
            String[] parts = s.split("\\.");
            s = parts[parts.length - 1];
            if( m != null ) {
                final String modelName = m.getModelName();
                final String i = m.getId();
                s = s + "(" + modelName;
                if( i != null && !i.equals(modelName) ) {
                    s = s + '[' + i + ']';
                }
                s = s + ")";
            }
            return s;
        }

      public String prettyName() {
          return getPrettyName(this);
      }

        public boolean isUsed() {
            return used;
        }

        public void setUsed() {
            this.used = true;
        }

        public boolean evaluateEarly() {
            return false;
        }

        // **************************************************************
	    // Loggable IMPLEMENTATION
	    // **************************************************************

		public dr.inference.loggers.LogColumn[] getColumns() {
			return new dr.inference.loggers.LogColumn[] {
				new LikelihoodColumn(getId())
			};
		}

		private class LikelihoodColumn extends dr.inference.loggers.NumberColumn {
			public LikelihoodColumn(String label) { super(label); }
			public double getDoubleValue() { return getLogLikelihood(); }
		}

		// **************************************************************
	    // Identifiable IMPLEMENTATION
	    // **************************************************************

		private String id = null;

		public void setId(String id) { this.id = id; }

		public String getId() { return id; }

		private final Model model;
		private double logLikelihood;
		private boolean likelihoodKnown = false;

        private boolean used = false;
	}


    // set to store all created likelihoods
    final static Set<Likelihood> FULL_LIKELIHOOD_SET = new HashSet<Likelihood>();

}
