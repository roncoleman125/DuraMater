package duramater.mnist.mlp;

import java.io.*;
import java.util.zip.CRC32;

public class MLoader2 implements IMLoader {

    public static CRC32 crc = new CRC32();
    public static int pixelsMagic;
    public static int labelsMagic;
    private final String pixelsPath;
    private final String labelsPath;

    /**
     *
     * @param pixelsPath the path of the pixels dataset
     * @param labelsPath the path of the labels dataset
     */
    public MLoader2(String pixelsPath, String labelsPath) {
        this.pixelsPath = pixelsPath;
        this.labelsPath = labelsPath;
    }

    /**
     * Loads the pixels and labels datasets into an array of MDigits

     * @return the array of MDigits with the loaded pixels and their respective labels
     */
    @Override
    public MDigit[] load() {

        try {
            DataInputStream pixels =
                    new DataInputStream(
                            new BufferedInputStream(
                                    new FileInputStream(pixelsPath)));

            DataInputStream labels =
                    new DataInputStream(
                            new BufferedInputStream(
                                    new FileInputStream(labelsPath)));

            // Headers from the Pixels Dataset
            pixelsMagic = pixels.readInt();
            int numberOfImages = pixels.readInt();
            int rows = pixels.readInt();
            int cols = pixels.readInt();

            // Headers from the Labels Dataset
            labelsMagic = labels.readInt();
            int numberOfLabels = labels.readInt();

            MDigit[] loadDigitsArray = new MDigit[numberOfImages];

            // Checks if the labels equal to the number of images
            if(numberOfImages == numberOfLabels) {

                for(int j = 0; j < numberOfImages; j++) {

                    // Gets the pixels for each image
                    double[] image = new double[rows * cols];
                    for(int i = 0; i < (rows * cols); i++) {
                        image[i] = pixels.readUnsignedByte();
                        crc.update((int)image[i]);
                    }

                    // Gets the label for each image
                    int label = labels.readUnsignedByte();

                    loadDigitsArray[j] = new MDigit(j, image, label);;
                }
            }
            else {
                System.out.println("The number of items in the pixels dataset do not match the number of labels. " +
                        "Recheck your dataset.");
            }

            // Closes both data input streams
            pixels.close();
            labels.close();

            return loadDigitsArray;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the magic number for the pixels dataset
     */
    @Override
    public int getPixelsMagic() {return pixelsMagic;}

    /**
     * Returns the magic number for the labels dataset
     */
    @Override
    public int getLabelsMagic() {return labelsMagic;}

    /**
     * Returns the checksum value for the pixels dataset
     */
    @Override
    public long getChecksum() { return crc.getValue();}

    /**
     * Normalize Function
     * @return
     */
    @Override
    public Normal normalize() {
        return null;
    }

    public static void main(String[] args) {
        IMLoader mloader = new MLoader2("data/train-images.idx3-ubyte","data/train-labels.idx1-ubyte");
        MDigit[] digits = mloader.load();
        System.out.println("check sum = "+mloader.getChecksum());
    }
}
