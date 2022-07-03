package duramater.util;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MatrixHelper {

    public static double[][] slice(double[][] src,int startRow, int endRow) {
        // Method 1:
        double[][] dest = Arrays.copyOfRange(src,startRow,endRow);

        // Method 2:
//        double[][] dest = Stream.of(src, startRow, endRow).toArray(double[][]::new);

        // Method 3:
//        int numRows = endRow-startRow;
//        assert(src.length >= numRows);
//
//        int numCols = src[0].length;
//
//        double[][] dest = new double[numRows][numCols];
//
//        IntStream.range(0,numCols).forEach(colno -> {
//            IntStream.range(startRow,startRow+numRows).forEach(rowno -> {
//                dest[rowno-startRow][colno] = src[rowno][colno];
//            });
//        });
        return dest;
    }

    public static double[][] transpose(double[][] src) {
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
}