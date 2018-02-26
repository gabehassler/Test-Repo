
package dr.inference.operators;

import dr.inference.model.Parameter;
import dr.math.MathUtils;

public class BitSwapOperator extends SimpleMCMCOperator {

    private final Parameter data;
    private final Parameter indicators;
    private final boolean impliedOne;
    private final int radius;

    public BitSwapOperator(Parameter data, Parameter indicators, int radius, double weight) {
        this.data = data;
        this.indicators = indicators;
        this.radius = radius;
        setWeight(weight);

        final int iDim = indicators.getDimension();
        final int dDim = data.getDimension();
        if (iDim == dDim - 1) {
            impliedOne = true;
        } else if (iDim == dDim) {
            impliedOne = false;
        } else {
            throw new IllegalArgumentException();
        }
    }


    public String getPerformanceSuggestion() {
        return "";
    }

    public String getOperatorName() {
        return "bitSwap(" + data.getParameterName() + ")";
    }

    public double doOperation() throws OperatorFailedException {
        final int dim = indicators.getDimension();
        if (dim < 2) {
            throw new OperatorFailedException("no swaps possible");
        }
        int nLoc = 0;
        int[] loc = new int[2 * dim];
        double hastingsRatio;
        int pos;
        int direction;

        int nOnes = 0;
        if (radius > 0) {
            for (int i = 0; i < dim; i++) {
                final double value = indicators.getStatisticValue(i);
                if (value > 0) {
                    ++nOnes;
                    loc[nLoc] = i;
                    ++nLoc;
                }
            }

            if (nOnes == 0 || nOnes == dim) {
                throw new OperatorFailedException("no swaps possible");  //??
                //return 0;
            }

            hastingsRatio = 0.0;
            final int rand = MathUtils.nextInt(nLoc);
            pos = loc[rand];
            direction = MathUtils.nextInt(2 * radius);
            direction -= radius - (direction < radius ? 0 : 1);
            for (int i = direction > 0 ? pos + 1 : pos + direction; i < (direction > 0 ? pos + direction + 1 : pos); i++) {
                if (i < 0 || i >= dim || indicators.getStatisticValue(i) > 0) {
                    throw new OperatorFailedException("swap faild");
                }
            }
        } else {
            double prev = -1;
            for (int i = 0; i < dim; i++) {
                final double value = indicators.getStatisticValue(i);
                if (value > 0) {
                    ++nOnes;
                    if (i > 0 && prev == 0) {
                        loc[nLoc] = -(i + 1);
                        ++nLoc;
                    }
                    if (i < dim - 1 && indicators.getStatisticValue(i + 1) == 0) {
                        loc[nLoc] = (i + 1);
                        ++nLoc;
                    }
                }
                prev = value;
            }

            if (nOnes == 0 || nOnes == dim) {
                return 0;
            }

            if (!(nLoc > 0)) {
                // System.out.println(indicators);
                assert false : indicators;
            }

            final int rand = MathUtils.nextInt(nLoc);
            pos = loc[rand];
            direction = pos < 0 ? -1 : 1;
            pos = (pos < 0 ? -pos : pos) - 1;
            final int maxOut = 2 * nOnes;

            hastingsRatio = (maxOut == nLoc) ? 0.0 : Math.log((double) nLoc / maxOut);
        }

//            System.out.println("swap " + pos + "<->" + nto + "  " +
//                              indicators.getParameterValue(pos) +  "<->" + indicators.getParameterValue(nto) +
//                 "  " +  data.getParameterValue(pos) +  "<->" + data.getParameterValue(nto));
        final int nto = pos + direction;
        double vto = indicators.getStatisticValue(nto);

        indicators.setParameterValue(nto, indicators.getParameterValue(pos));
        indicators.setParameterValue(pos, vto);

        final int dataOffset = impliedOne ? 1 : 0;
        final int ntodata = nto + dataOffset;
        final int posdata = pos + dataOffset;
        vto = data.getStatisticValue(ntodata);
        data.setParameterValue(ntodata, data.getParameterValue(posdata));
        data.setParameterValue(posdata, vto);

//            System.out.println("after " + pos + "<->" + nto + "  " +
//                              indicators.getParameterValue(pos) +  "<->" + indicators.getParameterValue(nto) +
//                 "  " +  data.getParameterValue(pos) +  "<->" + data.getParameterValue(nto));

        return hastingsRatio;
    }
}
