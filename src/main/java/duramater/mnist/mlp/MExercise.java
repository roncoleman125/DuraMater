package duramater.mnist.mlp;

import org.encog.mathutil.Equilateral;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;

public class MExercise {
    record Report(int tried,int hit) {}
    BasicNetwork network;
    MLDataSet dataset;
    public MExercise(BasicNetwork network, MLDataSet dataset) {
        this.network = network;
        this.dataset = dataset;
    }
    public Report report() {
        int hit = 0;
        Equilateral eq = new Equilateral(10,1.0,0.0);
        for(MLDataPair pair: dataset) {
            MLData input = pair.getInput();
            MLData predicted = network.compute(input);
            int predictedLabel = eq.decode(predicted.getData());
            MLData ideal = pair.getIdeal();
            int idealLabel = eq.decode(ideal.getData());
            if(predictedLabel == idealLabel)
                hit++;
        }
        return new Report(dataset.size(),hit);
    }
}
