package dr.app.tracer.application;
import dr.app.util.OSType;
import dr.inference.trace.LogFileTraces;
import jam.framework.DocumentFrame;
import jam.framework.SingleDocApplication;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.security.Permission;
import java.util.Locale;
public class InstantiableTracerApp extends SingleDocApplication {
private boolean exiting;
public InstantiableTracerApp (String nameString, String aboutString, Icon icon,
String websiteURLString, String helpURLString, boolean exiting) {
super(new TracerMenuBarFactory(), nameString, aboutString, icon,
websiteURLString, helpURLString);
addPreferencesSection(new GeneralPreferencesSection());
this.exiting = exiting;
}
@Override
public void doQuit() {
if(exiting) {   // Implemented this way because documentFrame is private in SingleDocApplication
super.doQuit();
}
else {
if (documentFrame == null) {
return;
}
if (documentFrame.requestClose()) {
documentFrame.setVisible(false);
documentFrame.dispose();
//            try {
//                System.exit(0);
//            }catch (ExitException e) {
//                System.setSecurityManager(null);
//            }
}
}
}
//    @Override
//    public void doQuit() {
//        //super.doQuit();
//        if (documentFrame == null) {
//            return;
//        }
//        if (documentFrame.requestClose()) {
//
//            documentFrame.setVisible(false);
//            documentFrame.dispose();
////            try {
////                System.exit(0);
////            }catch (ExitException e) {
////                System.setSecurityManager(null);
////            }
//        }
//    }
private DocumentFrame documentFrame = null;
private static void loadTracerInstance(String nameString, String logFileName, long bi, boolean exiting) {
final String name = nameString;
final String fileName = logFileName;
final long burnin = bi;
final boolean exit = exiting;
Thread thread = new Thread() {
public void run() {
try {
// There is a major issue with languages that use the comma as a decimal separator.
// To ensure compatibility between programs in the package, enforce the US locale.
Locale.setDefault(Locale.US);
boolean lafLoaded = false;
if (OSType.isMac()) {
System.setProperty("apple.awt.graphics.UseQuartz", "true");
System.setProperty("apple.awt.antialiasing","true");
System.setProperty("apple.awt.rendering","VALUE_RENDER_QUALITY");
System.setProperty("apple.laf.useScreenMenuBar","true");
System.setProperty("apple.awt.draggableWindowBackground","true");
System.setProperty("apple.awt.showGrowBox","true");
// set the Quaqua Look and Feel in the UIManager
try {
UIManager.setLookAndFeel(
"ch.randelshofer.quaqua.QuaquaLookAndFeel"
);
lafLoaded = true;
} catch (Exception e) {
//
}
UIManager.put("SystemFont", new Font("Lucida Grande", Font.PLAIN, 13));
UIManager.put("SmallSystemFont", new Font("Lucida Grande", Font.PLAIN, 11));
}
if (!lafLoaded) {
try {
UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
} catch (Exception e1) {
e1.printStackTrace();
}
}
java.net.URL url = TracerApp.class.getResource("images/Mapper.png");
Icon icon = null;
if (url != null) {
icon = new ImageIcon(url);
}
InstantiableTracerApp app = new InstantiableTracerApp(name,
"Tracer tool running through "+ name + ". Authors Wai Lok Sibon Li & Andrew Rambaut",
icon, "http://beast.bio.ed.ac.uk/", "http://beast.bio.ed.ac.uk/Tracer", exit);
TracerFrame frame = new TracerFrame(name);
app.setDocumentFrame(frame);
app.initialize();
File file = new File(fileName);
LogFileTraces[] traces = { new LogFileTraces(fileName, file) };
traces[0].setBurnIn((int) burnin);
frame.processTraces(traces);
}
catch(Exception e) {
e.printStackTrace();
throw new RuntimeException("Exception in InstantiableTracerApp");
}
}
};
//        System.setSecurityManager(new NoExitSecurityManager());
thread.start();
}
public static void loadExitingTracerInstance(String nameString, String logFileName, long bi) {
loadTracerInstance(nameString, logFileName, bi, true);
}
public static void loadNonExitingTracerInstance(String nameString, String logFileName, long bi) {
loadTracerInstance(nameString, logFileName, bi, false);
}
}
@Deprecated
class NoExitSecurityManager extends SecurityManager {
@Override
public void checkPermission(Permission perm) {
//            super.checkPermission(perm);
}
@Override
public void checkPermission(Permission perm, Object context) {
//            super.checkPermission(perm, context);
}
@Override
public void checkExit(int status) {
super.checkExit(status);
throw new ExitException(status);//ExitException(status);
}
}
@Deprecated
class ExitException extends SecurityException {
public final int status;
public ExitException(int status) {
super();
this.status = status;
}
}