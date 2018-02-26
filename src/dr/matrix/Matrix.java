package dr.matrix;
public interface Matrix {
	//***************************************************
	// Getter methods
	//***************************************************
	int getRowCount();
	int getColumnCount();
	int getElementCount();
	int getTriangleCount() throws Matrix.NotSquareException;
	int getDiagonalCount() throws Matrix.NotSquareException;
	double[] getElements();
	double[][] getElements2D();
	double[] getUpperTriangle() throws Matrix.NotSquareException;
	double[] getLowerTriangle() throws Matrix.NotSquareException;
	double[] getDiagonal() throws Matrix.NotSquareException;
	double getElement(int row, int column);
	double getElement(int index);
	double[] getRow(int row);
	double[] getColumn(int column);
	double getMinValue();
	double getMaxValue();
	boolean getIsSquare();
	boolean getIsSymmetric() throws Matrix.NotSquareException;
	String getRowId(int row);
	String getColumnId(int column);
	//***************************************************
	// Exception classes
	//***************************************************
	public class MatrixException extends Exception { 
		private static final long serialVersionUID = -5904166681730282246L;
		MatrixException() { super(); }
		MatrixException(String  message) { super(message); }
	}
	public class NotSquareException extends MatrixException {
		private static final long serialVersionUID = 5121968928197320497L; }
	public class WrongDimensionException extends MatrixException { 
		private static final long serialVersionUID = -1799942797975356399L;
		WrongDimensionException() { super(); }
		WrongDimensionException(String message) { super(message); }
	}
	//***************************************************
	// Util class
	//***************************************************
	public static class Util {
		public static double dotProduct(Matrix a, Matrix b) {
			throw new RuntimeException("not implemented yet");
		}
		public static void product(Matrix a, Matrix b, MutableMatrix result) throws Matrix.WrongDimensionException {
			int rca = a.getRowCount();
			int cca = a.getColumnCount();
			int rcb = b.getRowCount();
			int ccb = b.getColumnCount();
			if (cca != rcb)
				throw new Matrix.WrongDimensionException("column count of matrix a = " + cca + ", row count of matrix b = " + rcb);
			result.setDimension(rca, ccb);
			for (int r = 0; r < rca; r++) {
				for (int c = 0; c < ccb; c++) {
					double sum = 0;
					for (int i = 0; i < cca; i++) {
						sum += a.getElement(r, i) * b.getElement(i, c);
					}
					result.setElement(r, c, sum);
				}
			}
		}
		public static void kroneckerProduct(Matrix a, Matrix b, MutableMatrix result) {
			int ra, ca, rb, cb;
			int rca = a.getRowCount();
			int cca = a.getColumnCount();
			int rcb = b.getRowCount();
			int ccb = b.getColumnCount();
			for (rb = 0; rb < rcb; rb++) {
				for (cb = 0; cb < ccb; cb++) {
					for (ra = 0; ra < rca; ra++) {
						for (ca = 0; ca < cca; ca++) {
							result.setElement(rb*rca + ra, cb * cca + ca, 
								a.getElement(ra, ca) * b.getElement(rb, cb));
						}
					}
				}
			}
		}
		public static void add(Matrix a, Matrix b, MutableMatrix result) throws Matrix.WrongDimensionException {
			int rca = a.getRowCount();
			int cca = a.getColumnCount();
			if (rca != b.getRowCount() || cca != b.getColumnCount())
				throw new Matrix.WrongDimensionException();
			result.setDimension(rca, cca);
			for (int r = 0; r < rca; r++) {
				for (int c = 0; c < cca; c++) {
					result.setElement(r, c, a.getElement(r, c) + b.getElement(r, c));
				}
			}
		}
		public static void subtract(Matrix a, Matrix b, MutableMatrix result) throws Matrix.WrongDimensionException {
			int rca = a.getRowCount();
			int cca = a.getColumnCount();
			if (rca != b.getRowCount() || cca != b.getColumnCount())
				throw new Matrix.WrongDimensionException();
			result.setDimension(rca, cca);
			for (int r = 0; r < rca; r++) {
				for (int c = 0; c < cca; c++) {
					result.setElement(r, c, a.getElement(r, c) - b.getElement(r, c));
				}
			}
		}
		public static double det(Matrix matrix) throws Matrix.NotSquareException {
			int n = matrix.getRowCount();
			int col = matrix.getColumnCount();
			if (n != col) throw new Matrix.NotSquareException();
			double[][] D = new double[n][n];
			for (int i =0; i < n; i++) {
				for (int j=0; j < n; j++) {
					D[i][j] = matrix.getElement(i, j);
				}
			}
			org.apache.commons.math.linear.RealMatrixImpl RM = new org.apache.commons.math.linear.RealMatrixImpl(D);
			return RM.getDeterminant(); 
		}
		public static double logDet(Matrix matrix) throws Matrix.NotSquareException {
			throw new RuntimeException("not implemented yet");
		}
		public static void invert(MutableMatrix matrix) throws Matrix.NotSquareException {
			int n = matrix.getRowCount();
			int col = matrix.getColumnCount();
			if (n != col) throw new Matrix.NotSquareException();
			double[][] D = new double[n+1][n*2+2];
			for (int i =0; i < n; i++) {
				for (int j=0; j < n; j++) {
					D[i+1][j+1] = matrix.getElement(i, j);
				}
			}
			double alpha;
			double beta;
			int i;
			int j;
			int k;
//			int error;
//			error = 0;
			int n2 = 2*n;
			for( i = 1; i <= n; i++ )
			{
				for( j = 1; j <= n; j++ )
				{
					D[i][j+n] = 0.;
				}
				D[i][i+n] = 1.0;
			}
			for( i = 1; i <= n; i++ )
			{
				alpha = D[i][i];
				if( alpha == 0.0 ) /* error - singular matrix */
				{
//					error = 1;
					break;
				}
				else
				{
					for( j = 1; j <= n2; j++ )
					{
						D[i][j] = D[i][j]/alpha;
					}
					for( k = 1; k <= n; k++ )
					{
						if( (k-i) != 0 )
						{
							beta = D[k][i];
							for( j = 1; j <= n2; j++ )
							{
								D[k][j] = D[k][j] - beta*D[i][j];
							}
						}
					}
				}
			}
			for (i =0; i < n; i++) {
				for (j=0; j < n; j++) {
					matrix.setElement(i, j, D[i+1][j+n+1]);
				}
			}
		}
		public static void invert(Matrix a, Matrix b, MutableMatrix result) 
			throws Matrix.NotSquareException, Matrix.WrongDimensionException
		{
			throw new RuntimeException("not implemented yet");
		}
		public static void raise(Matrix matrix, double d, MutableMatrix result) throws Matrix.NotSquareException {
			throw new RuntimeException("not implemented yet");
		}
		public static Matrix createColumnVector(double[] v) { return new ColumnVector(v); }
		public static Matrix createRowVector(double[] v) { return new RowVector(v); }
		public static MutableMatrix createMutableMatrix(double[][] values) { return new ConcreteMatrix(values); }
	}
	//***************************************************
	// AbstractMatrix class
	//***************************************************
	public abstract class AbstractMatrix implements Matrix {
		//***************************************************
		// Getter methods
		//***************************************************
		public int getElementCount() {
			return getRowCount() * getColumnCount();
		}
		public int getTriangleCount() throws Matrix.NotSquareException {
			if (!getIsSquare())
				throw new Matrix.NotSquareException();
			int dim = getRowCount();
			return ((dim - 1) * dim) / 2;
		}
		public int getDiagonalCount() throws Matrix.NotSquareException {
			if (!getIsSquare())
				throw new Matrix.NotSquareException();
			return getRowCount();
		}
		public double[] getElements() {
			double[] values = new double[getElementCount()];
			int k = 0;
			int rc = getRowCount();
			int cc = getColumnCount();
			for (int r = 0; r < rc; r++) {
				for (int c = 0; c < cc; c++) {
					values[k] = getElement(r, c);
					k++;
				}
			}
			return values;
		}
		public double[][] getElements2D() {
			double[][] values = new double[getRowCount()][getColumnCount()];
			int rc = getRowCount();
			int cc = getColumnCount();
			for (int r = 0; r < rc; r++) {
				for (int c = 0; c < cc; c++) {
					values[r][c] = getElement(r, c);
				}
			}
			return values;
		}
		public double getElement(int index) {
			int r = index / getColumnCount();
			int c = index % getColumnCount();
			return getElement(r, c);
		}
		public double[] getUpperTriangle() throws Matrix.NotSquareException {
			if (!getIsSquare())
				throw new Matrix.NotSquareException();
			double[] values = new double[getTriangleCount()];
			int k = 0;
			int dim = getRowCount();
			for (int r = 0; r < dim; r++) {
				for (int c = r + 1; c < dim; c++) {
					values[k] = getElement(r, c);
					k++;
				}
			}
			return values;
		}
		public double[] getLowerTriangle() throws Matrix.NotSquareException {
			if (!getIsSquare())
				throw new Matrix.NotSquareException();
			double[] values = new double[getTriangleCount()];
			int k = 0;
			int dim = getRowCount();
			for (int r = 0; r < dim; r++) {
				for (int c = 0; c < r; c++) {
					values[k] = getElement(r, c);
					k++;
				}
			}
			return values;
		}
		public double[] getDiagonal() throws Matrix.NotSquareException {
			if (!getIsSquare())
				throw new Matrix.NotSquareException();
			int dim = getRowCount();
			double[] values = new double[dim];
			for (int r = 0; r < dim; r++) {
				values[r] = getElement(r, r);
			}
			return values;
		}
		public double[] getRow(int row) {
			int dim = getColumnCount();
			double[] values = new double[dim];
			for (int c = 0; c < dim; c++) {
				values[c] = getElement(row, c);
			}
			return values;
		}
		public double[] getColumn(int column) {
			int dim = getRowCount();
			double[] values = new double[dim];
			for (int r = 0; r < dim; r++) {
				values[r] = getElement(r, column);
			}
			return values;
		}
		public double getMinValue() {
			double value, minValue = getElement(0);
			int n = getElementCount();
			for (int i = 1; i < n; i++) {
				value = getElement(i);
				if (value < minValue)
					minValue = value;
			}
			return minValue;
		}
		public double getMaxValue() {
			double value, maxValue = getElement(0);
			int n = getElementCount();
			for (int i = 1; i < n; i++) {
				value = getElement(i);
				if (value > maxValue)
					maxValue = value;
			}
			return maxValue;
		}
		public boolean getIsSquare() {
			return getRowCount() == getColumnCount();
		}
		public boolean getIsSymmetric() throws Matrix.NotSquareException {
			if (!getIsSquare())
				throw new Matrix.NotSquareException();
			int dim = getRowCount();
			for (int r = 0; r < dim; r++) {
				for (int c = r + 1; r < dim; r++) {
					if (getElement(r, c) != getElement(c, r))
						return false;
				}
			}
			return true;
		}			
		public String getRowId(int row) { return null; }
		public String getColumnId(int column) { return null; }
	}
}
class ColumnVector extends Matrix.AbstractMatrix {
	public ColumnVector(double[] v) {
		this.values = new double[v.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = v[i];	
		}
	}
	public final int getColumnCount() { return 1; } 
	public final int getRowCount() { return values.length; }
	public final double getElement(int i, int j) { return values[i]; }
	double[] values = null;
}
class RowVector extends Matrix.AbstractMatrix {
	public RowVector(double[] v) {
		this.values = new double[v.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = v[i];	
		}
	}
	public final int getRowCount() { return 1; } 
	public final int getColumnCount() { return values.length; }
	public final double getElement(int i, int j) { return values[j]; }
	double[] values = null;
}