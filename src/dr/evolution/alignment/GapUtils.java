
package dr.evolution.alignment;

import java.util.List;

public class GapUtils {

    public static void getGapSizes(Alignment alignment, List gaps) {

        int stateCount = alignment.getDataType().getStateCount();
        int gapStart = Integer.MAX_VALUE;
        for (int i = 0; i < alignment.getSiteCount(); i++) {
            int x = alignment.getState(0,i);
            int y = alignment.getState(1,i);

            if (y < stateCount && x < stateCount) {
                // no gap
                if (gapStart < i) {
                    gaps.add(new Integer(i-gapStart));
                    gapStart = Integer.MAX_VALUE;
                }
            } else {
                // gap
                if (gapStart > i) gapStart = i;
            }
        }
    }
}
