package duramater.mlp.mnist;

import duramater.knn.mnist.model.MnistArrays;
import duramater.knn.mnist.model.MnistDataReader;
import duramater.knn.mnist.model.MnistMatrix;
import duramater.util.EncogHelper;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
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
public class MnistTrainNetwork {
    /**
     * These learning parameters generally give good results according to literature,
     * that is, the training algorithm converges with the tolerance below.
     * */
    public final static double LEARNING_RATE = 0.25;
    public final static double LEARNING_MOMENTUM = 0.25;

    /** Error tolerance: 1% */
    public final static double TOLERANCE = 0.01;

    /** Inputs necessary for XOR. */
    public static double XOR_INPUTS[][] = {
            {0.0, 0.0},
            {0.0, 1.0},
            {1.0, 0.0},
            {1.0, 1.0}
    };

    /** Ideals necessary for XOR.*/
    public static double XOR_IDEALS[][] = {
            {0.0},
            {1.0},
            {1.0},
            {0.0}};

    /**
     * The main method.
     * @param args No arguments are used.
     */
    public static void main(final String args[]) throws Exception {
        ////////////////
        MnistMatrix[] mnistTrainMatrix = new MnistDataReader().readData("data/train-images.idx3-ubyte", "data/train-labels.idx1-ubyte");
        MnistArrays trainingArrays = new MnistArrays(mnistTrainMatrix);

//        double[][] trainInputs = trainingArrays.getInputs(60000);
//        double[][] trainIdeals = trainingArrays.getIdeals(60000);
        double[][] trainInputs = trainingArrays.getInputs(2000);
        double[][] trainIdeals = trainingArrays.getIdeals(2000);

        BasicNetwork network = new BasicNetwork();

        network.addLayer(new BasicLayer(null, true, 28*28));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 100));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 75));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 9));

        network.getStructure().finalizeStructure();

        network.reset();

        MLDataSet trainingSet = new BasicMLDataSet(trainInputs, trainIdeals);

        // Use a training object for the learning algorithm, backpropagation.
        final BasicTraining training = new ResilientPropagation(network,trainingSet);
//      final BasicTraining training = new Backpropagation(network, trainingSet,LEARNING_RATE,LEARNING_MOMENTUM);

        // Set learning batch size: 0 = batch, 1 = online, n = batch size
        // See org.encog.neural.networks.training.BatchSize
        // train.setBatchSize(0);

        int epoch = 0;

        double minError = Double.MAX_VALUE;
        int sameCount = 0;
        double error = 0.0;
        final int MAX_SAME_COUNT = 5*EncogHelper.LOG_FREQUENCY;
        EncogHelper.log(epoch, training,false);
        do {
            training.iteration();

            epoch++;

            error = training.getError();

            if(error < minError) {
                minError = error;
                sameCount = 1;
            }
            else
                sameCount++;

            if(sameCount >= MAX_SAME_COUNT)
                break;

            EncogHelper.log(epoch, training,false);
        } while (error > TOLERANCE && epoch < EncogHelper.MAX_EPOCHS);

        EncogHelper.log(epoch, training,true);

        training.finishTraining();

        EncogDirectoryPersistence.saveObject(new File("/users/roncoleman/tmp/encogmnist.bin"),network);

//        EncogHelper.log(epoch, training,true);
//        EncogHelper.report(trainingSet, network);
//        EncogHelper.describe(network);

        Encog.getInstance().shutdown();
    }
}