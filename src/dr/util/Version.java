
package dr.util;

public interface Version {

    String getVersion();

	String getVersionString();

	String getBuildString();

	String getDateString();

    String[] getCredits();

    String getHTMLCredits();
}
