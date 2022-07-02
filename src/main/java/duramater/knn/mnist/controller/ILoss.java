package duramater.knn.mnist.controller;

public interface ILoss {
    public double calculate(double[] vector1, double[] vector2);
}
