package dr.app.oldbeauti;
import dr.app.beast.BeastVersion;
import dr.app.util.OSType;
import dr.util.Version;
import jam.framework.*;
import javax.swing.*;
import java.awt.*;
public class BeautiApp extends MultiDocApplication {
private final static Version version = new BeastVersion();
public BeautiApp(String nameString, String aboutString, Icon icon,
String websiteURLString, String helpURLString) {
super(new BeautiMenuBarFactory(), nameString, aboutString, icon, websiteURLString, helpURLString);
}
public Action getOpenAction() {
throw new UnsupportedOperationException("getOpenAction is not supported");
}
// Main entry point
static public void main(String[] args) {
if (args.length > 1) {
if (args.length != 3) {
System.err.println("Usage: beauti <input_file> <template_file> <output_file>");
return;
}
String inputFileName = args[0];
String templateFileName = args[1];
String outputFileName = args[2];
new CommandLineBeauti(inputFileName, templateFileName, outputFileName);
} else {
if (args.length == 1 && args[0].equalsIgnoreCase("-developer")) {
developer = true;
}
if (OSType.isMac()) {
System.setProperty("apple.laf.useScreenMenuBar","true");
System.setProperty("apple.awt.showGrowBox","true");
System.setProperty("apple.awt.graphics.UseQuartz","true");
UIManager.put("SystemFont", new Font("Lucida Grande", Font.PLAIN, 13));
UIManager.put("SmallSystemFont", new Font("Lucida Grande", Font.PLAIN, 11));
}
try {
UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
java.net.URL url = BeautiApp.class.getResource("images/beauti.png");
Icon icon = null;
if (url != null) {
icon = new ImageIcon(url);
}
final String nameString = "Old BEAUti";
final String versionString = version.getVersionString();
String aboutString = "<html><div style=\"font-family:sans-serif;\"><center>" +
"<div style=\"font-size:12;\"><p>(Old) Bayesian Evolutionary Analysis Utility<br>" +
"Version " + versionString + ", " + version.getDateString() + "</p>" +
"<p>by Andrew Rambaut and Alexei J. Drummond</p></div>" +
"<hr><div style=\"font-size:10;\">Part of the BEAST package:" +
version.getHTMLCredits() +
"</div></center></div></html>";
String websiteURLString = "http://beast.bio.ed.ac.uk/";
String helpURLString = "http://beast.bio.ed.ac.uk/BEAUti/";
BeautiApp app = new BeautiApp(nameString, aboutString, icon,
websiteURLString, helpURLString);
app.setDocumentFrameFactory(new DocumentFrameFactory() {
public DocumentFrame createDocumentFrame(Application app, MenuBarFactory menuBarFactory) {
return new BeautiFrame(nameString);
}
});
app.initialize();
app.doNew();
} catch (Exception e) {
JOptionPane.showMessageDialog(new JFrame(), "Fatal exception: " + e,
"Please report this to the authors",
JOptionPane.ERROR_MESSAGE);
e.printStackTrace();
}
}
}
public static boolean developer = false;
}
