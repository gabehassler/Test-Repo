package dr.evomodel.treelikelihood;
import dr.app.util.OSType;
import java.io.File;
import java.net.URISyntaxException;
public class NativeNucleotideLikelihoodCore extends AbstractLikelihoodCore {
	public NativeNucleotideLikelihoodCore() {
		super(4);
	}
	protected void calculateStatesStatesPruning(int[] states1, double[] matrices1,
												int[] states2, double[] matrices2,
												double[] partials3) {
        nativeStatesStatesPruning(states1, matrices1, states2, matrices2, patternCount, matrixCount, partials3);
    }
    protected void calculateStatesPartialsPruning(	int[] states1, double[] matrices1,
													double[] partials2, double[] matrices2,
													double[] partials3) {
        nativeStatesPartialsPruning(states1, matrices1, partials2, matrices2, patternCount, matrixCount, partials3);
    }
    private void out(PrintStream s, double[] a, String name) {
        s.print("double " + name + "["  + a.length + "] = {");
        for( double f : a )  s.print(f + ", ");
        s.println("};");
    }
    protected void calculatePartialsPartialsPruning(double[] partials1, double[] matrices1,
													double[] partials2, double[] matrices2,
													double[] partials3) {
            System.out.println("*//*nativePartialsPartialsPruning*//* int patternCount = " +
                    patternCount + "; int matrixCount = " + matrixCount);
            out(System.out, partials1, "partials1");
            out(System.out, partials2, "partials2");
            out(System.out, partials2, "matrices1");
            out(System.out, partials2, "matrices2");
        }*/
        nativePartialsPartialsPruning(partials1, matrices1, partials2, matrices2, patternCount, matrixCount, partials3);
    }
	protected void calculateStatesStatesPruning(int[] states1, double[] matrices1,
												int[] states2, double[] matrices2,
												double[] partials3, int[] matrixMap)
	{
		throw new RuntimeException("calculateStatesStatesPruning not implemented using matrixMap");
	}
	protected void calculateStatesPartialsPruning(	int[] states1, double[] matrices1,
													double[] partials2, double[] matrices2,
													double[] partials3, int[] matrixMap)
	{
		throw new RuntimeException("calculateStatesStatesPruning not implemented using matrixMap");
	}
	protected void calculatePartialsPartialsPruning(double[] partials1, double[] matrices1,
													double[] partials2, double[] matrices2,
													double[] partials3, int[] matrixMap)
	{
		throw new RuntimeException("calculateStatesStatesPruning not implemented using matrixMap");
	}
	protected void calculateIntegratePartials(double[] inPartials, double[] proportions, double[] outPartials)
	{
		nativeIntegratePartials(inPartials, proportions, patternCount, matrixCount, outPartials);
	}
	protected native void nativeStatesStatesPruning(	int[] states1, double[] matrices1,
														int[] states2, double[] matrices2,
														int patternCount, int matrixCount,
														double[] partials3);
	protected native void nativeStatesPartialsPruning(	int[] states1, double[] matrices1,
														double[] partials2, double[] matrices2,
														int patternCount, int matrixCount,
														double[] partials3);
	protected native void nativePartialsPartialsPruning(double[] partials1, double[] matrices1,
														double[] partials2, double[] matrices2,
														int patternCount, int matrixCount,
														double[] partials3);
	public native void nativeIntegratePartials(			double[] partials, double[] proportions,
														int patternCount, int matrixCount,
														double[] outPartials);
	public void calculateLogLikelihoods(double[] partials, double[] frequencies, double[] outLogLikelihoods)
	{
        int v = 0;
		for (int k = 0; k < patternCount; k++) {
			double sum = frequencies[0] * partials[v];	v++;
			sum += frequencies[1] * partials[v];	v++;
			sum += frequencies[2] * partials[v];	v++;
			sum += frequencies[3] * partials[v];	v++;
            outLogLikelihoods[k] = Math.log(sum) + getLogScalingFactor(k);
		}
    }
	public static boolean isAvailable() { return isNativeAvailable; }
	private static boolean isNativeAvailable = false;
	static {
        String currentDir = null;
        try {
            currentDir = new File(NativeNucleotideLikelihoodCore.class.getProtectionDomain().getCodeSource().
                 getLocation().toURI()).getParent() + System.getProperty("file.separator")
                 + "lib" + System.getProperty("file.separator"); // get path to find lib http://code.google.com/p/beast-mcmc/issues/detail?id=203
//            System.out.println("currentDir = " + currentDir);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            if (OSType.isWindows()) {
                System.load(currentDir + "NucleotideLikelihoodCore.dll");
            } else {
                currentDir = System.getProperty("user.dir");
                System.loadLibrary("NucleotideLikelihoodCore");
            }
			isNativeAvailable = true;
            System.out.println("Loading native NucleotideLikelihoodCore successfully from " + currentDir);
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Failed to load native NucleotideLikelihoodCore in : " + currentDir);
            System.err.println("Using Java nucleotide likelihood core because " + e.toString());
//            System.err.println("Looking for NucleotideLikelihoodCore in java.library.path : " + System.getProperty("java.library.path"));
//            System.err.println("Looking for NucleotideLikelihoodCore in user.dir : " + System.getProperty("user.dir"));
		}
	}
}
