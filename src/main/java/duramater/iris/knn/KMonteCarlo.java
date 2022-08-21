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
package duramater.iris.knn;

import java.util.Random;

/**
 * MC reinforcement learning to find best k for three variables.
 * @author Ron.Coleman
 */
public class KMonteCarlo {
    public static double NUM_SAMPLES = 1000000.0;
    public static void main(String[] args) {
        Random ran = new Random(0);
        for(int k=2; k <= 100; k++) {
            int count = 0;
            for(int sampleno = 0; sampleno < NUM_SAMPLES; sampleno++) {
                int x = ran.nextInt(k+1);
                int y = ran.nextInt(k-x+1);
                int z = k-x-y;
                if(x>z && y>z && x==y || (x==y && x==z))
                    count++;
            }
            System.out.println(k+" "+count);
        }
    }
}
