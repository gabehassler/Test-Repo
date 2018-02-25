package dr.math;
public abstract class MultivariateMinimum
{
//
// Public stuff
//
public int numFun;
public int maxFun = 0;
public int numFuncStops = 4;
public double findMinimum(MultivariateFunction f, double[] xvec)
{
optimize(f, xvec, MachineAccuracy.EPSILON, MachineAccuracy.EPSILON);
return f.evaluate(xvec);
}
public double findMinimum(MultivariateFunction f, double[] xvec,
int fxFracDigits, int xFracDigits)
{
return findMinimum(f,xvec,fxFracDigits,xFracDigits,null);
}
public double findMinimum(MultivariateFunction f, double[] xvec,
int fxFracDigits, int xFracDigits, MinimiserMonitor monitor)
{
double tolfx = Math.pow(10, -1-fxFracDigits);
double tolx = Math.pow(10, -1-xFracDigits);
optimize(f, xvec, tolfx, tolx,monitor);
// trim x
double m = Math.pow(10, xFracDigits);
for (int i = 0;  i < xvec.length; i++)
{
xvec[i] = Math.round(xvec[i]*m)/m;
}
// trim fx
return Math.round(f.evaluate(xvec)*m)/m;
}
public abstract void optimize(MultivariateFunction f, double[] xvec, double tolfx, double tolx);
public void optimize(MultivariateFunction f, double[] xvec, double tolfx, double tolx, MinimiserMonitor monitor) {
optimize(f,xvec,tolfx,tolx);
}
public boolean stopCondition(double fx, double[] x, double tolfx,
double tolx, boolean firstCall)
{
boolean stop = false;
if (firstCall)
{
countFuncStops = 0;
fxold = fx;
xold = new double[x.length];
copy(xold, x);
}
else
{
if (xStop(x, xold, tolx))
{
stop = true;
}
else
{
if (fxStop(fx, fxold, tolfx))
{
countFuncStops++;
}
else
{
countFuncStops = 0;
}
if (countFuncStops >= numFuncStops)
{
stop = true;
}
}
}
if (!stop)
{
fxold = fx;
copy(xold, x);
}
return stop;
}
public void copy(double[] target, double[] source)
{
for (int i = 0; i < source.length; i++)
{
target[i] = source[i];
}
}
//
// Private stuff
//
// number of fStops
private int countFuncStops;
// old function and parameter values
private double fxold;
private double[] xold;
private boolean xStop(double[] x, double[] xold, double tolx)
{
boolean stop = true;
for (int i = 0; i < x.length && stop == true; i++)
{
if (Math.abs(x[i]-xold[i]) > tolx)
{
stop = false;
}
}
return stop;
}
private boolean fxStop(double fx, double fxold, double tolfx)
{
if (Math.abs(fx-fxold) > tolfx)
{
return false;
}
else
{
return true;
}
}
}
