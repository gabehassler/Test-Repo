package dr.app.gui.util;
public abstract class LongTask {
private SwingWorker worker = null;
Object answer;
boolean finished = false;
public abstract Object doWork() throws java.lang.Exception;
public final void go() {
worker = new SwingWorker() {
public Object construct() {
try {
answer = doWork();
} catch (java.lang.Exception e) {
throw new RuntimeException(e.toString());
}
finished = true;
return answer;
}
};
worker.start();
}
public final Object getAnswer() {
return answer;
}
public abstract int getLengthOfTask();
public abstract int getCurrent();
public void stop() {
finished = true;
}
public boolean done() {
return finished;
}
public abstract String getMessage();
public String getDescription() {
return "Running a long task...";
}
}
