import duramater.util.MatrixHelper;
import junit.framework.TestCase;
import java.util.Arrays;
import java.util.stream.IntStream;

public class SliceTest extends TestCase {
    public void test() {
        double[][] TEST = {
                { 1,  2,  3},
                { 4,  5,  6},
                { 7,  8,  9},
                {10, 11, 12}
        };

        double[][] EXPECTED = {
                { 1,  2, 3},
                { 4,  5, 6},
        };

        double[][] slice = MatrixHelper.slice(TEST,0,2);

        IntStream.range(0,slice.length).forEach( rowno -> {
            IntStream.range(0,slice[0].length).forEach(colno -> {
                assert(slice[rowno][colno] == EXPECTED[rowno][colno]);
            });
        });

        Arrays.stream(slice).forEach(row -> {
            Arrays.stream(row).forEach(cell -> System.out.printf("%2.0f ",cell));
            System.out.println("");
        });
    }
}
