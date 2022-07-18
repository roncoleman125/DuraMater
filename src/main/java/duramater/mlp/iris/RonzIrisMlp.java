/*
 Copyright (c) Ron Coleman

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package duramater.mlp.iris;

import duramater.matrix.IMop;
import duramater.matrix.Mop;
import duramater.util.IrisHelper;
import duramater.util.EncogHelper;
import org.apache.commons.math3.stat.StatUtils;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.Equilateral;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This program was evolved from XorHelloWorld to train and test an MLP on iris data.
 * @author Ron.Coleman
 * @date 29.Oct.2019
 */
public class RonzIrisMlp {
    public static boolean DEBUGGING = Boolean.parseBoolean(System.getProperty("debug","false"));

    final static double NORMALIZED_HI = 1;
    final static double NORMALIZED_LO = -1;

    /** Error tolerance */
    public final static double TOLERANCE = 0.01;

    // Matrices will contain training & pt data
    static double TRAINING_INPUTS[][] = null;
    static double TRAINING_IDEALS[][] = null;

    static double TESTING_INPUTS[][] = null;
    static double TESTING_IDEALS[][] = null;

    static IrisHelper csvDicer = null;
    static List<NormalizedField> normalizers = new ArrayList<>();
    static final Equilateral eq = new Equilateral(IrisHelper.species2Cat.size(), NORMALIZED_HI, NORMALIZED_LO);

    /**
     * The main method.
     * @param args No arguments are used.
     */
    public static void main(final String args[]) {
        init();
//        System.exit(-1);

        // Build the network.
        BasicNetwork network = new BasicNetwork();

        // Input layer
        network.addLayer(new BasicLayer(null, true, 4));

        // Hidden layer
        network.addLayer(new BasicLayer(new ActivationTANH(), true, 4));

        // Output layer
        network.addLayer(new BasicLayer(new ActivationTANH(), false, 2));

        // No more layers to add
        network.getStructure().finalizeStructure();

        // Randomize the weights
        network.reset();
        System.out.println("Network description: before training");
        EncogHelper.describe(network);

        MLDataSet trainingSet = new BasicMLDataSet(TRAINING_INPUTS, TRAINING_IDEALS);

        // Use a training object for the learning algorithm, in this case, an improved
        // backpropagation. For details on what this does see the javadoc.
//        final Propagation train = new Backpropagation(network, trainingSet,LEARNING_RATE,LEARNING_MOMENTUM);
        final Propagation train = new ResilientPropagation(network, trainingSet);

        // Set learning batch size: 0 = batch, 1 = online, n = batch size
        // See org.encog.neural.networks.training.BatchSize
         train.setBatchSize(0);

        int epoch = 0;

        EncogHelper.log(epoch, train,false);
        do {
            train.iteration();

            epoch++;

            EncogHelper.log(epoch, train,false);

        } while (train.getError() > TOLERANCE && epoch < EncogHelper.MAX_EPOCHS);

        train.finishTraining();
        EncogHelper.log(epoch, train,true);
        EncogHelper.report(trainingSet, network);

        System.out.println("Network description: after training");
        EncogHelper.describe(network);

        Encog.getInstance().shutdown();

        // Test the neural network
        System.out.println("Network pt results:");

        int missed = 0;

        System.out.printf("%2s %11s %11s\n", "#", "Ideal", "Actual");

        // Receives the network output -- the equilateral encoding
        double[] output = new double[2];

        // Test each row in the pt data
        for(int k = 0; k < TESTING_INPUTS.length; k++) {
            // Get the input
            double[] input = TESTING_INPUTS[k];

            // Get the output and decode it to a subtype index.
            network.compute(input, output);
            int actualno = eq.decode(output);

            // Get the ideal and decode it to a subtype index.
            double[] ideals = TESTING_IDEALS[k];
            int idealno = eq.decode(ideals);

            // Convert them both to string names.
            String ideal = IrisHelper.cat2Species.get(idealno);
            String actual = IrisHelper.cat2Species.get(actualno);


            // If the string names aren't equal, record a miss.
            System.out.printf("%2d %11s %11s ", (k+1), ideal, actual);

            if(!ideal.equals(actual)) {
                System.out.print("MISSED!");
                missed++;
            }

            System.out.print("\n");
        }

        // Compute the performance
        double tried = 30;
        double rate = missed / tried;

        double success = (1.0-rate) * 100;
        System.out.printf("success rate = %d/%d (%4.1f%%)", (int) (tried-missed), (int) tried, success);

        Encog.getInstance().shutdown();
    }

    /**
     * Initializes the training and pt arrays.
     */
    static void init() {
        double[][] observations = IrisHelper.load("data/iris.csv");

        IMop mop = new Mop();
        double[][] observations_ = mop.dice(observations,0,4);
        double[][] inputs = normalize(observations_);

        TRAINING_INPUTS = mop.slice(inputs,0,120);
        TESTING_INPUTS = mop.slice(inputs,120,150);

        observations_ = mop.dice(observations,4,5);
        double[][] outputs = encode(observations_);

        TRAINING_IDEALS = mop.slice(outputs,0,120);
        TESTING_IDEALS = mop.slice(outputs,120,150);

        report("training",TRAINING_INPUTS,TRAINING_IDEALS);
    }

    static double[][] normalize(double[][] src) {
        IMop mop = new Mop();
        int nRows = src.length;
        int nCols = src[0].length;
        IntStream.range(0,nCols).forEach(colno -> {
            double[] column = mop.transpose(mop.dice(src,colno,colno+1))[0];
//            double[] column = new double[nRows];
//            IntStream.range(0,nRows).forEach(rowno -> {
//                column[rowno] = src[rowno][colno];
//            });
            double hi = StatUtils.max(column);
            double lo = StatUtils.min(column);

            NormalizedField normalizer = new NormalizedField(NormalizationAction.Normalize,
                    null,hi,lo, NORMALIZED_HI, NORMALIZED_LO);
            normalizers.add(normalizer);
        });

        double[][] dest = new double[nRows][nCols];
        IntStream.range(0,nRows).forEach(rowno -> {
            IntStream.range(0,nCols).forEach(colno -> {
                double datum = src[rowno][colno];
                NormalizedField normalizer = normalizers.get(colno);
                double normal = normalizer.normalize(datum);
                dest[rowno][colno] = normal;
            });
        });

        return dest;

    }

    /**
     * Encodes the src -- assumes column-major order.
     * @param src
     * @return
     */
    static double[][] encode(double[][] src) {
        int numRows = src.length;
        int numCols = src[0].length;
        assert(numCols == 1);

        // Need only one pass here since the category is already a set
        double[][] dest = new double[numRows][];
        IntStream.range(0,numRows).forEach(rowno -> {
            int cat = (int) src[rowno][0];
            dest[rowno] = eq.encode(cat);
        });
        return dest;
    }

    static void report(String msg, double[][] inputs, double[][] outputs) {
        System.out.println(msg+" inputs ---");

        final int numRows = inputs.length;
        final int numCols = inputs[0].length;

        IntStream.range(0,numCols).forEach(colno -> {
            String name = IrisHelper.COL_SHORT_NAMES[colno];
            double actualHi = normalizers.get(colno).getActualHigh();
            double actualLo = normalizers.get(colno).getActualLow();
            System.out.printf("%s: %5.2f - %5.2f\n",name,actualLo,actualHi);
        });

        System.out.printf("%3s ","#");
        IntStream.range(0, numCols).forEach(colno -> {
            String name = IrisHelper.COL_SHORT_NAMES[colno];
            System.out.printf("%-12s | ",name);
        });
        System.out.println("");

        IntStream.range(0, numRows).forEach(rowno -> {
            System.out.printf("%3d ",rowno);
            IntStream.range(0,numCols).forEach(colno -> {
                NormalizedField normalizer = normalizers.get(colno);
                double denorm = normalizer.deNormalize(inputs[rowno][colno]);
                double norm = inputs[rowno][colno];
                System.out.printf("%3.1f -> %5.2f | ",denorm,norm);
            });
            System.out.println("");
        });

        int numOutputRows = outputs.length;
        int numOutputCols = outputs[0].length;
        System.out.println(msg+" outputs ---");
//        System.out.printf("%5s %5s   %5s   %s\n","Index","t1","t2","Decoding");
        System.out.printf("%3s ","#");
        IntStream.range(0,numOutputCols).forEach(colno -> {
            System.out.printf("%4s%d   ","t",(colno+1));
        });
        System.out.println("  Decoding");

        for(int rowno=0; rowno < numOutputRows; rowno++) {
            double[] encoding = outputs[rowno];
            int decoding = eq.decode(encoding);
            String species = IrisHelper.decompile(decoding);
//            System.out.printf("%3d   %7.4f %7.4f %s\n",(rowno),encoding[0],encoding[1],species);
            System.out.printf("%3d   ",(rowno));
            IntStream.range(0,encoding.length).forEach(colno -> {
                System.out.printf("%7.4f ",encoding[colno]);
            });
            System.out.printf("%d -> %s\n",decoding, species);
        }
    }

    /**
     * Outputs the ideal encoded outputs.
     * @param outputs Encoded matrix
     */
    static void outputIdeals(double[][] outputs) {
        IrisHelper d = null;
        System.out.println("Iris encoded data outputs\n--------------------------------");
        System.out.printf("%5s %5s   %5s   %s\n","Index","t1","t2","Decoding");

        for(int rowIndex=0; rowIndex < outputs.length; rowIndex++) {
            double[] encoding = outputs[rowIndex];
            int decoding = eq.decode(encoding);
            String species = IrisHelper.decompile(decoding);
            System.out.printf("%3d   %7.4f %7.4f %s\n",(rowIndex),encoding[0],encoding[1],species);
        }
    }
}
