package dr.evomodel.sitemodel;
import java.util.List;
public class PairDistance implements Comparable<PairDistance> {
int x, y;
double distance;
List<Integer> gaps = null;
int alignmentLength;
public PairDistance(int x, int y, double distance, List<Integer> gaps, int alignmentLength) {
this.x = x;
this.y = y;
this.distance = distance;
this.gaps = gaps;
this.alignmentLength = alignmentLength;
}
public int compareTo(PairDistance p) {
if (p.distance > distance) return -1;
if (p.distance < distance) return 1;
return 0;
}
}