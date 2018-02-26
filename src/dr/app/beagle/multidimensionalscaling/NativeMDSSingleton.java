package dr.app.beagle.multidimensionalscaling;
public class NativeMDSSingleton {
    public static final String LIBRARY_NAME = "mds_jni";
    public static final String LIBRARY_PATH_LABEL = "mds.library.path";
    public static final String LIBRARY_PLATFORM_NAME = getPlatformSpecificLibraryName();
    private NativeMDSSingleton() {
    } // ensure singleton
    private static String getPlatformSpecificLibraryName() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();
        if (osName.startsWith("windows")) {
            if (osArch.equals("i386")) return LIBRARY_NAME + "32";
            if (osArch.startsWith("amd64") || osArch.startsWith("x86_64")) return LIBRARY_NAME + "64";
        }
        return "lib" + LIBRARY_NAME + ".dylib";
    }
    public static NativeMDSSingleton loadLibrary() throws UnsatisfiedLinkError {
        if (INSTANCE == null) {
            System.err.println("Trying to load MDS library...");
            String path = "";
            if (System.getProperty(LIBRARY_PATH_LABEL) != null) {
                path = System.getProperty(LIBRARY_PATH_LABEL);
                if (path.length() > 0 && !path.endsWith("/")) {
                    path += "/";
                }
            }
            System.load(path + LIBRARY_PLATFORM_NAME);
            INSTANCE = new NativeMDSSingleton();
            System.err.println("MDS library loaded.");
        }
        return INSTANCE;
    }
    private static NativeMDSSingleton INSTANCE = null;
    public native int initialize(int dimensionCount, int locationCount, long flags);
    public native void updateLocations(int instance, int updateCount, double[] locations);
    public native double getSumOfSquaredResiduals(int instance);
    public native double getSumOfLogTruncations(int instance);
    public native void storeState(int instance);
    public native void restoreState(int instance);
    public native void acceptState(int instance);
    public native void makeDirty(int instance);
    public native void setPairwiseData(int instance, double[] observations);
    public native void setParameters(int instance, double[] parameters);
}
