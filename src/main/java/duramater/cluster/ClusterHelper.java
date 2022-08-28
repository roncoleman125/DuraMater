package duramater.cluster;

import de.unknownreality.dataframe.DataFrame;
import de.unknownreality.dataframe.csv.CSVReader;
import de.unknownreality.dataframe.csv.CSVReaderBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClusterHelper {
    /**
     * Loads the data.
     * @return Data as list of 3D arrays.
     */
    public static List<Double[]> load(String path) {
        CSVReader csvReader = CSVReaderBuilder.create()
                .containsHeader(true)
                .withSeparator(',')
                .setColumnType("INCOME", Double.class)
                .setColumnType("SPEND", Double.class)
                .build();
        DataFrame frame = DataFrame.load(new File(path), csvReader);

        List<Double[]> data = new ArrayList<>();

        frame.rows().forEach(row -> {
            double income = row.getDouble(0);
            double spend = row.getDouble(1);
            double net =  income - spend;
            Double[] observation = {income,spend,net};
            data.add(observation);
        });

        return data;
    }
}
