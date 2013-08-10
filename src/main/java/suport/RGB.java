package suport;

import java.awt.Color;

public class RGB {
	public int red;
	public int green;
	public int blue;
	
	public RGB(int argb) {
		int[] rgb = getRGB(argb) ;
		red=rgb[0];
		green=rgb[1];
		blue=rgb[2];
	}
	
	public boolean hasIntensity(int R,int G, int B){
		return red>R && blue>B && green>G;
	}
	
	public String toString(){
		return "["+red+", "+green+", "+blue+"]";
	}
	
	private static int[] getRGB(int argb) {

		int rgb[] = new int[] {
		    (argb >> 16) & 0xff, //red
		    (argb >>  8) & 0xff, //green
		    (argb      ) & 0xff  //blue
		};
		return rgb;
	}
	
	public boolean isEqualToColor(Color c){
		return c.getBlue()==blue && c.getRed()==red && c.getGreen()==green;
	}
}
