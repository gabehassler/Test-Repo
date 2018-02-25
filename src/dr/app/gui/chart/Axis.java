package dr.app.gui.chart;
import dr.util.NumberFormatter;
import java.lang.IllegalArgumentException;
public interface Axis {
// These constants are used for automatic scaling to select exactly
// where the axis starts and stops.
static public final int AT_MAJOR_TICK=0;
static public final int AT_MAJOR_TICK_PLUS=1;
static public final int AT_MINOR_TICK=2;
static public final int AT_MINOR_TICK_PLUS=3;
static public final int AT_DATA=4;
static public final int AT_ZERO=5;
static public final int AT_VALUE=6;
void setAxisFlags(int minAxisFlag, int maxAxisFlag);
void setPrefNumTicks(int prefNumMajorTicks, int prefNumMinorTicks);
void setIsDiscrete(boolean isDiscrete);
void setLabelFirst(boolean labelFirst);
void setLabelLast(boolean labelLast);
boolean getIsDiscrete();
boolean getLabelFirst();
boolean getLabelLast();
void setManualRange(double minValue, double maxValue);
void setManualAxis(double minTick, double maxTick, double majorTick, double minorTick);
void setAutomatic();
public void setAutomatic(int minAxisFlag, int maxAxisFlag);
public void setRange(double minValue, double maxValue);
public void addRange(double minValue, double maxValue);
public double transform(double value);
public double untransform(double value);
public String format(double value);
public double getMinAxis();
public double getMaxAxis();
public double getMinData();
public double getMaxData();
public int getMajorTickCount();
public int getMinorTickCount(int majorTickIndex);
public double getMajorTickValue(int majorTickIndex);
public double getMinorTickValue(int minorTickIndex, int majorTickIndex);
public double getMajorTickSpacing();
public double getMinorTickSpacing();
public abstract class AbstractAxis implements Axis {
// The minimum and maximum values of the data
protected double minData=Double.POSITIVE_INFINITY, maxData=Double.NEGATIVE_INFINITY;
// The number of major ticks and minor ticks within them
protected int majorTickCount, minorTickCount; // calculated automatically
// The prefered minimum number of ticks
protected int prefMajorTickCount = 5, prefMinorTickCount = 2; // set manually
// Flags using the above constants
protected int minAxisFlag, maxAxisFlag;
// The distance between major ticks and minor Ticks
protected double majorTick, minorTick; // calculated automatically or set by user
// The value of the first and last major tick
protected double minTick, maxTick; // calculated automatically or set by user
// The value of the beginning and end of the axis
protected double minAxis, maxAxis;
// User defined axis range
protected double minValue, maxValue;
// Flags to give automatic scaling and integer division
protected boolean isAutomatic=true, isDiscrete=false;
// Flags to specify that the first tick and last tick should have labels.
// It is up to the AxisPanel to do something about this.
protected boolean labelFirst=false, labelLast=false;
protected boolean isCalibrated = false;
protected final NumberFormatter formatter = new NumberFormatter(8);
// Used internally
private double epsilon;
private int fraction;
public AbstractAxis() {
this(AT_MAJOR_TICK, AT_MAJOR_TICK, false);
}
public AbstractAxis(int minAxisFlag, int maxAxisFlag) {
this(minAxisFlag, maxAxisFlag, false);
}
public AbstractAxis(int minAxisFlag, int maxAxisFlag, boolean isDiscrete) {
this.minAxisFlag = minAxisFlag;
this.maxAxisFlag = maxAxisFlag;
this.isDiscrete = isDiscrete;
isAutomatic = true;
isCalibrated = false;
}
public AbstractAxis(double minTick, double maxTick,
double majorTick, double minorTick) {
setManualAxis(minTick, maxTick, majorTick, minorTick);
}
public void setAxisFlags(int minAxisFlag, int maxAxisFlag) {
this.minAxisFlag = minAxisFlag;
this.maxAxisFlag = maxAxisFlag;
isCalibrated = false;
}
public void setPrefNumTicks(int prefMajorTickCount, int prefMinorTickCount) {
this.prefMajorTickCount = prefMajorTickCount;
this.prefMinorTickCount = prefMinorTickCount;
isCalibrated = false;
}
public void setIsDiscrete(boolean isDiscrete) {
this.isDiscrete = isDiscrete;
isCalibrated = false;
}
public void setLabelFirst(boolean labelFirst) {
this.labelFirst = labelFirst;
}
public void setLabelLast(boolean labelLast) {
this.labelLast = labelLast;
}
public void setSignficantFigures(int sf) {
this.formatter.setSignificantFigures(sf);
}
public String format(double value) {
return formatter.format(value);
}
public boolean getIsDiscrete() {
return this.isDiscrete;
}
public boolean getLabelFirst() {
return getMinorTickCount(-1) != 0 && labelFirst;
}
public boolean getLabelLast() {
return getMinorTickCount(majorTickCount - 1) != 0 && labelLast;
}
public void setManualRange(double minValue, double maxValue) {
if (!Double.isInfinite(minValue) && !Double.isNaN(minValue)) {
this.minValue = minValue;
}
if (!Double.isInfinite(minValue) && !Double.isNaN(minValue)) {
this.maxValue = maxValue;
}
isCalibrated = false;
}
public void setManualAxis(double minTick, double maxTick,
double majorTick, double minorTick) {
this.minTick = minTick;
this.maxTick = maxTick;
this.majorTick = majorTick;
this.minorTick = minorTick;
majorTickCount = (int)((maxTick-minTick)/majorTick)+1; // Add 1 to include the last tick
minorTickCount = (int)(majorTick/minorTick)-1;	// Sub 1 to exclude the major tick
isAutomatic=false;
isCalibrated = false;
}
public void setAutomatic() {
setAutomatic(AT_MAJOR_TICK, AT_MAJOR_TICK);
}
public void setAutomatic(int minAxisFlag, int maxAxisFlag) {
setAxisFlags(minAxisFlag, maxAxisFlag);
isAutomatic = true;
isCalibrated = false;
}
public void setRange(double minValue, double maxValue) {
if (!Double.isNaN(minValue)) {
this.minData = minValue;
}
if (!Double.isNaN(maxValue)) {
this.maxData = maxValue;
}
isCalibrated = false;
}
public void addRange(double minValue, double maxValue) {
if (!Double.isNaN(minValue) && maxValue > maxData) {
maxData = maxValue;
}
if (!Double.isNaN(maxValue) && minValue < minData) {
minData = minValue;
}
//System.err.println("addRange("+minValue +", "+maxValue+")");
//System.err.println("maxValue = "+maxData);
//System.err.println("maxData = "+maxData);
isCalibrated = false;
}
static public double log10(double inValue) {
return Math.log(inValue)/Math.log(10.0);
}
static private final int UNIT=0;
static private final int HALFS=1;
static private final int QUARTERS=2;
static private final int FIFTHS=3;
public void calibrate() {
double minValue = minData;
double maxValue = maxData;
if( Double.isInfinite(minValue) || Double.isNaN(minValue) ||
Double.isInfinite(maxValue) || Double.isNaN(maxValue)) {
// I am not sure which exception is appropriate here.
throw new ChartRuntimeException("Illegal range values, can't calibrate");
}
if (minAxisFlag==AT_ZERO ) {
minValue = 0;
} else if (minAxisFlag == AT_VALUE) {
minValue = this.minValue;
}
if (maxAxisFlag==AT_ZERO) {
maxValue = 0;
} else if (maxAxisFlag == AT_VALUE) {
maxValue = this.maxValue;
}
double range = maxValue - minValue;
if (range < 0.0) {
range = 0.0;
}
epsilon = range * 1.0E-10;
if (isAutomatic) {
// We must find the optimum minMajorTick and maxMajorTick so
// that they contain the data range (minData to maxData) and
// are in the right order of magnitude
if (range < 1.0E-30) {
if (minData < 0.0) {
majorTick = Math.pow(10.0, Math.floor(log10(Math.abs(minData))));
minTick = Math.floor(minData / majorTick) * majorTick;
maxTick = 0.0;
} else if (minData > 0.0) {
majorTick = Math.pow(10.0, Math.floor(log10(Math.abs(minData))));
minTick = 0.0;
maxTick = Math.ceil(maxData / majorTick) * majorTick;
} else {
minTick = -1.0;
maxTick = 1.0;
majorTick = 1.0;
}
minorTick = majorTick;
majorTickCount = 1;
minorTickCount = 0;
} else {
// First find order of magnitude below the data range...
majorTick = Math.pow(10.0, Math.floor(log10(range)));
calcMinTick();
calcMaxTick();
calcMajorTick();
calcMinorTick();
}
}
minAxis = minTick;
maxAxis = maxTick;
handleAxisFlags();
isCalibrated=true;
}
public void calcMinTick() {
// Find the nearest multiple of majorTick below minData
if (minData == 0.0)
minTick = 0;
else
minTick = Math.floor(minData /  majorTick) * majorTick;
}
public void calcMaxTick() {
// Find the nearest multiple of majorTick above maxData
if (maxData == 0) {
maxTick = 0;
} else if (maxData < 0.0) {
// Added so that negative values are handled correctly -- AJD
maxTick = -Math.floor(-maxData / majorTick) * majorTick;
} else {
maxTick = Math.ceil(maxData / majorTick) * majorTick;
}
}
public void calcMajorTick() {
fraction=UNIT;
// make sure that there are at least prefNumMajorTicks major ticks
// by dividing up into halves, quarters, fifths or tenths
double u=majorTick;
double r=maxTick-minTick;
majorTickCount=(int)(r/u);
while (majorTickCount < prefMajorTickCount) {
u=majorTick/2;	// Try using halves
if (!isDiscrete || u==Math.floor(u)) { // u is an integer
majorTickCount=(int)(r/u);
fraction=HALFS;
if (majorTickCount >= prefMajorTickCount)
break;
}
u=majorTick/4;	// Try using quarters
if (!isDiscrete || u==Math.floor(u)) { // u is an integer
majorTickCount=(int)(r/u);
fraction=QUARTERS;
if (majorTickCount >= prefMajorTickCount)
break;
}
u=majorTick/5;	// Try using fifths
if (!isDiscrete || u==Math.floor(u)) { // u is an integer
majorTickCount=(int)(r/u);
fraction=FIFTHS;
if (majorTickCount >= prefMajorTickCount)
break;
}
if (isDiscrete && (majorTick/10)!=Math.floor(majorTick/10)) {
// majorTick/10 is not an integer so no point in further subdivision
u=majorTick;
majorTickCount=(int)(r/u);
break;
}
majorTick/=10;	// finally just divide by ten
u=majorTick;	// and go back to whole units
majorTickCount=(int)(r/u);
fraction=UNIT;
}
majorTick=u;
if (isDiscrete && majorTick<1.0) {
majorTick=1.0;
majorTickCount=(int)(r/majorTick);
fraction=UNIT;
}
majorTickCount++;	// Add 1 to give the final tick
// Trim down any excess major ticks either side of the data range
// Epsilon allows for any inprecision in the calculation
while ((minTick + majorTick - epsilon)<minData) {
minTick+=majorTick;
majorTickCount--;
}
while ((maxTick - majorTick + epsilon)>maxData) {
maxTick-=majorTick;
majorTickCount--;
}
}
public void calcMinorTick() {
minorTick=majorTick; // start with minorTick the same as majorTick
double u=minorTick;
double r=majorTick;
minorTickCount=(int)(r/u);
while (minorTickCount < prefMinorTickCount) {
// if the majorTick was divided as quarters, then we can't
// divide the minor ticks into halves or quarters.
if (fraction!=QUARTERS) {
u=minorTick/2;	// Try using halves
if (!isDiscrete || u==Math.floor(u)) { // u is an integer
minorTickCount=(int)(r/u);
if (minorTickCount>=prefMinorTickCount)
break;
}
u=minorTick/4;	// Try using quarters
if (!isDiscrete || u==Math.floor(u)) { // u is an integer
minorTickCount=(int)(r/u);
if (minorTickCount>=prefMinorTickCount)
break;
}
}
u=minorTick/5;	// Try using fifths
if (!isDiscrete || u==Math.floor(u)) { // u is an integer
minorTickCount=(int)(r/u);
if (minorTickCount>=prefMinorTickCount)
break;
}
if (isDiscrete && (minorTick/10)!=Math.floor(minorTick/10)) {
// minorTick/10 is not an integer so no point in further subdivision
u=minorTick;
minorTickCount=(int)(r/u);
break;
}
minorTick/=10;	// finally just divide by ten
u=minorTick;	// and go back to whole units
minorTickCount=(int)(r/u);
}
minorTick=u;
minorTickCount--;
}
public void handleAxisFlags() {
// Now we must honor the min/maxAxisFlag settings
if (minAxisFlag==AT_MAJOR_TICK_PLUS || minAxisFlag==AT_MINOR_TICK_PLUS) {
if (minAxis==minData) {
majorTickCount++;
//                    minTick-=majorTick;
minAxis=minTick;
}
}
if (minAxisFlag==AT_MINOR_TICK_PLUS) {
if ((minAxis+minorTick)<minData) {
majorTickCount--;
minTick+=majorTick;
while ((minAxis+minorTick)<minData) {
minAxis+=minorTick;
}
}
} else if (minAxisFlag==AT_MINOR_TICK) {
if ((minAxis+minorTick)<=minData) {
majorTickCount--;
minTick+=majorTick;
while ((minAxis+minorTick)<=minData) {
minAxis+=minorTick;
}
}
} else if (minAxisFlag==AT_DATA) {
if (minTick<minData) { // in case minTick==minData
majorTickCount--;
minTick+=majorTick;
}
minAxis=minData;
} else if (minAxisFlag==AT_VALUE) {
if (minTick<minValue) { // in case minTick==minValue
majorTickCount--;
minTick+=majorTick;
}
minAxis=minValue;
} else if (minAxisFlag==AT_ZERO) {
majorTickCount+=(int)(minTick/majorTick);
minTick=0;
minAxis=0;
}
if (maxAxisFlag==AT_MAJOR_TICK_PLUS || maxAxisFlag==AT_MINOR_TICK_PLUS) {
if (maxAxis==maxData) {
majorTickCount++;
maxTick+=majorTick;
maxAxis=maxTick;
}
}
if (maxAxisFlag==AT_MINOR_TICK_PLUS) {
if ((maxAxis-minorTick)>maxData) {
majorTickCount--;
maxTick-=majorTick;
while ((maxAxis-minorTick)>maxData) {
maxAxis-=minorTick;
}
}
} else if (maxAxisFlag==AT_MINOR_TICK) {
if ((maxAxis-minorTick)>=maxData) {
majorTickCount--;
maxTick-=majorTick;
while ((maxAxis-minorTick)>=maxData) {
maxAxis-=minorTick;
}
}
} else if (maxAxisFlag==AT_DATA) {
if (maxTick>maxData) { // in case maxTick==maxData
majorTickCount--;
maxTick-=majorTick;
}
maxAxis=maxData;
} else if (maxAxisFlag==AT_VALUE) {
if (maxTick>maxValue) { // in case maxTick==maxValue
majorTickCount--;
maxTick-=majorTick;
}
maxAxis=maxValue;
} else if (maxAxisFlag==AT_ZERO) {
majorTickCount+=(int)(-maxTick/majorTick);
maxTick=0;
maxTick=0;
}
}
public double scaleValue(double value) {
if (!isCalibrated)
calibrate();
final double ta = transform(minAxis);
return (transform(value)- ta)/(transform(maxAxis)- ta);
}
public double getMinAxis() {
if (!isCalibrated)
calibrate();
return minAxis;
}
public double getMaxAxis() {
if (!isCalibrated)
calibrate();
return maxAxis;
}
public double getMinData() { return minData; }
public double getMaxData() { return maxData; }
public int getMajorTickCount() {
if (!isCalibrated)
calibrate();
return majorTickCount;
}
public int getMinorTickCount(int majorTickIndex) {
if (!isCalibrated)
calibrate();
if (majorTickIndex == majorTickCount-1)
return (int)((maxAxis-maxTick)/minorTick);
else if (majorTickIndex==-1)
return (int)((minTick-minAxis)/minorTick);
else
return minorTickCount;
}
public double getMajorTickValue(int majorTickIndex) {
if (!isCalibrated)
calibrate();
return (majorTickIndex*majorTick)+minTick;
}
public double getMinorTickValue(int minorTickIndex, int majorTickIndex) {
if (!isCalibrated)
calibrate();
// get minorTickIndex+1 to skip the major tick
if (majorTickIndex==-1)
return minTick-((minorTickIndex+1)*minorTick);
else
return ((minorTickIndex+1)*minorTick)+getMajorTickValue(majorTickIndex);
}
public double getMajorTickSpacing() {
if (!isCalibrated)
calibrate();
return majorTick;
}
public double getMinorTickSpacing() {
if (!isCalibrated)
calibrate();
return minorTick;
}
}
}
