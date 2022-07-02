package duramater.knn.mnist.model;

import org.encog.mathutil.Equilateral;

/**
 * This class converts MNIST matrix arrays to input and ideal arrays for a multilayer perceptron.
 * @author Ron.Coleman
 */
public class MnistArrays {
    public final static int NUM_LABELS = 10;
    private final int stride;
    private final int numRows;
    private final int numCols;
    private final Equilateral eq;
    private MnistMatrix[] matrices;

    /**
     * Constructor
     * @param matrices Matrices
     */
    public MnistArrays(MnistMatrix[] matrices) {
        this.matrices = matrices;
        this.stride = matrices[0].getNumberOfRows() * matrices[0].getNumberOfColumns();
        this.numRows = matrices[0].getNumberOfRows();
        this.numCols = matrices[0].getNumberOfColumns();
        this.eq = new Equilateral(NUM_LABELS, 1.0, 0.0);
//        this.eq = new Equilateral(NUM_LABELS, 1.0, -1.0);

    }

    public double[][] getInputs() {
        return getInputs(matrices.length);
    }

    public double[][] getIdeals() {
        return getIdeals(matrices.length);
    }

    /**
     * Generates inputs as a nx784 matrix.
     * <p>784 = 28*28</p>
     * @return Inputs
     */
    public double[][] getInputs(int num) {
        double[][] inputs = new double[num][stride];

        for (int matrixno = 0; matrixno < num; matrixno++) {
            for (int rowno = 0; rowno < numRows; rowno++) {
                for (int colno = 0; colno < numCols; colno++) {
                    int idx = rowno * numCols + colno;
                    assert (idx < stride);

                    double pixel = matrices[matrixno].getValue(rowno, colno)/255.0;
                    inputs[matrixno][idx] = pixel;
                }
            }
        }
        return inputs;
    }

    /**
     * Generates ideals as an nx9 matrix.
     * <p>9 = equilateral encoded category</p>
     * @return Ideals
     */
    public double[][] getIdeals(int num) {
        double[][] ideals = new double[num][NUM_LABELS - 1];

        for (int matrixno = 0; matrixno < num; matrixno++) {
            int label = matrices[matrixno].getLabel();
            assert (label >= 0 && label <= 9);

            double[] code = eq.encode(label);
            ideals[matrixno] = code;
        }
        return ideals;
    }

    public void printInput(double[] input) {
        for (int rowno = 0; rowno < numRows; rowno++) {
            for (int colno = 0; colno < numCols; colno++) {
                double cell = input[rowno * numCols + colno];
                System.out.printf("%3.0f ", cell);
            }
            System.out.println("");
        }
    }

    public void printMatrix(final MnistMatrix matrix) {
        System.out.println("label: " + matrix.getLabel());
        for (int r = 0; r < matrix.getNumberOfRows(); r++ ) {
            for (int c = 0; c < matrix.getNumberOfColumns(); c++) {
                System.out.print(matrix.getValue(r, c) + " ");
            }
            System.out.println();
        }
    }

    public Equilateral getEq() {
        return eq;
    }
}
