package imageprocessing;

import boofcv.alg.filter.blur.GBlurImageOps;
import boofcv.alg.filter.convolve.GConvolveImageOps;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.border.BorderType;
import boofcv.struct.convolve.Kernel1D_S32;
import boofcv.struct.convolve.Kernel2D_S32;
import boofcv.struct.image.GrayU8;
//mvn exec:java '-Dexec.mainClass=imageprocessing.Convolution' '-Dexec.args=input_images/input.jpeg test.jpeg'

public class Convolution {



  /*______________________________________________________________________________________________
   *______________________________________________________________________________________________
   *__________________________________________MeanFilterSimple____________________________________
   *______________________________________________________________________________________________
   */
  public static void meanFilterSimple(GrayU8 input, GrayU8 output, int size) {
    for(int y = 0; y < input.height; y++) {
      for(int x = 0; x < input.width; x++) {
        int somme = 0;
        for(int i = -size; i <= size; i++) {
          for(int j = -size; j <= size; j++) {
            int u = x + i;
            int v = y + j;
            if(u >= 0 && u < input.width && v >= 0 && v < input.height) {
              somme += input.get(u, v);
            }
          }
        }
        output.set(x, y, somme / ((2 * size + 1) * (2 * size + 1)));
      }
    }
  }



  /*______________________________________________________________________________________________
   *______________________________________________________________________________________________
   *__________________________________________MeanFilterWithBorders_______________________________
   *______________________________________________________________________________________________
   */
  public static void meanFilterWithBorders(GrayU8 input, GrayU8 output, int size, BorderType borderType)
  {
    for(int y = 0; y < input.height; y++)
    {
      for(int x = 0; x < input.width; x++) 
      {
        int somme = 0;
        for(int i = -size; i <= size; i++)
        {
          for(int j = -size; j <= size; j++)
          {
            int u = x + i;
            int v = y + j;
            if(u >= 0 && u < input.width && v >= 0 && v < input.height)
            {
              somme += input.get(u, v);
            }
            else
            {
              if(borderType == BorderType.EXTENDED){
                if(u < 0)
                {
                  u = 0;
                }              
                else if(u >= input.width)
                {
                  //il faut mettre -1 car sinon on sort de l'image : "An exception occurred while executing the Java class. Requested pixel is out of bounds: 1920 0"
                  u = input.width - 1;
                }
                if(v < 0)
                {
                  v = 0;
                }
                else if(v >= input.height)
                {
                  v = input.height - 1;
                }
                somme += input.get(u, v);
              }
              else if(borderType == BorderType.REFLECT)
              {
                somme += input.get(Math.abs(u), Math.abs(v));
              }
              else if(borderType == BorderType.SKIP)
              {
                somme += 0;
              }
              else if(borderType == BorderType.NORMALIZED){
                
                
            }
          }
        }
        output.set(x, y, somme / ((2 * size + 1) * (2 * size + 1)));
      }
     }
    }

  }

  /*______________________________________________________________________________________________
   *______________________________________________________________________________________________
   *__________________________________________Convolution_______________________________
   *______________________________________________________________________________________________
   */

  public static void convolution(GrayU8 input, GrayU8 output, int[][] kernel)
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
    for(int y = n; y < input.height - n; y++) {
      for(int x = n; x < input.width - n; x++) {
        int somme = 0;
        for(int i = y-n; i <= y+n; i++)
        {
          for(int j = x-n; j <= x+n; j++)
          {
              somme += input.get(j, i) * kernel[i-y+n][j-x+n];
              //System.out.println(kernel[i-y+n][j-x+n]);
          }
          //System.out.println(" input : " + input.get(x,y) + "\n output : " + somme/count);
        }
        output.set(x, y, somme/count);
      }
    }
  }
  
  /*______________________________________________________________________________________________
   *______________________________________________________________________________________________
   *__________________________________________gradientImageSobel__________________________________
   *______________________________________________________________________________________________
   */
  public static void gradientImageSobel(GrayU8 input, GrayU8 output)
  {
    int mask1[][] = 
    {
      {-1,0,1},
      {-2,0,2},
      {-1,0,1}
    };
    int mask2[][] = 
    {
      {-1,-2,-1},
      {0,0,0},
      {1,2,1}
    };

    int Gx[][] = new int[input.height][input.width];
    int Gy[][] = new int[input.height][input.width];
    
    int size = mask1.length;
    int n = (size-1)/2;

    for(int y = n; y < input.height - n; y++) {
      for(int x = n; x < input.width - n; x++) {
        
        for(int i = y-n; i <= y+n; i++)
        {
          for(int j = x-n; j <= x+n; j++)
          {
              Gx[y][x] += input.get(j, i) * mask1[i-y+n][j-x+n];
          }

        }
      }
    }

    for(int y = n; y < input.height - n; y++) {
      for(int x = n; x < input.width - n; x++) {
        
        for(int i = y-n; i <= y+n; i++)
        {
          for(int j = x-n; j <= x+n; j++)
          {
              Gy[y][x] += input.get(j, i) * mask2[i-y+n][j-x+n];
          }
          
        }
      }
    }

    for(int y = 0; y < input.height; y++) {
      for(int x = 0; x < input.width; x++) {
        output.set(x, y, (int)Math.sqrt(Gx[y][x]*Gx[y][x] + Gy[y][x]*Gy[y][x]));
      }
    }
  
  }

  public static void main(final String[] args){
    // load image
    if (args.length < 2) {
      System.out.println("missing input or output image filename");
      System.exit(-1);
    }
    final String inputPath = args[0];
    GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
    GrayU8 output = input.createSameShape();

    int tab[][] = 
    {{1,2,3,2,1},
    {2,6,8,6,2},
    {3,8,10,8,3},
    {2,6,8,6,2},
    {1,2,3,2,1}};

    int matrice[][] = {
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
    // convolution(input, output, tab);
    //meanFilterSimple(input, output, 5);
    //meanFilterWithBorders(input, output, 5, BorderType.NORMALIZED);

    //convolution(input, output, tab);

    gradientImageSobel(input, output);

    // save output image
    final String outputPath = args[1];
    UtilImageIO.saveImage(output, outputPath);
    System.out.println("Image saved in: " + outputPath);
  }

}