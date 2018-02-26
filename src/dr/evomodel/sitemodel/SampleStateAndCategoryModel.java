
package dr.evomodel.sitemodel;

import dr.evomodel.substmodel.FrequencyModel;
import dr.evomodel.substmodel.SubstitutionModel;
import dr.evomodel.substmodel.YangCodonModel;
import dr.evomodelxml.sitemodel.SampleStateAndCategoryModelParser;
import dr.inference.model.*;

import java.util.Vector;



public class SampleStateAndCategoryModel extends AbstractModel implements SiteModel, CategorySampleModel {

    public static final double OMEGA_MAX_VALUE = 100.0;
    public static final double OMEGA_MIN_VALUE = 0.0;


    public SampleStateAndCategoryModel(Parameter muParameter,
                                       Parameter categoriesParameter,
                                       Vector substitutionModels) {

        super(SampleStateAndCategoryModelParser.SAMPLE_STATE_AND_CATEGORY_MODEL);


        this.substitutionModels = substitutionModels;

        for (int i = 0; i < substitutionModels.size(); i++) {
            addModel((SubstitutionModel) substitutionModels.elementAt(i));

        }

        this.categoryCount = substitutionModels.size();
        sitesInCategory = new int[categoryCount];
        //	stateCount = ((SubstitutionModel)substitutionModels.elementAt(0)).getDataType().getStateCount();

        this.muParameter = muParameter;
        addVariable(muParameter);
        muParameter.addBounds(new Parameter.DefaultBounds(1000.0, 0.0, 1));

        this.categoriesParameter = categoriesParameter;
        addVariable(categoriesParameter);

        if (categoryCount > 1) {
            for (int i = 0; i < categoryCount; i++) {
                Parameter p = (Parameter)((YangCodonModel) substitutionModels.elementAt(i)).getVariable(0);
                Parameter lower = null;
                Parameter upper = null;

                if (i == 0) {
                    upper = (Parameter)((YangCodonModel) substitutionModels.elementAt(i + 1)).getVariable(0);
                    p.addBounds(new omegaBounds(lower, upper));
                } else {
                    if (i == (categoryCount - 1)) {
                        lower = (Parameter)((YangCodonModel) substitutionModels.elementAt(i - 1)).getVariable(0);
                        p.addBounds(new omegaBounds(lower, upper));
                    } else {
                        upper = (Parameter)((YangCodonModel) substitutionModels.elementAt(i + 1)).getVariable(0);
                        lower = (Parameter)((YangCodonModel) substitutionModels.elementAt(i - 1)).getVariable(0);
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
        return false;
    }

    public int getCategoryCount() {
        return categoryCount;
    }

    public int getCategoryOfSite(int site) {
        return (int) categoriesParameter.getParameterValue(site);
    }

    public double getRateForCategory(int category) {
        throw new RuntimeException("getRateForCategory not available in this siteModel");
    }

    public double[] getCategoryRates() {
        throw new RuntimeException("getCategoryRates not available in this siteModel");
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

    // *****************************************************************
    // Interface CategorySampleModel
    // *****************************************************************

    public void setCategoriesParameter(int siteCount) {
        categoriesParameter.setDimension(siteCount);
        categoriesParameter.addBounds(new Parameter.DefaultBounds(categoryCount, 0.0, siteCount));
        for (int i = 0; i < siteCount; i++) {

            int r = (int) (Math.random() * categoryCount);
            categoriesParameter.setParameterValue(i, r);
        }

        for (int j = 0; j < categoryCount; j++) {
            sitesInCategory[j] = 0;
        }

        for (int i = 0; i < siteCount; i++) {
            int value = (int) categoriesParameter.getParameterValue(i);
            sitesInCategory[value] = sitesInCategory[value] + 1;
        }
    }

    public void addSitesInCategoryCount(int category) {
        sitesInCategory[category] = sitesInCategory[category] + 1;
    }

    public void subtractSitesInCategoryCount(int category) {
        sitesInCategory[category] = sitesInCategory[category] - 1;
    }

    public int getSitesInCategoryCount(int category) {
        return sitesInCategory[category];
    }

    public void toggleRandomSite() {
    }


    public double getProportionForCategory(int category) {
        throw new IllegalArgumentException("Not integrating across categories");
    }

    public double[] getCategoryProportions() {
        throw new IllegalArgumentException("Not integrating across categories");
    }

    // *****************************************************************
    // Interface ModelComponent
    // *****************************************************************

    protected void handleModelChangedEvent(Model model, Object object, int index) {
        // Substitution model has changed so fire model changed event
        listenerHelper.fireModelChanged(this, object, index);
    }

    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {

        if (variable == categoriesParameter) // instructs TreeLikelihood to set update flag for this pattern
            listenerHelper.fireModelChanged(this, this, index);
    }

    protected void storeState() {
    }

    protected void restoreState() {
    }

    protected void acceptState() {
    } // no additional state needs accepting

    public String toString() {
        StringBuffer s = new StringBuffer();

        for (int i = 0; i < categoryCount; i++) {
            s.append(sitesInCategory[i] + "\t");
        }
              t = (int)(categoriesParameter.getParameterValue(i));// get result as integer
              s.append(String.valueOf(t) + "\t");
          }*/

        return s.toString();
    }

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

    private final Parameter muParameter;

    private final int[] sitesInCategory;

    private final Parameter categoriesParameter;

    private final Vector substitutionModels;

    private final int categoryCount;

//	private int stateCount;
}
