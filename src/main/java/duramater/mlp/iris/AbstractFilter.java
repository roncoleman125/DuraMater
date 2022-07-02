package duramater.mlp.iris;

import java.util.stream.Stream;

abstract public class AbstractFilter {
    public enum Which { NONE, CODED_INPUTS, CODED_OUTPUTS }

    public abstract double[] encode(int cat);

    public abstract int decode(double[] output);

    public abstract String decompile(int decoding);

    public abstract int compile(String plain);

    public abstract double[][] slice(Which kind, int start, int end);

    protected AbstractFilter(double[][] inputs, double[][] outputs) {
        validate(inputs);
        validate(outputs);
    }

    protected void validate(double[][] observations) {
        assert(observations != null);
        assert(observations.length >= 1);

        int numRows = observations.length;
        assert(numRows > 0);

        Stream.of(observations).forEach(feature -> {
            assert(feature != null);
            assert(feature.length == numRows);
        });
    }
}
