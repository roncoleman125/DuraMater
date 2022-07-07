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
import duramater.mlp.iris.IrisMatrix;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import java.util.stream.IntStream;
import static duramater.mlp.iris.IrisMatrix.slice;
import static duramater.mlp.iris.IrisMatrix.transpose;

/**
 * Tests slice from start of matrix.
 * @author Ron.Coleman
 * @see <a href="https://www.softwaretestinghelp.com/juni-test-execution-order/">JUnit Test Execution Order</a>
 */
//@FixMethodOrder(MethodSorters.DEFAULT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SliceStartTest {
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

    /**
     * Tests that slice matches expectations.
     */
    @Test
    public void test() {
        final double[][] slice = slice(TEST_MATRIX,0,2);

        int numRows = slice.length;
        assert(numRows == EXPECTED_MATRIX.length);

        int numCols = slice[0].length;
        assert(numCols == EXPECTED_MATRIX[0].length);

        IntStream.range(0,numRows).forEach( rowno -> {
            IntStream.range(0,numCols).forEach(colno -> {
                assert(slice[rowno][colno] == EXPECTED_MATRIX[rowno][colno]);
            });
        });

        IrisMatrix.print(this.getClass().getName()+" slice",slice);
    }

    /**
     * Tests that slice and transpose do not commute.
     */
    @Test
    public void testCommutability() {
        double[][] sliceTranspose = transpose(slice(TEST_MATRIX,0,2));
        IrisMatrix.print(this.getClass().getName()+" slice, transpose",sliceTranspose);

        double[][] transposeSlice = slice(transpose(TEST_MATRIX),0,2);
        IrisMatrix.print(this.getClass().getName()+" slice, transpose",transposeSlice);
        assert(sliceTranspose.length != transposeSlice.length);
    }
}
