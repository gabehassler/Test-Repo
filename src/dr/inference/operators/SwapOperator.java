
package dr.inference.operators;

import dr.inference.model.Parameter;
import dr.inferencexml.operators.SwapOperatorParser;
import dr.math.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwapOperator extends SimpleMCMCOperator {
    private int size = 1;

    public SwapOperator(Parameter parameter, int size) {
        this.parameter = parameter;
        this.size = size;
        if (parameter.getDimension() < 2 * size) {
            throw new IllegalArgumentException();
        }

        int dimension = parameter.getDimension();
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < dimension; i++) {
            list.add(i);
        }
        masterList = Collections.unmodifiableList(list);
    }

    public Parameter getParameter() {
        return parameter;
    }

    public final double doOperation() {

        List<Integer> allIndices = new ArrayList<Integer>(masterList);
        int left, right;

        for (int i = 0; i < size; i++) {
            left = allIndices.remove(MathUtils.nextInt(allIndices.size()));
            right = allIndices.remove(MathUtils.nextInt(allIndices.size()));
            double value1 = parameter.getParameterValue(left);
            double value2 = parameter.getParameterValue(right);
            parameter.setParameterValue(left, value2);
            parameter.setParameterValue(right, value1);
        }

        return 0.0;
    }

    public String getOperatorName() {
        return SwapOperatorParser.SWAP_OPERATOR + "(" + parameter.getParameterName() + ")";
    }

    public String getPerformanceSuggestion() {
        return "No suggestions";
    }

    //PRIVATE STUFF

    private Parameter parameter = null;
    private List<Integer> masterList = null;
}
