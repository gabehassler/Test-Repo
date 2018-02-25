package dr.inference.model;
import java.util.List;
public class NotBooleanStatistic extends BooleanStatistic {
public NotBooleanStatistic(BooleanStatistic originalStatistic) {
this(originalStatistic, null);
}
public NotBooleanStatistic(BooleanStatistic originalStatistic, List<Integer> mark) {
super(originalStatistic.getStatisticName());
this.originalStatistic = originalStatistic;
this.mark = mark;
}
@Override
public boolean getBoolean(int dim) {
boolean rtnValue = originalStatistic.getBoolean(dim);
if (mark == null || mark.contains(dim)) {
rtnValue = !rtnValue;
}
return rtnValue;
}
public int getDimension() {
return originalStatistic.getDimension();
}
private final BooleanStatistic originalStatistic;
private final List<Integer> mark;
}
