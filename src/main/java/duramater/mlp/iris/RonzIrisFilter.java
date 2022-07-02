package duramater.mlp.iris;

import org.apache.commons.math3.stat.StatUtils;
import org.encog.mathutil.Equilateral;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RonzIrisFilter extends AbstractFilter {
    final static double HI = 1;
    final static double LO = -1;

    // Static since we're coding the reverse mappings manually.
    final protected static Map<Integer, String> cat2Species = new HashMap<>();
    protected final Map<Integer,NormalizedField> colIdx2Normalizer = new HashMap<>();

    static {
        // Reverses mapping to facilitate decoding.
        // See https://www.geeksforgeeks.org/how-to-iterate-hashmap-in-java/
        CsvDicer.species2Cat.forEach((species, cat) -> cat2Species.put(cat,species));
    }

    protected final Equilateral eq = new Equilateral(cat2Species.size(), HI, LO);

    protected double[][] inputsNormalized;
    protected double[][] outputsEncoded;

    public RonzIrisFilter(double[][] inputs, double[][] outputs) {
        super(inputs,outputs);
        transform(inputs,outputs);
    }

    protected void transform(double[][] inputs, double[][] outputs) {
        /////////////// Normalize inputs
        // Pass 1: calculate normalize fields
        int numCols = inputs.length;

        IntStream.range(0,numCols).forEach(colno -> {
            double[] column = inputs[colno];

            double hi = StatUtils.max(column);
            double lo = StatUtils.min(column);
            NormalizedField norm = new NormalizedField(NormalizationAction.Normalize,
                    null,hi,lo,HI,LO);
            colIdx2Normalizer.put(colno, norm);
        });

        // Pass 2: Using the normalized field, normalize the inputs
        int numRows = inputs[0].length;

        inputsNormalized = new double[numCols][];

        IntStream.range(0,numCols).forEach(colno -> {
            inputsNormalized[colno] = new double[numRows];
            IntStream.range(0,numRows).forEach(rowno -> {
                double datum = inputs[colno][rowno];
                NormalizedField normalizer =  colIdx2Normalizer.get(colno);
                inputsNormalized[colno][rowno] = normalizer.normalize(datum);
            });
        });

        /////////////// Encode outputs
        // Need only one pass here since the category is already a set
        outputsEncoded = new double[numRows][];
        IntStream.range(0,numRows).forEach(rowno -> {
            int cat = (int) outputs[0][rowno];
            outputsEncoded[rowno] = encode(cat);
        });

        report(inputs,outputs);
    }

    @Override
    public double[][] slice(Which kind, int start, int end) {
        switch(kind) {
            case CODED_INPUTS -> {
                return slice(inputsNormalized,start,end);
            }
            case CODED_OUTPUTS -> {
                return slice(outputsEncoded,start,end);
            }
            default -> {
                assert(false);
            }
        }
        return null;
    }

    protected double[][] slice(double[][] src,int start, int end) {
        double[][] dest = new double[src.length][end-start];

        IntStream.range(0,dest.length).forEach(colno -> {
            IntStream.range(0,dest[colno].length).forEach(rowno -> {
                dest[colno][rowno] = src[colno][rowno+start];
            });
        });
        return dest;
    }

    public double[] encode(int cat) {
        return eq.encode(cat);
    }
    public int decode(double[] output) {
        return eq.decode(output);
    }

    public String decompile(int decoding) {
        return cat2Species.get(decoding);
    }

    @Override
    public int compile(String plain) {
        assert(CsvDicer.species2Cat.containsKey(plain));
        return CsvDicer.species2Cat.get(plain);
    }

    protected void report(double[][] inputs, double[][] outputs) {
        System.out.println("Inputs ---");
        System.out.printf("%3s ","#");
        IntStream.range(0, inputsNormalized.length).forEach(colno -> {
            String name = CsvDicer.COL_SHORT_NAMES[colno];
            System.out.printf("%4s ",name);
        });

        IntStream.range(0, inputsNormalized[0].length).forEach(rowno -> {
            IntStream.range(0,inputsNormalized.length).forEach(colno -> {
                double unnormed = inputs[colno][rowno];
                double normed = inputsNormalized[colno][rowno];
                System.out.printf("%3d %4.1 -> %4.2 ",rowno,unnormed,normed);
            });
        });
    }

    public static void main(String[] args) {
//        // Create training data
//        CsvDicer csvDicer = new CsvDicer("data/iris.csv");
//        csvDicer.load();
//
//        double[][] inputs = Stream.of(csvDicer.getNames())
//                .filter(name -> !name.equals("Species"))
//                .map(name -> csvDicer.dice(name).observations()).toArray(double[][]::new);
//
//        double[][] outputs = Stream.of(csvDicer.getNames())
//                .filter(name -> name.equals("Species"))
//                .map(name -> csvDicer.dice(name).observations()).toArray(double[][]::new);
//
//        RonzIrisFilter rif = new RonzIrisFilter(inputs,outputs);
    }
}
