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

import duramater.util.EncogHelper;
import static duramater.mlp.iris.IrisMatrix.slice;

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
public class RonzIris {
    public static boolean DEBUGGING = Boolean.parseBoolean(System.getProperty("debug","false"));

    final static double HI = 1;
    final static double LO = -1;

    /** Error tolerance */
    public final static double TOLERANCE = 0.01;
    public final static double LEARNING_RATE = 0.50;
    public final static double LEARNING_MOMENTUM = 0.50;
    public static final int NUM_TRAINING_ROWS = 120;
    public static final int NUM_TESTING_ROWS = 30;

    // Matrices will contain training & testing data
    public static double TRAINING_INPUTS[][] = null;
    public static double TRAINING_IDEALS[][] = null;

    public static double TESTING_INPUTS[][] = null;
    public static double TESTING_IDEALS[][] = null;

    protected static List<NormalizedField> normalizers = new ArrayList<>();
    protected static final Equilateral eq = new Equilateral(CsvDicer.species2Cat.size(), HI, LO);

    /**
     * The main method.
     * @param args No arguments are used.
     */
    public static void main(final String args[]) {
        RonzIris ri = new RonzIris();
        // Initialize the data
        ri.init();

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
        System.out.println("Network testing results:");

        int missed = 0;

        System.out.printf("%2s %11s %11s\n", "#", "Ideal", "Actual");

        // Receives the network output -- the equilateral encoding
        double[] output = new double[2];

        // Test each row in the testing data
        for(int k = 0; k < TESTING_INPUTS.length; k++) {
            // Get the input
            double[] input = TESTING_INPUTS[k];

            // Get the output and decode it to a subtype index.
            network.compute(input, output);
//            int actualIndex = ric.decode(output);
//
//            // Get the ideal and decode it to a subtype index.
//            double[] ideals = TESTING_IDEALS[k];
//            int idealIndex = ric.decode(ideals);
//
//            // Convert them both to string names.
//            String ideal = ric.decompile(idealIndex);
//            String actual = ric.decompile(actualIndex);
//
//            // If the string names aren't equal, record a miss.
//            System.out.printf("%2d %11s %11s ", (k+1), ideal, actual);
//
//            if(!ideal.equals(actual)) {
//                System.out.print("MISSED!");
//                missed++;
//            }

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
     * Initializes the training and testing arrays.
     */
    public void init() {
        CsvDicer csvDicer = new CsvDicer("data/iris.csv");
        double[][] observations = csvDicer.dice();

        double[][] inputsNormalized = normalize(observations,0,4);
        report(inputsNormalized,null);

        TRAINING_INPUTS = slice(transpose(inputsNormalized),0,120);
        TESTING_INPUTS = slice(transpose(inputsNormalized),120,150);

//
//        double[][] inputs = Stream.of(csvDicer.getNames())
//                .filter(name -> !name.equals("Species"))
//                .map(name -> csvDicer.dice(name).observations()).toArray(double[][]::new);
//
//        double[][] outputs = Stream.of(csvDicer.getNames())
//                .filter(name -> name.equals("Species"))
//                .map(name -> csvDicer.dice(name).observations()).toArray(double[][]::new);
//
//        ric = new RonzIrisFilter(inputs,outputs);
//
//        TRAINING_INPUTS = ric.slice(AbstractFilter.Which.CODED_INPUTS,0,NUM_TRAINING_ROWS);
//        TRAINING_IDEALS = ric.slice(AbstractFilter.Which.CODED_OUTPUTS,0,NUM_TRAINING_ROWS);
//
//        TESTING_INPUTS = ric.slice(AbstractFilter.Which.CODED_INPUTS,NUM_TRAINING_ROWS,150);
//        TESTING_IDEALS = ric.slice(AbstractFilter.Which.CODED_OUTPUTS,NUM_TRAINING_ROWS,150);
    }

    protected double[][] normalize(double[][] observations, int startCol, int endCol) {
        /////////////// Normalize inputs
        // Pass 1: calculate normalize fields

        IntStream.range(startCol,endCol).forEach(colno -> {
            double[] column = observations[colno];

            double hi = StatUtils.max(column);
            double lo = StatUtils.min(column);
            NormalizedField normalizer = new NormalizedField(NormalizationAction.Normalize,
                    null,hi,lo,HI,LO);
            normalizers.add(normalizer);
        });

        // Pass 2: Using the normalized field, normalize the inputs
        int numRows = observations[0].length;
        int numCols = endCol-startCol;

        double[][] inputsNormalized = new double[numCols][];

        IntStream.range(0,numCols).forEach(colno -> {
            inputsNormalized[colno] = new double[numRows];
            IntStream.range(0,numRows).forEach(rowno -> {
                double datum = observations[colno][rowno];
                NormalizedField normalizer =  normalizers.get(colno);
                inputsNormalized[colno][rowno] = normalizer.normalize(datum);
            });
        });
        return inputsNormalized;
    }

    protected double[][] encodeOutputs(double[][] observations,int startCol,int endCol) {
        int numRows = observations[0].length;
        int numCols = endCol-startCol;
        assert(numCols == 1);

        // Need only one pass here since the category is already a set
        double[][] outputsEncoded = new double[numRows][];
        IntStream.range(0,numRows).forEach(rowno -> {
            int cat = (int) observations[startCol][rowno];
            outputsEncoded[rowno] = eq.encode(cat);
        });
        return outputsEncoded;
    }

    protected double[][] transpose(double[][] src) {
        int numRows = src[0].length;
        int numCols = src.length;

        double[][] dest = new double[numRows][numCols];

        IntStream.range(0,numRows).forEach(rowno -> {
            IntStream.range(0,numCols).forEach(colno -> {
                dest[rowno][colno] = src[colno][rowno];
            });
        });

        return dest;
    }

    protected void report(double[][] inputs, double[][] outputs) {
        System.out.println("Inputs ---");
        System.out.printf("%3s ","#");
        IntStream.range(0, inputs.length).forEach(colno -> {
            String name = CsvDicer.COL_SHORT_NAMES[colno];
            System.out.printf("%-12s | ",name);
        });
        System.out.println("");

        IntStream.range(0, inputs[0].length).forEach(rowno -> {
            System.out.printf("%3d ",rowno);
            IntStream.range(0,inputs.length).forEach(colno -> {
                NormalizedField normalizer = normalizers.get(colno);
                double denorm = normalizer.deNormalize(inputs[colno][rowno]);
                double norm = inputs[colno][rowno];
                assert(Math.abs(denorm-norm) < 1E-5);
                System.out.printf("%3.1f -> %5.2f | ",denorm,norm);
            });
            System.out.println("");
        });
    }

    /**
     * Outputs the ideal encoded outputs.
     * @param ideals Encoded matrix
     */
//    private void outputIdeals(double[][] ideals) {
//        System.out.println("Iris encoded data outputs\n--------------------------------");
//        System.out.printf("%5s %5s   %5s   %s\n","Index","t1","t2","Decoding");
//
//        for(int rowIndex=0; rowIndex < ideals.length; rowIndex++) {
//            double[] encoding = ideals[rowIndex];
//            int decoding = ric.decode(encoding);
//            String species = ric.decompile(decoding);
//            System.out.printf("%3d   %7.4f %7.4f %s\n",(rowIndex),encoding[0],encoding[1],species);
//        }
//    }
}
