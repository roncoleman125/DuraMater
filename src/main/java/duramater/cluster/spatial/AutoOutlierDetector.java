package duramater.cluster.spatial;

import duramater.cluster.util.ClusterHelper;
import org.apache.commons.math3.stat.StatUtils;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Detects outliers using unsupervised learning to establish epsilon.
 * @author Ron.Coleman
 * @see <a href="https://www.demo2s.com/java/apache-commons-dbscanclusterer-tutorial-with-examples.html>Apache Commons DBSCANClusterer tutorial with examples</a>
 */
public class AutoOutlierDetector extends OutlierDetector {
    // Samples from baseline data may vary depending on data size
    final int NUM_SAMPLES = Math.min(100,data.size());

    // Increasing this value reduces the number of clusters, hence number of outliers
    final double PERCENTILE_DISTANCES = 2.5;

    public AutoOutlierDetector(final List<Double[]> data) {
        super(data);
    }

    @Override
    void init() {
        super.init();

        List<Double[]> samples = data.subList(0,NUM_SAMPLES);

        Random ran = new Random(1);
        Collections.shuffle(samples,ran);

        double[] array = IntStream
                .range(1,samples.size())
                .mapToDouble(idx -> ClusterHelper.getDist(samples.get(idx),samples.get(idx-1)))
                .toArray();

        double l2 = StatUtils.percentile(array,PERCENTILE_DISTANCES);
        double eps = Math.sqrt(l2);
        this.eps = eps;
    }

    public static void main(String[] args) {
        List<Double[]> data = ClusterHelper.load("data/KMeans Dataset.csv");
        OutlierDetector autood = new AutoOutlierDetector(data);
        autood.train();

        report(autood);
//        Set<Double[]> outliers = od.getOutliers();
//        System.out.println("outliers: "+outliers.size());
//        System.out.printf("%6s %6s\n","INCOME","SPEND");
//
//        outliers.stream().forEach(outlier -> {
//            System.out.printf("%6.0f %6.0f\n",outlier[0],+outlier[1]);
//        });
    }
}
