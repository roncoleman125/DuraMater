package duramater.mnist.knn.gui.model;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * See https://github.com/turkdogan/mnist-data-reader
 */
public class MnistDataReader  {

    public MnistMatrix[] readData(String dataFilePath, String labelFilePath) throws IOException {
        return readData(dataFilePath,labelFilePath,null);
    }

    public MnistMatrix[] readData(String dataFilePath, String labelFilePath, IObserver observer) throws IOException {

        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(dataFilePath)));
        int magicNumber = dataInputStream.readInt();
        int numberOfItems = dataInputStream.readInt();
        if(observer != null)
            observer.start(new String[]{dataFilePath,labelFilePath}, numberOfItems);
        int nRows = dataInputStream.readInt();
        int nCols = dataInputStream.readInt();

        System.out.println("magic number is " + magicNumber);
        System.out.println("number of items is " + numberOfItems);
        System.out.println("number of rows is: " + nRows);
        System.out.println("number of cols is: " + nCols);

        DataInputStream labelInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(labelFilePath)));
        int labelMagicNumber = labelInputStream.readInt();
        int numberOfLabels = labelInputStream.readInt();

        System.out.println("labels magic number is: " + labelMagicNumber);
        System.out.println("number of labels is: " + numberOfLabels);

        MnistMatrix[] data = new MnistMatrix[numberOfItems];

        assert numberOfItems == numberOfLabels;

        for(int i = 0; i < numberOfItems; i++) {
            MnistMatrix mnistMatrix = new MnistMatrix(nRows, nCols);
            mnistMatrix.setLabel(labelInputStream.readUnsignedByte());
            for (int r = 0; r < nRows; r++) {
                for (int c = 0; c < nCols; c++) {
                    mnistMatrix.setValue(r, c, dataInputStream.readUnsignedByte());
                }
            }
            data[i] = mnistMatrix;
            if(observer != null)
                observer.update(i+1, mnistMatrix);
        }

        dataInputStream.close();
        labelInputStream.close();

        if(observer != null)
            observer.finish(numberOfItems);

        return data;
    }
}
