package test.dr.evomodel.substmodel;
import dr.app.beagle.evomodel.substmodel.ComplexSubstitutionModel;
import dr.app.beagle.evomodel.substmodel.FrequencyModel;
import dr.app.beagle.evomodel.substmodel.MarkovJumpsSubstitutionModel;
import dr.evolution.datatype.DataType;
import dr.evolution.datatype.TwoStates;
import dr.inference.markovjumps.MarkovJumpsType;
import dr.inference.model.Parameter;
import dr.math.matrixAlgebra.Vector;
import test.dr.math.MathTestCase;
public class TwoStateMarkovRewardsTest extends MathTestCase {
    public void testTwoStateRewards() {
        DataType dataType = TwoStates.INSTANCE;
        FrequencyModel freqModel = new FrequencyModel(TwoStates.INSTANCE, new double[]{0.5, 0.5});
        Parameter rates = new Parameter.Default(new double[]{4.0, 6.0});
        ComplexSubstitutionModel twoStateModel = new ComplexSubstitutionModel("two", dataType, freqModel, rates) {
//    protected EigenSystem getDefaultEigenSystem(int stateCount) {
//        return new DefaultEigenSystem(stateCount);
//    }
        };
        twoStateModel.setNormalization(false);
        MarkovJumpsSubstitutionModel markovRewards = new MarkovJumpsSubstitutionModel(twoStateModel,
                MarkovJumpsType.REWARDS);
        double[] r = new double[2];
        double[] q = new double[4];
        double[] c = new double[4];
        int mark = 0;
        double weight = 1.0;
        r[mark] = weight;
        markovRewards.setRegistration(r);
        twoStateModel.getInfinitesimalMatrix(q);
        System.out.println("Q = " + new Vector(q));
        System.out.println("Reward for state 0");
        double time = 1.0;
        markovRewards.computeCondStatMarkovJumps(time, c);
        System.out.println("Reward conditional on X(0) = i, X(t) = j: " + new Vector(c));
        double endTime = 10.0;
        int steps = 10;
        for (time = 0.0; time < endTime; time += (endTime / steps)) {
            markovRewards.computeCondStatMarkovJumps(time, c);
            System.out.println(time + "," + c[0]);   // start = 0, end = 0
        }
    }
//    0 -> \alpha -> 1;   1 -> \beta -> 0
//    \eigenvectors =
//      \left[
//          1, -\alpha \\
//          1, \beta \\
//    \right]
//    \inveigenvectors =
//        \frac{1}{\alpha + \beta}
//     \left[
//            \beta,  \alpha, \\
//               -1, 1  \\
//        \right]
    private static double analyticProb(int from, int to, double alpha, double beta, double t) {
        double total = alpha + beta;
        int entry = from * 2 + to;
        switch (entry) {
            case 0: // 0 -> 0
                return (beta + alpha * Math.exp(-total * t)) / total;
            case 1: // 0 -> 1
                return (alpha - alpha * Math.exp(-total * t)) / total;
            case 2: // 1 -> 0
                return (beta - beta * Math.exp(-total * t)) / total;
            case 3: // 1 -> 1
                return (alpha + beta * Math.exp(-total * t)) / total;
            default:
                throw new RuntimeException();
        }
    }
}
