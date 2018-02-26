
package dr.inference.markovchain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Profiler {

    private static boolean profilerAvailable = true;

    private static Map<String, Profile> profiles = new HashMap<String, Profile>();
    //private static String currentProfile = null;
    private static long startTime;

    public static boolean startProfile() { //String name) {
        if (profilerAvailable) {
            //@todo unfortunately I can't get my Ant Build to work with asserts yet. Help!
            //assert currentProfile == null : "Profile " + currentProfile + " is already recording";
            //currentProfile = name;
            startTime = getCurrentThreadCpuTime();
        }
        return true;
    }

    public static boolean stopProfile(String name) {
        if (profilerAvailable) {
            long stopTime = getCurrentThreadCpuTime();
            //@todo unfortunately I can't get my Ant Build to work with asserts yet. Help!
            //assert name.equals(currentProfile) : "Profile " + name + " is not recording";

            long count = 1;
            long totalTime = stopTime - startTime;
            Profile profile = profiles.get(name);
            if (profile != null) {
                totalTime += profile.time;
                count += profile.count;
            }

            profiles.put(name, new Profile(count, totalTime));
            //currentProfile = null;
        }
        return true;
    }

    public static void report() {
        if (profilerAvailable) {
            Iterator<String> iter = profiles.keySet().iterator();
            while (iter.hasNext()) {
                String name = iter.next();
                Profile profile = profiles.get(name);
                long average = profile.time / profile.count;
                System.err.println("PROFILE: " + name + " [" + profile.time + " ms, " + profile.count + " calls, " + average + " ms / call]");
            }
        }
    }

    private static class Profile {
        long time;
        long count;

        Profile(long count, long time) {
            this.count = count;
            this.time = time;
        }
    }

    public static native long getCurrentThreadCpuTime();

    static {
        try {
            System.loadLibrary("mcmcprof");
        } catch (UnsatisfiedLinkError ule) {
            profilerAvailable = false;
        }
    }
}
