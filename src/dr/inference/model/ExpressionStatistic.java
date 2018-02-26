package dr.inference.model;
import java.util.Vector;
public class ExpressionStatistic extends Statistic.Abstract {
    String expression = "";
	public ExpressionStatistic(String name, String expression) {
		super(name);
        this.expression = expression;
	}
	public void addStatistic(Statistic statistic) {
        if (statistic.getDimension() != 1) {
            throw new IllegalArgumentException("Can only have statistics of dimension 1");
        }
		statistics.add(statistic);
	}
	public int getDimension() { return 1; }
	public double getStatisticValue(int dim) {
		System.err.println("Error in parsing expression " + expression + " : JEP expression parser not included with this version");
        return 0;
	}
	// ****************************************************************
	// Private and protected stuff
	// ****************************************************************
	private Vector statistics = new Vector();
}
