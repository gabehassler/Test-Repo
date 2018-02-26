package dr.matrix;
public interface MutableMatrix extends Matrix {
	//***************************************************
	// Setter methods
	//***************************************************
	void setDimension(int rows);
	void setDimension(int rows, int columns);
	void setMatrix(Matrix matrix);
	void setElements(double[][] values);
	void setElements(double[] values);
	void setElements(double value);
	void setElement(int row, int column, double value);
	void setElement(int index, double value);
	void setRow(int row, double[] values);
	void setColumn(int column, double[] values);
	void setUpperTriangle(double[] values) throws Matrix.NotSquareException;
	void setLowerTriangle(double[] values) throws Matrix.NotSquareException;
	void setDiagonal(double[] values) throws Matrix.NotSquareException;
	//***************************************************
	// Manipulation methods
	//***************************************************
	void makeTransposed();
	void makeSymmetricFromUpperTriangle() throws Matrix.NotSquareException;
	void makeSymmetricFromLowerTriangle() throws Matrix.NotSquareException;
	void makeIdentity() throws Matrix.NotSquareException;
	//***************************************************
	// AbstractMutableMatrix class
	//***************************************************
	public abstract class AbstractMutableMatrix extends Matrix.AbstractMatrix 
												implements MutableMatrix {
		public AbstractMutableMatrix() {
		}
		public AbstractMutableMatrix(int rows) {
			setDimension(rows);
		}
		public AbstractMutableMatrix(int rows, int columns) {
			setDimension(rows, columns);
		}
		public AbstractMutableMatrix(Matrix matrix) {
			setDimension(matrix.getRowCount(), matrix.getColumnCount());
			setMatrix(matrix);
		}
		public AbstractMutableMatrix(double[][] values) {
			setDimension(values.length, values[0].length);
			setElements(values);
		}
		public AbstractMutableMatrix(int rows, int columns, double[] values) {
			setDimension(rows, columns);
			setElements(values);
		}
		public AbstractMutableMatrix(int rows, int columns, double value) {
			setDimension(rows, columns);
			setElements(value);
		}
		//***************************************************
		// Setter methods
		//***************************************************
		public void setDimension(int rows) {
			setDimension(rows, rows);
		}
		public void setMatrix(Matrix matrix) {
			int rc = matrix.getRowCount();
			int cc = matrix.getColumnCount();
			for (int r = 0; r < rc; r++) {
				for (int c = 0; c < cc; c++) {
					setElement(r, c, matrix.getElement(r, c));
				}
			}
		}
		public void setElements(double[][] values) {
			setDimension(values.length, values[0].length);
			for (int r = 0; r < values.length; r++) {
				for (int c = 0; c < values[0].length; c++) {
					setElement(r, c, values[r][c]);
				}
			}
		}
		public void setElements(double[] values) {
			int k = 0;
			int rc = getRowCount();
			int cc = getColumnCount();
			for (int r = 0; r < rc; r++) {
				for (int c = 0; c < cc; c++) {
					setElement(r, c, values[k]);
					k++;
				}
			}
		}
		public void setElements(double value) {
			int rc = getRowCount();
			int cc = getColumnCount();
			for (int r = 0; r < rc; r++) {
				for (int c = 0; c < cc; c++) {
					setElement(r, c, value);
				}
			}
		}
		public void setElement(int index, double value) {
			int r = index / getColumnCount();
			int c = index % getColumnCount();
			setElement(r, c, value);
		}
		public void setRow(int row, double[] values) {
			int cc = getColumnCount();
			for (int c = 0; c < cc; c++) {
				setElement(row, c, values[c]);
			}
		}
		public void setColumn(int column, double[] values) {
			int rc = getRowCount();
			for (int r = 0; r < rc; r++) {
				setElement(r, column, values[r]);
			}
		}
		public void setUpperTriangle(double[] values) throws Matrix.NotSquareException {
			if (!getIsSquare())
				throw new Matrix.NotSquareException();
			int k = 0;
			int dim = getRowCount();
			for (int r = 0; r < dim; r++) {
				for (int c = r + 1; c < dim; c++) {
					setElement(r, c, values[k]);
					k++;
				}
			}
		}
		public void setLowerTriangle(double[] values) throws Matrix.NotSquareException {
			if (!getIsSquare())
				throw new Matrix.NotSquareException();
			int k = 0;
			int dim = getRowCount();
			for (int r = 0; r < dim; r++) {
				for (int c = 0; c < r; c++) {
					setElement(r, c, values[k]);
					k++;
				}
			}
		}
		public void setDiagonal(double[] values) throws Matrix.NotSquareException {
			if (!getIsSquare())
				throw new Matrix.NotSquareException();
			int dim = getRowCount();
			for (int r = 0; r < dim; r++) {
				setElement(r, r, values[r]);
			}
		}
		//***************************************************
		// Manipulation methods
		//***************************************************
		public void makeTransposed() {
			double[][] values = getElements2D();
			int cc = getRowCount();
			int rc = getColumnCount();
			setDimension(rc, cc);
			for (int r = 0; r < rc; r++) {
				for (int c = 0; c < cc; c++) {
					setElement(r, c, values[c][r]);
				}
			}
		}
		public void makeSymmetricFromUpperTriangle() throws Matrix.NotSquareException {
			if (!getIsSquare())
				throw new Matrix.NotSquareException();
			double[] values = getUpperTriangle();
			setLowerTriangle(values);
		}
		public void makeSymmetricFromLowerTriangle() throws Matrix.NotSquareException {
			if (!getIsSquare())
				throw new Matrix.NotSquareException();
			double[] values = getUpperTriangle();
			setLowerTriangle(values);
		}
		public void makeIdentity() throws Matrix.NotSquareException {
			if (!getIsSquare())
				throw new Matrix.NotSquareException();
			setElements(0.0);
			for (int i = 0; i < getRowCount(); i++)
				setElement(i, i, 1.0);
		}
	}
}