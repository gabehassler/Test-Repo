
package dr.evomodel.indel;





public class NativeTreeLikelihood {
	
  private static boolean isNativeAvailable = false;
  private int[][] iSequences;                       // rest are references to existing arrays
  private int iNN;
  private int iMUD;
  private long iStaticDataHandle;
    
    static {
	try {

	    System.loadLibrary("NativeTreeLikelihood");
	    System.err.println("Fast TKF91 homology integrator code found");
	    isNativeAvailable = true;

	} catch (UnsatisfiedLinkError e) {

	    System.err.println("Using Java TKF91 homology integrator code");

	}
    }
    


    public NativeTreeLikelihood() { }



    public boolean isAvailable() {
	return isNativeAvailable;
    }


    private native long nativeInit(int[] iParent, double[] iEquil, double[] iTrans, 
				   double[] iH, double[] iN, double[] iE, double[] iB);


    private native double nativeTreeRecursion(int[] iSignature, int[] iColumn, int iNumNucs, int iMUD, long iSD);


    private native void nativeDestruct(long iSD);



    public void init(int iNumNucs, int iMaxUnalignDimension, int[] iParent, double[] iEquil, double[][][] iTrans,
		     int[][] iSequences0,
		     double[] iN, double[] iH, double[] iE, double[] iB)
    {
	if (!isNativeAvailable)
	    return;

	// copy data into local variables
	iNN = iNumNucs;
        iMUD = iMaxUnalignDimension;
	iSequences = iSequences0;

	// translate iTrans[][][] array into flat array
	int iNumNodes = iTrans.length;
	double[] iFlatTrans = new double[ iNN*iNN*iNumNodes ];
	for (int i=0; i<iNumNodes; i++) {
	    for (int j=0; j<iNN; j++) {
		for (int k=0; k<iNN; k++) {
		    iFlatTrans[ k + iNN*( j + iNN*i ) ] = iTrans[i][j][k];
		}
	    }
	}

	// store data someplace where the C code has fast access
	iStaticDataHandle = nativeInit(iParent, iEquil, iFlatTrans, iH, iN, iE, iB);
    }



    protected void finalize() throws Throwable {

	if (iStaticDataHandle != 0) {
	    nativeDestruct( iStaticDataHandle );
	}
	super.finalize();
    }



    public double treeRecursion(IntMathVec iSignature, IntMathVec iPos)
    {

	int[] iColumn = new int[ iSignature.iV.length ];
	for (int i=0; i<iSignature.iV.length; i++) {
	    if (iSignature.iV[i] != 0)
		iColumn[i] = iSequences[i][iPos.iV[i]];
	}

	return nativeTreeRecursion(iSignature.iV,
				   iColumn,
				   iNN,
				   iMUD,
				   iStaticDataHandle);
    }

}	

	

