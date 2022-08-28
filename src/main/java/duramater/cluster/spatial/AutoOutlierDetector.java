package duramater.cluster.spatial;

import duramater.cluster.util.ClusterHelper;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.stat.StatUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
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

        Collections.shuffle(samples);

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
        OutlierDetector od = new AutoOutlierDetector(data);
        od.train();
        Set<Double[]> outliers = od.getOutliers();
        System.out.println(outliers);
    }
}
