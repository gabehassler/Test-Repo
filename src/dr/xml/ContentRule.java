
package dr.xml;

import java.util.Collections;
import java.util.Set;

public class ContentRule implements XMLSyntaxRule {

	public ContentRule(String htmlDescription) {
		this.htmlDescription = htmlDescription;
	}

	public boolean isSatisfied(XMLObject xo) { return true; }

    public boolean containsAttribute(String name) {
        return false;
    }

	public String ruleString() { return htmlDescription; }

	public String htmlRuleString(XMLDocumentationHandler handler) {
		return htmlDescription;
	}

	public String wikiRuleString(XMLDocumentationHandler handler, String prefix) {
		return prefix + ":" + htmlDescription;
	}

	public String ruleString(XMLObject xo) { return null; }

	public Set<Class> getRequiredTypes() { return Collections.EMPTY_SET; }

    public boolean isLegalElementName(String elementName) {
        return true;
    }

    public boolean isLegalSubelementName(String elementName) {
        return true;
    }

    public boolean isLegalElementClass(Class c) {
        return true;
    }

    public boolean isAttributeRule() { return false; }

	private final String htmlDescription;
}
