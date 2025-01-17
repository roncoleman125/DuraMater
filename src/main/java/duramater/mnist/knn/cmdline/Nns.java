package duramater.mnist.knn.cmdline;

import duramater.mnist.knn.gui.model.MnistArrays;
import duramater.mnist.knn.gui.model.MnistDataReader;
import duramater.mnist.knn.gui.model.MnistMatrix;
import org.encog.mathutil.Equilateral;

import java.io.IOException;

public class Nns {
    public static void main(String[] args) throws Exception {
        Nns test = new Nns();
        test.execute();
    }

    public void execute() throws IOException {
        System.out.println(this.getClass().getName());

        ////////////////
        MnistMatrix[] mnistTrainMatrix = new MnistDataReader().readData("data/train-images.idx3-ubyte", "data/train-labels.idx1-ubyte");
        MnistArrays trainingArrays = new MnistArrays(mnistTrainMatrix);

        double[][] trainInputs = trainingArrays.getInputs(60000);
        double[][] trainIdeals = trainingArrays.getIdeals(60000);

        ////////////////
        MnistMatrix[] mnistTestMatrix = new MnistDataReader().readData("data/t10k-images.idx3-ubyte", "data/t10k-labels.idx1-ubyte");
        MnistArrays testArrays = new MnistArrays(mnistTestMatrix);

        double[][] testInputs = testArrays.getInputs(10000);
        double[][] testIdeals = testArrays.getIdeals(10000);

        Equilateral eq = new Equilateral(10, 1.0, 0.0);
        int hit = 0;
        for (int testno = 0; testno < testInputs.length; testno++) {
            if(testno % 100 == 0)
                System.out.println("testno: "+testno+" hit:"+hit);
            double[] testInput = testInputs[testno];
            double minDist2 = Double.MAX_VALUE;
            int minDistIdex = -1;
            for (int trainno = 0; trainno < trainIdeals.length; trainno++) {
                double dist2 = 0;
                double[] trainInput = trainInputs[trainno];
                for (int colno = 0; colno < testInput.length; colno++) {
                    dist2 += (testInput[colno] - trainInput[colno]) * (testInput[colno] - trainInput[colno]);
                }
                if (dist2 < minDist2) {
                    minDist2 = dist2;
                    minDistIdex = trainno;
                }
            }
            int predictedLabel = eq.decode(trainIdeals[minDistIdex]);
            int actualLabel = eq.decode(testIdeals[testno]);
            if (predictedLabel == actualLabel)
                hit++;
        }
        double ratio = (double)hit / testIdeals.length;
        System.out.println("tests: "+testInputs.length+" hits: "+hit);
        System.out.println("ratio: "+ratio);
    }

}
