
package dr.util;

import java.io.Serializable;
import java.text.DecimalFormat;

public class NumberFormatter implements Serializable {

    private int sf;
    private double upperCutoff;
    private double[] cutoffTable;
    private final DecimalFormat decimalFormat = new DecimalFormat();
    private DecimalFormat scientificFormat = null;
    private boolean isPadding = false;
    private int fieldWidth;


    public NumberFormatter(int sf) {
        setSignificantFigures(sf);
    }

    public NumberFormatter(int sf, int fieldWidth) {
        setSignificantFigures(sf);
        setPadding(true);
        setFieldWidth(fieldWidth);
    }

    public void setSignificantFigures(int sf) {
        this.sf = sf;
        upperCutoff = Math.pow(10,sf-1);
        cutoffTable = new double[sf];
        long num = 10;
        for (int i =0; i < cutoffTable.length; i++) {
            cutoffTable[i] = (double)num;
            num *= 10;
        }
        decimalFormat.setMinimumIntegerDigits(1);
        decimalFormat.setMaximumFractionDigits(sf-1);
        decimalFormat.setMinimumFractionDigits(sf-1);
        decimalFormat.setGroupingUsed(false);
        scientificFormat = new DecimalFormat(getScientificPattern(sf));
        fieldWidth = sf;
    }

    public void setPadding(boolean padding) {
        isPadding = padding;
    }

    public void setFieldWidth(int fw) {
        if (fw < sf+4) throw new IllegalArgumentException();
        fieldWidth = fw;
    }

    public int getFieldWidth() { return fieldWidth; }

    public String formatToFieldWidth(String s, int fieldWidth) {
        int size = fieldWidth - s.length();
        StringBuffer buffer = new StringBuffer(s);
        for (int i =0; i < size; i++) {
            buffer.append(' ');
        }
        return buffer.toString();
    }

    public String formatDecimal(double value, int numFractionDigits) {

        decimalFormat.setMaximumFractionDigits(numFractionDigits);
        decimalFormat.setMinimumFractionDigits(Math.min(numFractionDigits, 1));
        return decimalFormat.format(value);
    }

    public String format(double value) {

        StringBuffer buffer = new StringBuffer();

        double absValue = Math.abs(value);

        if ((absValue > upperCutoff) || (absValue < 0.1 && absValue != 0.0)) {
            buffer.append(scientificFormat.format(value));
        } else {
            int numFractionDigits = 0;
            if (value != (int)value) {
                numFractionDigits = getNumFractionDigits(value);
            }
            buffer.append(formatDecimal(value, numFractionDigits));
        }

        if (isPadding) {
            int size = fieldWidth - buffer.length();
            for (int i =0; i < size; i++) {
                buffer.append(' ');
            }
        }
        return buffer.toString();
    }

    private int getNumFractionDigits(double value) {
        value = Math.abs(value);
        for (int i =0; i < cutoffTable.length; i++) {
            if (value < cutoffTable[i]) return sf-i-1;
        }
        return sf - 1;
    }

    private String getScientificPattern(int sf) {
        String pattern = "0.";
        for (int i =0; i < sf-1; i++) {
            pattern += "#";
        }
        pattern += "E0";
        return pattern;
    }
}
