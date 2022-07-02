package duramater.knn.mnist.model;

public interface IObserver {
    public void start(String[] paths, int itemCount);
    public void update(int itemno, MnistMatrix matrix);
    public void finish(int itemCount);
}
