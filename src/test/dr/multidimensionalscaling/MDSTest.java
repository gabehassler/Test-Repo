package test.dr.multidimensionalscaling;
import dr.app.beagle.multidimensionalscaling.NativeMDSSingleton;
import test.dr.math.MathTestCase;
public class MDSTest extends MathTestCase {
    private static NativeMDSSingleton mds = loadLibrary();
    private static NativeMDSSingleton loadLibrary() {
        try {
            return NativeMDSSingleton.loadLibrary();
        } catch (UnsatisfiedLinkError error) {
            System.err.println("Unable to load MDS library; no trying tests");
            return null;
        }
    }
    public void testInitialization() {
        if (mds != null) {
            int i = mds.initialize(2, 100, 0);
            assertEquals(i, 0);
            i = mds.initialize(2, 100, 0);
            assertEquals(i, 1);
        } else {
            System.out.println("testInitialization skipped");
        }
    }
    public void testMakeDirty() {
        if (mds != null) {
            mds.makeDirty(0);
        } else {
            System.out.println("testMakeDirty skipped");
        }
    }
}
