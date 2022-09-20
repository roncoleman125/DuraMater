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

public interface IMop {
    /**
     * Slices the matrix in row-major order.
     * @param src Source input
     * @param start Start row
     * @param end End row (exclusive)
     * @return Sliced matrix
     */
    double[][] slice(double[][] src,int start,int end);

    /**
     * Dices the matrix in row-major order.
     * @param src Source input
     * @param start Start column
     * @param end End column (exclusive)
     * @return Diced matrix
     */
    double[][] dice(double[][] src,int start, int end);

    /**
     * Transposes a matrix in row-major order
     * @param src Source input
     * @return Transposed output
     */
    double[][] transpose(double[][] src);

    /**
     * Outputs the matrix
     * @param msg Preamble message
     * @param src Source input
     */
    void print(String msg, double[][] src);
}
