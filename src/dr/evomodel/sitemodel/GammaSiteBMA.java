package dr.evomodel.sitemodel;

import dr.evomodel.substmodel.SubstitutionModel;
import dr.evomodel.substmodel.FrequencyModel;
import dr.inference.model.*;
import dr.math.distributions.GammaDistribution;

public class GammaSiteBMA  extends AbstractModel implements SiteModel {

    private SubstitutionModel substitutionModel = null;
    private Parameter muParameter =null;
    private Variable<Integer> modelChoose = null;
    private Variable<Double> logShape = null;
    private Variable<Double> logitInvar = null;
    private int categoryCount = -1;
    private double[] categoryRates;
    private double[] categoryProportions;
    private boolean ratesKnown;
    public static final int SHAPE_INDEX = 0;
    public static final int INVAR_INDEX = 1;
    public static final int PRESENT = 1;
    public static final int ABSENT = 0;

    public GammaSiteBMA(SubstitutionModel substitutionModel,
                        Parameter muParameter,
                        Variable<Double> logitInvar,
                        Variable<Double> logShape,
                        int categoryCount,
                        Variable<Integer> modelChoose){
        super("GammaSiteBMA");

        this.substitutionModel = substitutionModel;
        addModel(substitutionModel);

        addVariable(muParameter);
        muParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        this.muParameter = muParameter;

        addVariable(logShape);
        logShape.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        this.logShape = logShape;

        addVariable(logitInvar);
        logitInvar.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        this.logitInvar = logitInvar;



        //the modelChoose integer variable is of length = 2,
        //where indices 0 and 1 indicates the presence or absence of alpha and pInvar repectively.
        addVariable(modelChoose);
        modelChoose.addBounds(new Bounds.Int(modelChoose,0,1));
        this.modelChoose = modelChoose;
        this.categoryCount = categoryCount +1;

        categoryRates = new double[this.categoryCount];
        categoryProportions = new double[this.categoryCount];

        ratesKnown = false;

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

    public double getRateForCategory(int category) {
        synchronized (this) {
            if (!ratesKnown) {
                calculateCategoryRates();
            }
        }

        final double mu = (muParameter != null) ? muParameter.getParameterValue(0) : 1.0;

        return categoryRates[category] * mu;
    }

    
    public double[] getCategoryRates() {
        synchronized (this) {
            if (!ratesKnown) {
                calculateCategoryRates();
            }
        }

        final double mu = (muParameter != null) ? muParameter.getParameterValue(0) : 1.0;

        final double[] rates = new double[categoryRates.length];
        for (int i = 0; i < rates.length; i++) {
            rates[i] = categoryRates[i] * mu;
        }

        return rates;
    }

    public void getTransitionProbabilities(double substitutions, double[] matrix) {
        substitutionModel.getTransitionProbabilities(substitutions, matrix);
    }

    public double getProportionForCategory(int category) {
        synchronized (this) {
            if (!ratesKnown) {
                calculateCategoryRates();
            }
        }

        return categoryProportions[category];
    }

    public double[] getCategoryProportions() {
        synchronized (this) {
            if (!ratesKnown) {
                calculateCategoryRates();
            }
        }

        return categoryProportions;
    }

    private void calculateCategoryRates() {

        double propVariable = 1.0;
        int cat = 0;
        //System.out.println("modelChoose: "+ modelChoose.getValue(0)+" "+modelChoose.getValue(1));
        //If including the site invariant parameter.
        
        categoryRates[0] = 0.0;
        //back transform from logit space
        categoryProportions[0] = modelChoose.getValue(INVAR_INDEX)*(1/(1+Math.exp(-logitInvar.getValue(0))));

        propVariable = 1.0 - categoryProportions[0];
        cat = 1;
        

        //If including the gamma shape parameter.
        if (modelChoose.getValue(SHAPE_INDEX) == PRESENT) {

            //back transform from log-space
            final double a = Math.exp(logShape.getValue(0));
            double mean = 0.0;
            final int gammaCatCount = categoryCount - cat;

            for (int i = 0; i < gammaCatCount; i++) {

                categoryRates[i + cat] = GammaDistribution.quantile((2.0 * i + 1.0) / (2.0 * gammaCatCount), a, 1.0 / a);
                //sum of the gamma categorical rates
                mean += categoryRates[i + cat];

                categoryProportions[i + cat] = propVariable / gammaCatCount;
            }
            
            //mean rate over all categories.
            mean = (propVariable * mean) / gammaCatCount;

            for (int i = 0; i < gammaCatCount; i++) {
                //divide rates by the mean so that the average across all sites equals to 1.0
                categoryRates[i + cat] /= mean;

            }
        } else {

            final int gammaCatCount = categoryCount - cat;
            for (int i = 0; i < gammaCatCount; i++) {
                categoryRates[i + cat] = 1.0 / propVariable;
                categoryProportions[i + cat] = propVariable/gammaCatCount;
            }
        }

            System.out.print(categoryRates[i]+" ");
        }
        System.out.println();*/
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

    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        if (variable == logShape) {
            ratesKnown = false;
        } else if (variable == logitInvar) {
            ratesKnown = false;
        } else if (variable == modelChoose) {
            //System.out.println("Changing model");
            ratesKnown = false;
        } else {
            // is the muParameter and nothing needs to be done
        }
        listenerHelper.fireModelChanged(this, variable, index);
    }

    protected void storeState() {
        for(int i = 0; i < categoryRates.length;i++){
            System.out.print(categoryRates[i]+" ");
        }
        System.out.println();*/
    } // no additional state needs storing

    protected void restoreState() {
        //System.out.println("restore, modelChoose: "+ modelChoose.getValue(0)+" "+modelChoose.getValue(1) );
        ratesKnown = false;
    }

    protected void acceptState() {
    } // no additional state needs accepting
}
