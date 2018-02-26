
package dr.inference.loggers;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class TabDelimitedFormatter implements LogFormatter {

    protected final PrintWriter printWriter;
    private final boolean outputLabels;
    private final boolean closeFile;


    public TabDelimitedFormatter(PrintWriter printWriter) {
        this.printWriter = printWriter;
        outputLabels = true;
        closeFile = false;
    }

    public TabDelimitedFormatter(OutputStream stream) {
        this.printWriter = new PrintWriter(new OutputStreamWriter(stream));
        outputLabels = true;
        closeFile = false;
    }

    public TabDelimitedFormatter(PrintWriter printWriter, boolean labels) {

        this.printWriter = printWriter;
        outputLabels = labels;
        closeFile = true;
    }

    public void startLogging(String title) {
        // DO NOTHING    
    }

    public void logHeading(String heading) {
        if (heading != null) {
            String[] lines = heading.split("[\r\n]");
            for (String line : lines) {
                printWriter.println("# " + line);
            }
        }
        printWriter.flush();
    }

    public void logLine(String line) {
        printWriter.println(line);
        printWriter.flush();
    }

    public void logLabels(String[] labels) {
        if (outputLabels) {
            if (labels.length > 0) {
                printWriter.print(labels[0]);
            }

            for (int i = 1; i < labels.length; i++) {
                printWriter.print('\t');
                printWriter.print(labels[i]);
            }

            printWriter.println();
            printWriter.flush();
        }
    }

    public void logValues(String[] values) {

        if (values.length > 0) {
            printWriter.print(values[0]);
        }

        for (int i = 1; i < values.length; i++) {
            printWriter.print('\t');
            printWriter.print(values[i]);
        }

        printWriter.println();
        printWriter.flush();
    }

    public void stopLogging() {
        printWriter.flush();
        if (closeFile) {
            printWriter.close();
        }
    }

}
