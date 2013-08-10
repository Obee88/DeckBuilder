package blake.structures;

public class Suport {
	public static byte[] intToByteArray(int a)
	{
	    return new byte[] {
	        (byte) ((a >> 24) & 0xFF),
	        (byte) ((a >> 16) & 0xFF),   
	        (byte) ((a >> 8) & 0xFF),   
	        (byte) (a & 0xFF)
	    };
	}
	
	public static int byteArrayToInt(byte[] b) 
	{
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}

	public static String toHex(byte[] digest) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i<digest.length; i++){
			if(i%4==0 && i!=0) sb.append(" ");
			if(i%32==0 && i!=0) sb.append("\n");
			sb.append(String.format("%02x", digest[i]));
		}
		return sb.toString().toUpperCase();
	}
	
	public static byte[] intAr2byteAr(int[] l){
		byte[] ret = new byte[4*l.length];
		for(int i=0; i<l.length; i++ ){
			byte[] intBytes = int2byteAr(l[i]);
			for(int j = 0; j<4;j++){
				ret[4*i+j]=intBytes[j];
			}
		}
		return ret;
	}
	
	public static byte[] int2byteAr(int val){
		byte[] b = new byte[4];
		b[3] = (byte) (val );
        b[2] = (byte) (val >>>  8);
        b[1] = (byte) (val >>> 16);
        b[0] = (byte) (val >>> 24);
        return b;
	}
	
	public static String toHex(int[] d) {
		byte[] bytes = intAr2byteAr(d);
		return toHex(bytes);
	}
	
	public static byte[] long2byteAr(long val){
		byte[] b = new byte[8];
		b[7] = (byte) (val );
        b[6] = (byte) (val >>>  8);
        b[5] = (byte) (val >>> 16);
        b[4] = (byte) (val >>> 24);
        b[3] = (byte) (val >>> 32);
        b[2] = (byte) (val >>> 40);
        b[1] = (byte) (val >>> 48);
        b[0] = (byte) (val >>> 56);
        return b;
	}
	
}
