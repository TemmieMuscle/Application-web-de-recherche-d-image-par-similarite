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


	public static void LuminosityChange(GrayU8 input, int t) {
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				if (255-gl > t) {
					gl += t;
				}else{
					gl = 255;
				}
				if(gl<0){
					gl=0;
				}
				input.set(x, y, gl);
			}
		}
	}

	public static void ContrasteChange(GrayU8 input) {
		int min=255;
		int max=0;
		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				if (gl<min){
					min =gl;
				}
				if (gl>max){
					max =gl;
				}
			}
		}

		for (int y = 0; y < input.height; ++y) {
			for (int x = 0; x < input.width; ++x) {
				int gl = input.get(x, y);
				gl = (255*(gl-min))/(max-min);
				input.set(x, y, gl);
			}
		}
	}

	public static void ContrasteChangeLUT(GrayU8 input) {
		int[] tab = new int[256];
		int min=255;
		int max=0;
		for (int y=0; y<input.height; ++y) {
			for (int x=0; x<input.width; ++x) {
				int gl = input.get(x,y);
				if (gl<min){
					min=gl;
				}
				if (gl>max){
					max=gl;
				}
			}
		}

		for (int i=0; i<255; i++){
			tab[i]=(255*(i-min))/(max-min);
		}
		
		for (int y=0; y<input.height; ++y) {
			for (int x=0; x<input.width; ++x) {
				int gl=input.get(x,y);
				input.set(x,y,tab[gl]);
			}
		}
	}

	public static void ContrasteEgaliseur(GrayU8 input) {
		int[] h=new int[256];
		int[] C =new int[256];
		for (int y=0; y<input.height; ++y) {
			for (int x=0; x<input.width; ++x) {
				int gl=input.get(x,y);
				h[gl]++;
			}
		}
		int temp=0;
		for(int i=0; i<256;i++){
			temp+=h[i];
			C[i]=temp;
		}
		for (int y= 0; y<input.height; ++y) {
			for (int x =0; x<input.width; ++x) {
				int gl=input.get(x,y);
				gl=(C[gl]*255)/(input.width*input.height);
				input.set(x,y,gl);
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
	
		GrayU8 input = UtilImageIO.loadImage(inputPath, GrayU8.class);
		if(input == null) {
			System.err.println("Cannot read input file '" + inputPath);
			System.exit(-1);
		}

		// processing
		
		ContrasteEgaliseur(input);
		//ContrasteChangeLUT(input);
		//ContrasteChange(input);
		//LuminosityChange(input, -50);
        //threshold(input, 128);
		
		// save output image
		final String outputPath = args[1];
		UtilImageIO.saveImage(input, outputPath);
		System.out.println("Image saved in: " + outputPath);
	}

}