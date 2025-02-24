package imageprocessing;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.alg.color.ColorHsv;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.Planar;
import java.awt.image.BufferedImage;

public class Convolution_couleur {

  public static void meanFilter(Planar<GrayU8> input, Planar<GrayU8> output, int size) {
    for (int band=0; band<input.getNumBands(); band++){
      for (int i=0; i<input.width; ++i){
        for (int j=0;j<input.height; ++j){
          int moyenne=0;
          int cmp=0;
          for (int x=i-(size/2); x <= i+(size/2); ++x){
            for (int y=j-(size/2); y <= j+(size/2); ++y){
              if(x>=0 && y>=0 && x<input.width && y<input.height){
                moyenne += input.getBand(band).get(x,y);
                cmp++;
              }
            }
          }
            
          if (cmp!=0) {
            moyenne=moyenne/cmp;
            output.getBand(band).set(i,j,moyenne);
          }
        }
      }
    }
  }

  public static void grayFilter(Planar<GrayU8> input, Planar<GrayU8> output) {
    for (int i=0; i<input.width; ++i){
      for (int j=0;j<input.height; ++j){
        int r=input.getBand(0).get(i,j);
        int g=input.getBand(1).get(i,j);
        int b=input.getBand(2).get(i,j);
        int gris=(int) (0.3*r + 0.59*g + 0.11*b);
        output.getBand(0).set(i,j,gris);
        output.getBand(1).set(i,j,gris); 
        output.getBand(2).set(i,j,gris);
      }
    }
  }

  public static void colorFilter(Planar<GrayU8> input, Planar<GrayU8> output, float h) {
    for (int i=0; i<input.width; ++i){
      for (int j=0;j<input.height; ++j){
        float r=input.getBand(0).get(i,j);
        float g=input.getBand(1).get(i,j);
        float b=input.getBand(2).get(i,j);

        float[] hsv=new float[3];
        ColorHsv.rgbToHsv(r,g,b,hsv);
        float[] rgb=new float[3];
        //ColorHsv.hsvToRgb(hsv[0],0,hsv[2],rgb);
        ColorHsv.hsvToRgb((h*((float)Math.PI/180f)),hsv[1],hsv[2],rgb);

        output.getBand(0).set(i,j,(int)rgb[0]);
        output.getBand(1).set(i,j,(int)rgb[1]); 
        output.getBand(2).set(i,j,(int)rgb[2]);
      }
    }
  }

  public static void histo(Planar<GrayU8> input, Planar<GrayU8> output){
    int[] tab=new int[360];
    for (int i=0; i<input.width; ++i){
      for (int j=0;j<input.height; ++j){
        float r=input.getBand(0).get(i,j);
        float g=input.getBand(1).get(i,j);
        float b=input.getBand(2).get(i,j);
        float[] hsv=new float[3];
        ColorHsv.rgbToHsv(r,g,b,hsv);
        tab[(int) (hsv[0]*(180f/(float) Math.PI))] ++;
      }
    }
    int max = 0;
    for(int i=0; i<tab.length; i++){
      if(tab[i]>max){
        max=tab[i];
      }
    }
    for(int i=0; i<tab.length; i++){
      tab[i]=tab[i]*(output.height-1)/max;
    }
    for(int i=0; i<360;i++){
      while (tab[i]!=0) {
        output.getBand(0).set(i,(output.height-1)-tab[i],255);
        output.getBand(1).set(i,(output.height-1)-tab[i],255); 
        output.getBand(2).set(i,(output.height-1)-tab[i],255);    
        tab[i]--;  
      }
    }
  }


  public static void main(final String[] args) {
    // load image
    if (args.length < 2) {
      System.out.println("missing input or output image filename");
      System.exit(-1);
    }
    final String inputPath = args[0];
    BufferedImage input = UtilImageIO.loadImage(inputPath);
    Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);

    //Planar<GrayU8> image_out = new Planar<>(GrayU8.class, image.width, image.height, image.getNumBands());
    Planar<GrayU8> image_out = new Planar<>(GrayU8.class,360,201,3);

    histo(image,image_out);
    //colorFilter(image,image_out,270);
    //grayFilter(image,image_out);
    //meanFilter(image,image_out,11);

    final String outputPath = args[1];
    UtilImageIO.saveImage(image_out, outputPath);
    System.out.println("Image saved in: " + outputPath);


/* // Test des fonction rgbToHsv et hsvToRgb //

    float r=150/255.0f;
    float g=150/255.0f;
    float b=150/255.0f;
    float[] hsv=new float[3];
    ColorHsv.rgbToHsv(r,g,b,hsv);
    System.out.println("H = "+hsv[0]+ " S = "+hsv[1]+" V = "+hsv[2]);

    System.out.println();
    
    float h=300.0f;
    float s=1.0f;
    float v=1.0f;
    float[] rgb=new float[3];
    ColorHsv.hsvToRgb(h,s,v,rgb);
    System.out.println("R = "+(rgb[0]*255)+" G = "+(rgb[1]*255)+" B = "+(rgb[2]*255));*/
  }
}
