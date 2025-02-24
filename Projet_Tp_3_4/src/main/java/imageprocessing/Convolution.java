package imageprocessing;

import boofcv.core.image.ConvertImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;


public class Convolution {

  public static void meanFilter(GrayU8 input, GrayU8 output, int size) {
    for (int i = 0; i < input.width; ++i){
      for (int j = 0; j < input.height; ++j){
        int moyenne = 0;
        int cmp = 0;
        for (int x = i-(size/2); x <= i+(size/2); ++x){
          for (int y = j-(size/2); y <= j+(size/2); ++y){
            if(x>=0 && y>=0 && x<input.width && y<input.height){
              moyenne += input.get(x,y);
              cmp++;
            }
          }
        }
        if(cmp!=0){
          moyenne = moyenne/cmp;
          output.set(i,j,moyenne);
        }
      }
    }
  }

  public static void convolution(GrayU8 input, GrayS16 output, int[][] kernel) {
    int n = kernel.length / 2; 
    for (int i=n; i<input.width-n; ++i){
      for (int j=n;j<input.height-n; ++j){
        int temp=0;
        for (int x=-n; x<=n; ++x){
          for (int y=-n; y<=n; ++y){
            temp+=input.get(i+x, j+y)*kernel[x+n][y+n];            
          }
        }
        output.set(i,j,temp);
      }
    }
  }


  public static void gradientImage(GrayU8 input, GrayU8 output, int[][] kernelX, int[][] kernelY) {
    GrayS16 gradX=new GrayS16(input.width,input.height);
    GrayS16 gradY=new GrayS16(input.width,input.height);
    
    convolution(input,gradX,kernelX);
    convolution(input,gradY,kernelY);
    
    for (int i=0; i<input.width; ++i) {
      for (int j=0; j<input.height; ++j){
        int x=gradX.get(i,j);
        int y=gradY.get(i,j);
        int temp=(int) Math.sqrt(x*x+y*y);
        temp = Math.min(255,temp);
        output.set(i,j,temp);
      }
    }
  }
 

  public static void gradientImageSobel(GrayU8 input, GrayU8 output){
    int[][] kernelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    int[][] kernelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
    gradientImage(input, output, kernelX, kernelY);
  }

  public static void gradientImagePrewitt(GrayU8 input, GrayU8 output){
    int[][] kernelX = {{-1, 0, 1}, {-1, 0, 1}, {-1, 0, 1}};
    int[][] kernelY = {{-1, -1, -1}, {0, 0, 0}, {1, 1, 1}};
    gradientImage(input, output, kernelX, kernelY);
  }

  
  public static void main(final String[] args) {
    // load image
    if (args.length < 2) {
      System.out.println("missing input or output image filename");
      System.exit(-1);
    }
    final String inputPath = args[0];
    GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
    //GrayU8 output = input.createSameShape(); //mettre en commentaire pour convolutoin


    ///////processing
    
    //gradientImagePrewitt(input, output);

    GrayS16 output = input.createSameShape(GrayS16.class);
    int[][] kernel = {
      {1,1,1},
      {1,1,1},
      {1,1,1}
    };
    convolution(input, output, kernel);
    GrayU8 output2 = ConvertImage.convert(output, (GrayU8) null);

    //meanFilter(input, output, 11);
    
    // save output image
    final String outputPath = args[1];
    UtilImageIO.saveImage(output2, outputPath); //mettre output2 pour convolution
    System.out.println("Image saved in: " + outputPath);
  }

}
