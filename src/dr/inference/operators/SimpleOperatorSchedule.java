
package dr.inference.operators;

import dr.inference.loggers.LogColumn;
import dr.inference.loggers.Loggable;
import dr.inference.loggers.NumberColumn;
import dr.math.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SimpleOperatorSchedule implements OperatorSchedule, Loggable {

	List<MCMCOperator> operators = null;
	double totalWeight = 0;
	int current = 0;
	boolean sequential = false;
	int optimizationSchedule = OperatorSchedule.DEFAULT_SCHEDULE;

	public SimpleOperatorSchedule() {
		operators = new Vector<MCMCOperator>();
	}

	public void addOperators(List<MCMCOperator> operators) {
		for (MCMCOperator operator : operators) {
			this.operators.add(operator);
			totalWeight += operator.getWeight();
		}
	}

	public void operatorsHasBeenUpdated() {
		totalWeight = 0.0;
		for (MCMCOperator operator : operators) {
			totalWeight += operator.getWeight();
		}
	}

	public void addOperator(MCMCOperator op) {
		operators.add(op);
		totalWeight += op.getWeight();
	}

	public double getWeight(int index) {
		return operators.get(index).getWeight();
	}

	public int getNextOperatorIndex() {

		if (sequential) {
			int index = getWeightedOperatorIndex(current);
			current += 1;
			if (current >= totalWeight) {
				current = 0;
			}
			return index;
		}

        final double v = MathUtils.nextDouble();
        //System.err.println("v=" + v);
        return getWeightedOperatorIndex(v * totalWeight);
	}

	public void setSequential(boolean seq) {
		sequential = seq;
	}

	private int getWeightedOperatorIndex(double q) {
		int index = 0;
		double weight = getWeight(index);
		while (weight <= q) {
			index += 1;
			weight += getWeight(index);
		}
		return index;
	}

	public MCMCOperator getOperator(int index) {
		return operators.get(index);
	}

	public int getOperatorCount() {
		return operators.size();
	}

	public double getOptimizationTransform(double d) {
        switch( optimizationSchedule ) {
            case LOG_SCHEDULE:  return Math.log(d);
            case SQRT_SCHEDULE: return Math.sqrt(d);
        }
		return d;
	}

	public void setOptimizationSchedule(int schedule) {
		optimizationSchedule = schedule;
	}

    public int getMinimumAcceptAndRejectCount() {
        int minCount = Integer.MAX_VALUE;
        for( MCMCOperator op : operators ) {
            if( op.getAcceptCount() < minCount || op.getRejectCount() < minCount ) {
                minCount = op.getCount();
            }
        }
        return minCount;
    }

	// **************************************************************
	// Loggable IMPLEMENTATION
	// **************************************************************

	public LogColumn[] getColumns() {
		List<LogColumn> columnList = new ArrayList<LogColumn>();
		for (int i = 0; i < getOperatorCount(); i++) {
			MCMCOperator op = getOperator(i);
			columnList.add(new OperatorAcceptanceColumn(op.getOperatorName(), op));
			if (op instanceof CoercableMCMCOperator) {
				columnList.add(new OperatorSizeColumn(op.getOperatorName() + "_size", (CoercableMCMCOperator)op));
			}
		}
		LogColumn[] columns = columnList.toArray(new LogColumn[columnList.size()]);
		return columns;
	}

    private class OperatorAcceptanceColumn extends NumberColumn {
		private final MCMCOperator op;

		public OperatorAcceptanceColumn(String label, MCMCOperator op) {
			super(label);
			this.op = op;
		}

		public double getDoubleValue() {
			return MCMCOperator.Utils.getAcceptanceProbability(op);
		}
	}

	private class OperatorSizeColumn extends NumberColumn {
		private final CoercableMCMCOperator op;

		public OperatorSizeColumn(String label, CoercableMCMCOperator op) {
			super(label);
			this.op = op;
		}

		public double getDoubleValue() {
			return op.getRawParameter();
		}
	}
}
