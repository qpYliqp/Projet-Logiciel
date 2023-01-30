package imageprocessing;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;

public class GrayLevelProcessing {

	public static void threshold(GrayU8 input, int t) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				if (gl < t) {
					gl = 0;
				} else {
					gl = 255;
				}
				input.set(x, y, gl);
			}
		}
	}

	public static void luminosity(GrayU8 input, int delta) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				gl += delta;
				if (gl < 0) {
					gl = 0;
				} else if (gl > 255) {
					gl = 255;
				}
				input.set(x, y, gl);
			}
		}
	}

	public static void contrast(GrayU8 input, int delta) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				gl = (int) (gl * (1 + delta / 100.0));
				if (gl < 0) {
					gl = 0;
				} else if (gl > 255) {
					gl = 255;
				}
				input.set(x, y, gl);
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
		GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
		if (input == null) {
			System.err.println("Cannot read input file '" + inputPath);
			System.exit(-1);
		}

		// processing

		luminosity(input, 100);

		// save output image
		final String outputPath = args[1];
		UtilImageIO.saveImage(input, outputPath);
		System.out.println("Image saved in: " + outputPath);
	}

}