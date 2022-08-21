package duramater.mnist.knn.cmdline;

import duramater.mnist.knn.gui.model.MnistArrays;
import duramater.mnist.knn.gui.model.MnistDataReader;
import duramater.mnist.knn.gui.model.MnistMatrix;
import org.encog.mathutil.Equilateral;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Knns {
    /**
     * See https://blog.jetbrains.com/idea/2020/03/java-14-and-intellij-idea/#:%7E:text=Configure%20IntelliJ%20IDEA%202020.1%20to,IntelliJ%20IDEA%20and%20configure%20it.
     */
    public record Prediction(int label, int numVotes) {}

    public void execute() throws IOException {
        System.out.println(this.getClass().getName());

        ////////////////
        MnistMatrix[] mnistTrainMatrix = new MnistDataReader().readData("observations/train-images.idx3-ubyte", "observations/train-labels.idx1-ubyte");
        MnistArrays candidateArrays = new MnistArrays(mnistTrainMatrix);

        double[][] candidateInputs = candidateArrays.getInputs(60000);
        double[][] candidateIdeals = candidateArrays.getIdeals(60000);

        ////////////////
        MnistMatrix[] mnistTestMatrix = new MnistDataReader().readData("observations/t10k-images.idx3-ubyte", "observations/t10k-labels.idx1-ubyte");
        MnistArrays targetArrays = new MnistArrays(mnistTestMatrix);

        double[][] targetInputs = targetArrays.getInputs(10000);
        double[][] targetIdeals = targetArrays.getIdeals(10000);

        Equilateral eq = new Equilateral(10, 1.0, 0.0);
        ////////////////

        int hit = 0;
        final int K = 11;
        for (int targetIdx = 0; targetIdx < targetInputs.length; targetIdx++) {
//            System.out.println("test number: "+testIdx);
            if (targetIdx % 100 == 0 && targetIdx != 0)
                System.out.println("tested: " + targetIdx + " hits: " + hit);

            double[] target = targetInputs[targetIdx];

            List<Double> minDists2 = new ArrayList<>();
            List<Integer> minIndexes = new ArrayList<>();
            for (int idx = 0; idx < K; idx++) {
                minDists2.add(Double.MAX_VALUE);
                minIndexes.add(-1);
            }

            for (int candidateIdx = 0; candidateIdx < candidateIdeals.length; candidateIdx++) {
                double dist2 = 0;
                double[] candidate = candidateInputs[candidateIdx];
                for (int colIdx = 0; colIdx < target.length; colIdx++) {
                    dist2 += (target[colIdx] - candidate[colIdx]) * (target[colIdx] - candidate[colIdx]);
                }
                int slot = getBetterNo(dist2, minDists2);
                if(slot >= 0)
                    insertAt(slot, dist2, minDists2, candidateIdx, minIndexes);
            }

            Prediction prediction = getPrediction(candidateIdeals, minIndexes, eq);
            int actual = eq.decode(targetIdeals[targetIdx]);
            if (prediction.label() == actual)
                hit++;
            else
                System.out.println("mismatch target="+targetIdx+" actual label: "+actual+" "+prediction);
        }
        double ratio = (double) hit / targetIdeals.length;
        System.out.println("tests: " + targetInputs.length + " hits: " + hit);
        System.out.println("ratio: " + ratio);
    }

    /**
     * Gets the best label.
     * @param inputs Inputs (training)
     * @param inputIndexes Candidate indexes
     * @param eq Equilateral encoder
     * @return Best label
     */
    protected static Prediction getPrediction(double[][] inputs, List<Integer> inputIndexes, Equilateral eq) {
        Map<Integer,Integer> votes = new HashMap<>();
        for(int candidateno=0; candidateno < inputIndexes.size(); candidateno++) {
            int index = inputIndexes.get(candidateno);
            double[] input = inputs[index];
            int label = eq.decode(input);
            int freq = votes.getOrDefault(label,0);
            votes.put(label,freq+1);
        }

        int bestLabel = -1;
        int mostVotes = -1;
        for(int label: votes.keySet()) {
            if(votes.get(label) > mostVotes) {
                mostVotes = votes.get(label);
                bestLabel = label;
            }
        }
        return new Prediction(bestLabel,mostVotes);
    }

    /**
     * Gets a better index assuming distances are sorted in ascending order.
     * @param distance Candidate distance
     * @param allDistances All distances
     * @return Index where better found.
     */
    protected static int getBetterNo(double distance, List<Double> allDistances) {
        for (int idxi = 0; idxi < allDistances.size(); idxi++) {
            if (distance < allDistances.get(idxi)) {
                return idxi;
            }
        }
        return -1;
    }

    protected static void insertAt(int slot, double newDist, List<Double> distances, int newIndex, List<Integer> distanceIndexes) {
        // Shift right
        for (int toIdx = distances.size() - 1; toIdx > slot; toIdx--) {
            int fromIdx = toIdx-1;
            distances.set(toIdx, distances.get(fromIdx));
        }

        // Insert into hole
        distances.set(slot,newDist);
        distanceIndexes.set(slot,newIndex);
    }

    public static void main(String[] args) throws Exception {
        Knns test = new Knns();
        test.execute();
    }
}
