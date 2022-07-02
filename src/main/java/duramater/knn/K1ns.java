package duramater.knn;

import duramater.knn.mnist.model.MnistArrays;
import duramater.knn.mnist.model.MnistDataReader;
import duramater.knn.mnist.model.MnistMatrix;
import org.encog.mathutil.Equilateral;

import java.io.IOException;

public class K1ns {
    public static void main(String[] args) throws Exception {
        K1ns test = new K1ns();
        test.execute();
    }

    public void execute() throws IOException {
        System.out.println(this.getClass().getName());

        ////////////////
        MnistMatrix[] mnistTrainMatrix = new MnistDataReader().readData("observations/train-images.idx3-ubyte", "observations/train-labels.idx1-ubyte");
        MnistArrays trainingArrays = new MnistArrays(mnistTrainMatrix);

        double[][] trainInputs = trainingArrays.getInputs(60000);
        double[][] trainIdeals = trainingArrays.getIdeals(60000);

        ////////////////
        MnistMatrix[] mnistTestMatrix = new MnistDataReader().readData("observations/t10k-images.idx3-ubyte", "observations/t10k-labels.idx1-ubyte");
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
