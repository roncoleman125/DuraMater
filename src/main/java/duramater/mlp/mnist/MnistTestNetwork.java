package duramater.mlp.mnist;

import duramater.knn.mnist.model.MnistArrays;
import duramater.knn.mnist.model.MnistDataReader;
import duramater.knn.mnist.model.MnistMatrix;
import duramater.util.EncogHelper;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.File;

/**
 * XOR: This example is essentially the "Hello World" of neural network
 * programming. This example shows how to construct an Encog neural network to
 * predict the report from the XOR operator. This example uses backpropagation
 * to train the neural network.
 *
 * This example attempts to use a minimum of Encog values to create and train
 * the neural network. This allows you to see exactly what is going on. For a
 * more advanced example, that uses Encog factories, refer to the XORFactory
 * example.
 *
 * The original version of this code does not appear to converge. I fixed this
 * problem by using two neurons in the hidden layer and instead of ramped activation,
 * sigmoid activation. This makes the network reflect the model in figure 1.1
 * in the book, d. 11. I also added more comments to make the code more explanatory.
 * @author Ron Coleman
 * @date 24 Oct 2017
 */
public class MnistTestNetwork {
    /**
     * The main method.
     * @param args No arguments are used.
     */
    public static void main(final String args[]) throws Exception {
        ////////////////
        MnistMatrix[] mnistTestMatrix = new MnistDataReader().readData("data/t10k-images.idx3-ubyte", "data/t10k-labels.idx1-ubyte");
        MnistArrays testArrays = new MnistArrays(mnistTestMatrix);

        double[][] testInputs = testArrays.getInputs(10000);
        double[][] testIdeals = testArrays.getIdeals(10000);

//        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File("/users/roncoleman/tmp/encogmnist.bin"));
        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File("/users/roncoleman/tmp/encogmnist.bin"));


        MLDataSet testSet = new BasicMLDataSet(testInputs, testIdeals);

        int hit = 0;
        for(int no=0; no < testSet.size(); no++) {
            MLDataPair pair = testSet.get(no);

            MLData outputs = network.compute(pair.getInput());

            double[] encoded = outputs.getData();
            double[] ideal = pair.getIdeal().getData();

            int predicted = testArrays.getEq().decode(encoded);
            int actual = testArrays.getEq().decode(ideal);
            if(predicted == actual) {
                hit++;
                System.out.println(hit+" of "+no);
            }
        }
        System.out.println(hit+" of "+testSet.size());

    }
}
