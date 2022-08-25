package duramater.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.zip.CRC32;

public class KMeans2 {
    public final int MAX_ITERATIONS = 300;

    List<Double[]> data;
    int k;
    List<Cluster2> clusters = new ArrayList<>();

    public KMeans2(int k, List<Double[]> data) {
        this.k = k;
        this.data = data;
    }

    public List<Cluster2> getCluster() {
        return clusters;
    }
    public void train() {
        init();

        int iteration = 0;
        CRC32 crc = new CRC32();

        long oldHash = -1;
        while(iteration < MAX_ITERATIONS) {
            data.forEach(candidate -> {
                assign(candidate);
            });

            clusters.forEach(cluster -> {
                crc.update(cluster.members.size());
            });
            long newHash = crc.getValue();
            if(newHash == oldHash)
                break;
            oldHash = newHash;
            crc.reset();
            recenter();
        }

    }

    void recenter() {
        for(int clusterIdx=0; clusterIdx < clusters.size(); clusterIdx++) {
            Cluster2 cluster = clusters.get(clusterIdx);
            Double[] centroid = cluster.centroid;
            List<Double[]> members = cluster.members;
            int numCols = centroid.length;
            int numRows = members.size();

            for(int colno=0; colno < numCols; colno++) {
                double sum = 0.0;
                for(int rowno=0; rowno < numRows; rowno++) {
                    sum += members.get(rowno)[colno];
                }
                double mean = sum / numRows;
                centroid[colno] = mean;
            }
        }
    }

    void init() {
        List<Double[]> centroids = data.subList(0,k);
        IntStream.range(0,k).forEach(idx -> {
            Double[] centroid = centroids.get(k);
            List<Double[]> members = new ArrayList<>();
            Cluster2 cluster = new Cluster2(centroid,members);
            clusters.add(cluster);
        });

    }

    void assign(Double[] candidate) {
        int nearest = IntStream
                .range(0,clusters.size())
                .reduce((a,b) -> getDist(clusters.get(a).centroid,candidate) < getDist(clusters.get(b).centroid,candidate)?a:b)
                .getAsInt();

        clusters.get(nearest).members.add(candidate);
    }

    double getDist(Double[] a, Double[] b) {
        return 0;
    }

    public static void main(String[] args) {
        KMeans2 km = new KMeans2(5,null);
    }
}
