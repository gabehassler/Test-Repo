
package dr.app.beast;

import dr.util.Version;

public class BeastVersion implements Version {

    private static final String VERSION = "1.8.3";

    private static final String DATE_STRING = "2002-2015";

    private static final boolean IS_PRERELEASE = true;

    // this is now being manually updated since the move to GitHub. Using date in yyyymmdd format (suffix
    // with b,c,d etc if multiple revisions in a day.
    private static final String REVISION = "GitHub 20150808";

    public String getVersion() {
        return VERSION;
    }

    public String getVersionString() {
        return "v" + VERSION + (IS_PRERELEASE ? " Prerelease " + getBuildString() : "");
    }

    public String getDateString() {
        return DATE_STRING;
    }

    public String[] getCredits() {
        return new String[]{
                "Designed and developed by",
                "Alexei J. Drummond, Andrew Rambaut and Marc A. Suchard",
                "",
                "Department of Computer Science",
                "University of Auckland",
                "alexei@cs.auckland.ac.nz",
                "",
                "Institute of Evolutionary Biology",
                "University of Edinburgh",
                "a.rambaut@ed.ac.uk",
                "",
                "David Geffen School of Medicine",
                "University of California, Los Angeles",
                "msuchard@ucla.edu",
                "",
                "Downloads, Help & Resources:",

                "\thttp://beast.bio.ed.ac.uk",
                "",
                "Source code distributed under the GNU Lesser General Public License:",
                "\thttp://github.com/beast-dev/beast-mcmc",
                "",
                "BEAST developers:",
                "\tAlex Alekseyenko, Guy Baele, Trevor Bedford, Filip Bielejec, Erik Bloomquist, Matthew Hall,",
                "\tJoseph Heled, Sebastian Hoehna, Denise Kuehnert, Philippe Lemey, Wai Lok Sibon Li,",
                "\tGerton Lunter, Sidney Markowitz, Vladimir Minin, Michael Defoin Platel,",
                "\tOliver Pybus, Chieh-Hsi Wu, Walter Xie",
                "",
                "Thanks to:",
                "\tRoald Forsberg, Beth Shapiro and Korbinian Strimmer"};
    }

    public String getHTMLCredits() {
        return
                "<p>Designed and developed by<br>" +
                        "Alexei J. Drummond, Andrew Rambaut and Marc A. Suchard</p>" +
                        "<p>Department of Computer Science, University of Auckland<br>" +
                        "<a href=\"mailto:alexei@cs.auckland.ac.nz\">alexei@cs.auckland.ac.nz</a></p>" +
                        "<p>Institute of Evolutionary Biology, University of Edinburgh<br>" +
                        "<a href=\"mailto:a.rambaut@ed.ac.uk\">a.rambaut@ed.ac.uk</a></p>" +
                        "<p>David Geffen School of Medicine, University of California, Los Angeles<br>" +
                        "<a href=\"mailto:msuchard@ucla.edu\">msuchard@ucla.edu</a></p>" +
                        "<p><a href=\"http://beast.bio.ed.ac.uk\">http://beast.bio.ed.ac.uk</a></p>" +
                        "<p>Source code distributed under the GNU LGPL:<br>" +
                        "<a href=\"http://github.com/beast-dev/beast-mcmc\">http://github.com/beast-dev/beast-mcmc</a></p>" +
                        "<p>BEAST developers:<br>" +
                        "Alex Alekseyenko, Guy Baele, Trevor Bedford, Filip Bielejec, Erik Bloomquist, Matthew Hall,<br>"+
                        "Joseph Heled, Sebastian Hoehna, Denise Kuehnert, Philippe Lemey, Wai Lok Sibon Li,<br>"+
                        "Gerton Lunter, Sidney Markowitz, Vladimir Minin, Michael Defoin Platel,<br>"+
                        "Oliver Pybus, Chieh-Hsi Wu, Walter Xie</p>" +
                        "<p>Thanks to Roald Forsberg, Beth Shapiro and Korbinian Strimmer</p>";
    }

    public String getBuildString() {
        try {
            return "r" + REVISION.split(" ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "Invalid Revision String : " + REVISION;
        }
    }
}
