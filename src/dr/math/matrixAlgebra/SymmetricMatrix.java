package dr.math.matrixAlgebra;
public class SymmetricMatrix extends Matrix {
	private static int lupCRLCriticalDimension = 36;
	public SymmetricMatrix(double[][] a) {
		super(a);
	}
	public SymmetricMatrix(int n) throws NegativeArraySizeException {
		super(n, n);
	}
	public SymmetricMatrix(int n, int m) throws NegativeArraySizeException {
		super(n, m);
	}
	public SymmetricMatrix add(SymmetricMatrix a)
			throws IllegalDimension {
		return new SymmetricMatrix(addComponents(a));
	}
	private SymmetricMatrix crlInverse() throws ArithmeticException {
		if (rows() == 1)
			return inverse1By1();
		else if (rows() == 2)
			return inverse2By2();
		Matrix[] splitMatrices = split();
		SymmetricMatrix b1 = (SymmetricMatrix) splitMatrices[0].inverse();
		Matrix cb1 = splitMatrices[2].secureProduct(b1);
		SymmetricMatrix cb1cT = new SymmetricMatrix(
				cb1.productWithTransposedComponents(splitMatrices[2]));
		splitMatrices[1] = ((SymmetricMatrix)
				splitMatrices[1]).secureSubtract(cb1cT).inverse();
		splitMatrices[2] = splitMatrices[1].secureProduct(cb1);
		splitMatrices[0] = b1.secureAdd(new SymmetricMatrix(
				cb1.transposedProductComponents(splitMatrices[2])));
		return SymmetricMatrix.join(splitMatrices);
	}
	public static SymmetricMatrix fromComponents(double[][] comp)
			throws IllegalDimension, NonSymmetricComponents {
		if (comp.length != comp[0].length)
			throw new IllegalDimension("Non symmetric components: a "
					+ comp.length + " by " + comp[0].length
					+ " matrix cannot be symmetric");
		for (int i = 0; i < comp.length; i++) {
			for (int j = 0; j < i; j++) {
				if (comp[i][j] != comp[j][i])
					throw new NonSymmetricComponents(
							"Non symmetric components: a[" + i + "][" + j
									+ "]= " + comp[i][j] + ", a[" + j + "]["
									+ i + "]= " + comp[j][i]);
			}
		}
		return new SymmetricMatrix(comp);
	}
	public static SymmetricMatrix identityMatrix(int n) {
		double[][] a = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) a[i][j] = 0;
			a[i][i] = 1;
		}
		return new SymmetricMatrix(a);
	}
	public Matrix inverse() throws ArithmeticException {
		return rows() < lupCRLCriticalDimension
				? new SymmetricMatrix(
				(new LUPDecomposition(this)).inverseMatrixComponents())
				: crlInverse();
	}
	private SymmetricMatrix inverse1By1() {
		double[][] newComponents = new double[1][1];
		newComponents[0][0] = 1 / components[0][0];
		return new SymmetricMatrix(newComponents);
	}
	private SymmetricMatrix inverse2By2() {
		double[][] newComponents = new double[2][2];
		double inverseDeterminant = 1 / (components[0][0] * components[1][1]
				- components[0][1] * components[1][0]);
		newComponents[0][0] = inverseDeterminant * components[1][1];
		newComponents[1][1] = inverseDeterminant * components[0][0];
		newComponents[0][1] = newComponents[1][0] =
				-inverseDeterminant * components[1][0];
		return new SymmetricMatrix(newComponents);
	}
	private static SymmetricMatrix join(Matrix[] a) {
		int p = a[0].rows();
		int n = p + a[1].rows();
		double[][] newComponents = new double[n][n];
		for (int i = 0; i < p; i++) {
			for (int j = 0; j < p; j++)
				newComponents[i][j] = a[0].components[i][j];
			for (int j = p; j < n; j++)
				newComponents[i][j] = newComponents[j][i] =
						-a[2].components[j - p][i];
		}
		for (int i = p; i < n; i++) {
			for (int j = p; j < n; j++)
				newComponents[i][j] = a[1].components[i - p][j - p];
		}
		return new SymmetricMatrix(newComponents);
	}
	private int largestPowerOf2SmallerThan(int n) {
		int m = 2;
		int m2;
		while (true) {
			m2 = 2 * m;
			if (m2 >= n)
				return m;
			m = m2;
		}
	}
	public Matrix product(double a) {
		return new SymmetricMatrix(productComponents(a));
	}
	public SymmetricMatrix product(SymmetricMatrix a) throws IllegalDimension {
		return new SymmetricMatrix(productComponents(a));
	}
	public SymmetricMatrix productWithTransposed(SymmetricMatrix a)
			throws IllegalDimension {
		if (a.columns() != columns())
			throw new IllegalDimension(
					"Operation error: cannot multiply a "
							+ rows() + " by " + columns()
							+ " matrix with the transpose of a "
							+ a.rows() + " by " + a.columns() + " matrix");
		return new SymmetricMatrix(productWithTransposedComponents(a));
	}
	protected SymmetricMatrix secureAdd(SymmetricMatrix a) {
		return new SymmetricMatrix(addComponents(a));
	}
	protected SymmetricMatrix secureProduct(SymmetricMatrix a) {
		return new SymmetricMatrix(productComponents(a));
	}
	protected SymmetricMatrix secureSubtract(SymmetricMatrix a) {
		return new SymmetricMatrix(subtractComponents(a));
	}
	private Matrix[] split() {
		int n = rows();
		int p = largestPowerOf2SmallerThan(n);
		int q = n - p;
		double[][] a = new double[p][p];
		double[][] b = new double[q][q];
		double[][] c = new double[q][p];
		for (int i = 0; i < p; i++) {
			for (int j = 0; j < p; j++)
				a[i][j] = components[i][j];
			for (int j = p; j < n; j++)
				c[j - p][i] = components[i][j];
		}
		for (int i = p; i < n; i++) {
			for (int j = p; j < n; j++)
				b[i - p][j - p] = components[i][j];
		}
		Matrix[] answer = new Matrix[3];
		answer[0] = new SymmetricMatrix(a);
		answer[1] = new SymmetricMatrix(b);
		answer[2] = new Matrix(c);
		return answer;
	}
	public SymmetricMatrix subtract(SymmetricMatrix a)
			throws IllegalDimension {
		return new SymmetricMatrix(subtractComponents(a));
	}
	public Matrix transpose() {
		return this;
	}
	public SymmetricMatrix transposedProduct(SymmetricMatrix a)
			throws IllegalDimension {
		if (a.rows() != rows())
			throw new IllegalDimension(
					"Operation error: cannot multiply a tranposed "
							+ rows() + " by " + columns() + " matrix with a " +
							a.rows() + " by " + a.columns() + " matrix");
		return new SymmetricMatrix(transposedProductComponents(a));
	}
}