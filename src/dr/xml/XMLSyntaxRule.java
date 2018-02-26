
package dr.xml;

import java.util.Set;

public interface XMLSyntaxRule {

	public boolean isSatisfied(XMLObject object);

    public boolean containsAttribute(String name);

	public String ruleString();

	public String htmlRuleString(XMLDocumentationHandler handler);

	public String wikiRuleString(XMLDocumentationHandler handler, String prefix);
	
	public String ruleString(XMLObject object);

	public Set<Class> getRequiredTypes();

    boolean isLegalElementName(String elementName);

    boolean isLegalElementClass(Class c);

    boolean isLegalSubelementName(String elementName);
}
