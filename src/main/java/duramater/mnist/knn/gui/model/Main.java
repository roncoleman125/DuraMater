package duramater.mnist.knn.gui.model;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        MnistMatrix[] mnistMatrix = new MnistDataReader().readData("observations/train-images.idx3-ubyte", "observations/train-labels.idx1-ubyte");
        printMnistMatrix(mnistMatrix[mnistMatrix.length - 1]);
        mnistMatrix = new MnistDataReader().readData("observations/t10k-images.idx3-ubyte", "observations/t10k-labels.idx1-ubyte");
        printMnistMatrix(mnistMatrix[0]);
    }

    private static void printMnistMatrix(final MnistMatrix matrix) {
        System.out.println("label: " + matrix.getLabel());
        for (int r = 0; r < matrix.getNumberOfRows(); r++ ) {
            for (int c = 0; c < matrix.getNumberOfColumns(); c++) {
                System.out.print(matrix.getValue(r, c) + " ");
            }
            System.out.println();
        }
    }
}
