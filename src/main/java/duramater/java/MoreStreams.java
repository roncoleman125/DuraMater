package duramater.java;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MoreStreams {
    public static void main(String[] args) {
        // 1. Slice a 2D array using streams.
        // See https://stackabuse.com/java-convert-array-to-stream/
        // See https://stackoverflow.com/questions/40902315/java-stream-toarray-convert-to-a-specific-type-of-array
        double[][] inputs = {{1,2},{3,4},{5,6},{7,8}};
        double[][] arrays = Arrays.stream(inputs,0,2).toArray(double[][]::new);
        Stream.of(arrays).forEach(e -> {
            Arrays.stream(e).forEach(d -> System.out.print(d+" "));
            System.out.println("");
        });
    }
}
