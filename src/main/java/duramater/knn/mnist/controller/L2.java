package duramater.knn.mnist.controller;

import java.util.stream.IntStream;

public class L2 implements ILoss {
    @Override
    public double calculate(double[] vector1, double[] vector2) {
        assert(vector1.length > 0);
        assert(vector1.length == vector2.length);

        double loss = IntStream.range(0,vector1.length)
                .mapToDouble(j -> (vector1[j]-vector2[j]) * (vector1[j]-vector2[j]))
                .reduce(0, (a,b) -> a+b);

        return loss;
    }
}
