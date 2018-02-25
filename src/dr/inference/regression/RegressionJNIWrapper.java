package dr.inference.regression;
public class RegressionJNIWrapper {
public static final String LIBRARY_NAME = "bsccs_jni";
public static final String LIBRARY_PATH_LABEL = "bsccs.library.path";
public static final String LIBRARY_PLATFORM_NAME = getPlatformSpecificLibraryName();
public static final int NO_PRIOR = 0;
public static final int LAPLACE_PRIOR = 1;
public static final int NORMAL_PRIOR = 2;
private RegressionJNIWrapper() {
}
public native int loadData(String fileName);
public native double getLogLikelihood(int instance);
public native double getLogPrior(int instance);
public native double getBeta(int instance, int index);
public native int getBetaSize(int instance);
public native double getHessian(int instance, int index1, int index2);
public native void setBeta(int instance, int index, double value);
public native void setBeta(int instance, double[] values);
public native double getHyperprior(int instance);
public native void setHyperprior(int instance, double value);
public native void findMode(int instance);
public native int getUpdateCount(int instance);
public native int getLikelihoodCount(int instance);
public native void setPriorType(int instance, int type);
public native void makeDirty(int instance);
private static String getPlatformSpecificLibraryName() {
String osName = System.getProperty("os.name").toLowerCase();
String osArch = System.getProperty("os.arch").toLowerCase();
if (osName.startsWith("windows")) {
if(osArch.equals("i386")) return LIBRARY_NAME + "32";
if(osArch.startsWith("amd64")||osArch.startsWith("x86_64")) return LIBRARY_NAME + "64";
}
return LIBRARY_NAME;
}
public static RegressionJNIWrapper loadLibrary() throws UnsatisfiedLinkError {
if (INSTANCE == null) {
System.err.println("Trying to load BSCCS library...");
String path = "";
if (System.getProperty(LIBRARY_PATH_LABEL) != null) {
path = System.getProperty(LIBRARY_PATH_LABEL);
if (path.length() > 0 && !path.endsWith("/")) {
path += "/";
}
}
System.loadLibrary(path + LIBRARY_PLATFORM_NAME);
INSTANCE = new RegressionJNIWrapper();
System.err.println("BSCCS library loaded.");
}
return INSTANCE;
}
private static RegressionJNIWrapper INSTANCE = null;
}
