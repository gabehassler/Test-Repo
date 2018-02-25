package dr.xml;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class StringAttributeRule extends AttributeRule {
public StringAttributeRule(String name, String description) {
this(name, description, (String) null, false);
}
public StringAttributeRule(String name, String description, String example) {
this(name, description, example, false);
}
public StringAttributeRule(String name, String description, boolean optional) {
this(name, description, null, optional, 0, Integer.MAX_VALUE);
}
public StringAttributeRule(String name, String description, String example, boolean optional) {
this(name, description, example, optional, 0, Integer.MAX_VALUE);
}
public StringAttributeRule(String name, String description, String[] valid, boolean optional) {
this(name, description, null, optional, 0, Integer.MAX_VALUE);
validValues = new ArrayList<String>();
for (String aValid : valid) {
validValues.add(aValid);
}
this.example = null;
}
public StringAttributeRule(String name, String description, Enum[] valid, boolean optional) {
this(name, description, null, optional, 0, Integer.MAX_VALUE);
validValues = new ArrayList<String>();
for (Enum aValid : valid) {
validValues.add(aValid.name());
}
this.example = null;
}
public StringAttributeRule(String name, String description, String[][] valid, boolean optional) {
this(name, description, null, optional, 0, Integer.MAX_VALUE);
validValues = new ArrayList<String>();
for (String[] aValid : valid) {
for (String anAValid : aValid) {
validValues.add(anAValid);
}
}
this.example = null;
}
private StringAttributeRule(String name, String description, String example, boolean optional, int minLength, int maxLength) {
setName(name);
setAttributeClass(String.class);
setOptional(optional);
setDescription(description);
this.example = example;
this.minLength = minLength;
this.maxLength = maxLength;
}
public boolean isSatisfied(XMLObject xo) {
if (super.isSatisfied(xo)) {
if (!getOptional()) {
try {
final String str = (String) getAttribute(xo);
if (validValues != null) {
for (Object validValue : validValues) {
if (str.equals(validValue)) return true;
}
return false;
} else {
return (str.length() >= minLength || str.length() <= maxLength);
}
} catch (XMLParseException xpe) {
//
}
}
return true;
}
return false;
}
public String ruleString() {
StringBuffer rule = new StringBuffer(super.ruleString());
if (validValues != null && validValues.size() > 0) {
rule.append(" from {");
rule.append(validValues.get(0));
for (int i = 1; i < validValues.size(); i++) {
rule.append(", ");
rule.append(validValues.get(i));
}
}
return rule.toString();
}
public String htmlRuleString(XMLDocumentationHandler handler) {
String rule =
"<div class=\"" + (getOptional() ? "optional" : "required") + "rule\"> Attribute " +
" <span class=\"attrname\">" + getName() +
"</span>";
if (validValues != null) {
rule += " &isin; {<tt>" + validValues.get(0) + "</tt>";
for (int i = 1; i < validValues.size(); i++) {
rule += ", <tt>" + validValues.get(i) + "</tt>";
}
rule += "}";
} else {
rule += " is string";
}
rule += " <div class=\"description\">" + getDescription() + "</div>";
rule += "</div>";
return rule;
}
public String getExample() {
Random random = new Random();
if (validValues != null) {
return validValues.get(random.nextInt(validValues.size()));
} else return example;
}
public boolean hasExample() {
return (validValues != null || example != null);
}
private int minLength = 0, maxLength = Integer.MAX_VALUE;
private List<String> validValues = null;
private String example = null;
}
