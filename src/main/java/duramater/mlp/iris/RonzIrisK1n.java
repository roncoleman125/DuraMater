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

import org.apache.commons.math3.stat.StatUtils;
import org.encog.Encog;
import org.encog.mathutil.Equilateral;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static duramater.mlp.iris.IrisMatrix.slice;
import static duramater.mlp.iris.IrisMatrix.transpose;


/**
 * This program was evolved from XorHelloWorld to train and test an MLP on iris data.
 * @author Ron.Coleman
 * @date 29.Oct.2019
 */
public class RonzIrisK1n {
    record Candidate(double[] pt, double dist, int no) {}

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

    static CsvDicer csvDicer = null;
    static List<NormalizedField> normalizers = new ArrayList<>();
    static final Equilateral eq = new Equilateral(CsvDicer.species2Cat.size(), NORMALIZED_HI, NORMALIZED_LO);

    /**
     * The main method.
     * @param args No arguments are used.
     */
    public static void main(final String args[]) {
        init();

        int missed = 0;

        System.out.printf("%2s %11s %11s\n", "#", "Ideal", "Actual");

        // Receives the network output -- the equilateral encoding
        double[] output = new double[2];

        // Test each row in the pt data
        for(int k = 0; k < TESTING_INPUTS.length; k++) {
            // Get the input
            double[] target = TESTING_INPUTS[k];

            Candidate nearest = getNearest(target);

            // Get the output and decode it to a subtype index.
            int predictedno = eq.decode(TRAINING_IDEALS[nearest.no()]);

            // Get the ideal and decode it to a subtype index.
            double[] ideals = TESTING_IDEALS[k];
            int idealno = eq.decode(ideals);

            // Convert them both to string names.
            String ideal = CsvDicer.cat2Species.get(idealno);
            String predicted = CsvDicer.cat2Species.get(predictedno);

            // If the string names aren't equal, record a miss.
            System.out.printf("%2d %11s %11s ", (k+1), ideal, predicted);

            if(!predicted.equals(ideal)) {
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

    static Candidate getNearest(double[] target) {
        List<Candidate> candidates =
                IntStream.range(0,TRAINING_INPUTS.length)
                        .mapToObj(no -> new Candidate(TRAINING_INPUTS[no],getDist(TRAINING_INPUTS[no],target),no))
                        .sorted((obj1, obj2) -> {
                            if(obj1.dist() > obj2.dist())
                                return 1;
                            else
                                return -1;
                        }).collect(Collectors.toList());
//        Candidate nearest = candidates.get(0);
//        return nearest;
        Map<Integer,Integer> votes = new HashMap<>();
        IntStream.range(0,4).forEach(idx -> {
            int candidate = eq.decode(TRAINING_IDEALS[candidates.get(idx).no()]);
            int freq = votes.getOrDefault(candidate,0);
            votes.put(candidate,freq+1);
        });

        int winner = votes.entrySet().stream().sorted((e1,e2) -> {
            if(e1.getValue() > e2.getValue())
                return 1;
            else
                return -1;
        }).collect(Collectors.toList()).get(0).getKey();

        Candidate popular = candidates.get(winner);
        return popular;
    }

    public static double getDist(double[] p1, double[] p2) {
        double l2 = IntStream.range(0,p1.length)
                             .mapToDouble(idx -> (p1[idx]-p2[idx])*(p1[idx]-p2[idx])).sum();
        return l2;
    }

    /**
     * Initializes the training and pt arrays.
     */
    static void init() {
        CsvDicer csvDicer = new CsvDicer("data/iris.csv");
        double[][] observations = csvDicer.dice();

        double[][] inputs = normalize(observations,0,4);

        TRAINING_INPUTS = slice(transpose(inputs),0,120);
        TESTING_INPUTS = slice(transpose(inputs),120,150);

        double[][] outputs = encode(observations,4,5);

        TRAINING_IDEALS = slice(outputs,0,120);
        TESTING_IDEALS = slice(outputs,120,150);

        report("training",TRAINING_INPUTS,TRAINING_IDEALS);

    }

    static double[][] normalize(double[][] observations, int startCol, int endCol) {
        /////////////// Normalize inputs
        // Pass 1: calculate normalize fields

        IntStream.range(startCol,endCol).forEach(colno -> {
            double[] column = observations[colno];

            double hi = StatUtils.max(column);
            double lo = StatUtils.min(column);
            NormalizedField normalizer = new NormalizedField(NormalizationAction.Normalize,
                    null,hi,lo, NORMALIZED_HI, NORMALIZED_LO);
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

    static double[][] encode(double[][] observations, int startCol, int endCol) {
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

    static void report(String msg, double[][] inputs, double[][] outputs) {
        System.out.println(msg+" inputs ---");

        final int numRows = inputs.length;
        final int numCols = inputs[0].length;

        IntStream.range(0,numCols).forEach(colno -> {
            String name = CsvDicer.COL_SHORT_NAMES[colno];
            double actualHi = normalizers.get(colno).getActualHigh();
            double actualLo = normalizers.get(colno).getActualLow();
            System.out.printf("%s: %5.2f - %5.2f\n",name,actualLo,actualHi);
        });

        System.out.printf("%3s ","#");
        IntStream.range(0, numCols).forEach(colno -> {
            String name = CsvDicer.COL_SHORT_NAMES[colno];
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
            String species = CsvDicer.decompile(decoding);
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
        CsvDicer d = null;
        System.out.println("Iris encoded data outputs\n--------------------------------");
        System.out.printf("%5s %5s   %5s   %s\n","Index","t1","t2","Decoding");

        for(int rowIndex=0; rowIndex < outputs.length; rowIndex++) {
            double[] encoding = outputs[rowIndex];
            int decoding = eq.decode(encoding);
            String species = CsvDicer.decompile(decoding);
            System.out.printf("%3d   %7.4f %7.4f %s\n",(rowIndex),encoding[0],encoding[1],species);
        }
    }
}
