package dr.app.beauti.components.dollo;
import dr.app.beauti.options.*;
import dr.app.beauti.types.PriorScaleType;
import java.util.List;
public class DolloComponentOptions implements ComponentOptions {
    public static final String DEATH_RATE = "death.rate";
    public static final String DATA_NAME = "binaryDolloDataType";
    public static final String MODEL_NAME = "binaryDolloSubstModel";
	final private BeautiOptions options;
	public DolloComponentOptions(final BeautiOptions options) {
		this.options = options;
	}
	public void createParameters(ModelOptions modelOptions) {
        for (AbstractPartitionData partition : options.dataPartitions) {
            if (partition.getPartitionSubstitutionModel().isDolloModel()) {
                String prefix = partition.getName() + ".";
                modelOptions.createParameterExponentialPrior(prefix + DEATH_RATE, "Stochastic Dollo death rate",
                        PriorScaleType.SUBSTITUTION_RATE_SCALE, 1.0E-5, 1.0E-4, 0.0);
                modelOptions.createScaleOperator(prefix + DEATH_RATE, modelOptions.demoTuning, 1.0);
            }
        }
	}
	public void selectOperators(ModelOptions modelOptions, List<Operator> ops) {
        for (AbstractPartitionData partition : options.dataPartitions) {
            PartitionSubstitutionModel model = partition.getPartitionSubstitutionModel();
            if (model.isDolloModel()) {
                String prefix = partition.getName() + ".";
                ops.add(modelOptions.getOperator(prefix + DEATH_RATE));
            }
        }
	}
	public void selectParameters(ModelOptions modelOptions,
			List<Parameter> params) {
        for (AbstractPartitionData partition : options.dataPartitions) {
            PartitionSubstitutionModel model = partition.getPartitionSubstitutionModel();
            if (model.isDolloModel()) {
                String prefix = partition.getName() + ".";
                params.add(modelOptions.getParameter(prefix + DEATH_RATE));
                break;
            }
        }
	}
	public void selectStatistics(ModelOptions modelOptions,
			List<Parameter> stats) {
		// Do nothing
	}
	public BeautiOptions getOptions() {
		return options;
	}
    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean isActive() {
        return active;
    }
    private boolean active = false;
}