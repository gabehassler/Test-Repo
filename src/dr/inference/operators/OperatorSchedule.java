package dr.inference.operators;
import java.io.Serializable;
import java.util.List;
public interface OperatorSchedule extends Serializable {
int getNextOperatorIndex();
int getOperatorCount();
MCMCOperator getOperator(int index);
void addOperator(MCMCOperator op);
void addOperators(List<MCMCOperator> v);
void operatorsHasBeenUpdated();
double getOptimizationTransform(double d);
int getMinimumAcceptAndRejectCount();
final int DEFAULT_SCHEDULE = 0;
final int LOG_SCHEDULE = 1;
final int SQRT_SCHEDULE = 2;
final String DEFAULT_STRING = "default";
final String LOG_STRING = "log";
final String SQRT_STRING = "sqrt";
}
