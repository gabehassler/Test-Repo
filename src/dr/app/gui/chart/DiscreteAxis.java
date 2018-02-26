
package dr.app.gui.chart;



public class DiscreteAxis extends Axis.AbstractAxis {

	private boolean originBetweenCategories;
	private boolean showEveryCategory;

	public DiscreteAxis(boolean originBetweenCategories, boolean showEveryCategory) {
		super(AT_MAJOR_TICK, AT_MAJOR_TICK, true);

		this.originBetweenCategories = originBetweenCategories;
		this.showEveryCategory = showEveryCategory;
	}

	public double transform(double value) {
		return value;	// a linear transform !
	}

	public double untransform(double value) {
		return value;	// a linear transform !
	}

	public void calibrate() {
		majorTick=1;
		minorTick=1;

		minTick=minData;
		maxTick=maxData;
		majorTickCount = (int)((maxTick-minTick)/majorTick)+1;
		minorTickCount = 0;

		if (!showEveryCategory) {
			while (majorTickCount > prefMajorTickCount) {
				majorTickCount=(int)((maxTick-minTick)/(majorTick*2))+1;
				if (majorTickCount > prefMajorTickCount) {
					majorTick*=2;
					break;
				}
				majorTickCount=(int)((maxTick-minTick)/(majorTick*4))+1;
				if (majorTickCount > prefMajorTickCount) {
					majorTick*=4;
					break;
				}
				majorTickCount=(int)((maxTick-minTick)/(majorTick*5))+1;
				if (majorTickCount > prefMajorTickCount) {
					majorTick*=5;
					break;
				}

				majorTick*=10;
				majorTickCount=(int)((maxTick-minTick)/majorTick)+1;
			}
		}

		minorTickCount=(int)(majorTick)-1;

		minAxis=minTick;
		maxAxis=maxTick;

		handleAxisFlags();

		if (originBetweenCategories) {
			minAxis-=0.5;
			maxAxis+=0.5;
		}
	}
}
