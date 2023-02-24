package imageprocessing;

import java.awt.image.BufferedImage;

import boofcv.alg.filter.blur.GBlurImageOps;
import boofcv.alg.filter.convolve.GConvolveImageOps;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.io.wrapper.images.BufferedFileImageSequence;
import boofcv.struct.border.BorderType;
import boofcv.struct.convolve.Kernel1D_S32;
import boofcv.struct.convolve.Kernel2D_S32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
//mvn exec:java '-Dexec.mainClass=imageprocessing.ColorProcessing' '-Dexec.args=input_images/input.jpeg test.jpeg'

public class ColorProcessing {

    public static void brightnessColor(Planar<GrayU8> input, int delta) {
        for (int y = 0; y < input.height; ++y) {
            for (int x = 0; x < input.width; ++x) {
                for (int i = 0; i < 3; i++) {
                    int pixel = input.getBand(i).get(x, y);
                    pixel += delta;
                    if (pixel < 0) {
                        pixel = 0;
                    } else if (pixel > 255) {
                        pixel = 255;
                    }
                    input.getBand(i).set(x, y, pixel);
                }
            }
        }

    }

    public static void main(String[] args) {

        // load image
        if (args.length < 2) {
            System.out.println("missing input or output image filename");
            System.exit(-1);
        }
        final String inputPath = args[0];
        BufferedImage input = UtilImageIO.loadImage(inputPath);
        Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);

        if (input == null) {
            System.err.println("Cannot read input file '" + inputPath);
            System.exit(-1);
        }

        brightnessColor(image, 50);
        final String outputPath = args[1];

        UtilImageIO.saveImage(input, outputPath);

    }

}
