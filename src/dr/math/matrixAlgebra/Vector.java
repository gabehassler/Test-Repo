package dr.math.matrixAlgebra;
public class Vector {
protected double[] components;
public Vector(double comp[]) throws NegativeArraySizeException {
int n = comp.length;
if (n <= 0)
throw new NegativeArraySizeException(
"Vector components cannot be empty");
components = new double[n];
System.arraycopy(comp, 0, components, 0, n);
}
public Vector(int comp[]) throws NegativeArraySizeException {
int n = comp.length;
if (n <= 0)
throw new NegativeArraySizeException(
"Vector components cannot be empty");
components = new double[n];
//	System.arraycopy( comp, 0, components, 0, n);
for (int i = 0; i < n; i++)
components[i] = comp[i];
}
public Vector(int dimension) throws NegativeArraySizeException {
if (dimension <= 0)
throw new NegativeArraySizeException(
"Requested vector size: " + dimension);
components = new double[dimension];
clear();
}
public void accumulate(double[] x) throws IllegalDimension {
if (this.dimension() != x.length)
throw new IllegalDimension("Attempt to add a "
+ this.dimension() + "-dimension vector to a "
+ x.length + "-dimension array");
for (int i = 0; i < this.dimension(); i++)
components[i] += x[i];
}
public void accumulate(Vector v) throws IllegalDimension {
if (this.dimension() != v.dimension())
throw new IllegalDimension("Attempt to add a "
+ this.dimension() + "-dimension vector to a "
+ v.dimension() + "-dimension vector");
for (int i = 0; i < this.dimension(); i++)
components[i] += v.components[i];
}
public void accumulateNegated(double[] x) throws IllegalDimension {
if (this.dimension() != x.length)
throw new IllegalDimension("Attempt to add a "
+ this.dimension() + "-dimension vector to a "
+ x.length + "-dimension array");
for (int i = 0; i < this.dimension(); i++)
components[i] -= x[i];
}
public void accumulateNegated(Vector v) throws IllegalDimension {
if (this.dimension() != v.dimension())
throw new IllegalDimension("Attempt to add a "
+ this.dimension() + "-dimension vector to a "
+ v.dimension() + "-dimension vector");
for (int i = 0; i < this.dimension(); i++)
components[i] -= v.components[i];
}
public Vector add(Vector v) throws IllegalDimension {
if (this.dimension() != v.dimension())
throw new IllegalDimension("Attempt to add a "
+ this.dimension() + "-dimension vector to a "
+ v.dimension() + "-dimension vector");
double[] newComponents = new double[this.dimension()];
for (int i = 0; i < this.dimension(); i++)
newComponents[i] = components[i] + v.components[i];
return new Vector(newComponents);
}
public void clear() {
for (int i = 0; i < components.length; i++) components[i] = 0;
}
public double component(int n) {
return components[n];
}
public int dimension() {
return components.length;
}
public boolean equals(Vector v) {
int n = this.dimension();
if (v.dimension() != n)
return false;
for (int i = 0; i < n; i++) {
if (v.components[i] != components[i])
return false;
}
return true;
}
public double norm() {
double sum = 0;
for (int i = 0; i < components.length; i++)
sum += components[i] * components[i];
return Math.sqrt(sum);
}
public Vector normalizedBy(double x) {
for (int i = 0; i < this.dimension(); i++)
components[i] /= x;
return this;
}
public Vector product(double d) {
double newComponents[] = new double[components.length];
for (int i = 0; i < components.length; i++)
newComponents[i] = d * components[i];
return new Vector(newComponents);
}
public double product(Vector v) throws IllegalDimension {
int n = v.dimension();
if (components.length != n)
throw new IllegalDimension(
"Dot product with mismatched dimensions: "
+ components.length + ", " + n);
return secureProduct(v);
}
public Vector product(Matrix a) throws IllegalDimension {
int n = a.rows();
int m = a.columns();
if (this.dimension() != n)
throw new IllegalDimension(
"Product error: transposed of a " + this.dimension()
+ "-dimension vector cannot be multiplied with a "
+ n + " by " + m + " matrix");
return secureProduct(a);
}
public Vector scaledBy(double x) {
for (int i = 0; i < this.dimension(); i++)
components[i] *= x;
return this;
}
protected double secureProduct(Vector v) {
double sum = 0;
for (int i = 0; i < v.dimension(); i++)
sum += components[i] * v.components[i];
return sum;
}
protected Vector secureProduct(Matrix a) {
int n = a.rows();
int m = a.columns();
double[] vectorComponents = new double[m];
for (int j = 0; j < m; j++) {
vectorComponents[j] = 0;
for (int i = 0; i < n; i++)
vectorComponents[j] += components[i] * a.components[i][j];
}
return new Vector(vectorComponents);
}
public Vector subtract(Vector v) throws IllegalDimension {
if (this.dimension() != v.dimension())
throw new IllegalDimension("Attempt to add a "
+ this.dimension() + "-dimension vector to a "
+ v.dimension() + "-dimension vector");
double[] newComponents = new double[this.dimension()];
for (int i = 0; i < this.dimension(); i++)
newComponents[i] = components[i] - v.components[i];
return new Vector(newComponents);
}
public Matrix tensorProduct(Vector v) {
int n = dimension();
int m = v.dimension();
double[][] newComponents = new double[n][m];
for (int i = 0; i < n; i++) {
for (int j = 0; j < m; j++)
newComponents[i][j] = components[i] * v.components[j];
}
return n == m ? new SymmetricMatrix(newComponents)
: new Matrix(newComponents);
}
public double[] toComponents() {
int n = dimension();
double[] answer = new double[n];
System.arraycopy(components, 0, answer, 0, n);
return answer;
}
public String toString() {
StringBuffer sb = new StringBuffer();
char[] separator = {'[', ' '};
for (int i = 0; i < components.length; i++) {
sb.append(separator);
sb.append(components[i]);
separator[0] = ',';
}
sb.append(']');
return sb.toString();
}
public static Vector buildOneTimesElementVector(int dim, double element){
double[] component=new double[dim];
for (int i = 0; i <dim ; i++) {
component[i]=element;
}
return new Vector(component);
}
}