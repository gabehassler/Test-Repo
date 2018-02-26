
package dr.app.beauti.components.marginalLikelihoodEstimation;

import dr.app.beauti.components.ComponentFactory;
import dr.app.beauti.generator.ComponentGenerator;
import dr.app.beauti.options.BeautiOptions;
import dr.app.beauti.options.ComponentOptions;

public class MarginalLikelihoodEstimationComponentFactory implements ComponentFactory {

    private MarginalLikelihoodEstimationComponentFactory() {
        // singleton pattern - private constructor
    }

    @Override
    public Class getOptionsClass() {
        return MarginalLikelihoodEstimationOptions.class;
    }

    public ComponentGenerator createGenerator(final BeautiOptions beautiOptions) {
        return new MarginalLikelihoodEstimationGenerator(beautiOptions);
    }

    public ComponentOptions createOptions(final BeautiOptions beautiOptions) {
        return new MarginalLikelihoodEstimationOptions();
    }

    public static ComponentFactory INSTANCE = new MarginalLikelihoodEstimationComponentFactory();
}
