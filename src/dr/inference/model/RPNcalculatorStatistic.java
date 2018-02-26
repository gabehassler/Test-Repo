
package dr.inference.model;

import java.util.Map;

public class RPNcalculatorStatistic extends Statistic.Abstract {

    private final RPNexpressionCalculator[] expressions;
    private final String[] names;
    private final Map<String, Statistic> variables;

    RPNexpressionCalculator.GetVariable vars = new RPNexpressionCalculator.GetVariable() {
        public double get(String name) {
            return variables.get(name).getStatisticValue(0);
        }
    };

    public RPNcalculatorStatistic(String name, String[] expressions, String[] names,
                                  Map<String, Statistic> variables) {
        super(name);

        this.expressions = new RPNexpressionCalculator[expressions.length];
        for(int i = 0; i < expressions.length; ++ i) {
            this.expressions[i] = new RPNexpressionCalculator(expressions[i]);

            String err = this.expressions[i].validate();
            if( err != null ) {
                throw new RuntimeException("Error in expression " + i + ": " + err);
            }
        }

        this.names = names;
        this.variables = variables;
    }

	public int getDimension() {
        return expressions.length;
    }

    public String getDimensionName(int dim) {
        return names[dim];
    }

	public double getStatisticValue(int dim) {
        return expressions[dim].evaluate(vars);
	}

}