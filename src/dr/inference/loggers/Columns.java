package dr.inference.loggers;
public class Columns {
    public Columns(LogColumn[] columns) {
        this.columns = columns;
    }
    public LogColumn[] getColumns() {
        return columns;
    }
    private LogColumn[] columns = null;
}
