package blake.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Block512 {
	byte[] bytes;
	int INT_NUM = 16, LONG_NUM=8, BYTE_NUM=64;
	public int dataBytes;
	
	public Block512(byte[] data, int usable) {
		bytes=data;
		this.dataBytes = usable;
	}
	
	public int[] toIntArr(){
		int[] ret = new int[INT_NUM];
		for(int i = 0;i<INT_NUM;++i){
			ret[i] = Suport.byteArrayToInt(Arrays.copyOfRange(bytes, i*4, i*4+4));
		}
		return ret;
	}
	
	public static List<Block512> fromByteArray(byte[] data){
		List<Block512> ret = new ArrayList();
		int i;
		for(i =0;i+64<data.length;i+=64){
			ret.add(new Block512(Arrays.copyOfRange(data, i, i+64),64));
		}
		int usable = data.length-i;
		byte[] pd = prepareData(Arrays.copyOfRange(data, i, data.length), ret.size()*512);
		ret.add(new Block512(pd,usable));
		return ret;
	}


	private static byte[] prepareData(byte[] data, int addBytes) {
		Long len = (long) (data.length*8)+addBytes;
		List<Byte> dataList = new ArrayList<Byte>();
		for(byte d:data) dataList.add(d);
		dataList.add(new Byte((byte) 0x80));
		long c=len;
		while((c+8)%512!=440){
			dataList.add(new Byte((byte) 0x00));
			c+=8;
		}
		dataList.add(new Byte((byte) 0x01));
		byte[] lenBytes = Suport.long2byteAr(len);
		for(byte b: lenBytes) dataList.add(b);
		byte[] ret = new byte[dataList.size()];
		for(int i = 0; i<dataList.size();i++) ret[i]=dataList.get(i);
		return ret;
	}
	
}
