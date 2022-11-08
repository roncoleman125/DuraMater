package duramater.mnist.mlp;


import org.encog.mathutil.Equilateral;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.zip.CRC32;

public class MLoader implements IMLoader {
    protected String dataPath;
    protected String labelsPath;
    protected int pixelsMagic;
    protected int labelsMagic;
    protected CRC32 crc = new CRC32();

    public MLoader(String dataPath, String labelsPath) {
        this.dataPath = dataPath;
        this.labelsPath = labelsPath;
    }

    @Override
    public MDigit[] load() {
        try {
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(dataPath)));
            this.pixelsMagic = dataInputStream.readInt();
            int nDigits = dataInputStream.readInt();

            int nRows = dataInputStream.readInt();
            int nCols = dataInputStream.readInt();

            DataInputStream labelInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(labelsPath)));
            this.labelsMagic = labelInputStream.readInt();
            int nLabels = labelInputStream.readInt();

            assert (nDigits == nLabels);

            final MDigit[] digits = new MDigit[nDigits];

            crc.reset();

            IntStream.range(0,nDigits).forEach(digitno -> {
                int label = -1;
                try {
                    label = labelInputStream.readUnsignedByte();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                double[] pixels = new double[nRows * nCols];
                IntStream.range(0,nRows*nCols).forEach(idx -> {
                    try {
                        double pixel = dataInputStream.readUnsignedByte();
                        pixels[idx] = pixel;
                        crc.update((int)pixel);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                digits[digitno] = new MDigit(digitno, pixels, label);
            });

            dataInputStream.close();
            labelInputStream.close();
            return digits;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getPixelsMagic() {
        return pixelsMagic;
    }

    @Override
    public int getLabelsMagic() {
        return labelsMagic;
    }

    @Override
    public long getChecksum() {
        return crc.getValue();
    }

    @Override
    public Normal normalize() {
        MDigit[] digits = this.load();

        int nRows = digits.length;
        int nCells = 28*28;

        double[][] pixels = new double[nRows][nCells];
        double[][] labels = new double[nRows][];

        Equilateral eq = new Equilateral(10,1.0,0.0);
        for(int digitno=0; digitno < nRows; digitno++) {
            MDigit digit = digits[digitno];
//            System.out.println(digit.toString()+"");
            for(int cellno=0; cellno < nCells; cellno++) {
                double pixel = digit.pixels()[cellno];
                double normalizedPixel = pixel / 255.0;
                pixels[digitno][cellno] = normalizedPixel;
            }
            int cat = digits[digitno].label();
            double[] encodedLabel = eq.encode(cat);
            labels[digitno] = encodedLabel;
        }
        return new Normal(pixels,labels);
    }

    public static void main(String[] args) throws Exception {
        Random ran = new Random();
//        IMLoader loader = new MLoader("data/t10k-images.idx3-ubyte","data/t10k-labels.idx1-ubyte");
        IMLoader loader = new MLoader("data/train-images.idx3-ubyte","data/train-labels.idx1-ubyte");

        MDigit[] digits = loader.load();
        System.out.println("digits: "+digits.length);
        System.out.println("pixels magic: "+loader.getPixelsMagic());
        System.out.println("labels magic: "+loader.getLabelsMagic());
        System.out.println("checksum: "+loader.getChecksum());
        for(int idx=27; idx < digits.length; idx++) {
            if(digits[idx].label() == 7) {
                System.out.println(digits[idx] + "");
                break;
            }
        }
//        int digitno = 2; //ran.nextInt(digits.length);
//        System.out.println("digitno: "+digitno);
//        System.out.println(digits[digitno]+"");

    }
}
