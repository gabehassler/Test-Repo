
package dr.app.beauti.components.discrete;

import dr.app.beauti.options.*;
import dr.app.beauti.types.OperatorType;
import dr.app.beauti.types.PriorScaleType;
import dr.evolution.datatype.GeneralDataType;
import dr.inference.operators.RateBitExchangeOperator;

import java.util.List;
import java.util.Set;

public class DiscreteTraitsComponentOptions implements ComponentOptions {

    private final BeautiOptions options;

    public DiscreteTraitsComponentOptions(final BeautiOptions options) {
        this.options = options;
    }

    public void createParameters(final ModelOptions modelOptions) {
        for (AbstractPartitionData partitionData : options.getDataPartitions(GeneralDataType.INSTANCE)) {
            String prefix = partitionData.getName() + ".";

            if (!modelOptions.parameterExists(prefix + "frequencies")) {

                modelOptions.createZeroOneParameterUniformPrior(prefix + "frequencies", "discrete state frequencies", 0.25);
                modelOptions.createCachedGammaPrior(prefix + "rates", "discrete trait instantaneous transition rates",
                        PriorScaleType.SUBSTITUTION_PARAMETER_SCALE, 1.0, 1.0, 1.0, false);

                // BSSVS
                modelOptions.createParameter(prefix + "indicators", "a vector of bits indicating non-zero rates for BSSVS", 1.0);

                // Poisson Prior on non zero ratesBSSVS
                modelOptions.createDiscreteStatistic(prefix + "nonZeroRates", "the number of non-zero rates for BSSVS");

                modelOptions.createScaleOperator(prefix + "frequencies", 0.75, 1.0);
                modelOptions.createOperator(prefix + "rates", OperatorType.SCALE_INDEPENDENTLY, 0.75, 15.0);
                modelOptions.createOperator(prefix + "indicators", OperatorType.BITFLIP, -1.0, 7.0);
//                modelOptions.createScaleOperator(prefix + "mu", demoTuning, 10);

                modelOptions.createZeroOneParameterUniformPrior(prefix + "root.frequencies", "discrete state root frequencies", 0.25);
                modelOptions.createOperator(prefix + "root.frequencies", OperatorType.DELTA_EXCHANGE, 0.75, 1.0);

                //bit Flip on clock.rate in PartitionClockModelSubstModelLink
//                modelOptions.createBitFlipInSubstitutionModelOperator(OperatorType.BITFIP_IN_SUBST.toString() + "mu", prefix + "mu",
//                        "bit Flip In Substitution Model Operator on trait.mu", getParameter("trait.mu"), this, demoTuning, 30);
                modelOptions.createOperatorUsing2Parameters(RateBitExchangeOperator.OPERATOR_NAME,
                        "(indicators, rates)",
                        "rateBitExchangeOperator (If both BSSVS and asymmetric subst selected)",
                        prefix + "indicators", prefix + "rates", OperatorType.RATE_BIT_EXCHANGE, -1.0, 7.0);

            }
        }
    }

    public void selectParameters(final ModelOptions modelOptions, final List<Parameter> params) {
        for (PartitionSubstitutionModel substitutionModel : options.getPartitionSubstitutionModels(GeneralDataType.INSTANCE)) {
            String prefix = substitutionModel.getName() + ".";

            if (substitutionModel.isActivateBSSVS()) {
                modelOptions.getParameter(prefix + "indicators");
                Parameter nonZeroRates = modelOptions.getParameter(prefix + "nonZeroRates");

                Set<String> states = substitutionModel.getDiscreteStateSet();
                int K = states.size();
                if (substitutionModel.getDiscreteSubstType() == DiscreteSubstModelType.SYM_SUBST) {
                    nonZeroRates.mean = Math.log(2); // mean = 0.693 and offset = K-1
                    nonZeroRates.offset = K - 1;
                } else if (substitutionModel.getDiscreteSubstType() == DiscreteSubstModelType.ASYM_SUBST) {
                    nonZeroRates.mean = K - 1; // mean = K-1 and offset = 0
                    nonZeroRates.offset = 0.0;
                } else {
                    throw new IllegalArgumentException("unknown discrete substitution type");
                }

                params.add(nonZeroRates);
            }

            params.add(modelOptions.getParameter(prefix + "frequencies"));
            params.add(modelOptions.getParameter(prefix + "rates"));
        }

        for (AbstractPartitionData partition : options.getDataPartitions(GeneralDataType.INSTANCE)) {
            String prefix = partition.getPrefix();

            if (partition.getPartitionSubstitutionModel().getDiscreteSubstType() == DiscreteSubstModelType.ASYM_SUBST) {
                params.add(modelOptions.getParameter(prefix + "root.frequencies"));
            }

        }

    }

    public void selectStatistics(final ModelOptions modelOptions, final List<Parameter> stats) {
        // no statistics
    }

    public void selectOperators(final ModelOptions modelOptions, final List<Operator> ops) {
        for (PartitionSubstitutionModel substitutionModel : options.getPartitionSubstitutionModels(GeneralDataType.INSTANCE)) {
            String prefix = substitutionModel.getName() + ".";

//            ops.add(modelOptions.getOperator(prefix + "frequencies")); // Usually fixed
            ops.add(modelOptions.getOperator(prefix + "rates"));

            if (substitutionModel.isActivateBSSVS()) {
                ops.add(modelOptions.getOperator(prefix + "indicators"));
            }
        }
        for (AbstractPartitionData partitionData : options.getDataPartitions(GeneralDataType.INSTANCE)) {
            if (partitionData.getPartitionSubstitutionModel().getDiscreteSubstType() == DiscreteSubstModelType.ASYM_SUBST) {
                ops.add(modelOptions.getOperator(partitionData.getName() + ".root.frequencies"));
            }
        }
    }


}