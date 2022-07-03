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
import duramater.util.MatrixHelper;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import java.util.stream.IntStream;

/**
 * Tests slice from start of matrix.
 * @author Ron.Coleman
 * @see <a href="https://www.softwaretestinghelp.com/juni-test-execution-order/">JUnit Test Execution Order</a>
 */
@FixMethodOrder(MethodSorters.DEFAULT)
public class SliceStartTest {
    @Test
    public void test() {
        // Matrix is this size to anticipate start, mid, end testing.
        final double[][] TEST_MATRIX = {
                { 1,  2,  3},
                { 4,  5,  6},
                { 7,  8,  9},
                {10, 11, 12},
                {13, 14, 15}
        };

        final double[][] EXPECTED_MATRIX = {
                { 1,  2, 3},
                { 4,  5, 6}
        };

        final double[][] slice = MatrixHelper.slice(TEST_MATRIX,0,2);

        int numRows = slice.length;
        assert(numRows == EXPECTED_MATRIX.length);

        int numCols = slice[0].length;
        assert(numCols == EXPECTED_MATRIX[0].length);

        IntStream.range(0,numRows).forEach( rowno -> {
            IntStream.range(0,numCols).forEach(colno -> {
                assert(slice[rowno][colno] == EXPECTED_MATRIX[rowno][colno]);
            });
        });

        MatrixHelper.print(this.getClass().getName()+" slice",slice);
    }
}
