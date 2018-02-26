
package dr.app.beauti.util;

import dr.util.Attribute;

import java.io.Writer;
import java.util.List;

public class XMLWriter extends java.io.PrintWriter {

    int level = 0;

    public XMLWriter(Writer writer) {
        super(writer);
    }

    public void increaseLevel() {
        level += 1;
    }

    public void decreaseLevel() {
        level -= 1;
    }

    public void writeComment(String... comments) {
        writeComment(80, comments);
    }

    public void writeComment(int length, String... comments) {
        writeBlankLine();

        for (String comment : comments) {
            StringBuffer buffer = new StringBuffer("<!-- ");
            buffer.append(comment);
            for (int i = buffer.length(); i < (length - 3); i++) {
                buffer.append(' ');
            }
            buffer.append("-->");
            writeText(buffer.toString());
        }
    }

    public void writeOpenTag(String tagname) {
        writeText("<" + tagname + ">");
        increaseLevel();
    }

    public void writeOpenTag(String tagname, Attribute attribute) {
        writeTag(tagname, new Attribute[]{attribute}, false);
    }

    public void writeOpenTag(String tagname, Attribute[] attributes) {
        writeTag(tagname, attributes, false);
    }

    public void writeOpenTag(String tagname, List<Attribute> attributes) {
        writeTag(tagname, attributes.toArray(new Attribute[attributes.size()]), false);
    }

    public void writeTag(String tagname, Attribute attribute, boolean close) {
        writeTag(tagname, new Attribute[]{attribute}, close);
    }

    public void writeTag(String tagname, Attribute[] attributes, boolean close) {
        StringBuffer buffer = new StringBuffer("<");
        buffer.append(tagname);
        for (Attribute attribute : attributes) {
            buffer.append(' ');
            buffer.append(attribute.getAttributeName());
            buffer.append("=\"");
            buffer.append(attribute.getAttributeValue());
            buffer.append("\"");
        }
        if (close) {
            buffer.append("/");
        }
        buffer.append(">");
        writeText(buffer.toString());
        if (!close) {
            increaseLevel();
        }
    }

    public void writeTag(String tagname, Attribute[] attributes, String content, boolean close) {
        StringBuffer buffer = new StringBuffer("<");
        buffer.append(tagname);
        for (Attribute attribute : attributes) {
            buffer.append(' ');
            buffer.append(attribute.getAttributeName());
            buffer.append("=\"");
            buffer.append(attribute.getAttributeValue());
            buffer.append("\"");
        }
        if (content != null) {
            buffer.append(">");
            buffer.append(content);
            if (close) {
                buffer.append("</");
                buffer.append(tagname);
                //buffer.append("/");
            }
        } else if (close) {
            buffer.append("/");
        }
        buffer.append(">");
        writeText(buffer.toString());
        if (!close) {
            increaseLevel();
        }
    }

    public void writeCloseTag(String tagname) {
        decreaseLevel();
        writeText("</" + tagname + ">");
    }

    public void writeBlankLine() {
        println();
        flush();
    }

    public void writeText(String string) {

        int subLength = 2000;

        for (int l = 0; l < string.length(); l += subLength) {
            int e = l + subLength;
            if (e > string.length()) e = string.length();
            for (int i = 0; i < level; i++) {
                write('\t');
            }
            println(string.substring(l, e));
            flush();
        }

    }

    public void checkText(String string) {
        int count = 0;
        for (char ch : string.toCharArray()) {
            if (ch == 'A' || ch == 'C' || ch == 'G' || ch == 'T') {

            } else {
                System.out.println("invalid char = " + ch + ", count = " + count);
            }
            count++;
        }
    }

    public void writeIDref(String tagName, String paramName) {
        writeTag(tagName, new Attribute[]{new Attribute.Default<String>(dr.xml.XMLParser.IDREF, paramName)}, true);
    }
}

