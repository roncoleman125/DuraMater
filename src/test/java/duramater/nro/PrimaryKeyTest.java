package duramater.nro;

import de.unknownreality.dataframe.DataFrame;
import de.unknownreality.dataframe.DataRow;
import de.unknownreality.dataframe.io.FileFormat;
import org.junit.Test;
import java.net.MalformedURLException;
import java.net.URL;

public class PrimaryKeyTest {
    @Test
    public void test() throws MalformedURLException {
        URL csvUrl = new URL("https://raw.githubusercontent.com/nRo/DataFrame/master/src/test/resources/data_index.csv");

        DataFrame dataFrame = DataFrame.load(csvUrl, FileFormat.CSV);

        dataFrame.setPrimaryKey("UID");
        dataFrame.addIndex("id_name_idx","ID","NAME");

        DataRow row = dataFrame.selectByPrimaryKey(1);
        System.out.println(row);
//1;A;1

        DataFrame idxExample = dataFrame.selectByIndex("id_name_idx",3,"A");

        idxExample.print();
/*
    ID	NAME	UID
    3	A	4
    3	A	8
 */
        idxExample.getStringColumn("NAME").map((value -> value + "_idx_example"));
        idxExample.print();
/*
    ID	NAME	UID
    3	A_idx_example	4
    3	A_idx_example	8
 */

        dataFrame.joinInner(idxExample,"UID").print();
/*
    ID.A    NAME.A	UID	ID.B	NAME.B
    3   A   4	3   A_idx_example
    3   A   8	3   A_idx_example
 */
    }
}
