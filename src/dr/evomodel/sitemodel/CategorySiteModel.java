package dr.evomodel.sitemodel;
import dr.evomodel.substmodel.FrequencyModel;
import dr.evomodel.substmodel.SubstitutionModel;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
public class CategorySiteModel extends AbstractModel implements SiteModel {
    public CategorySiteModel(SubstitutionModel substitutionModel,
                             Parameter muParameter,
                             Parameter rateParameter,
                             String categoryString,
                             String stateString,
                             int relativeTo) {
        super(SITE_MODEL);
        this.substitutionModel = substitutionModel;
        addModel(substitutionModel);
        this.muParameter = muParameter;
        addVariable(muParameter);
        muParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        this.rateParameter = rateParameter;
        addVariable(rateParameter);
        rateParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, Double.MIN_VALUE, rateParameter.getDimension()));
        states = stateString;
        if (states.length() != (rateParameter.getDimension() + 1)) {
            throw new IllegalArgumentException("States must have one more dimension than rate parameter!");
        }
        categoryCount = states.length();
        categories = new int[categoryString.length()];
        categoryWeights = new int[categoryCount];
        categoryRates = new double[categoryCount];
        for (int i = 0; i < categories.length; i++) {
            char state = categoryString.charAt(i);
            categories[i] = states.indexOf(state);
            categoryWeights[categories[i]] += 1;
        }
        siteCount = categories.length;
        this.relativeTo = relativeTo;
        ratesKnown = false;
    }
    public void setMu(double mu) {
        muParameter.setParameterValue(0, mu);
    }
    public final double getMu() {
        return muParameter.getParameterValue(0);
    }
    public Parameter getMutationRateParameter() {
        return muParameter;
    }
    // *****************************************************************
    // Interface SiteModel
    // *****************************************************************
    public boolean integrateAcrossCategories() {
        return false;
    }
    public int getCategoryCount() {
        return categoryCount;
    }
    public int getCategoryOfSite(int site) {
        return categories[site];
    }
    public double getRateForCategory(int category) {
        synchronized (this) {
            if (!ratesKnown) {
                calculateCategoryRates();
            }
        }
        double mu = 1.0;
        if (muParameter != null) {
            mu = muParameter.getParameterValue(0);
        }
        return categoryRates[category] * mu;
    }
    public double[] getCategoryRates() {
        synchronized (this) {
            if (!ratesKnown) {
                calculateCategoryRates();
            }
        }
        double[] rates = new double[categoryRates.length];
        double mu = 1.0;
        if (muParameter != null) {
            mu = muParameter.getParameterValue(0);
        }
        for (int i = 0; i < rates.length; i++) {
            rates[i] = categoryRates[i] * mu;
        }
        return rates;
    }
    public void getTransitionProbabilities(double substitutions, double[] matrix) {
        substitutionModel.getTransitionProbabilities(substitutions, matrix);
    }
    public double getProportionForCategory(int category) {
        throw new UnsupportedOperationException();
    }
    public double[] getCategoryProportions() {
        throw new UnsupportedOperationException();
    }
    private void calculateCategoryRates() {
        categoryRates[relativeTo] = 1.0;
        double total = 1.0;
        int count = 0;
        for (int i = 0; i < categoryRates.length; i++) {
            if (i != relativeTo) {
                categoryRates[i] = rateParameter.getParameterValue(count);
                total = categoryRates[i] * categoryWeights[i];
                count += 1;
            }
        }
        total /= (double) siteCount;
        // normalize so that total output rate is 1.0
        for (int i = 0; i < categoryRates.length; i++) {
            categoryRates[i] /= total;
        }
        ratesKnown = true;
    }
    public FrequencyModel getFrequencyModel() {
        return substitutionModel.getFrequencyModel();
    }
    public SubstitutionModel getSubstitutionModel() {
        return substitutionModel;
    }
    // *****************************************************************
    // Interface ModelComponent
    // *****************************************************************
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        // Substitution model has changed so fire model changed event
        listenerHelper.fireModelChanged(this, object, index);
    }
    public void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        if (variable == rateParameter) {
            ratesKnown = false;
        }
        listenerHelper.fireModelChanged(this, variable, index);
    }
    protected void storeState() {
    } // no additional state needs storing
    protected void restoreState() {
        ratesKnown = false;
    }
    protected void acceptState() {
    } // no additional state needs accepting
    private SubstitutionModel substitutionModel = null;
    private final Parameter muParameter;
    private final Parameter rateParameter;
    private boolean ratesKnown;
    private final int categoryCount;
    private final double[] categoryRates;
    private final int[] categoryWeights;
    private final int[] categories;
    private final String states;
    private final int siteCount;
    private int relativeTo = 0;
}
