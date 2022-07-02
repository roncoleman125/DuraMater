package duramater.knn.mnist.model;

import java.io.IOException;

public class MnistFrame {
    protected MnistArrays arrays;
    public MnistFrame(String pathImages, String pathLabels) {
        try {
            MnistMatrix[] matrices = new MnistDataReader().readData(pathImages, pathLabels);
            this.arrays = new MnistArrays(matrices);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double[] getImageRow(int rowno) {
        return this.arrays.getInputs()[rowno];
    }

    public double[] getLabelRow(int rowno) {
        return this.arrays.getIdeals()[rowno];
    }
}
