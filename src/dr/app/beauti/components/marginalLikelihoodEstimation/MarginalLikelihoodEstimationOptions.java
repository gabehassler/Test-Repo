
package dr.app.beauti.components.marginalLikelihoodEstimation;

import dr.app.beauti.options.ComponentOptions;
import dr.app.beauti.options.ModelOptions;
import dr.app.beauti.options.Operator;
import dr.app.beauti.options.Parameter;

import java.util.List;

public class MarginalLikelihoodEstimationOptions implements ComponentOptions {

    MarginalLikelihoodEstimationOptions() {
    }

    @Override
    public void createParameters(ModelOptions modelOptions) {
        // nothing to do
    }

    @Override
    public void selectParameters(ModelOptions modelOptions, List<Parameter> params) {
        // nothing to do
    }

    @Override
    public void selectStatistics(ModelOptions modelOptions, List<Parameter> stats) {
        // nothing to do
    }

    @Override
    public void selectOperators(ModelOptions modelOptions, List<Operator> ops) {
        // nothing to do
    }

    //MLE options
    public boolean performMLE = false;
    public boolean performMLEGSS = false;
    public boolean printOperatorAnalysis = false;
    public int pathSteps = 100;
    public int mleChainLength = 1000000;
    public int mleLogEvery = 1000;
    public String mleFileName = "MLE.log";
    public String pathScheme = "betaquantile";
    //public String choiceParameterWorkingPrior = "normal";
    public String choiceTreeWorkingPrior = "Product of exponential distributions";
    public double schemeParameter = 0.30;

}