/*
 Copyright (c) Ron Coleman

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package duramater.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.zip.CRC32;

/**
 * Your basic k-means clustering algorithm
 * @author Ron.Coleman
 */
public class KMeans {
    public final int MAX_ITERATIONS = 300;

    int k;
    List<Double[]> data;
    List<Cluster> clusters = new ArrayList<>();

    /**
     * Constructor
     * @param k Number of clusters
     * @param data Data to be clustered
     */
    public KMeans(int k, List<Double[]> data) {
        this.k = k;
        this.data = data;
    }

    /**
     * Makes the clusters.
     */
    public void train() {
        init();

        // To detect when the clusters converge
        CRC32 crc = new CRC32();

        long oldHash = -1;

        int iteration = 0;

        while(iteration < MAX_ITERATIONS) {
            // Put each observation in its cluster
            data.forEach(observation -> { assign(observation); });

            // Sequence of sizes, if they don't change then clustering has converge
            clusters.forEach(cluster -> { crc.update(cluster.members.size()); });

            // If the has changed, try one more time
            long newHash = crc.getValue();

            // If has has NOT changed, clustering converged
            if(newHash == oldHash)
                break;

            // Reestablish the hash
            oldHash = newHash;

            crc.reset();

            // Update the centroids
            recenter();

            iteration++;
        }
    }

    /**
     * Updates the centroids.
     */
    void recenter() {
        clusters.forEach(cluster -> {
            Double[] centroid = cluster.centroid;

            List<Double[]> members = cluster.members;

            int numCols = centroid.length;
            int numRows = members.size();

            // Calculate the mean over each column
            for(int colno=0; colno < numCols; colno++) {
                // For this column, sum all the rows
                double sum = 0.0;
                for(int rowno=0; rowno < numRows; rowno++) {
                    sum += members.get(rowno)[colno];
                }
                // Here's mean for this column
                double mean = sum / numRows;
                centroid[colno] = mean;
            }
            // Won't need members since we have centroid -- members will be re-added by assign
            members.clear();
        });
    }

    /**
     * Initializes the clusters
     */
    void init() {
        // Take the first k rows of data for the centroids
        List<Double[]> centroids = data.subList(0,k);

        // Build the clusters now that we have centroids
        IntStream.range(0,k).forEach(clusterIdx -> {
            Double[] centroid = centroids.get(clusterIdx);

            List<Double[]> members = new ArrayList<>();

            Cluster cluster = new Cluster(centroid,members);
            clusters.add(cluster);
        });

    }

    /**
     * Assigns a candidate to a cluster.
     * @param candidate Candidate
     */
    void assign(Double[] candidate) {
        // Find the nearest cluster by its centroid
        int nearest = IntStream
                .range(0,clusters.size())
                .reduce((a,b) ->
                        getDist(clusters.get(a).centroid,candidate) < getDist(clusters.get(b).centroid,candidate)?a:b)
                .getAsInt();

        // Nearest is a cluster index
        clusters.get(nearest).members.add(candidate);
    }

    /**
     * Gets the distance between a, b vectors.
     * @param a Vector
     * @param b Vector
     * @return Distance
     */
    double getDist(Double[] a, Double[] b) {
        double dist2 = 0;
        for(int i=0; i < a.length; i++) {
            dist2 += (a[i]-b[i])*(a[i]-b[i]);
        }
        return dist2;
    }


    /**
     * Gets the clusters.
     * @return List of clusters
     */
    public List<Cluster> getCluster() {
        return clusters;
    }
}
