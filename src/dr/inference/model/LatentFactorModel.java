package dr.inference.model;
import dr.math.matrixAlgebra.Matrix;
import dr.util.Citable;
import dr.util.Citation;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Vector;
public class LatentFactorModel extends AbstractModelLikelihood implements Citable {
//    private Matrix data;
//    private Matrix factors;
//    private Matrix loadings;
private final MatrixParameter data;
private final MatrixParameter factors;
private final MatrixParameter loadings;
private MatrixParameter sData;
private final DiagonalMatrix rowPrecision;
private final DiagonalMatrix colPrecision;
private final Parameter continuous;
private final boolean scaleData;
private final int dimFactors;
private final int dimData;
private final int nTaxa;
private boolean newModel;
private boolean likelihoodKnown = false;
private boolean isDataScaled=false;
private boolean storedLikelihoodKnown;
private boolean residualKnown=false;
private boolean LxFKnown=false;
private boolean storedResidualKnown=false;
private boolean storedLxFKnown;
private boolean traceKnown=false;
private boolean storedTraceKnown;
private boolean logDetColKnown=false;
private boolean storedLogDetColKnown;
private double trace;
private double storedTrace;
private double logLikelihood;
private double storedLogLikelihood;
private double logDetCol;
private double storedLogDetCol;
private boolean[][] changed;
private boolean[][] storedChanged;
private boolean RecomputeResiduals=false;
private boolean RecomputeFactors=false;
private Vector<Integer> changedValues;
private boolean factorsKnown=false;
private boolean storedFactorsKnown=false;
private double[] residual;
private double[] LxF;
private double[] storedResidual;
private double[] storedLxF;
private double pathParameter=1.0;
public LatentFactorModel(MatrixParameter data, MatrixParameter factors, MatrixParameter loadings,
DiagonalMatrix rowPrecision, DiagonalMatrix colPrecision,
boolean scaleData, Parameter continuous, boolean newModel
) {
super("");
changedValues=new Vector<Integer>();
for (int i = 0; i <data.getDimension(); i++) {
changedValues.add(i);
}
//        data = new Matrix(dataIn.getParameterAsMatrix());
//        factors = new Matrix(factorsIn.getParameterAsMatrix());
//        loadings = new Matrix(loadingsIn.getParameterAsMatrix());
this.newModel=newModel;
this.scaleData=scaleData;
this.data = data;
this.factors = factors;
// Put default bounds on factors
for (int i = 0; i < factors.getParameterCount(); ++i) {
Parameter p = factors.getParameter(i);
System.err.println(p.getId() + " " + p.getDimension());
p.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, p.getDimension()));
}
this.continuous=continuous;
this.loadings = loadings;
//        storedData=new MatrixParameter(null);
//        for (int i = 0; i <continuous.getDimension(); i++) {
//            if(continuous.getParameterValue(i)==0)
//                storedData.addParameter(new Parameter.Default(data.getColumnDimension()));
//
//        }
// Put default bounds on loadings
//        loadings.addBounds();
changed=new boolean[loadings.getRowDimension()][factors.getColumnDimension()];
storedChanged=new boolean[loadings.getRowDimension()][factors.getColumnDimension()];
for (int i = 0; i <loadings.getRowDimension() ; i++) {
for (int j = 0; j <factors.getColumnDimension() ; j++) {
changed[i][j]=true;
}
}
this.rowPrecision = rowPrecision;
this.colPrecision = colPrecision;
addVariable(data);
addVariable(factors);
addVariable(loadings);
addVariable(rowPrecision);
addVariable(colPrecision);
dimFactors = factors.getRowDimension();
dimData = loadings.getRowDimension();
//        nTaxa = factors.getParameterCount();
//        nTaxa = factors.getParameter(0).getDimension();
nTaxa = factors.getColumnDimension();
//        System.out.print(nTaxa);
//        System.out.print("\n");
//        System.out.print(dimData);
//        System.out.print("\n");
//        System.out.println(dimFactors);
//        System.out.println(data.getDimension());
//        System.out.println(data.getRowDimension());
//        System.out.println(data.getColumnDimension());
//        System.out.println(new Matrix(data.getParameterAsMatrix()));
//        System.out.println(new Matrix(factors.getParameterAsMatrix()));
if (nTaxa * dimData != data.getDimension()) {
throw new RuntimeException("LOADINGS MATRIX AND FACTOR MATRIX MUST HAVE EXTERNAL DIMENSIONS WHOSE PRODUCT IS EQUAL TO THE NUMBER OF DATA POINTS\n");
//            System.exit(10);
}
if (dimData < dimFactors) {
throw new RuntimeException("MUST HAVE FEWER FACTORS THAN DATA POINTS\n");
}
residual=new double[loadings.getRowDimension()*factors.getColumnDimension()];
LxF=new double[loadings.getRowDimension()*factors.getColumnDimension()];
storedResidual=new double[residual.length];
storedLxF=new double[LxF.length];
if(!isDataScaled & !scaleData){
sData=this.data;
isDataScaled=true;
}
if(!isDataScaled){
sData = computeScaledData();
isDataScaled=true;
for (int i = 0; i <sData.getRowDimension() ; i++) {
for (int j = 0; j <sData.getColumnDimension() ; j++) {
this.data.setParameterValue(i,j,sData.getParameterValue(i,j));
//                    System.out.println(this.data.getParameterValue(i,j));
}
}
data.fireParameterChangedEvent();
}
double sum=0;
for(int i=0; i<sData.getRowDimension(); i++){
for (int j = 0; j <sData.getColumnDimension() ; j++) {
if(continuous.getParameterValue(i)==0 && sData.getParameterValue(i,j)!=0)
{sum+=-.5*Math.log(2*StrictMath.PI)-.5*sData.getParameterValue(i,j)*sData.getParameterValue(i,j);}
}
}
System.out.println("Constant Value for Path Sampling (normal 0,1): " + -1*sum);
computeResiduals();
//        System.out.print(new Matrix(residual.toComponents()));
//        System.out.print(calculateLogLikelihood());
}
//    public Matrix getData(){
//        Matrix ans=data;
//        return ans;
//    }
//
//    public Matrix getFactors(){
//        Matrix ans=factors;
//        return ans;
//    }
//
//    public Matrix getLoadings(){
//        Matrix ans=loadings;
//        return ans;
//    }
//
//    public Matrix getResidual(){
//        Matrix ans=residual;
//        return ans;
//    }
public MatrixParameter getFactors(){return factors;}
public MatrixParameter getColumnPrecision(){return colPrecision;}
public MatrixParameter getLoadings(){return loadings;}
public MatrixParameter getData(){return data;}
public Parameter returnIntermediate(){
if(!residualKnown && checkLoadings()){
computeResiduals();
}
return data;
}
//    public Parameter returnIntermediate(int PID)
//    {   //residualKnown=false;
//        if(!residualKnown && checkLoadings()){
//        computeResiduals();
//        }
//    return data.getParameter(PID);
//    }
public MatrixParameter getScaledData(){return data;}
public Parameter getContinuous(){return continuous;}
public int getFactorDimension(){return factors.getRowDimension();}
private void Multiply(MatrixParameter Left, MatrixParameter Right, double[] answer){
int dim=Left.getColumnDimension();
int n=Left.getRowDimension();
int p=Right.getColumnDimension();
for (int i = 0; i < n; i++) {
for (int j = 0; j < p; j++) {
if((changed[i][j]==true && continuous.getParameterValue(i)!=0) || newModel){
double sum = 0;
for (int k = 0; k < dim; k++)
sum += Left.getParameterValue(i, k) * Right.getParameterValue(k,j);
answer[i*p+j]=sum;
//changed[i][j]=false;
}
}
}
}
private void add(MatrixParameter Left, MatrixParameter Right, double[] answer){
int row=Left.getRowDimension();
int col=Left.getColumnDimension();
for (int i = 0; i <row ; i++) {
for (int j = 0; j < col; j++) {
answer[i*col+j]=Left.getParameterValue(i,j)+Right.getParameterValue(i,j);
}
}
}
private void subtract(MatrixParameter Left, double[] Right, double[] answer){
int row=Left.getRowDimension();
int col=Left.getColumnDimension();
if(!RecomputeResiduals && LxFKnown || !RecomputeFactors && !factorsKnown){
while(!changedValues.isEmpty()){
int id=changedValues.remove(0);
int tcol=id/row;
int trow=id%row;
//                System.out.println(Left.getParameterValue(id)==Left.getParameterValue(tcol,trow));
answer[trow*col+tcol]=Left.getParameterValue(id)-Right[trow*col+tcol];
}
}
else{
for (int i = 0; i <row ; i++) {
if(continuous.getParameterValue(i)!=0 ||newModel){
for (int j = 0; j < col; j++) {
answer[i*col+j]=Left.getParameterValue(i,j)-Right[i*col+j];
}
}
//              else{
//                  for (int j = 0; j <col; j++) {
//                        Left.setParameterValueQuietly(i,j, Right[i*col+j]);
//                  }
//                    containsDiscrete=true;
//                }
}
}
//        if(containsDiscrete){
//            Left.fireParameterChangedEvent();}
}
private double TDTTrace(double[] array, DiagonalMatrix middle){
int innerDim=middle.getRowDimension();
int outerDim=array.length/innerDim;
double sum=0;
for (int j = 0; j <innerDim ; j++){
if(continuous.getParameterValue(j)!=0 || newModel) {
for (int i = 0; i < outerDim; i++) {
double s1 = array[j * outerDim + i];
double s2 = middle.getParameterValue(j, j);
sum += s1 * s1 * s2;
}
}
}
return sum;
}
private MatrixParameter computeScaledData(){
MatrixParameter answer=new MatrixParameter(data.getParameterName() + ".scaled");
answer.setDimensions(data.getRowDimension(), data.getColumnDimension());
//       Matrix answer=new Matrix(data.getRowDimension(), data.getColumnDimension());
double[][] aData=data.getParameterAsMatrix();
double[] meanList=new double[data.getRowDimension()];
double[] varList=new double[data.getRowDimension()];
double[] count=new double[data.getRowDimension()];
for(int i=0; i<data.getColumnDimension(); i++){
for (int j=0; j<data.getRowDimension(); j++){
if(data.getParameterValue(j,i)!=0) {
meanList[j] += data.getParameterValue(j, i);
count[j]++;
}
}
}
for(int i=0; i<data.getRowDimension(); i++){
if(continuous.getParameterValue(i)==1)
meanList[i]=meanList[i]/count[i];
else
meanList[i]=0;
}
double[][] answerTemp=new double[data.getRowDimension()][data.getColumnDimension()];
for(int i=0; i<data.getColumnDimension(); i++){
for(int j=0; j<data.getRowDimension(); j++){
if(aData[j][i]!=0) {
answerTemp[j][i] = aData[j][i] - meanList[j];
}
}
}
//        System.out.println(new Matrix(answerTemp));
for(int i=0; i<data.getColumnDimension(); i++){
for(int j=0; j<data.getRowDimension(); j++){
varList[j]+=answerTemp[j][i]*answerTemp[j][i];
}
}
for(int i=0; i<data.getRowDimension(); i++){
if(continuous.getParameterValue(i)==1){
varList[i]=varList[i]/(count[i]-1);
varList[i]=StrictMath.sqrt(varList[i]);}
else{
varList[i]=1;
}
}
//        System.out.println(data.getColumnDimension());
//        System.out.println(data.getRowDimension());
for(int i=0; i<data.getColumnDimension(); i++){
for(int j=0; j<data.getRowDimension(); j++){
answer.setParameterValue(j,i, answerTemp[j][i]/varList[j]);
}
}
//        System.out.println(new Matrix(answerTemp));
//        computeResiduals();
return answer;
}
private Matrix copy(CompoundParameter parameter, int dimMajor, int dimMinor) {
return new Matrix(parameter.getParameterValues(), dimMajor, dimMinor);
}
private void computeResiduals() {
//    LxFKnown=false;
//        if(firstTime || (!factorVariablesChanged.empty() && !loadingVariablesChanged.empty())){
if(!LxFKnown){
Multiply(loadings, factors, LxF);
}
subtract(data, LxF, residual);
LxFKnown=true;
residualKnown=true;
factorsKnown=true;
//        firstTime=false;}
//        else{
//            while(!factorVariablesChanged.empty()){
//
//            }
//            while(!loadingVariablesChanged.empty()){
//
//            }
//        }
//
}
@Override
protected void handleModelChangedEvent(Model model, Object object, int index) {
// Do nothing
}
@Override
protected void storeState() {
data.storeParameterValues();
loadings.storeValues();
factors.storeValues();
storedLogLikelihood = logLikelihood;
storedLikelihoodKnown = likelihoodKnown;
storedLogDetColKnown=logDetColKnown;
storedLogDetCol=logDetCol;
storedTrace=trace;
storedTraceKnown=traceKnown;
storedResidualKnown=residualKnown;
storedLxFKnown=LxFKnown;
storedFactorsKnown=factorsKnown;
System.arraycopy(residual, 0, storedResidual, 0, residual.length);
System.arraycopy(LxF, 0, storedLxF, 0, residual.length);
System.arraycopy(changed, 0, storedChanged, 0, changed.length);
}
@Override
protected void restoreState() {
changed=storedChanged;
data.restoreParameterValues();
loadings.restoreValues();
factors.restoreValues();
logLikelihood = storedLogLikelihood;
likelihoodKnown = storedLikelihoodKnown;
trace=storedTrace;
traceKnown=storedTraceKnown;
residualKnown=storedResidualKnown;
LxFKnown=storedLxFKnown;
residual=storedResidual;
storedResidual=new double[residual.length];
LxF=storedLxF;
storedLxF=new double[LxF.length];
logDetCol=storedLogDetCol;
logDetColKnown=storedLogDetColKnown;
factorsKnown=storedFactorsKnown;
//        System.out.println(data.getParameterValue(10, 19));
//        int index=0;
//        for (int i = 0; i <continuous.getDimension() ; i++) {
//            if(continuous.getParameterValue(i)==0){
//                for (int j = 0; j <data.getParameter(i).getDimension() ; j++) {
//                    data.getParameter(i).setParameterValueQuietly(j, storedData.getParameter(index).getParameterValue(j));
//                }
//                index++;
//            }
//        }
}
@Override
protected void acceptState() {
// Do nothing
}
@Override
protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
if(variable==getScaledData()){
residualKnown=false;
traceKnown=false;
likelihoodKnown=false;
if(!RecomputeResiduals){
if(index!=-1)
changedValues.add(index);
else
LxFKnown=false;
}
}
if(variable==factors){
//            for (int i = 0; i <loadings.getRowDimension() ; i++) {
//                changed[i][index/factors.getRowDimension()]=true;
//            }
if(!RecomputeFactors){
factorsKnown=false;
if(index!=-1)
for (int i = 0; i <data.getRowDimension() ; i++) {
changedValues.add(index);
}
}
//            factorVariablesChanged.push(index);
LxFKnown=false;
residualKnown=false;
traceKnown=false;
likelihoodKnown = false;
}
if(variable==loadings){
//            System.out.println("Loadings Changed");
//            System.out.println(index);
//            System.out.println(index/loadings.getRowDimension());
//            for (int i = 0; i <factors.getColumnDimension(); i++) {
//                changed[index%loadings.getRowDimension()][i]=true;
//            }
//            factorVariablesChanged.push(index);
LxFKnown=false;
residualKnown=false;
traceKnown=false;
likelihoodKnown = false;
}
if(variable==colPrecision){
logDetColKnown=false;
traceKnown=false;
likelihoodKnown = false;
}
}
@Override
public List<Citation> getCitations() {
return null;  //To change body of implemented methods use File | Settings | File Templates.
}
@Override
public Model getModel() {
return this;
}
@Override
public double getLogLikelihood() {
likelihoodKnown=false;
if (!likelihoodKnown) {
logLikelihood = calculateLogLikelihood();
likelihoodKnown = true;
}
return logLikelihood;
}
@Override
public void makeDirty() {
likelihoodKnown = false;
}
private boolean checkLoadings(){
for(int i=0; i<StrictMath.min(loadings.getRowDimension(),loadings.getColumnDimension()); i++)
{
if(loadings.getParameterValue(i,i)<0)
{
return false;
}
}
return true;
}
private double calculateLogLikelihood() {
//         if(!checkLoadings()){
//             if(pathParameter==1)
//                return Double.NEGATIVE_INFINITY;
//            else{
//                return Math.log(1-pathParameter);}}
//        Matrix tRowPrecision= new Matrix(rowPrecision.getParameterAsMatrix());
//        Matrix tColPrecision= new Matrix(colPrecision.getParameterAsMatrix());
//        residualKnown=false;
if(!residualKnown){
computeResiduals();
}
//        expPart = residual.productInPlace(rowPrecision.productInPlace(residual.transposeThenProductInPlace(colPrecision, TResidualxC), RxTRxC), expPart);
//            logDetRow=StrictMath.log(rowPrecision.getDeterminant());
//       logDetColKnown=false;
if(!logDetColKnown){
logDetColKnown=true;
double product=1;
for (int i = 0; i <colPrecision.getRowDimension() ; i++) {
if (continuous.getParameterValue(i)!=0)
product*=colPrecision.getParameterValue(i,i);
}
logDetCol=StrictMath.log(product);
}
//            System.out.println(logDetCol);
//            System.out.println(logDetRow);
//        traceKnown=false;
if(!traceKnown){
traceKnown=true;
trace=TDTTrace(residual, colPrecision);
}
//        if(expPart.getRowDimension()!=expPart.getColumnDimension())
//        {
//            System.err.print("Matrices are not conformable");
//            System.exit(0);
//        }
//        else{
//            for(int i=0; i<expPart.getRowDimension(); i++){
//                trace+=expPart.getParameterValue(i, i);
//            }
//        }
//        System.out.println(expPart);
return -.5*trace + .5*data.getColumnDimension()*logDetCol +.5*data.getRowDimension()
-.5*data.getRowDimension()*data.getColumnDimension()*Math.log(2.0 * StrictMath.PI);
}
//    public void setPathParameter(double beta){
//        pathParameter=beta;
//        data.product(pathParameter);
//    }
//    @Override
//    public double getLikelihoodCorrection() {
//        return 0;
//    }
}
