package dr.util;
public class Timer {
private long start = 0, stop = 0;
public void start() {
start = System.currentTimeMillis();
}
public void stop() {
stop = System.currentTimeMillis();
}
public void update() {
stop = System.currentTimeMillis();
}
public long calibrate(double fraction) {
long timeTaken = System.currentTimeMillis() - start;		
return Math.round(((double)timeTaken / fraction) * (1.0 - fraction));
}
public double toSeconds() {
update();
return toSeconds(stop - start);
}
public static double toSeconds(long millis) {
return millis / 1000.0;
}
public static double toMinutes(long millis) {
return toSeconds(millis) / 60.0; 
}
public static double toHours(long millis) {
return toMinutes(millis) / 60.0;
}
public static double toDays(long millis) {
return toHours(millis) / 24.0;
}
public String toString() {
update();
return toString(stop - start);
}
public static String toString(long millis) {
if (toDays(millis) > 1.0) return toDays(millis) + " days";
if (toHours(millis) > 1.0) return toHours(millis) + " hours";
if (toMinutes(millis) > 1.0) return toMinutes(millis) + " minutes";
return toSeconds(millis) + " seconds";
}
}
