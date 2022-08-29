package duramater.cluster.spatial;

import duramater.cluster.util.ClusterHelper;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import java.util.*;

/**
 * Detects outliners using spatial clustering.
 * @author Ron.Coleman
 * @see <a href="https://www.demo2s.com/java/apache-commons-dbscanclusterer-tutorial-with-examples.html>Apache Commons DBSCANClusterer tutorial with examples</a>
 */
public class OutlierDetector {
    // Min (Euclidean) distance between clusters because 10 nice number.
    public final double EPSILON = 10;

    // Min members of cluster -- seems reasonable for clusters.
    public final int MIN_PTS = 2;

    final List<Double[]> data;
    List<Cluster<DoublePoint>> clusters;
    List<DoublePoint> points;
    double eps = 10;
    int minPts = MIN_PTS;


    /**
     * Constructor
     * @param data Data in which to detect outliers.
     */
    public OutlierDetector(final List<Double[]> data) {
        this.data = data;
    }

    /**
     * Trains on finding outliers.
     */
    public void train() {
        init();

        DBSCANClusterer clusterer = new DBSCANClusterer(eps,minPts);

        clusters = clusterer.cluster(points);
    }

    public List<Cluster<DoublePoint>> getClusters() {
        return clusters;
    }

    /**
     * Gets outliers.
     * @return Set of outliers.
     */
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
                outliers.add(new Double[]{income,spend});
            }
        });
        return outliers;
    }

    public Double[] getUpperFences() {
        Double[] origin = {0.,0.};
        double upper = -Double.MAX_VALUE;
//        double lower = Double.MAX_VALUE;
        DoublePoint upperPoint = null;
        for(Cluster cluster: clusters) {
            List<DoublePoint> points = cluster.getPoints();
            for(DoublePoint point: points) {
                double l2 = ClusterHelper.getDist(new Double[]{point.getPoint()[0],point.getPoint()[1]},origin);
                if(l2>upper) {
                    upper = l2;
                    upperPoint = point;
                }

            }
        }
        return new Double[] {upperPoint.getPoint()[0],upperPoint.getPoint()[1]};
    }

    public Double[] getLowerFences() {
        Double[] origin = {0.,0.};
//        double upper = -Double.MAX_VALUE;
        double lower = Double.MAX_VALUE;
        DoublePoint lowerPoint = null;
        for(Cluster cluster: clusters) {
            List<DoublePoint> points = cluster.getPoints();
            for(DoublePoint point: points) {
                double l2 = ClusterHelper.getDist(new Double[]{point.getPoint()[0],point.getPoint()[1]},origin);
                if(l2<lower) {
                    lower = l2;
                    lowerPoint = point;
                }

            }
        }
        return new Double[] {lowerPoint.getPoint()[0],lowerPoint.getPoint()[1]};
    }

    /**
     * Initializes the internal data structures -- invoke prior to train.
     */
    void init() {
        Collections.shuffle(data);
        points = new ArrayList<>();
        data.forEach(datum -> {
            points.add(new DoublePoint(new double[] {datum[0], datum[1]}));
        });
    }

    public static void main(String[] args) {
        List<Double[]> data = ClusterHelper.load("data/KMeans Dataset.csv");
        OutlierDetector od = new OutlierDetector(data);
        od.train();

        System.out.println("outliers");
        Set<Double[]> outliers = od.getOutliers();
        outliers.forEach(outlier -> {
            System.out.printf("%5.2f %5.2f\n",outlier[0],outlier[1]);
        });

        Double[] upperFences = od.getUpperFences();
        Double[] lowerFences = od.getLowerFences();
        System.out.println("fences:");
        System.out.printf("%5.2f, %5.2f\n",upperFences[0],upperFences[1]);
        System.out.printf("%5.2f, %5.2f\n",lowerFences[0],lowerFences[1]);

        od.getClusters().forEach(cluster -> {
            System.out.println("###");
            cluster.getPoints().forEach(point -> {
                System.out.printf("%5.2f, %5.2f\n",point.getPoint()[0],point.getPoint()[1]);
            });
        });
    }
}
