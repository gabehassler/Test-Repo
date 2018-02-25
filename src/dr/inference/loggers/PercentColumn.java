package dr.inference.loggers;
public class PercentColumn extends NumberColumn {
private final NumberColumn column;
public PercentColumn(NumberColumn col) {
super(col.getLabel());
this.column = col;
}
public void setSignificantFigures(int sf) {
column.setSignificantFigures(sf);
}
public int getSignificantFigures() {
return column.getSignificantFigures();
}
public void setMinimumWidth(int minimumWidth) {
column.setMinimumWidth(minimumWidth);
}
public int getMinimumWidth() {
return column.getMinimumWidth();
}
public String getFormattedValue() {
double val = column.getDoubleValue();
if( val >= 0 && val <= 1 ) {
return column.formatValue(val * 100) + "%";
}
return column.getFormattedValue();
}
public double getDoubleValue() {
return column.getDoubleValue();
}
}