package imageprocessing;

import boofcv.alg.filter.blur.GBlurImageOps;
import boofcv.alg.filter.convolve.GConvolveImageOps;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.border.BorderType;
import boofcv.struct.convolve.Kernel1D_S32;
import boofcv.struct.convolve.Kernel2D_S32;
import boofcv.struct.image.GrayU8;

public class Convolution {

  public static void meanFilterSimple(GrayU8 input, GrayU8 output, int size) {
    for(int y = 0; y < input.height; y++) {
      for(int x = 0; x < input.width; x++) {
        int sum = 0;
        for(int i = -size; i <= size; i++) {
          for(int j = -size; j <= size; j++) {
            int xx = x + i;
            int yy = y + j;
            if(xx >= 0 && xx < input.width && yy >= 0 && yy < input.height) {
              sum += input.get(xx, yy);
            }
          }
        }
        output.set(x, y, sum / ((2 * size + 1) * (2 * size + 1)));
      }
    }
  }

  public static void meanFilterWithBorders(GrayU8 input, GrayU8 output, int size, BorderType borderType) {
    
  }
  
  public static void convolution(GrayU8 input, GrayU8 output, int[][] kernel) {
    
  }

  public static void main(final String[] args) {
    // load image
    if (args.length < 2) {
      System.out.println("missing input or output image filename");
      System.exit(-1);
    }
    final String inputPath = args[0];
    GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
    GrayU8 output = input.createSameShape();

    meanFilterSimple(input, output, 3);

    // save output image
    final String outputPath = args[1];
    UtilImageIO.saveImage(output, outputPath);
    System.out.println("Image saved in: " + outputPath);
  }

}