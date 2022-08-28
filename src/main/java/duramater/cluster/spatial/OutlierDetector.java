package duramater.cluster.spatial;

import duramater.cluster.ClusterHelper;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Outlier detector.
 * @author Ron.Coleman
 * @see <a href="https://www.demo2s.com/java/apache-commons-dbscanclusterer-tutorial-with-examples.html>Apache Commons DBSCANClusterer tutorial with examples</a>
 */
public class OutlierDetector {
    final List<Double[]> data;
    List<Cluster> clusters;
    List<DoublePoint> points;

    public OutlierDetector(final List<Double[]> data) {
        this.data = data;
    }

    public void train() {
        init();
        DBSCANClusterer clusterer = new DBSCANClusterer(1,2);

        clusters = clusterer.cluster(points);
    }

    public Set<Double[]> getOutliers() {
        Set<Double[]> outliers = new HashSet<>();

        points.forEach(point -> {
            boolean found = false;
            for(Cluster cluster: clusters) {
                List<DoublePoint> clustered = cluster.getPoints();
                if(clustered.contains(point)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                double income = point.getPoint()[0];
                double spend = point.getPoint()[1];
                for(Double[]pair: outliers) {
                    if(pair[0] == income && pair[1] == spend) {
                        outliers.add(new Double[] {income, spend});
                    }
                }
            }
        });
        return outliers;
    }

    void init() {
        Collections.shuffle(data);
        points = new ArrayList<>();
        data.forEach(datum -> {
            points.add(new DoublePoint(new double[] {datum[0], datum[1]}));
        });
//        List<Double[]> deltas = IntStream.range(1,data.size()).mapToObj(idx -> {
//            Double[] delta = new Double[]{
//                    data.get(idx)[0]-data.get(idx-1)[0],
//                    data.get(idx)[1]-data.get(idx-1)[1]
//            };
//            return delta;
//        }).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        List<Double[]> data = ClusterHelper.load("data/KMeans Dataset.csv");
        OutlierDetector od = new OutlierDetector(data);
        od.train();
        Set<Double[]> outliers = od.getOutliers();
        System.out.println(outliers);
    }
}
