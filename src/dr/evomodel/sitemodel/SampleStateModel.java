package dr.evomodel.sitemodel;
import dr.evomodel.substmodel.FrequencyModel;
import dr.evomodel.substmodel.SubstitutionModel;
import dr.evomodel.substmodel.YangCodonModel;
import dr.evomodelxml.sitemodel.SampleStateModelParser;
import dr.inference.model.*;
import java.util.Vector;
public class SampleStateModel extends AbstractModel implements SiteModel {
    public static final double OMEGA_MAX_VALUE = 100.0;
    public static final double OMEGA_MIN_VALUE = 0.0;
    public SampleStateModel(Parameter muParameter,
                            Parameter proportionParameter,
                            Vector substitutionModels) {
        super(SampleStateModelParser.SAMPLE_STATE_MODEL);
        this.substitutionModels = substitutionModels;
        for (int i = 0; i < substitutionModels.size(); i++) {
            addModel((SubstitutionModel) substitutionModels.elementAt(i));
        }
        this.categoryCount = substitutionModels.size();
//		stateCount = ((SubstitutionModel)substitutionModels.elementAt(0)).getDataType().getStateCount();
        addVariable(muParameter);
        muParameter.addBounds(new Parameter.DefaultBounds(1000.0, 0.0, 1));
        this.proportionParameter = proportionParameter;
        addVariable(proportionParameter);
        proportionParameter.addBounds(new Parameter.DefaultBounds(1.0, 0.0, proportionParameter.getDimension()));
        proportionParameter.setParameterValue(0, (0.5));
        for (int i = 1; i < categoryCount; i++) {
            proportionParameter.setParameterValue(i, (0.5 / (categoryCount - 1)));
        }
        if (categoryCount > 1) {
            for (int i = 0; i < categoryCount; i++) {
                Parameter p = (Parameter) ((YangCodonModel) substitutionModels.elementAt(i)).getVariable(0);
                Parameter lower = null;
                Parameter upper = null;
                if (i == 0) {
                    upper = (Parameter) ((YangCodonModel) substitutionModels.elementAt(i + 1)).getVariable(0);
                    p.addBounds(new omegaBounds(lower, upper));
                } else {
                    if (i == (categoryCount - 1)) {
                        lower = (Parameter) ((YangCodonModel) substitutionModels.elementAt(i - 1)).getVariable(0);
                        p.addBounds(new omegaBounds(lower, upper));
                    } else {
                        upper = (Parameter) ((YangCodonModel) substitutionModels.elementAt(i + 1)).getVariable(0);
                        lower = (Parameter) ((YangCodonModel) substitutionModels.elementAt(i - 1)).getVariable(0);
                        p.addBounds(new omegaBounds(lower, upper));
                    }
                }
            }
        }
    }
    // *****************************************************************
    // Interface SiteModel
    // *****************************************************************
    public SubstitutionModel getSubstitutionModel() {
        return null;
    }
    public boolean integrateAcrossCategories() {
        return true;
    }
    public int getCategoryCount() {
        return categoryCount;
    }
    public int getCategoryOfSite(int site) {
        throw new IllegalArgumentException("Integrating across categories");
    }
    public double[] getCategoryRates() {
        throw new RuntimeException("getCategoryRates not available in this siteModel");
    }
    public double getRateForCategory(int category) {
        throw new RuntimeException("getRateForCategory not available in this siteModel");
    }
    public double getSubstitutionsForCategory(int category, double time) {
        throw new RuntimeException("getSubstitutionsForCategory not available in this siteModel");
    }
    public void getTransitionProbabilities(double substitutions, double[] matrix) {
        throw new RuntimeException("getTransitionProbabilities not available in this siteModel");
    }
    public FrequencyModel getFrequencyModel() {
        return ((SubstitutionModel) substitutionModels.elementAt(0)).getFrequencyModel();
    }
    public double getProportionForCategory(int category) {
        return proportionParameter.getParameterValue(category);
    }
    public double[] getCategoryProportions() {
        double[] probs = new double[categoryCount];
        for (int i = 0; i < categoryCount; i++) {
            probs[i] = proportionParameter.getParameterValue(i);
        }
        return probs;
    }
    // *****************************************************************
    // Interface ModelComponent
    // *****************************************************************
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        // Substitution model has changed so fire model changed event
        listenerHelper.fireModelChanged(this, object, index);
    }
    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
              for(int i = 0; i < categoryCount; i++){
                  if(parameter == classSizes[i] && i == 0) // proportions have changed
                      resetProportions(i+1);// adjust the proportion of class above
                  if(parameter == classSizes[i] && i != 0) // proportions have changed
                      resetProportions(i-1);// adjust the proportion of class below
              }
          } else{}*/
    }
    protected void storeState() {
    }
    protected void restoreState() {
    }
    protected void acceptState() {
    } // no additional state needs accepting
    public String toString() {
        StringBuffer s = new StringBuffer();
        return s.toString();
    }
         double[] probs = getCategoryProportions();
         double sum = 0.0;
         double current = classSizes[classToAlter].getParameterValue(0);
         for(int i = 0; i < categoryCount; i++){
             sum += probs[i];
         }
         sum = 1 - sum;
         classSizes[classToAlter].setParameterValue(0,(current+sum));
     }*/
    private class omegaBounds implements Bounds<Double> {
        private final Parameter lowerOmega, upperOmega;
        public omegaBounds(Parameter lowerOmega, Parameter upperOmega) {
            this.lowerOmega = lowerOmega;
            this.upperOmega = upperOmega;
        }
        public omegaBounds(Parameter nearestOmega, boolean isUpper) {
            if (isUpper) {
                lowerOmega = nearestOmega;
                upperOmega = null;
            } else {
                lowerOmega = null;
                upperOmega = nearestOmega;
            }
        }
        public Double getUpperLimit(int dimension) {
            if (dimension != 0)
                throw new RuntimeException("omega parameters have wrong dimension " + dimension);
            if (upperOmega == null)
                return OMEGA_MAX_VALUE;
            else
                return upperOmega.getParameterValue(dimension);
        }
        public Double getLowerLimit(int dimension) {
            if (dimension != 0)
                throw new RuntimeException("omega parameters have wrong dimension " + dimension);
            if (lowerOmega == null)
                return OMEGA_MIN_VALUE;
            else
                return lowerOmega.getParameterValue(dimension);
        }
        public int getBoundsDimension() {
            return 1;
        }
    }
    class classSizeBounds implements Bounds {
        private final Parameter catProportionUnder;
        private final Parameter catProportionAbove;
        private final Parameter catProportion;
        public classSizeBounds(Parameter catProportion, Parameter catProportionUnder, Parameter catProportionAbove) {
            this.catProportionUnder = catProportionUnder;
            this.catProportionAbove = catProportionAbove;
            this.catProportion = catProportion;
        }
        public Double getUpperLimit(int dimension) {
            if (dimension != 0)
                throw new RuntimeException("class size parameters have wrong dimension " + dimension);
            if (catProportionUnder == null)
                return catProportion.getParameterValue(dimension) + catProportionAbove.getParameterValue(dimension);
            else
                return catProportion.getParameterValue(dimension) + catProportionUnder.getParameterValue(dimension);
        }
        public Double getLowerLimit(int dimension) {
            if (dimension != 0)
                throw new RuntimeException("class size parameters have wrong dimension " + dimension);
            return 0.0;
        }
        public int getBoundsDimension() {
            return 1;
        }
    }
    private final Vector substitutionModels;
    private final Parameter proportionParameter;
    private final int categoryCount;
//	private int stateCount;
}
