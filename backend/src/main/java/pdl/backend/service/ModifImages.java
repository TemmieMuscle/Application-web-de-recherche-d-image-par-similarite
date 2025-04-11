package pdl.backend.service;

import boofcv.struct.image.GrayU8;

import java.util.Random;

import boofcv.alg.color.ColorHsv;
import boofcv.struct.image.Planar;


public class ModifImages {

    public static void histo(Planar<GrayU8> input, int[] result){
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
            result[i]=tab[i]/max;
        }
    }

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


    public static void pixelFilter(Planar<GrayU8> input, Planar<GrayU8> output, int blockSize) {
      for (int band = 0; band < input.getNumBands(); band++) {
          for (int y = 0; y < input.height; y += blockSize) {
              for (int x = 0; x < input.width; x += blockSize) {
                  int sum = 0;
                  int count = 0;
                    for (int dy = 0; dy < blockSize; dy++) {
                      for (int dx = 0; dx < blockSize; dx++) {
                          int px = x + dx;
                          int py = y + dy;
                          if (px < input.width && py < input.height) {
                              sum += input.getBand(band).get(px, py);
                              count++;
                          }
                      }
                  }
                  int average = (count != 0) ? sum / count : 0;
                    for (int dy = 0; dy < blockSize; dy++) {
                      for (int dx = 0; dx < blockSize; dx++) {
                          int px = x + dx;
                          int py = y + dy;
                          if (px < output.width && py < output.height) {
                              output.getBand(band).set(px, py, average);
                          }
                      }
                  }
              }
          }
      }
    }
  

    public static void zoomFilter(Planar<GrayU8> input, Planar<GrayU8> output) {
    int zoomWidth = input.width / 10;
    int zoomHeight = input.height / 10;

    Random random = new Random();
    int x0 = random.nextInt(input.width - zoomWidth);
    int y0 = random.nextInt(input.height - zoomHeight);

    for (int band = 0; band < input.getNumBands(); band++) {
        for (int y = 0; y < output.height; y++) {
            for (int x = 0; x < output.width; x++) {
                int srcX = x0 + (x * zoomWidth / output.width);
                int srcY = y0 + (y * zoomHeight / output.height);
                int val = input.getBand(band).get(srcX, srcY);
                output.getBand(band).set(x, y, val);
            }
        }
    }
}


}
