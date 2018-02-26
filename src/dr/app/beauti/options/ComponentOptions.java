package dr.app.beauti.options;
import java.io.Serializable;
import java.util.List;
public interface ComponentOptions extends Serializable {
    void createParameters(ModelOptions modelOptions);
    void selectParameters(ModelOptions modelOptions, List<Parameter> params);
    void selectStatistics(ModelOptions modelOptions, List<Parameter> stats);
    void selectOperators(ModelOptions modelOptions, List<Operator> ops);
}
