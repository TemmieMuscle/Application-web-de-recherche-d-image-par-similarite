package imageprocessing;

import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.Planar;
import java.awt.image.BufferedImage;

public class GrayLevelProcessing_couleur {

    public static void LuminosityChange(Planar<GrayU8> input, int t) {
        for(int band=0; band<input.getNumBands(); band++){
            for(int x=0; x<input.getBand(band).width; ++x){
                for(int y=0; y<input.getBand(band).height; ++y){                
                    int value=input.getBand(band).get(x,y);
                    if(255-value>t){
                        value+=t;
                    }else{
                        value =255;
                    }
                    if(value<0){
                        value = 0;
                    }
                    input.getBand(band).set(x,y,value);
                }
            }
        }
    }

    public static void main( String[] args ) {

    	// load image
		if (args.length < 2) {
			System.out.println("missing input or output image filename");
			System.exit(-1);
		}

		final String inputPath = args[0];
		BufferedImage input = UtilImageIO.loadImage(inputPath);
		Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);

		LuminosityChange(image,50);
		
		final String outputPath = args[1];
		BufferedImage output = ConvertBufferedImage.convertTo(image, null, true);
		UtilImageIO.saveImage(output, outputPath);
		System.out.println("Image saved in: " + outputPath);
	}

}