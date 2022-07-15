package duramater.knn.mnist.model;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class MDataFactory implements IMDataFactory {
    String dataPath;
    String labelsPath;
    int pixelMagic = -1;
    int labelsMagic = -1;
    CRC32 crc = new CRC32();

    public MDataFactory(String dataPath, String labelsPath) {
        this.dataPath = dataPath;
        this.labelsPath = labelsPath;
    }

    @Override
    public MDigit[] getDigits() throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(dataPath)));
        this.pixelMagic = dataInputStream.readInt();
        int numDigits = dataInputStream.readInt();

        int nRows = dataInputStream.readInt();
        int nCols = dataInputStream.readInt();

        DataInputStream labelInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(labelsPath)));
        this.labelsMagic = labelInputStream.readInt();
        int numLabels = labelInputStream.readInt();

        assert(numDigits == numLabels);

        MDigit[] digits = new MDigit[numDigits];

        for(int i = 0; i < numDigits; i++) {
            int[] pixels = new int[nRows*nCols];

            int label = labelInputStream.readUnsignedByte();
            for (int r = 0; r < nRows; r++) {
                for (int c = 0; c < nCols; c++) {
                    int pixel = dataInputStream.readUnsignedByte();
                    pixels[nCols*r + c] = pixel;
                    crc.update(pixel);
                }
            }
            digits[numDigits] = new MDigit(pixels,label);
        }

        dataInputStream.close();
        labelInputStream.close();

        return digits;
    }

    @Override
    public int getPixelsMagic() {
        return pixelMagic;
    }

    @Override
    public int getLabelsMagic() {
        return labelsMagic;
    }

    @Override
    public long getChecksum() {
        return crc.getValue();
    }
}
