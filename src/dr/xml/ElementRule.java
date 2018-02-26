package dr.xml;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
public class ElementRule implements XMLSyntaxRule {
	public ElementRule(Class type) {
		this(type, null, null, 1, 1);
	}
	public ElementRule(Class type, boolean optional) {
		this(type, null, null, (optional ? 0 : 1), 1);
	}
	public ElementRule(Class type, String description) {
		this(type, description, null, 1, 1);
	}
	public ElementRule(Class type, int min, int max) {
		this(type, null, null, min, max);
	}
	public ElementRule(Class type, String description, String example) {
		this(type, description, example, 1, 1);
	}
	public ElementRule(Class type, String description, int min, int max) {
		this(type, description, null, min, max);
	}
	public ElementRule(Class type, String description, String example, int min, int max) {
		if (type == null) throw new IllegalArgumentException("Class cannot be null!");
		this.c = type;
		this.min = min;
		this.max = max;
		this.description = description;
		this.example = example;
	}
	public ElementRule(String name, Class type) {
		this.name = name;
		this.rules = new XMLSyntaxRule[] { new ElementRule(type)};
	}
	public ElementRule(String name, Class type, String description) {
		this.name = name;
		this.description = description;
		this.rules = new XMLSyntaxRule[] { new ElementRule(type)};
	}
	public ElementRule(String name, Class type, String description, boolean optional) {
		this.name = name;
		this.description = description;
		this.rules = new XMLSyntaxRule[] { new ElementRule(type)};
		this.min = 1;
		this.max = 1;
		if (optional) this.min = 0;
	}
	public ElementRule(String name, Class type, String description, int min, int max) {
		this.name = name;
		this.description = description;
		this.rules = new XMLSyntaxRule[] { new ElementRule(type)};
		this.min = min;
		this.max = max;
	}
	public ElementRule(String name, XMLSyntaxRule[] rules) {
		this.name = name;
		this.rules = rules;
	}
	public ElementRule(String name, XMLSyntaxRule[] rules, boolean optional) {
		this.name = name;
		this.rules = rules;
		this.min = 1;
		this.max = 1;
		if (optional) this.min = 0;
	}
	public ElementRule(String name, XMLSyntaxRule[] rules, int min, int max) {
		this.name = name;
		this.rules = rules;
		this.min = min;
		this.max = max;
	}
	public ElementRule(String name, XMLSyntaxRule[] rules, String description) {
		this.name = name;
		this.rules = rules;
		this.description = description;
	}
	public ElementRule(String name, XMLSyntaxRule[] rules, String description, boolean optional) {
		this.name = name;
		this.rules = rules;
		this.description = description;
		this.min = 1;
		this.max = 1;
		if (optional) this.min = 0;
	}
	public ElementRule(String name, XMLSyntaxRule[] rules, String description, int min, int max) {
		this.name = name;
		this.rules = rules;
		this.description = description;
		this.min = min;
		this.max = max;
	}
	public Class getElementClass() { return c; }
	public String getDescription() {
		return description;
	}
	public boolean hasDescription() { return description != null; }
	public String getExample() {
		return example;
	}
	public boolean hasExample() { return example != null; }
	public boolean isSatisfied(XMLObject xo) {
		// first check if no matches and its optional
		int nameCount = 0;
		for (int i = 0; i < xo.getChildCount(); i++) {
			Object xoc = xo.getChild(i);
			if (xoc instanceof XMLObject && ((XMLObject)xoc).getName().equals(name)) {
				nameCount += 1;
			}
		}
		if (min == 0 && nameCount == 0) return true;
		// if !optional or nameCount > 0 then check if exactly one match exists
		int matchCount = 0;
		for (int i = 0; i < xo.getChildCount(); i++) {
			Object xoc = xo.getChild(i);
			if (isCompatible(xoc)) {
				matchCount += 1;
			}
		}
		return (matchCount >= min && matchCount <= max);
	}
    public boolean containsAttribute(String name) {
        return false;
    }
	public String ruleString() {
		 String howMany;
            if( min == 1 && max == 1 ) {
                howMany = "Exactly one";
            } else if (min == max) {
                howMany = "Exactly " + min;
            } else if( (min <= 1) && max == Integer.MAX_VALUE ) {
                howMany = "Any number of";
            } else {
                howMany = "between " + min + " and " + max;
            }
        if (c != null) {
            return howMany + " ELEMENT of type " + getTypeName() + " REQUIRED";
		} else {
            StringBuffer buffer = new StringBuffer(howMany + " ELEMENT of name " + name + " REQUIRED containing");
            for (XMLSyntaxRule rule : rules) {
                buffer.append("\n    ").append(rule.ruleString());
            }
            return buffer.toString();
		}
	}
	public String htmlRuleString(XMLDocumentationHandler handler) {
		if (c != null) {
			String html = "<div class=\"" + (min == 0 ? "optional" : "required") + "rule\">" + handler.getHTMLForClass(c);
			if (max > 1) {
				html += " elements (";
				if (min == 0) {
					html += "zero";
				} else if (min == 1) {
					html += "one";
				} else if (min == max) {
					html += "exactly " + min;
				}
				if (max != min) {
					if (max < Integer.MAX_VALUE) {
						html += " to " + max;
					} else {
						html += " or more";
					}
				}
			} else {
				html += " element (";
				if (min == 0) {
					html += "zero or one";
				} else {
					html += "exactly one";
				}
			}
			html += ")";
			if (hasDescription()) {
				html += "<div class=\"description\">" + getDescription() + "</div>\n";
			}
			return html + "</div>\n";
		} else {
			StringBuffer buffer = new StringBuffer("<div class=\"" + (min == 0 ? "optional" : "required") + "compoundrule\">Element named <span class=\"elemname\">" + name + "</span> containing:");
            for (XMLSyntaxRule rule : rules) {
                buffer.append(rule.htmlRuleString(handler));
            }
            if (hasDescription()) {
                buffer.append("<div class=\"description\">").append(getDescription()).append("</div>\n");
			}
			buffer.append("</div>\n");
			return buffer.toString();
		}
	}
    public String wikiRuleString(XMLDocumentationHandler handler, String prefix) {
		if (c != null) {
			String wiki = prefix + handler.getHTMLForClass(c);
			if (max > 1) {
				wiki += " elements (";
				if (min == 0) {
					wiki += "zero";
				} else if (min == 1) {
					wiki += "one";
				} else if (min == max) {
					wiki += "exactly " + min;
				}
				if (max != min) {
					if (max < Integer.MAX_VALUE) {
						wiki += " to " + max;
					} else {
						wiki += " or more";
					}
				}
			} else {
				wiki += " element (";
				if (min == 0) {
					wiki += "zero or one";
				} else {
					wiki += "exactly one";
				}
			}
			wiki += ")\n";
			if (hasDescription()) {
				wiki += prefix + ":''" + getDescription() + "''\n";
			} else {
                wiki += prefix + ":\n";
            }
			return wiki;
		} else {
			StringBuffer buffer = new StringBuffer(prefix + "Element named <code>&lt;" + name + "&gt;</code> containing:\n");
            for (XMLSyntaxRule rule : rules) {
                buffer.append(rule.wikiRuleString(handler, prefix + "*"));
            }
            if (hasDescription()) {
                buffer.append(prefix).append("*:''").append(getDescription()).append("''\n");
			} else {
                buffer.append(prefix).append("*:\n");
            }
			return buffer.toString();
		}
	}
	public String ruleString(XMLObject xo) {
		return ruleString();
	}
	public boolean isAttributeRule() { return false; }
	public String getName() { return name; }
	public XMLSyntaxRule[] getRules() { return rules; }
	private boolean isCompatible(Object o) {
		if (rules != null) {
			if (o instanceof XMLObject) {
				XMLObject xo = (XMLObject)o;
				if (xo.getName().equals(name)) {
                    for (XMLSyntaxRule rule : rules) {
                        if (!rule.isSatisfied(xo)) {
                            return false;
                        }
                    }
                    return true;
				}
			}
		} else {
			if (c == null) {
				return true;
			}
			if (c.isInstance(o)) {
				return true;
			}
			if (o instanceof String) {
				if (c == Double.class) {
					try {
						Double.parseDouble((String)o);
						return true;
					} catch (NumberFormatException nfe) { return false; }
				}
				if (c == Integer.class) {
					try {
						Integer.parseInt((String)o);
						return true;
					} catch (NumberFormatException nfe) { return false; }
				}
				if (c == Float.class) {
					try {
						Float.parseFloat((String)o);
						return true;
					} catch (NumberFormatException nfe) { return false; }
				}
				if (c == Boolean.class) {
					return (o.equals("true") || o.equals("false"));
				}
				if (c == Number.class) {
					try {
						Double.parseDouble((String)o);
						return true;
					} catch (NumberFormatException nfe) { return false; }
				}
			}
		}
		return false;
	}
	private String getTypeName() {
		if (c == null) return "Object";
		String name = c.getName();
		return name.substring(name.lastIndexOf('.')+1);
	}
	public Set<Class> getRequiredTypes() {
		if (c != null) {
			return Collections.singleton(c);
		} else {
			Set<Class> set = new TreeSet<Class>(ClassComparator.INSTANCE);
            for (XMLSyntaxRule rule : rules) {
                set.addAll(rule.getRequiredTypes());
            }
            return set;
		}
	}
    public boolean isLegalElementName(String elementName) {
        return c == null &&  name != null && name.equals(elementName);
    }
    public boolean isLegalElementClass(Class c) {
        return this.c != null && this.c.isAssignableFrom(c);
    }
    public boolean isLegalSubelementName(String elementName) {
        for( XMLSyntaxRule r : rules ) {
            if( r.isLegalElementName(elementName) ) {
                return true;
            }
        }
        return false;
    }
    public int getMin() { return min; }
	public int getMax() { return max; }
	public String toString() { return ruleString(); }
	private Class c = null;
	private String name = null;
	private XMLSyntaxRule[] rules = null;
	private int min = 1;
	private int max = 1;
	private String description = null;
	private String example = null;
}
