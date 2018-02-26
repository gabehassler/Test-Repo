package dr.xml;
import java.io.PrintWriter;
import java.util.ArrayList;
public class Report {
    public static final String REPORT = "report";
    public static final String FILENAME = "fileName";
    protected String title = "";
    protected ArrayList<Object> objects = new ArrayList<Object>();
    private PrintWriter writer;
    public void createReport() {
		if (!title.equalsIgnoreCase("")) {
			writer.println(getTitle());
			writer.println();
		}
        for (Object object : objects) {
            final String item;
            if (object instanceof Reportable) {
                item = ((Reportable) object).getReport();
            } else {
                item = object.toString();
            }
            writer.print(item.trim());
            writer.print(" ");
        }
        writer.println();
        writer.flush();
    }
    public void setOutput(PrintWriter writer) {
        this.writer = writer;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public void add(Object object) {
        objects.add(object);
    }
    public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
        public String getParserName() {
            return REPORT;
        }
        public Object parseXMLObject(XMLObject xo) throws XMLParseException {
            Report report;
            if (xo.hasAttribute("type")) {
                if (xo.getAttribute("type").equals("TEXT"))
                    report = new Report();
                else if (xo.getAttribute("type").equals("XHTML"))
                    report = new XHTMLReport();
                else
                    throw new XMLParseException("unknown document type, " + xo.getAttribute("type") + ", for report");
            } else
                report = new Report();
            report.setTitle(xo.getAttribute("title", ""));
            for (int i = 0; i < xo.getChildCount(); i++) {
                Object child = xo.getChild(i);
                report.add(child);
            }
            report.setOutput(XMLParser.getFilePrintWriter(xo, getParserName()));
            report.createReport();
            return report;
        }
        //************************************************************************
        // AbstractXMLObjectParser implementation
        //************************************************************************
        public String getParserDescription() {
            return "Generates a report using the given text and elements";
        }
        public XMLSyntaxRule[] getSyntaxRules() {
            return rules;
        }
        private final XMLSyntaxRule[] rules = {
                new StringAttributeRule("type", "The format of the report", new String[]{"TEXT", "XHTML"}, true),
                new StringAttributeRule("title", "The title of the report", "Report", true),
                new ElementRule(Object.class, "An arbitrary mixture of text and elements to report", 1, Integer.MAX_VALUE),
                AttributeRule.newStringRule(FILENAME, true),
        };
        public Class getReturnType() {
            return Report.class;
        }
    };
}
