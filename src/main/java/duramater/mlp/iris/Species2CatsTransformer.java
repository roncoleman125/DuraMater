package duramater.mlp.iris;

import de.unknownreality.dataframe.DataFrame;
import de.unknownreality.dataframe.column.StringColumn;
import de.unknownreality.dataframe.transform.ColumnDataFrameTransform;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Species2CatsTransformer implements ColumnDataFrameTransform<StringColumn> {
    static Map<String, Integer> species2Cats = Stream.of(new Object[][]{
            {"setosa", 0},
            {"virginica", 1},
            {"versicolor", 2}
    }).collect(Collectors.toMap(row -> (String) row[0], row -> (Integer) row[1]));

    protected String name;
    public Species2CatsTransformer(String name) {
        this.name = name;
    }
    @Override
    public DataFrame transform(StringColumn species) {
        DataFrame df = DataFrame.create().addDoubleColumn(name);

        species.forEach( specie -> {
            df.append(species2Cats.get(specie));
        });

        return df;
    }
}
