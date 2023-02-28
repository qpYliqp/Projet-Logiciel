package imageprocessing;

import java.awt.image.BufferedImage;

import boofcv.alg.color.ColorHsv;
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

   /*______________________________________________________________________________________________
   *______________________________________________________________________________________________
   *__________________________________________brightnessColor_____________________________________
   *______________________________________________________________________________________________
   */

    public static void brightnessColor(Planar<GrayU8> input, int delta) {
        for (int y = 0; y < input.height; y++) {
            for (int x = 0; x < input.width; x++) {
                int r = input.getBand(0).get(x, y);
                int g = input.getBand(1).get(x, y);
                int b = input.getBand(2).get(x, y);
                //System.out.print(r +" g"+ g + " b" + b+"\n");
                int newR = r + delta;
                int newG = g + delta;
                int newB = b + delta;
                if(newR > 255) newR = 255;
                if(newG > 255) newG = 255;
                if(newB > 255) newB = 255;
                if(newR < 0) newR = 0;
                if(newG < 0) newG = 0;
                if(newB < 0) newB = 0;
    
                
                input.getBand(0).set(x, y, newR);
                input.getBand(1).set(x, y, newG);
                input.getBand(2).set(x, y, newB);
            }
        }
    }

   /*_____________________________________________________________________________________________
   *______________________________________________________________________________________________
   *__________________________________________convolutionColor____________________________________
   *______________________________________________________________________________________________
   */

    public static void convolutionColor(Planar<GrayU8> input, int[][] kernel)
    {
      
        int count = 0;
        int size = kernel.length;
        int n = (size-1)/2;
    
        for(int i = 0 ; i < kernel.length ; i++)
        {
            for(int j = 0 ; j < kernel[i].length ; j++)
            {
                count += kernel[i][j];
            }
        
        }
        for(int x = n; x < input.width - n; x++) 
        {
            for(int y = n; y < input.height - n; y++)
            {
                
                for(int b = 0; b < input.getNumBands(); b++)
                {
                    int somme = 0;
                    for(int i = -n; i <= n; i++)
                    {
                        for(int j = -n; j <= n; j++)
                        {
                            int u = x + i;
                            int v = y + j;
                    
                            somme += input.getBand(b).get(u, v) * kernel[i+n][j+n];
                            //System.out.println(kernel[i-y+n][j-x+n]);
                        }
                    }
                    input.getBand(b).set(x, y, (int)somme/count);
                    //System.out.println(" input : " + input.get(x,y) + "\n output : " + somme/count);
                }
                
            }   
        }
    }

   /*_____________________________________________________________________________________________
   *______________________________________________________________________________________________
   *__________________________________________ConversionColorToGray_______________________________
   *______________________________________________________________________________________________
   */
      

    public static void ConversionColorToGray(Planar<GrayU8> input) {
        for (int y = 0; y < input.height; y++) {
            for (int x = 0; x < input.width; x++) {
                int r = input.getBand(0).get(x, y);
                int g = input.getBand(1).get(x, y);
                int b = input.getBand(2).get(x, y);
                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                input.getBand(0).set(x, y, gray);
                input.getBand(1).set(x, y, gray);
                input.getBand(2).set(x, y, gray);
            }
        }
    }


   /*_____________________________________________________________________________________________
   *______________________________________________________________________________________________
   *__________________________________________RGBtoHSV____________________________________________
   *______________________________________________________________________________________________
   */

    public static void imageToHsv(Planar<GrayU8> input)
    {
            for(int y = 0 ; y < input.height ; y++)
            {
                for(int x = 0 ; x < input.width ; x++)
                {
                    int r = input.getBand(0).get(x, y);
                    int g = input.getBand(1).get(x, y);
                    int b = input.getBand(2).get(x, y);
                    float[] hsv = new float[3];
                    rgbToHsv(r, g, b, hsv);
                    System.out.print("h : " + hsv[0] + " s : " + hsv[1] + " v : " + hsv[2] + "\n");
                    input.getBand(0).set(x, y, (int)hsv[0]);
                    input.getBand(1).set(x, y, (int)hsv[1]);
                    input.getBand(2).set(x, y, (int)hsv[2]);
                }
            }
    }

    // public static void imageToHsvBOOF(Planar<GrayU8> input)
    // {
    //         ColorHsv.rgbToHsv(input, null);
    // }

    public static void rgbToHsv(int r, int g, int b, float[] hsv)
    {
        float min, max, delta;
        min = Math.min(Math.min(r, g), b);
        max = Math.max(Math.max(r, g), b);
        hsv[2] = max; 
        delta = max - min;
        if(max == min)
        {
            hsv[0] = 0;
            hsv[1] = 0;
            return;
        }
        else if(max == r)
        {
          hsv[0] = (60 * ((g - b) / delta) + 360) % 30;
        }
        else if(max == g)
        {
          hsv[0] = 60 * ((b - r) / delta) + 120;
        }
        else if(max == b)
        {
          hsv[0] = 60 * ((r - g) / delta) + 240;
        }
        if(max == 0)
        {
            hsv[1] = 0;
        }
        else
        {
             hsv[1] = 1 - min/max;
        }
        hsv[2] = max;
        
    }

  /*______________________________________________________________________________________________
   *______________________________________________________________________________________________
   *__________________________________________hsvToRgb____________________________________________
   *______________________________________________________________________________________________
   */

    void hsvToRgb(float h, float s, float v, int[] rgb)
    {
        
    }

   /*_____________________________________________________________________________________________
   *______________________________________________________________________________________________
   *__________________________________________extension_dynamique_color___________________________
   *______________________________________________________________________________________________
   */

    public static void extension_dynamique_color(Planar<GrayU8> input, int min, int max)
    {
        for(int y = 0; y < input.height ; y++)
        {
            for(int x = 0; x < input.width ; x++)
            {
                for(int b = 0; b < input.getNumBands(); b++)
                {
                    int val = input.getBand(b).get(x, y);
                    int new_val = (int) (255.0 * (val - min) / (max - min));
                    if(new_val > 255) new_val = 255;
                    if(new_val < 0) new_val = 0;
                    input.getBand(b).set(x, y, new_val);
                }
            }
        }
    }


    public static void histogram_color(Planar<GrayU8> input)
    {
        int[] histogram = new int[256];
        for(int y = 0; y < input.height ; y++)
        {
            for(int x = 0; x < input.width ; x++)
            {
                for(int b = 0; b < input.getNumBands(); b++)
                {
                    int val = input.getBand(b).get(x, y);
                    histogram[val]++;
                }
            }
        }
        int min = 0;
        int max = 255;
        for(int i = 0; i < 256; i++)
        {
            if(histogram[i] != 0)
            {
                min = i;
                break;
            }
        }
        for(int i = 255; i >= 0; i--)
        {
            if(histogram[i] != 0)
            {
                max = i;
                break;
            }
        }
        extension_dynamique_color(input, min, max);
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

        //brightnessColor(image, -100);
        int tab[][] = 
        {{1,2,3,2,1},
        {2,6,8,6,2},
        {3,8,10,8,3},
        {2,6,8,6,2},
        {1,2,3,2,1}};

        int matrice[][] = 
        {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };
        
        //Planar<GrayU8> output = new Planar<>(GrayU8.class, input.getWidth(), input.getHeight(), image.getNumBands());

        //convolutionColor(image, matrice);
        //ConversionColorToGray(image);


       

        
        //imageToHsv(image);
        extension_dynamique_color(image, 5, 55);
        //brightnessColor(image, -50);
        //histogram_color(image);


        final String outputPath = args[1];
        

        /*POUR UTILISER LE RESTE : */ UtilImageIO.saveImage(image, outputPath);


    }

}
