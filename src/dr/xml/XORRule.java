
package dr.xml;

import java.util.Set;
import java.util.TreeSet;

public class XORRule implements XMLSyntaxRule {

	public XORRule(XMLSyntaxRule a, XMLSyntaxRule b) {
		this(a, b, false);
	}

	public XORRule(XMLSyntaxRule[] rules) {
        this(rules, false);
	}

    public XORRule(XMLSyntaxRule a, XMLSyntaxRule b, boolean optional) {
        this(new XMLSyntaxRule[] {a, b}, optional);
    }

    public XORRule(XMLSyntaxRule[] rules, boolean optional) {
		this.rules = rules;
        this.optional = optional;
	}

	public XMLSyntaxRule[] getRules() { return rules; }

	public boolean isSatisfied(XMLObject xo) {

		int satisfiedCount = 0;
        for (XMLSyntaxRule rule : rules) {
            if (rule.isSatisfied(xo)) {
                satisfiedCount += 1;
                if (satisfiedCount > 1) return false;
            }
        }
        return optional || satisfiedCount == 1;
	}

    public boolean containsAttribute(String name) {
        for( XMLSyntaxRule rule : rules ) {
            if( rule.containsAttribute(name) ) {
                return true;
            }
        }
        return false;
    }

	public String ruleString() {
		StringBuffer buffer = new StringBuffer();
        if (optional) {
            buffer.append("*Optionally, one of \n");
        } else {
            buffer.append("*One of \n");
        }
        for (XMLSyntaxRule rule : rules) {
            buffer.append("*").append(rule.ruleString()).append("\n");
        }
        return buffer.toString();
	}

	public String htmlRuleString(XMLDocumentationHandler handler) {
		StringBuffer buffer = new StringBuffer("<div class=\"requiredcompoundrule\">One of:\n");
        for (XMLSyntaxRule rule : rules) {
            buffer.append(rule.htmlRuleString(handler));
        }
        buffer.append("</div>\n");
		return buffer.toString();
	}

	public String wikiRuleString(XMLDocumentationHandler handler, String prefix) {
		StringBuffer buffer = new StringBuffer(prefix + "One of:\n");
        for (XMLSyntaxRule rule : rules) {
            buffer.append(rule.wikiRuleString(handler, prefix + "*"));
        }
        buffer.append("\n");
		return buffer.toString();
	}


	public String ruleString(XMLObject xo) {
		return ruleString();
	}

	public Set<Class> getRequiredTypes() {

		Set<Class> set = new TreeSet<Class>(ClassComparator.INSTANCE);

        for (XMLSyntaxRule rule : rules) {
            set.addAll(rule.getRequiredTypes());
        }
        return set;
	}

    public boolean isLegalElementName(String elementName) {
        for (XMLSyntaxRule rule : rules) {
            if( rule.isLegalElementName(elementName) ) {
                return true;
            }
        }
        return false;
    }

    public boolean isLegalElementClass(Class c) {
        for (XMLSyntaxRule rule : rules) {
            if( rule.isLegalElementClass(c) ) {
                return true;
            }
        }
        return false;
    }


    public boolean isLegalSubelementName(String elementName) {
        for (XMLSyntaxRule rule : rules) {
            if( rule.isLegalSubelementName(elementName) ) {
                return true;
            }
        }
        return false;
    }

    private final XMLSyntaxRule[] rules;
    private final boolean optional;
}
