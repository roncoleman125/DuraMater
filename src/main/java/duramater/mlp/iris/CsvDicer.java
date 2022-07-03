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
package duramater.mlp.iris;

import de.unknownreality.dataframe.DataFrameColumn;
import de.unknownreality.dataframe.DataRow;
import de.unknownreality.dataframe.csv.CSVReader;
import de.unknownreality.dataframe.csv.CSVReaderBuilder;
import de.unknownreality.dataframe.DataFrame;
import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Loads and "dices" iris data.
 *
 * @author Ron.Coleman
 */
public class CsvDicer {
    public static String[] COL_NAMES = {
            "Sepal Length",
            "Sepal Width",
            "Petal Length",
            "Petal Width",
            "Species"
    };
    /**
     * Names by which we refer to the columns, not necessarily the names in the CSV file.
     */
    static String[] COL_SHORT_NAMES = {
            "SL",
            "SW",
            "PL",
            "PW",
            "SP"
    };

    /**
     * We need this to convert from string iris species names to numerical values.
     * See https://www.baeldung.com/java-initialize-hashmap
     */
    static Map<String, Integer> species2Cat = Stream.of(new Object[][]{
            {"setosa", 0},
            {"virginica", 1},
            {"versicolor", 2}
    }).collect(Collectors.toMap(row -> (String) row[0], row -> (Integer) row[1]));

    /** Path to the CSV file */
    String path;

    /** Used to load the data */
    DataFrame frame;

    /** Actual iris observations in numerical form */
    double[][] observations;

    /**
     * Constructor
     * @param path Path to csv file
     */
    public CsvDicer(String path) {
        this.path = path;
    }

    /**
     * Loads the iris data from the CSV file.
     */
    public double[][] dice() {
        // Id,Sepal Length,Sepal Width,Petal Length,Petal Width,Species
        CSVReader csvReader = CSVReaderBuilder.create()
                .containsHeader(true)
                .withSeparator(',')
                .ignoreColumn("Id")
                .setColumnType(COL_NAMES[0], Double.class)
                .setColumnType(COL_NAMES[1], Double.class)
                .setColumnType(COL_NAMES[2], Double.class)
                .setColumnType(COL_NAMES[3], Double.class)
                .setColumnType(COL_NAMES[4], String.class)
                .build();
        frame = DataFrame.load(new File(path), csvReader/*FileFormat.CSV*/);

        frame.shuffle();

        DataFrame df = frame.getStringColumn(COL_NAMES[4]).transform(new Species2CatsTransformer(COL_NAMES[4]));
        frame.replaceColumn(COL_NAMES[4],df.getColumn(COL_NAMES[4]));

        populate();

        return observations;
    }

    /**
     * Populates the observation array from the frame.
     */
    void populate() {
        int numCols = frame.getColumns().size();
        int numRows = frame.getRows().size();

        observations = new double[numCols][numRows];

        IntStream.range(0, numCols).forEach(colno -> {
            String name = COL_NAMES[colno];
            DataFrameColumn col = frame.getColumn(name);
            IntStream.range(0, numRows).forEach(rowno -> {
                DataRow row = frame.getRow(rowno);
                Double cell = row.get(colno,Double.class);
                observations[colno][rowno] = cell;
            });
        });
    }
    /**
     * Gets number of observations
     * @return Number of observations
     */
    public int getNumObservations() {
        if(frame == null || observations == null)
            return 0;

        return observations[0].length;
    }

    /**
     * Gets the observations as a 2D matrix.
     * @return Observations matrix
     */
    public double[][] getObservations() {
        return observations;
    }

    public int getNumColumns() {
        return observations.length;
    }

    /**
     * Compiles a species name to its categorical integer encoding.
     * @param plaintext Plaintext
     * @return Integer encoding.
     */
    public int compile(String plaintext) {
        return species2Cat.get(plaintext);
    }

    /**
     * Runs a unit test of the load and dice.
     * @param args Command line args (not used)
     */
    public static void main(String[] args) {
        CsvDicer csvDicer = new CsvDicer("data/iris.csv");
        double[][] observations = csvDicer.dice();

        int numRows = csvDicer.getNumObservations();
        int numCols = csvDicer.getNumColumns();

        System.out.printf("%3s ","#");
        Stream.of(CsvDicer.COL_SHORT_NAMES).forEach(name -> System.out.printf("%3s ",name));
        System.out.println("");
        IntStream.range(0, numRows).forEach(row -> {
            System.out.printf("%3d ", row);
            IntStream.range(0, numCols).forEach(col -> {
                double datum = observations[col][row];
                String format = "%3.1f ";
                if (col == numCols - 1)
                    format = "%3.0f ";
                System.out.printf(format, datum);
            });
            System.out.println("");
        });
    }
}