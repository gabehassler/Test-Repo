package dr.app.util;
public enum OSType {
WINDOWS,
MAC,
UNIX_LINUX;
static OSType detect() {
if (os.indexOf("mac") >= 0) {
return MAC;
}
if (os.indexOf("win") >= 0) {
return WINDOWS;
}
if (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0) {
return UNIX_LINUX;
}
return null;
}
public static boolean isWindows(){
//windows
return (os.indexOf( "win" ) >= 0);
}
public static boolean isMac(){
//Mac
return (os.indexOf( "mac" ) >= 0);
}
public static boolean isUnixOrLinux(){
//linux or unix
return (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0);
}
public String toString() {
return os;
}
public String version() {
return System.getProperty("os.version");
}
static final String os = System.getProperty("os.name").toLowerCase();
}
