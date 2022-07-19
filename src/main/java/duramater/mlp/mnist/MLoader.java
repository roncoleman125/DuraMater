package duramater.mlp.mnist;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
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
    public MDigit[] load() throws Exception {
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(dataPath)));
        this.pixelsMagic = dataInputStream.readInt();
        int nDigits = dataInputStream.readInt();

        int nRows = dataInputStream.readInt();
        int nCols = dataInputStream.readInt();

        DataInputStream labelInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(labelsPath)));
        this.labelsMagic = labelInputStream.readInt();
        int nLabels = labelInputStream.readInt();

        assert(nDigits == nLabels);

        MDigit[] digits = new MDigit[nDigits];

        for(int digitno = 0; digitno < nDigits; digitno++) {
            double[] pixels = new double[nRows*nCols];

            int label = labelInputStream.readUnsignedByte();
            for (int r = 0; r < nRows; r++) {
                for (int c = 0; c < nCols; c++) {
                    int pixel = dataInputStream.readUnsignedByte();
                    crc.update(pixel);
                    pixels[nCols*r + c] = pixel;
                    crc.update(pixel);
                }
            }
            digits[digitno] = new MDigit(pixels,label);
        }

        dataInputStream.close();
        labelInputStream.close();

        return digits;
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

    public static void main(String[] args) throws Exception {
        IMLoader loader = new MLoader("data/t10k-images.idx3-ubyte","data/t10k-labels.idx1-ubyte");
        MDigit[] digits = loader.load();
        System.out.println("digits: "+digits.length);
        System.out.println("pixels magic: "+loader.getPixelsMagic());
        System.out.println("labels magic: "+loader.getLabelsMagic());
        System.out.println("checksum: "+loader.getChecksum());
    }
}
