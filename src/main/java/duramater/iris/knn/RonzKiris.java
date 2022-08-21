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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * This program was evolved from XorHelloWorld to train and test an MLP on iris data.
 *
 * @author Ron.Coleman
 * @date 29.Oct.2019
 */
public class RonzKiris extends RonzNnIris {
    /**
     * The main method.
     *
     * @param args No arguments are used.
     */
    public static void main(final String args[]) {
        new RonzKiris().go();
    }

    @Override
    Nearest getNearest(double[] target,double[][] model) {
        List<Candidate> candidates = getCandidates(target,model);
        final int k = 5;
        Map<Integer, Integer> votes = new HashMap<>();
        IntStream.range(0, k).forEach(idx -> {
            int candidate = eq.decode(TRAINING_IDEALS[candidates.get(idx).no()]);
            int freq = votes.getOrDefault(candidate, 0);
            votes.put(candidate, freq + 1);
        });

        int winner = votes.entrySet().stream().sorted((e1, e2) -> {
            if (e1.getValue() > e2.getValue())
                return 1;
            else
                return -1;
        }).collect(Collectors.toList()).get(0).getKey();

        Candidate popular = candidates.get(winner);
        return new Nearest(popular, votes);
    }


    List<Candidate> getCandidates(double[] target, double[][] model) {
        // Sort candidates by distance to target
        List<Candidate> candidates =
                IntStream.range(0, model.length)
                        .mapToObj(no -> new Candidate(no, model[no], getDist(model[no], target)))
                        .sorted((obj1, obj2) -> {
                            if (obj1.dist() > obj2.dist())
                                return 1;
                            else
                                return -1;
                        }).collect(Collectors.toList());
        return candidates;
    }
}
