package imageprocessing;

import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.misc.ImageStatistics;
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

	public static int[] histogram(GrayU8 input) {
		// PARTIE 1 : CALCULER L'HISTOGRAMME
		int[] histogram = new int[256];
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int pixel = input.get(x, y);
				histogram[pixel]++;
			}
		}
		// PARTIE 2 : CALCULER L'HISTOGRAMME CUMULÉ
		int[] cumulative_histogram = new int[256];
		cumulative_histogram[0] = histogram[0];
		for (int i = 1; i < 256; i++) {
			cumulative_histogram[i] = cumulative_histogram[i - 1] + histogram[i];
		}

		// PARTIE 3 : UTILISER L'HISTOGRAMME CUMULÉ COMME LUT
		int min = cumulative_histogram[0];
		int max = cumulative_histogram[255];
		int[] lut = new int[256];
		for (int i = 0; i < 256; i++) {
			lut[i] = (int) (255.0 * (cumulative_histogram[i] - min) / (max - min));
			if (lut[i] < 0) {
				lut[i] = 0;
			} else if (lut[i] > 255) {
				lut[i] = 255;
			}
		}
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int pixel = input.get(x, y);
				input.set(x, y, lut[pixel]);
			}
		}
		return cumulative_histogram;
	}

	public static void dynamique_lineaire_lookuptable(GrayU8 input, int min, int max) {
		int[] lut = new int[256];
		for (int i = 0; i < 256; i++) {
			lut[i] = (int) (255.0 * (i - min) / (max - min));
			if (lut[i] < 0) {
				lut[i] = 0;
			} else if (lut[i] > 255) {
				lut[i] = 255;
			}
		}
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int pixel = input.get(x, y);
				input.set(x, y, lut[pixel]);
			}
		}
		// Temps d'exection : 3033815639900 nano secondes pour min = 254 et max = 1
		// On en déduit donc que cette fonction est plus rapide
		System.out.print("Temps d'exection : " + System.nanoTime() + "\n");
	}

	public static void dynamique_lineaire(GrayU8 input, int min, int max) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int pixel = input.get(x, y);
				pixel = (int) (255.0 * (pixel - min) / (max - min));
				if (pixel < 0) {
					pixel = 0;
				} else if (pixel > 255) {
					pixel = 255;
				}
				input.set(x, y, pixel);
			}
		}
		// Temps d'exection : 3082104035600 nano secondes pour min = 254 et max = 1
		// On en déduit donc que cette fonction est plus lente
		System.out.print("Temps d'exection : " + System.nanoTime());
	}

	public static void brightness(GrayU8 input, int delta) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int pixel = input.get(x, y);
				pixel += delta;
				if (pixel < 0) {
					pixel = 0;
				} else if (pixel > 255) {
					pixel = 255;
				}
				input.set(x, y, pixel);
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

		// histogram(input);
		int[] histogram = new int[256];
		int[] transform = new int[256];

		ImageStatistics.histogram(input, 0, histogram);
		EnhanceImageOps.equalize(histogram, transform);
		GrayU8 adjusted = input.createSameShape();
		EnhanceImageOps.applyTransform(input, transform, adjusted);

		// save output image
		final String outputPath = args[1];
		UtilImageIO.saveImage(adjusted, outputPath);
		System.out.println("Image saved in: " + outputPath);
	}

}