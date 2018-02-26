
package dr.math.matrixAlgebra;

import dr.math.iterations.IterativeProcess;

public class LargestEigenvalueFinder extends IterativeProcess
{
	private double eigenvalue;
	private Vector eigenvector;
	private Vector transposedEigenvector;
	private Matrix matrix;

public LargestEigenvalueFinder ( double prec, Matrix a)
{
	this(a);
	this.setDesiredPrecision ( prec);
}
public LargestEigenvalueFinder ( Matrix a) 
{
	matrix = a;
	eigenvalue = Double.NaN;
}
public double eigenvalue ( )
{
	return eigenvalue;
}
public Vector eigenvector ( )
{
	return eigenvector.product( 1.0 / eigenvector.norm());
}
public double evaluateIteration()
{
	double oldEigenvalue = eigenvalue;
	transposedEigenvector = 
						transposedEigenvector.secureProduct( matrix);
	transposedEigenvector = transposedEigenvector.product( 1.0 
							/ transposedEigenvector.components[0]);
	eigenvector = matrix.secureProduct( eigenvector);
	eigenvalue = eigenvector.components[0];
	eigenvector = eigenvector.product( 1.0 / eigenvalue);
	return Double.isNaN( oldEigenvalue)
					? 10 * getDesiredPrecision()
					: Math.abs( eigenvalue - oldEigenvalue);
}
public void initializeIterations()
{
	eigenvalue = Double.NaN;
	int n = matrix.columns();
	double [] eigenvectorComponents = new double[ n];
	for ( int i = 0; i < n; i++) { eigenvectorComponents [i] = 1.0;}
	eigenvector = new Vector( eigenvectorComponents);
	n = matrix.rows();
	eigenvectorComponents = new double[ n];
	for ( int i = 0; i < n; i++) { eigenvectorComponents [i] = 1.0;}
	transposedEigenvector = new Vector( eigenvectorComponents);
}
public LargestEigenvalueFinder nextLargestEigenvalueFinder ( )
{
	double norm = 1.0 / eigenvector.secureProduct(
											transposedEigenvector);
	Vector v1 = eigenvector.product( norm);
	return new LargestEigenvalueFinder( getDesiredPrecision(),
			matrix.secureProduct(SymmetricMatrix.identityMatrix(
				v1.dimension()).secureSubtract(v1.tensorProduct(
											transposedEigenvector))));
}
public String toString()
{
	StringBuffer sb = new StringBuffer();
	sb.append( eigenvalue);
	sb.append(" (");
	sb.append( eigenvector.toString());
	sb.append(')');
	return sb.toString();
}
}