package dr.app.pathogen;
import dr.app.util.OSType;
import dr.util.Version;
import jam.framework.*;
import javax.swing.*;
import java.awt.*;
public class PathogenApp extends MultiDocApplication {
    private final static Version version = new Version() {
        private static final String VERSION = "1.5";
        public String getVersion() {
            return VERSION;
        }
        public String getVersionString() {
            return "v" + VERSION;
        }
        public String getDateString() {
            return "2003-2015";
        }
        public String getBuildString() {
            return "Build r3656";
        }
        public String[] getCredits() {
            return new String[0];
        }
        public String getHTMLCredits() {
            return "<p>by<br>" +
                    "Andrew Rambaut</p>" +
                    "<p>Institute of Evolutionary Biology, University of Edinburgh<br>" +
                    "<a href=\"mailto:a.rambaut@ed.ac.uk\">a.rambaut@ed.ac.uk</a></p>" +
                    "<p>Part of the BEAST package:<br>" +
                    "<a href=\"http://beast.bio.ed.ac.uk/\">http://beast.bio.ed.ac.uk/</a></p>";
        }
    };
    public PathogenApp(String nameString, String aboutString, Icon icon,
                       String websiteURLString, String helpURLString) {
        super(new PathogenMenuBarFactory(), nameString, aboutString, icon, websiteURLString, helpURLString);
    }
    // Main entry point
    static public void main(String[] args) {
        if (OSType.isMac()) {
            System.setProperty("apple.laf.useScreenMenuBar","true");
            System.setProperty("apple.awt.showGrowBox","true");
            System.setProperty("apple.awt.graphics.UseQuartz","true");
            UIManager.put("SystemFont", new Font("Lucida Grande", Font.PLAIN, 13));
            UIManager.put("SmallSystemFont", new Font("Lucida Grande", Font.PLAIN, 11));
        }
        try {
            try {
                javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        try {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            java.net.URL url = PathogenApp.class.getResource("images/pathogen.png");
            Icon icon = null;
            if (url != null) {
                icon = new ImageIcon(url);
            }
            final String nameString = "Path-O-Gen";
            final String versionString = version.getVersionString();
            String aboutString = "<html><center><p>Temporal Signal Investigation Tool<br>" +
                    "Version " + versionString + ", " + version.getDateString() + "</p>" +
                    version.getHTMLCredits() +
                    "</center></html>";
            String websiteURLString = "http://tree.bio.ed.ac.uk/";
            String helpURLString = "http://tree.bio.ed.ac.uk/software/pathogen";
            PathogenApp app = new PathogenApp(nameString, aboutString, icon,
                    websiteURLString, helpURLString);
            app.setDocumentFrameFactory(new DocumentFrameFactory() {
                public DocumentFrame createDocumentFrame(Application app, MenuBarFactory menuBarFactory) {
                    return new PathogenFrame(nameString);
                }
            });
            app.initialize();
            app.doOpen();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Fatal exception: " + e,
                    "Please report this to the authors",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}