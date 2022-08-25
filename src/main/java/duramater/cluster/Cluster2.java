package duramater.cluster;

import java.util.ArrayList;
import java.util.List;

public class Cluster2 {
    public Double[] centroid;
//    public double[][] members;
    List<Double[]> members = null;
    public Cluster2(Double[] centroid, List<Double[]> members) {
        this.centroid = centroid;
        this.members = members;
    }
}
