package dr.xml;
import java.util.Set;
import java.util.TreeSet;
public class AndRule implements XMLSyntaxRule {
public AndRule(XMLSyntaxRule a, XMLSyntaxRule b) {
rules = new XMLSyntaxRule[] {a,b};
}
public AndRule(XMLSyntaxRule[] rules) {
this.rules = rules;
}
public XMLSyntaxRule[] getRules() {
return rules;
}
public boolean isSatisfied(XMLObject xo) {
for (XMLSyntaxRule rule : rules) {
if (!rule.isSatisfied(xo)) return false;
}
return true;
}
public boolean containsAttribute(String name) {
for( XMLSyntaxRule rule : rules ) {
if( rule.containsAttribute((name)) ) {
return true;
}
}
return false;
}
public String ruleString() {
String ruleString = "(" + rules[0].ruleString();
for (int i = 1; i < rules.length; i++) {
ruleString += "& " + rules[i].ruleString();
}
return ruleString + ")";
}
public String htmlRuleString(XMLDocumentationHandler handler) {
String html = "<div class=\"requiredcompoundrule\">All of:";
for (XMLSyntaxRule rule : rules) {
html += rule.htmlRuleString(handler);
}
html += "</div>";
return html;
}
public String wikiRuleString(XMLDocumentationHandler handler, String prefix) {
String html = prefix + "All of:";
for (XMLSyntaxRule rule : rules) {
html += rule.wikiRuleString(handler, prefix + "*");
}
html += "\n";
return html;
}
public String ruleString(XMLObject xo) {
for (XMLSyntaxRule rule : rules) {
if (!rule.isSatisfied(xo)) return rule.ruleString(xo);
}
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
XMLSyntaxRule[] rules;
}