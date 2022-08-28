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
package duramater.cluster.kmeans;

import de.unknownreality.dataframe.DataFrame;
import de.unknownreality.dataframe.csv.CSVReader;
import de.unknownreality.dataframe.csv.CSVReaderBuilder;
import duramater.cluster.ClusterHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the "net-money" demonstration translated from Python.
 * @author Ron.Coleman
 * @see <a href="https://www.kaggle.com/code/inubayuaji/clustering-money-income-and-spend-with-kmeans>Clustering income and spend with kmeans</a>
 */
public class NetMoney {
    public static void main(String[] args) {

        // Load the data
        List<Double[]> data = ClusterHelper.load("data/KMeans Dataset.csv");

        // 3 clusters corresponding to wasteful, moderate, and thrifty
        // But...the data also has 3 variables: income, spend, and net money
        KMeans km = new KMeans(3,data);

        // Do the cluster analysis
        km.train();

        List<Cluster> clusters = km.getCluster();

        // Now, here we only output income vs. spend separated by their cluster breaks.
        // Graph of three clusters should look like inubayuaji plots.
        final int INCOME = 0;
        final int SPEND = 1;
        clusters.forEach(cluster -> {
            cluster.members().forEach(member -> {
                System.out.println(member[INCOME]+","+member[SPEND]);
            });
            System.out.println("###");
        });
    }
}
