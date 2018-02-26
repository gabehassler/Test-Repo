
package dr.app.gui.chart;

public class LogAxis extends Axis.AbstractAxis {

//	private int extraMinorTickCount;


	public LogAxis() {
		super();
        setSignficantFigures(0);
    }

	public LogAxis(int minAxisFlag, int maxAxisFlag) {

		setAxisFlags(minAxisFlag, maxAxisFlag);
	}

	public void setAxisFlags(int minAxisFlag, int maxAxisFlag) {

		if (minAxisFlag==AT_DATA || minAxisFlag==AT_ZERO) {
			minAxisFlag = this.minAxisFlag;
		}

		if (maxAxisFlag==AT_DATA || maxAxisFlag==AT_ZERO) {
			maxAxisFlag = this.maxAxisFlag;
		}

		super.setAxisFlags(minAxisFlag, maxAxisFlag);
	}

	public void setRange(double minValue, double maxValue) {

		if (maxValue <= 0.0)
			maxValue = 1.0E-100;

		if (minValue <= 0.0)
			minValue = maxValue;
		super.setRange(minValue, maxValue);

	}

	public void addRange(double minValue, double maxValue) {

		if (maxValue <= 0.0)
			maxValue = 1.0E-100;

		if (minValue <= 0.0)
			minValue = maxValue;
 		super.addRange(minValue, maxValue);

	}

	public void calcMinTick() {
		minTick=1;
		// Find the nearest multiple of majorTick below minData
		if (minData>1) { // work upwards
			while ((minTick*10)<minData)
				minTick*=10;
		} else if (minData<1) { // work downwards
			while (minTick>minData)
				minTick/=10;
		}
	}

	public void calcMaxTick() {
		maxTick=1;
		// Find the nearest multiple of majorTick above maxData
		if (maxData>1) { // work upwards
			while (maxTick<maxData)
				maxTick*=10;
		} else if (maxData<1) { // work downwards
			while ((maxTick/10)>maxData)
				maxTick/=10;
		}
	}

	public void calcMajorTick() {
		majorTick=10;
		majorTickCount=(int)Math.round(log10(maxTick/minTick))+1;
	}

	public void calcMinorTick() {
		minorTick=1;
		minorTickCount=8; // 1 and 10 are major ticks
	}

	public void handleAxisFlags() {
		if (minAxisFlag==AT_MINOR_TICK) {
			if (minAxis+minTick<=minData) {
				while ((minAxis+minTick)<=minData) {
					minAxis+=minTick;
				}
				majorTickCount--;
				minTick*=10;
			}
		}

		if (maxAxisFlag==AT_MINOR_TICK) {
			if (maxAxis-(maxTick/10)>=maxData) {
				majorTickCount--;
				maxTick/=10;
				while ((maxAxis-maxTick)>=maxData) {
					maxAxis-=maxTick;
				}
			}
		}
	}

	public double transform(double value) {
		return log10(value);
	}

	public double untransform(double value) {
		return Math.pow(10.0, value);
	}

	public int getMinorTickCount(int majorTickIndex) {
		if (!isCalibrated)
			calibrate();

		if (majorTickIndex==majorTickCount-1)
			return (int)((maxAxis-maxTick)/maxTick);
		else if (majorTickIndex==-1) {
			return (int)((minTick-minAxis)/(minTick/10));
		} else
			return minorTickCount;
	}


	public double getMajorTickValue(int majorTickIndex) {
		if (!isCalibrated)
			calibrate();

		if (majorTickIndex==majorTickCount-1)
			return maxTick;
		else
			return Math.pow(10, majorTickIndex)*minTick;
	}

	public double getMinorTickValue(int minorTickIndex, int majorTickIndex) {
		if (!isCalibrated)
			calibrate();

		if (majorTickIndex==-1)
			return minTick-((minorTickIndex+1)*(minTick/10));
		else
			return (minorTickIndex+2)*getMajorTickValue(majorTickIndex);

		//	This last line is the equivalent of:
		//	return ((minorTickNo+1)*getMajorTick(majorTickNo))+getMajorTick(majorTickNo);
	}
}

