package dr.app.gui.util;
public abstract class SimpleLongTask extends LongTask {
boolean background = false;
private SwingWorker worker = null;
public int current = 0;
public int length = 1;
public boolean pleaseStop = false;
public String message = "";
public String description = "";
public int getLengthOfTask() {
return length;
}
public int getCurrent() {
return current;
}
public void stop() {
pleaseStop = true;
}
public String getMessage() {
return message;
}
public String getDescription() {
return description;
}
}
