package duramater.mlp.iris;

import de.unknownreality.dataframe.DataFrame;
import de.unknownreality.dataframe.csv.CSVReader;
import de.unknownreality.dataframe.csv.CSVReaderBuilder;
import de.unknownreality.dataframe.group.aggr.Aggregate;
import de.unknownreality.dataframe.io.FileFormat;
import java.io.File;
import java.net.URL;

public class Os {
    public static void main(String[] args) throws Exception {
        URL csvUrl = new URL("https://raw.githubusercontent.com/nRo/DataFrame/master/src/test/resources/users.csv");

//        DataFrame users = DataFrame.load("C:/marist/Duramater/datadata/y.csv", FileFormat.CSV);
        DataFrame users = DataFrame.load(csvUrl, FileFormat.CSV);

        users.select("(name == 'Schmitt' || name == 'Meier') && country == 'Germany'")
                .groupBy("age").agg("count", Aggregate.count())
                .sort("age")
                .print();


        // Id,Sepal Length,Sepal Width,Petal Length,Petal Width,Species
        CSVReader csvReader = CSVReaderBuilder.create()
                .containsHeader(true)
                .withSeparator(',')
                .setColumnType("Id", Integer.class)
                .setColumnType("Sepal Length", Double.class)
                .setColumnType("Sepal Width", Double.class)
                .setColumnType("Petal Length", Double.class)
                .setColumnType("Petal Width", Double.class)
                .setColumnType("Species", String.class)
                .build();
        DataFrame dataFrame = DataFrame.load(new File("data/iris.csv"), csvReader/*FileFormat.CSV*/);

        dataFrame.shuffle();

        DataFrame df = dataFrame.getStringColumn("Species").transform(new Species2CatsTransformer("Species"));
        dataFrame.replaceColumn("Species",df.getColumn("Species"));
        System.out.println(dataFrame.size());



    }
}
