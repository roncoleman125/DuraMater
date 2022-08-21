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

public class KnnsPlus {
    public static void main(String[] args) throws Exception {
        KnnsPlus test = new KnnsPlus();
        test.execute();
    }

    public void execute() throws IOException {
        System.out.println(this.getClass().getName());

        ////////////////
        MnistMatrix[] mnistTrainMatrix = new MnistDataReader().readData("observations/train-images.idx3-ubyte", "observations/train-labels.idx1-ubyte");
        MnistArrays trainingArrays = new MnistArrays(mnistTrainMatrix);

        double[][] trainInputs = trainingArrays.getInputs(60000);
        double[][] trainIdeals = trainingArrays.getIdeals(60000);

        ////////////////
        MnistMatrix[] mnistTestMatrix = new MnistDataReader().readData("observations/t10k-images.idx3-ubyte", "observations/t10k-labels.idx1-ubyte");
        MnistArrays testArrays = new MnistArrays(mnistTestMatrix);

        double[][] testInputs = testArrays.getInputs(10000);
        double[][] testIdeals = testArrays.getIdeals(10000);

        Equilateral eq = new Equilateral(10, 1.0, 0.0);
        int hit = 0;
        final int K = 11;
        for (int testIdx = 0; testIdx < testInputs.length; testIdx++) {
//            System.out.println("test number: "+testIdx);
            if (testIdx % 100 == 0 && testIdx != 0)
                System.out.println("testno: " + testIdx + " hits: " + hit);

            double[] testInput = testInputs[testIdx];

            List<Double> minDists2 = new ArrayList<>();
            List<Integer> minIndexes = new ArrayList<>();
            for (int idx = 0; idx < K; idx++) {
                minDists2.add(Double.MAX_VALUE);
                minIndexes.add(-1);
            }

            for (int trainIdx = 0; trainIdx < trainIdeals.length; trainIdx++) {
                double dist2 = 0;
                double[] trainInput = trainInputs[trainIdx];
                for (int colIdx = 0; colIdx < testInput.length; colIdx++) {
                    dist2 += (testInput[colIdx] - trainInput[colIdx]) * (testInput[colIdx] - trainInput[colIdx]);
                }
                int idx = getBetterIndex(dist2, minDists2);
                if(idx >= 0)
                    insertAt(idx, dist2, trainIdx, minDists2, minIndexes);
            }

            int predictedLabel = getBestLabel(trainIdeals,minDists2,minIndexes,eq);
            int actualLabel = eq.decode(testIdeals[testIdx]);
            if (predictedLabel == actualLabel)
                hit++;
            else
                System.out.println("mismatch testIdx="+testIdx+" predicted="+predictedLabel+" actual="+actualLabel);
        }
        double ratio = (double) hit / testIdeals.length;
        System.out.println("tests: " + testInputs.length + " hits: " + hit);
        System.out.println("ratio: " + ratio);
    }

    /**
     * Gets the best label.
     * @param inputs Inputs (training)
     * @param distances Candidate distances
     * @param inputIndexes Candidate indexes
     * @param eq Equilateral encoder
     * @return Best label
     */
    protected static int getBestLabel(double[][] inputs, List<Double> distances, List<Integer> inputIndexes, Equilateral eq) {
        assert(inputs != null);
        assert(distances != null);
        assert(eq != null);

        Map<Integer,Integer> votes = new HashMap<>();
        Map<Integer,Double> totalDists = new HashMap<>();
        for(int candidateno=0; candidateno < inputIndexes.size(); candidateno++) {
            int index = inputIndexes.get(candidateno);
            double[] input = inputs[index];
            int label = eq.decode(input);
            int freq = votes.getOrDefault(label,0);
            votes.put(label,freq+1);
            if(freq == 0)
                totalDists.put(label,distances.get(candidateno));
            else {
                Double totalDist = totalDists.get(label) + distances.get(candidateno);
                totalDists.put(label, totalDist);
            }
        }
//        int maxCount = 0;
        int bestLabel = -1;
        Double minAvgDist = Double.MAX_VALUE;
        for(Integer label: votes.keySet()) {
            int count = votes.get(label);
            double totalDist = totalDists.get(label);
            double avgDist = totalDist/count;
            if(avgDist < minAvgDist) {
                minAvgDist = avgDist;
                bestLabel = label;
            }
        }
        return bestLabel;
    }

    protected static int getBetterIndex(double distance, List<Double> distances) {
        for (int idxi = 0; idxi < distances.size(); idxi++) {
            if (distance < distances.get(idxi)) {
                return idxi;
            }
        }
        return -1;
    }

    protected static void insertAt(int targetIdx, double newDist, int newIndex, List<Double> distances, List<Integer> distanceIndexes) {
        assert(targetIdx >= 0);
        assert(newIndex >= 0);
        assert(!Double.isNaN(newDist));
        for (int toIdx = distances.size() - 1; toIdx > targetIdx; toIdx--) {
            int fromIdx = toIdx-1;
            distances.set(toIdx, distances.get(fromIdx));
        }
        distances.set(targetIdx,newDist);
        distanceIndexes.set(targetIdx,newIndex);
    }
}
