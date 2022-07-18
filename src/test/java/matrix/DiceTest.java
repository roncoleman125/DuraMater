package matrix;

import duramater.matrix.IMop;
import duramater.matrix.Mop;
import org.junit.Test;

public class DiceTest {
    final double[][] TEST_MATRIX = {
            { 1,  2,  3},
            { 4,  5,  6},
            { 7,  8,  9},
            {10, 11, 12},
            {13, 14, 15}
    };
    @Test
    public void test() {
        IMop mop = new Mop();
        double[][] dicedCol = mop.dice(TEST_MATRIX,0,1);
        mop.print("diced column",dicedCol);
    }
}
