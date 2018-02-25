package dr.inference.loggers;
public interface Logger {
void startLogging();
void log(long state);
void stopLogging();
}
