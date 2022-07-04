/*
 * Copyright (c) Ron Coleman
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package duramater.nro;

import de.unknownreality.dataframe.DataFrame;
import de.unknownreality.dataframe.group.aggr.Aggregate;
import de.unknownreality.dataframe.io.FileFormat;
import org.junit.Test;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Runs basic test of nro frame from the website.
 * @author Ron.Coleman
 */
public class BasicTest {
    @Test
    public void test() throws MalformedURLException {
        URL csvUrl = new URL("https://raw.githubusercontent.com/nRo/DataFrame/master/src/test/resources/users.csv");

        DataFrame users = DataFrame.load(csvUrl, FileFormat.CSV);

        users.select("(name == 'Schmitt' || name == 'Meier') && country == 'Germany'")
                .groupBy("age").agg("count", Aggregate.count())
                .sort("age")
                .print();
    }
}
