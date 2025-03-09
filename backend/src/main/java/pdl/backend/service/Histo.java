package pdl.backend.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import pdl.backend.repository.ImageRepository;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.alg.color.ColorHsv;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.Planar;
import java.awt.image.BufferedImage;



public class Histo {

    
    
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

}
