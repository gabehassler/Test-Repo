package dr.inference.loggers;
public interface LogFormatter {
    void startLogging(String title);
    void logHeading(String heading);
    void logLine(String line);
    void logLabels(String[] labels);
    void logValues(String[] values);
    void stopLogging();
}
