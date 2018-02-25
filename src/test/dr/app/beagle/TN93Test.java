package test.dr.app.beagle;
import dr.app.beagle.evomodel.substmodel.EigenDecomposition;
import dr.app.beagle.evomodel.substmodel.FrequencyModel;
import dr.app.beagle.evomodel.substmodel.TN93;
import dr.evolution.datatype.Nucleotides;
import dr.inference.model.Parameter;
import dr.math.matrixAlgebra.Vector;
import test.dr.math.MathTestCase;
public class TN93Test extends MathTestCase {
public void testTN93() {
Parameter kappa1 = new Parameter.Default(5.0);
Parameter kappa2 = new Parameter.Default(2.0);
double[] pi = new double[]{0.40, 0.20, 0.30, 0.10};
double time = 0.1;
FrequencyModel freqModel = new FrequencyModel(Nucleotides.INSTANCE, pi);
TN93 tn = new TN93(kappa1, kappa2, freqModel);
EigenDecomposition decomp = tn.getEigenDecomposition();
Vector eval = new Vector(decomp.getEigenValues());
System.out.println("Eval = " + eval);
double[] probs = new double[16];
tn.getTransitionProbabilities(time, probs);
System.out.println("new probs = " + new Vector(probs));
// check against old implementation
dr.evomodel.substmodel.FrequencyModel oldFreq = new dr.evomodel.substmodel.FrequencyModel(Nucleotides.INSTANCE, pi);
dr.evomodel.substmodel.TN93 oldTN = new dr.evomodel.substmodel.TN93(kappa1, kappa2, oldFreq);
double[] oldProbs = new double[16];
oldTN.getTransitionProbabilities(time, oldProbs);
System.out.println("old probs = " + new Vector(oldProbs));
assertEquals(probs, oldProbs, 10E-6);
}
}
