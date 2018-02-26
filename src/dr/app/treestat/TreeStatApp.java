
package dr.app.treestat;

import dr.app.beast.BeastVersion;
import dr.app.util.OSType;
import dr.evolution.io.NexusImporter;
import dr.util.Version;
import jam.framework.SingleDocApplication;
import jam.mac.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class TreeStatApp extends SingleDocApplication {

    private final static Version version = new BeastVersion();

    public TreeStatApp(String nameString, String aboutString, Icon icon, String websiteURLString, String helpURLString) {
        super(nameString, aboutString, icon, websiteURLString, helpURLString);
    }

    // Main entry point
    static public void main(String[] args) {

        // There is a major issue with languages that use the comma as a decimal separator.
        // To ensure compatibility between programs in the package, enforce the US locale.
        Locale.setDefault(Locale.US);

        // don't display warnings from NexusImporter as they are only relevant to Beast-MCMC-1.x
        NexusImporter.setSuppressWarnings(true);


        if (OSType.isMac()) {
            if (Utils.getMacOSXVersion().startsWith("10.5")) {
                System.setProperty("apple.awt.brushMetalLook","true");
            }
            System.setProperty("apple.laf.useScreenMenuBar","true");
            System.setProperty("apple.awt.showGrowBox","true");
            UIManager.put("SystemFont", new Font("Lucida Grande", Font.PLAIN, 13));
            UIManager.put("SmallSystemFont", new Font("Lucida Grande", Font.PLAIN, 11));
        }

        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            java.net.URL url = TreeStatApp.class.getResource("images/TreeStat.png");
            Icon icon = null;

            if (url != null) {
                icon = new ImageIcon(url);
            }

            final String versionString = version.getVersionString();
            String nameString = "TreeStat " + versionString;
            String aboutString = "<html><center><p>Tree Statistic Calculation Tool<br>" +
                    versionString + ", " + version.getDateString() + "</p>" +
//                    "Version 1.2, 2005-2010</p>" +
                    "<p>by<br>" +
                    "Andrew Rambaut and Alexei J. Drummond</p>" +
                    "<p>Institute of Evolutionary Biology, University of Edinburgh<br>" +
                    "<a href=\"mailto:a.rambaut@ed.ac.uk\">a.rambaut@ed.ac.uk</a></p>" +
                    "<p>Department of Computer Science, University of Auckland<br>" +
                    "<a href=\"mailto:alexei@cs.auckland.ac.nz\">alexei@cs.auckland.ac.nz</a></p>" +
                    "<p>Visit the BEAST page:<br>" +
                    "<a href=\"http://beast.bio.ed.ac.uk/\">http://beast.bio.ed.ac.uk/</a></p>" +
                    "</center></html>";

            String websiteURLString = "http://beast.bio.ed.ac.uk/";
            String helpURLString = "http://beast.bio.ed.ac.uk/TreeStat/";

            TreeStatApp app = new TreeStatApp(nameString, aboutString, icon,
                    websiteURLString, helpURLString);
            app.setDocumentFrame(new TreeStatFrame(app, nameString));
            app.initialize();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Fatal exception: " + e,
                    "Please report this to the authors",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
