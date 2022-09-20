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
