package duramater.mnist.mlp;

import duramater.matrix.Mop;
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
import java.util.Date;

public class MnistTrainNetwork {
    /**
     * These learning parameters generally give good results according to literature,
     * that is, the training algorithm converges with the tolerance below.
     * */
    public final static double LEARNING_RATE = 0.25;
    public final static double LEARNING_MOMENTUM = 0.25;

    /** Error tolerance: 1% */
    public final static double TOLERANCE = 0.01;

    public final static int NUM_SAMPLES = 2000;

    public final static String IN_DIR = System.getProperty("indir","c:/tmp");
    public final static String NET_PATH = IN_DIR + "/encogmnist-"+NUM_SAMPLES+".bin";

    /**
     * The main method.
     * @param args No arguments are used.
     */
    public static void main(final String args[]) throws Exception {
        System.out.println("started: "+new Date());
        System.out.println("model: "+NET_PATH);

        ////////////////
//        MnistMatrix[] mnistTrainMatrix = new MnistDataReader().readData("data/train-images.idx3-ubyte", "data/train-labels.idx1-ubyte");
//        MnistArrays trainingArrays = new MnistArrays(mnistTrainMatrix);
//
////        double[][] trainInputs = trainingArrays.getInputs(60000);
////        double[][] trainIdeals = trainingArrays.getIdeals(60000);
//        double[][] trainInputs = trainingArrays.getInputs(NUM_SAMPLES);
//        double[][] trainIdeals = trainingArrays.getIdeals(NUM_SAMPLES);
        IMLoader mloader = new MLoader("data/train-images.idx3-ubyte", "data/train-labels.idx1-ubyte");
        IMLoader.Normal normal = mloader.normalize();

        Mop mop = new Mop();
        double[][] trainInputs = mop.slice(normal.pixels(),0,NUM_SAMPLES);
        double[][] trainIdeals = mop.slice(normal.labels(),0,NUM_SAMPLES);

        assert(trainIdeals[0].length == 9);
        assert(trainInputs[0].length == 28*28);

        BasicNetwork network = new BasicNetwork();

        network.addLayer(new BasicLayer(null, true, 28*28));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 100));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 75));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 9));

        network.getStructure().finalizeStructure();

        network.reset();
        EncogHelper.summarize(network);

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
        final int MAX_SAME_COUNT = 3*EncogHelper.LOG_FREQUENCY;
        EncogHelper.log(epoch, error,false, false);
        do {
            training.iteration();

            epoch++;

            error = training.getError();

            if(error < minError) {
                minError = error;
                sameCount = 1;
                EncogDirectoryPersistence.saveObject(new File(NET_PATH),network);
            }
            else
                sameCount++;

            if(sameCount >= MAX_SAME_COUNT)
                break;

            EncogHelper.log(epoch, error, false, false);
        } while (error > TOLERANCE && epoch < EncogHelper.MAX_EPOCHS);

        EncogHelper.log(epoch, error,sameCount > MAX_SAME_COUNT, true);

        training.finishTraining();

        if(error < minError)
            EncogDirectoryPersistence.saveObject(new File(NET_PATH), network);

        MExercise mv = new MExercise(network,trainingSet);
        MExercise.Report report = mv.report();
        double success = report.hit()/((double) report.tried()) * 100;
        System.out.printf("success rate = %d/%d (%4.1f%%)\n", report.hit(), report.tried(), success);

        Encog.getInstance().shutdown();

        System.out.println("finished: "+new Date());
    }
}
