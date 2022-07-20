package duramater.mlp.mnist;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public record MDigit(int no, double[] pixels, int label) {
    @Override
    public  String toString() {
//        AtomicReference<String> s = new AtomicReference<>(label + "\n");
//        IntStream.range(0,28).forEach(rowno -> {
//            IntStream.range(0,28).forEach(colno -> {
//                int pixel = (int) pixels[rowno * 28 + colno];
//                String code = ".";
//                if(pixel == 255)
//                    code = 2+"";
//                else if(pixel == 254)
//                    code = 1+"";
//                String finalCode = code;
//                s.updateAndGet(v -> v + finalCode);
//            });
//        });
        String codes = "0123456789ABCDEF";
        String s = "# "+no+" label:"+label+"\n";
        for(int rowno=0; rowno < 28; rowno++) {
            for(int colno=0; colno < 28; colno++) {
                int pixel = (int) pixels[rowno * 28 + colno];
                String code = ".";
                int idx = 255-pixel;
                if(idx >= codes.length())
                    code = ".";
                else
                    code = codes.charAt(idx)+"";

                s += code;
            }
            s += "\n";
        }
        return s;
    }
}
