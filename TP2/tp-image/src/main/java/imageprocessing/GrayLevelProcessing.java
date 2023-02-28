package imageprocessing;

import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.concurrency.BoofConcurrency;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;

//mvn exec:java '-Dexec.mainClass=imageprocessing.GrayLevelProcessing' '-Dexec.args=input_images/input.jpeg test.jpeg'


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

	/*__________________________________________________________________
	* _________________________________________________________________
	* ______________________brightness_________________________________
	* _________________________________________________________________
	*/
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

	/*__________________________________________________________________
	 * _________________________________________________________________
	 * ______________________extend_gray________________________________
	 * _________________________________________________________________
	 */

	public static void extend_gray(GrayU8 input) {
		// Trouver la valeur max et min de l'image :
		int max = 0, min = 10000000;
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int pixel = input.get(x, y);
				if (pixel > max) {
					max = pixel;
				}
				if (pixel < min) {
					min = pixel;
				}
			}
		}
		// Appliquer la formule du cours
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int pixel = input.get(x, y);
				pixel = (int) (255 * (pixel - min) / (max - min));
				if (pixel < 0) {
					pixel = 0;
				} else if (pixel > 255) {
					pixel = 255;
				}
				input.set(x, y, pixel);
			}
		}
	}


	/*__________________________________________________________________
	 * _________________________________________________________________
	 * ______________________dynamique_lineaire_________________________
	 * _________________________________________________________________
	 */
	public static void dynamique_lineaire(GrayU8 input, int min, int max) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int pixel = input.get(x, y);
				pixel = (int) (255 * (pixel - min) / (max - min));
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
		System.out.print("Temps d'execution : " + System.nanoTime() + "\n");
	}




	/*__________________________________________________________________
	 * _________________________________________________________________
	 * ______________________dynamique_lineaire_lut_____________________
	 * _________________________________________________________________
	 */
	public static void dynamique_lineaire_lut(GrayU8 input, int min, int max) {
		int[] lut = new int[256];
		for (int i = 0; i < 256; i++) {
			lut[i] = (int) (255 * (i - min) / (max - min));
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
		System.out.print("Temps d'execution : " + System.nanoTime() + "\n");
	}



	/*__________________________________________________________________
	 * _________________________________________________________________
	 * ______________________histogram__________________________________
	 * _________________________________________________________________
	 */

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
		System.out.print("Temps d'execution : " + System.nanoTime() + "\n");
		return cumulative_histogram;
	}



	/*__________________________________________________________________
	 * _________________________________________________________________
	 * ______________________histogram_threads__________________________
	 * _________________________________________________________________
	 */

	public static int[] histogram_threads(GrayU8 input, int thread) {
		BoofConcurrency.setMaxThreads(thread);
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
		BoofConcurrency.loopFor(0, input.height, y -> {
			for (int x = 0; x < input.width; ++x) {
				int pixel = input.get(x, y);
				input.set(x, y, lut[pixel]);
			}
		});
		System.out.print("Temps d'execution : " + System.nanoTime() + "\n");
		return cumulative_histogram;
	}




	/*__________________________________________________________________
	* __________________________________________________________________
	* ______________________boof_histogram______________________________
	* __________________________________________________________________
	*/

	// Fonction qui permet de faire un histogramme cumulé avec BoofCV
	// Elle permet aussi de générer directement l'image,
	// Pour cette raison, il faut mettre en commentaire la ligne "UtilImageIO.saveImage(input, outputPath);" dans la fonction main
		public static void boof_histogram(GrayU8 input, String output) {
			int[] histogram = new int[256];
			int[] transform = new int[256];

			ImageStatistics.histogram(input, 0, histogram);
			EnhanceImageOps.equalize(histogram, transform);
			GrayU8 adjusted = input.createSameShape();
			EnhanceImageOps.applyTransform(input, transform, adjusted);
			UtilImageIO.saveImage(adjusted, output);
		}



	/*________________________________________________________________________ */

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

		/*
		 * _________________________________________________________________
		 * _________________________________________________________________
		 * _________________Fonctions :_____________________________________
		 * _________________________________________________________________
		 */
		// histogram(input);
		// extend_gray(input);
		// dynamique_lineaire(input, 0, 255);
		// dynamique_lineaire_lut(input, 0, 255);
		histogram_threads(input,12 );

		//__________________________________________________________________

		// save output image
		final String outputPath = args[1];
		/* POUR TESTER boof_histogram: */ //boof_histogram(input, outputPath);
		/* Pour tester les autres fonctions : */ UtilImageIO.saveImage(input, outputPath);
		// UtilImageIO.saveImage(adjusted, outputPath);
		System.out.println("Image saved in: " + outputPath);
	}

}