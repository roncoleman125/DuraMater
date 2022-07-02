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
package duramater.knn.mnist.view;

import duramater.knn.mnist.model.IObserver;
import duramater.knn.mnist.model.MatrixType;
import duramater.knn.mnist.model.MnistDataReader;
import duramater.knn.mnist.model.MnistMatrix;
import javax.swing.*;
import java.io.IOException;

/**
 * Runs Count Basic.
 */
public class CountBasic extends JFrame implements IObserver {
    static protected MnistMatrix[] candidateMatrices;
    static protected MnistMatrix[] targetMatrices;

    protected double progress = 0;

    /**
     * Constructor
     */
    public CountBasic() {
        try {
            load(this);

            render();
        } catch (Exception e) {
        }
    }

    public void render() {
        add(new Dac());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440 * 2, 535);
        setLocationRelativeTo(null);
        setTitle("Count Basic");
        setResizable(false);
        setVisible(true);
    }

    public static void load(IObserver observer) throws Exception {
        candidateMatrices = new MnistDataReader().readData("observations/train-images.idx3-ubyte", "observations/train-labels.idx1-ubyte", observer);
        targetMatrices = new MnistDataReader().readData("observations/t10k-images.idx3-ubyte", "observations/t10k-labels.idx1-ubyte", observer);
    }

    public static void main(String[] args) throws IOException {
        new CountBasic();
    }

    public static MnistMatrix[] getMatrices(MatrixType type) {
        assert(type != null);
        switch (type) {
            case TARGET:
                return targetMatrices;

            case CANDIDATE:
                return candidateMatrices;

            case NONE:
            default:
                return null;
        }
    }

    int itemCount = 0;
    String lasts = "";

    @Override
    public void start(String[] paths, int itemCount) {
        System.out.println("loading:");
        for (String path : paths) {
            System.out.println(path);
        }
        this.itemCount = itemCount;
        lasts = String.format("%3.0f%%", progress);
    }

    @Override
    public void update(int itemno, MnistMatrix matrix) {
        if (itemno % 500 == 0 || itemno == itemCount) {
            for (int i = 0; i < lasts.length(); i++)
                System.out.print("\b");
            progress = ((double) itemno) / this.itemCount;
            lasts = String.format("%3.0f%%", progress * 100);
            System.out.print(lasts);
            System.out.flush();
        }
    }

    @Override
    public void finish(int itemCount) {
        System.out.println("");
    }
}
