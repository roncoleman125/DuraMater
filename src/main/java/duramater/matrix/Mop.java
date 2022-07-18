package duramater.matrix;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Mop implements IMop {

    public double[][] slice(double[][] src,int startRow, int endRow) {
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

    public double[][] transpose(double[][] src) {
        int nRows = src[0].length;
        int nCols = src.length;

        double[][] dest = new double[nRows][nCols];

        IntStream.range(0,nRows).forEach(rowno -> {
            IntStream.range(0,nCols).forEach(colno -> {
                dest[rowno][colno] = src[colno][rowno];
            });
        });

        return dest;
    }

    @Override
    public double[][] dice(double[][] src, int startCol, int endCol) {
//        double[][] dest = transpose(slice(transpose(src),start,end));

        int nCols = endCol-startCol;
        int nRows = src.length;
        double[][] dest = new double[nRows][nCols];
        IntStream.range(0,nRows).forEach(rowno -> {
            IntStream.range(startCol,endCol).forEach(colno -> {
                dest[rowno][colno-startCol] = src[rowno][colno];
            });
        });

        return dest;
    }

    public void print(String caption,double[][] src) {
        System.out.printf("--- %s: %dx%d\n",caption,src.length,src[0].length);
        Arrays.stream(src).forEach(row -> {
            Arrays.stream(row).forEach(cell -> System.out.printf("%2.0f ",cell));
            System.out.println("");
        });
    }
}
