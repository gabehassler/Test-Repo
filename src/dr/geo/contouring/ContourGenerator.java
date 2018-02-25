package dr.geo.contouring;
import dr.geo.contouring.ContourAttrib;
import java.util.*;
public class ContourGenerator {
//	Debug flag.
private static final boolean DEBUG = false;
//	Error messages.
private static final String kCancelMsg = "Method ContourGenerator.getContours() canceled by user.";
private static final String kInconsistantArrMsg = "Inconsistant array sizes.";
private static final String kArrSizeMsg = "Data arrays must have more than one row or column.";
private static final String kNegLogDataMsg = "Function data must be > 0 for logarithmic intervals.";
//	Path buffer size.
private static final int kBufSize = 1000;
//	The minimum number of points allowed in a contour path.
private static final int kMinNumPoints = 3;
//	A list of contour paths.
private List pathList = new ArrayList();
//	A flag to indicate that the contours have been computed or not.
private boolean cCalculated = false;
//	Data arrays used for generating the contours.
private double[][] xArray, yArray, funcArray;
//	Data arrays used when generating contours for 1D X & Y arrays.
private double[] xArr1D, yArr1D;
//	Array of contour attributes, one for each contour level.
private ContourAttrib[] cAttr;
//	The fraction of the task that is completed.
private float fracComplete = 0;
private boolean isCanceled = false;
//	Variables in the original FORTRAN program.
private double[] pathbufxt, pathbufyt;
private int[] pathbufia;
private int lnstrt;				//	lnstrt=1 indicates starting a new line.
private int ignext;
private int icont;				//	Current contour level index.
private double cont;			//	The current contour level.
private int iss, iee, jss, jee;	//	i & j start and end index values.
private int ima;				//	ima tells which boundary region we are on.
private int iae;				//	Index to last element in the IA list.
private int ibeg, jbeg;
private int gi, gj;				//	Indexes into data arrays.
private double fij;				//	Data value at i,j in data array.
private int idir;				//	Indicates current direction.
private int np=0;				//	Number of points in current contour line.
private double wx=0, wy=0;		//	Starting point of a contour line.
public ContourGenerator(double[][] xArr, double[][] yArr, double[][] fArr, ContourAttrib[] cAttr) {
//	Make sure input data is reasonable.
if (yArr.length != xArr.length || yArr.length != fArr.length)
throw new IllegalArgumentException(kInconsistantArrMsg);
if (yArr[0].length != xArr[0].length || yArr[0].length != fArr[0].length)
throw new IllegalArgumentException(kInconsistantArrMsg);
if (xArr.length <= 1 || xArr[0].length <= 1)
throw new IllegalArgumentException(kArrSizeMsg);
this.cAttr = cAttr;
xArray = xArr;
yArray = yArr;
funcArray = fArr;
}
public ContourGenerator(double[] xArr, double[] yArr, double[][] fArr, ContourAttrib[] cAttr) {
//	Make sure input data is reasonable.
if (yArr.length != fArr.length || xArr.length != fArr[0].length)
throw new IllegalArgumentException(kInconsistantArrMsg);
if (xArr.length <= 1)
throw new IllegalArgumentException(kArrSizeMsg);
this.cAttr = cAttr;
xArr1D = xArr;
yArr1D = yArr;
funcArray = fArr;
}
public ContourGenerator(double[][] xArr, double[][] yArr, double[][] fArr,
int nc, boolean logInterval) {
//	Make sure input data is reasonable.
if (yArr.length != xArr.length || yArr.length != fArr.length)
throw new IllegalArgumentException(kInconsistantArrMsg);
if (yArr[0].length != xArr[0].length || yArr[0].length != fArr[0].length)
throw new IllegalArgumentException(kInconsistantArrMsg);
if (xArr.length <= 1 || xArr[0].length <= 1)
throw new IllegalArgumentException(kArrSizeMsg);
xArray = xArr;
yArray = yArr;
funcArray = fArr;
if (logInterval)
findLogIntervals(nc);
else
findLinearIntervals(nc);
}
public ContourGenerator(double[] xArr, double[] yArr, double[][] fArr,
int nc, boolean logInterval) {
//	Make sure input data is reasonable.
if (yArr.length != fArr.length || xArr.length != fArr[0].length)
throw new IllegalArgumentException(kInconsistantArrMsg);
if (xArr.length <= 1)
throw new IllegalArgumentException(kArrSizeMsg);
xArr1D = xArr;
yArr1D = yArr;
funcArray = fArr;
if (logInterval)
findLogIntervals(nc);
else
findLinearIntervals(nc);
}
public ContourPath[] getContours() throws InterruptedException {
if (!cCalculated) {
isCanceled = false;
pathList.clear();
//	Go off an compute the contour paths.
computeContours();
//	Now turn loose all our data arrays to be garbage collected.
cAttr = null;
xArray = yArray = funcArray = null;
xArr1D = yArr1D = null;
//	Set our "done" flags.
cCalculated = true;
fracComplete = 1;
}
//	Turn our pathList into an array and return the array.
int size = pathList.size();
ContourPath[] arr = new ContourPath[size];
for (int i=0; i < size; ++i)
arr[i] = (ContourPath)pathList.get(i);
return arr;
}
public boolean done() {
return cCalculated;
}
public void cancel() {
isCanceled = true;
}
public float getProgress() {
return fracComplete;
}
private void findLinearIntervals(int nc) {
//	Find min and max Z values.
double zMin = Double.MAX_VALUE;
double zMax = -zMin;
int ni = funcArray.length;
for (int i=0; i < ni; ++i) {
int nj = funcArray[i].length;
for (int j=0; j < nj; ++j) {
double zVal = funcArray[i][j];
zMin = Math.min(zMin, zVal);
zMax = Math.max(zMax, zVal);
}
}
//	Allocate memory for contour attribute array.
cAttr = new ContourAttrib[nc];
//	Determine contour levels.
double delta = (zMax-zMin)/(nc+1);
for (int i=0; i < nc; i++) {
cAttr[i] = new ContourAttrib( zMin + (i+1)*delta );
if (DEBUG)
System.out.println("level[" + i + "] = " + (zMin + (i+1)*delta));
}
}
private void findLogIntervals(int nc) {
//	Find min and max Z values.
double zMin = Double.MAX_VALUE;
double zMax = -zMin;
int ni = funcArray.length;
for (int i=0; i < ni; ++i) {
int nj = funcArray[i].length;
for (int j=0; j < nj; ++j) {
double zVal = funcArray[i][j];
zMin = Math.min(zMin, zVal);
zMax = Math.max(zMax, zVal);
}
}
if (zMin < 0)
throw new IllegalArgumentException(kNegLogDataMsg);
//	Allocate memory for contour attribute array.
cAttr = new ContourAttrib[nc];
//	Determine contour levels.
double temp = Math.log(zMin);
double delta = (Math.log(zMax) - temp)/(nc+1);
for (int i=0; i < nc; i++)
cAttr[i] = new ContourAttrib( Math.exp(temp + (i+1)*delta) );
}
private void computeContours() throws InterruptedException {
int ncont = cAttr.length;		//	Number of contour levels.
//	Find the number of data points in "I" and "J" directions.
int nx=0, ny=0;
if (xArray != null) {
ny = xArray.length;
nx = xArray[0].length;
} else {
nx = xArr1D.length;
ny = yArr1D.length;
}
//	Allocate temporary storage space for path buffers.
pathbufxt = new double[kBufSize];
pathbufyt = new double[kBufSize];
pathbufia = new int[kBufSize*3];
//	lnstrt=1 (line start) means we're starting a new line.
lnstrt = 1;
ignext = 0;
//	Loop through each contour level.
for (icont = 0; icont < ncont; ++icont) {
//	Check to see if the user has canceled.
if (isCanceled)
throw new InterruptedException(kCancelMsg);
//	Begin working on this contour level.
cont = cAttr[icont].getLevel();
iss = 1;
iee = nx;
jss = 1;
jee = ny;
boolean subDivFlg = false;
//	Find where function increases through the contour level.
FlagContourPassings();
boolean L10flg = false;
if (!L10flg) {
ima = 1;
ibeg = iss - 1;
jbeg = jss;
}
do {
if (!L10flg) {
boolean imb = false;
boolean doneFlg = false;
do {
switch(ima) {
case 1:
++ibeg;
if (ibeg == iee)
ima = 2;
break;
case 2:
++jbeg;
if (jbeg == jee)
ima = 3;
break;
case 3:
--ibeg;
if (ibeg == iss)
ima = 4;
break;
case 4:
--jbeg;
if (jbeg == jss)
ima = 5;
break;
case 5:
continue imaLoop;
}
if (funcArray[jbeg -1][ibeg -1] <= cont) {
imb = true;
doneFlg = false;
} else if (imb == true)
doneFlg = true;
} while (!doneFlg);
//	Got a start point.
gi = ibeg;							//	x index of starting point.
gj = jbeg;							//	y index of starting point.
fij = funcArray[jbeg -1][ibeg -1];	//	z value of starting point.
//	Round the corner if necessary.
switch (ima) {
case 1:
Routine_L21();
break;
case 2:
if (gj != jss) {
if (!Routine_L31())
Routine_L21();
} else
Routine_L21();
break;
case 3:
if (gi != iee) {
if (!Routine_L41())
Routine_L21();
} else {
if (!Routine_L31())
Routine_L21();
}
break;
case 4:
if (gj != jee) {
if (!Routine_L51())
Routine_L21();
} else {
if (!Routine_L41())
Routine_L21();
}
break;
case 5:
if (!Routine_L51())
Routine_L21();
break;
}
}	//	end if(!L10flg)
//	This is the end of a contour line.  After this, we'll start a
//	new line.
L10flg = false;
ignext = 0;
accumContour(np, icont, pathbufxt, pathbufyt, cAttr[icont]);
//	If we're not done looking along the boundaries,
//	go look there some more.
} while (ima != 5);
//	Otherwise, get the next start out of IA.
int ntmp3 = iae;
for (int iia = 1; iia <= ntmp3; ++iia) {
if (pathbufia[iia -1] != 0) {
//	This is how we start in the middle of the region, using IA.
gi = pathbufia[iia - 1]/1000;
gj = pathbufia[iia - 1] - gi*1000;
fij = funcArray[gj -1][gi -1];
pathbufia[iia - 1] = 0;
Routine_L21();
L10flg = true;
break;
}
}
}
} while ( L10flg );
subDivFlg = false;
if (iee == nx) {
if (jee != ny) {
jss = jee;
jee = ny;
subDivFlg = true;
}
} else {
iss = iee;
iee = nx;
subDivFlg = true;
}
} while (subDivFlg);
//	Update progress information.
fracComplete = (float)(icont+1)/(float)(ncont);
//	Loop back for the next contour level.
}	// Next icont
//	Turn loose temporary arrays used to generate contours.
pathbufxt = null;
pathbufyt = null;
pathbufia = null;
}
private void FlagContourPassings() {
iae = 0;
int ntmp2 = jee - 1;
for (int j=jss + 1; j <= ntmp2; ++j) {
boolean imb = false;
int iaend = iae;
int ntmp3 = iee;
for (int i=iss; i <= ntmp3; ++i) {
if (funcArray[j -1][i -1] <= cont)
imb = true;
else if (imb == true) {
++iae;
pathbufia[iae - 1] = i*1000 + j;
imb = false;
if (iae == kBufSize*3) {
if (j > jss + 1) {
iae = iaend;
jee = j;
} else {
//	Compute minimum.
jee = Math.min(j+1, jee);
iee = i;
}
//	Break out of i & j loops.
return;
}
}
}	//	Next i
}	//	Next j
}
private void Routine_L21() {
while (true) {
--gi;
if (gi < iss)
return;						//	Goto L90.
idir = 1;
if (funcArray[gj -1][gi -1] <= cont) {
//	Wipe this point out of IA if it's in the list.
int ij = gi*1000 + gj + 1000;
int ntmp3 = iae;
for (int iia = 1; iia <= ntmp3; ++iia) {
if (pathbufia[iia - 1] == ij) {
pathbufia[iia - 1] = 0;
break;
}
}
}
doInterpolation();
return;						//	Goto L90.
}
fij = funcArray[gj -1][gi -1];
if (Routine_L31())	return;		//	Goto L90
}
}
private boolean Routine_L31() {
--gj;
if (gj < jss)
return true;
idir = 2;
if (funcArray[gj -1][gi -1] <= cont) {
doInterpolation();
return true;
}
fij = funcArray[gj -1][gi -1];
return (Routine_L41());
}
private boolean Routine_L41() {
++gi;
if (gi > iee)
return true;
idir = 3;
if (funcArray[gj -1][gi -1] <= cont) {
doInterpolation();
return true;
}
fij = funcArray[gj -1][gi -1];
return (Routine_L51());
}
private boolean Routine_L51() {
++gj;
idir = 4;
if (gj > jee)
return true;
if (funcArray[gj -1][gi -1] <= cont) {
doInterpolation();
return true;
}
fij = funcArray[gj -1][gi -1];
return false;
}
private void doInterpolation() {
//	Do interpolation for X,Y coordinates.
double func = funcArray[gj -1][gi -1];
double xyf = (cont - func)/(fij - func);
if (xyf == 0)
++ignext;
double wxx=0, wyy=0;
double xVal=0, yVal=0;
if (xArray != null) {
//	We have 2D arrays for the X & Y grid points.
xVal = xArray[gj -1][gi -1];
yVal = yArray[gj -1][gi -1];
switch (idir) {
case 1:				//	East
wxx = xVal + xyf*(xArray[gj -1][gi + 1 -1] - xVal);
wyy = yVal + xyf*(yArray[gj -1][gi + 1 -1] - yVal);
break;
case 2:				//	North
wxx = xVal + xyf*(xArray[gj + 1 -1][gi -1] - xVal);
wyy = yVal + xyf*(yArray[gj + 1 -1][gi -1] - yVal);
break;
case 3:				//	West
wxx = xVal + xyf*(xArray[gj -1][gi - 1 -1] - xVal);
wyy = yVal + xyf*(yArray[gj -1][gi - 1 -1] - yVal);
break;
case 4:				//	South
wxx = xVal + xyf*(xArray[gj - 1 -1][gi -1] - xVal);
wyy = yVal + xyf*(yArray[gj - 1 -1][gi -1] - yVal);
break;
}
} else {
//	We have 1D arrays for the X & Y grid points.
xVal = xArr1D[gi -1];
yVal = yArr1D[gj -1];
switch (idir) {
case 1:				//	East
wxx = xVal + xyf*(xArr1D[gi + 1 -1] - xVal);
wyy = yVal;
break;
case 2:				//	North
wxx = xVal;
wyy = yVal + xyf*(yArr1D[gj + 1 -1] - yVal);
break;
case 3:				//	West
wxx = xVal + xyf*(xArr1D[gi - 1 -1] - xVal);
wyy = yVal;
break;
case 4:				//	South
wxx = xVal;
wyy = yVal + xyf*(yArr1D[gj - 1 -1] - yVal);
break;
}
}
if (DEBUG) {
System.out.println("i, j = " + gi + "," + gj);
System.out.println("cont = " + (float)cont + ",  fij = " + (float)fij +
",  func = " + (float)func + ",  xyf = " + (float)xyf);
System.out.println("xVal = " + (float)xVal + ",  yVal = " + (float)yVal);
System.out.println("wxx = " + (float)wxx + ",  wyy = " + (float)wyy);
}
//	Figure out what to do with this point.
if (lnstrt == 1) {
//	This is the 1st point in the contour line.
np = 1;
pathbufxt[np -1] = wxx;
pathbufyt[np -1] = wyy;
//	Save starting point as wx, wy.
wx = wxx;
wy = wyy;
//	Clear the first point flag, we've got one now.
lnstrt = 0;
} else {
boolean skipFlg = false;
//	Second point and after comes here.
//	Add a point to this line.  Check for duplicate point first.
if (ignext == 2) {
if (wxx == pathbufxt[np -1] && wyy == pathbufyt[np -1]) {
ignext = 0;
skipFlg = true;
} else
ignext = 1;
}
if (!skipFlg) {
//	Increment # of points in contour.
++np;
pathbufxt[np -1] = wxx;
pathbufyt[np -1] = wyy;
//	See if the temporary array xt, yt are full.
if (np == kBufSize) {
accumContour(np, icont, pathbufxt, pathbufyt, cAttr[icont]);
//	Last point becomes 1st point to continue.
pathbufxt[0] = pathbufxt[np -1];
pathbufyt[0] = pathbufyt[np -1];
np =1;
}
//	Check to see if we're back to the intial point.
if (wxx == wx && wyy == wy)
return;
}
}
//	Search for the next point on this line.
case 1:
++gi;
if (!Routine_L51())
Routine_L21();
break;
case 2:
++gj;
Routine_L21();
break;
case 3:
--gi;
if (!Routine_L31())
Routine_L21();
break;
case 4:
--gj;
if (!Routine_L41())
Routine_L21();
break;
}
return;
}
private void accumContour(int np, int icont, double[] x, double[] y, ContourAttrib cAttr) {
//	To few points for a contour line.
if (np < kMinNumPoints)	return;
//	Copy over coordinate points from buffers to their own arrays.
double[] xArr = new double[np];
double[] yArr = new double[np];
System.arraycopy(x, 0, xArr, 0, np);
System.arraycopy(y, 0, yArr, 0, np);
//	Create a new contour path and add it to the list.
ContourPath path = new ContourPath(cAttr, icont, xArr, yArr);
pathList.add(path);
}
}
