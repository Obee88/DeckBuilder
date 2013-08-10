package suport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class IO {
	static BufferedReader fbr,cbr;
	
	public static void pl(Object o){
		System.out.println(o.toString());
	}
	
	public static void OpenFile(File f) throws IOException{
		fbr=new BufferedReader(
				new InputStreamReader(new FileInputStream(f)));
	}
	
	public static String frl() throws IOException{
		if (fbr==null) 
			throw new IOException("File not opened!");
		return fbr.readLine();
	}
	
	public static String rl() throws IOException{
		if (cbr==null) 
			cbr=new BufferedReader(new InputStreamReader(System.in));
		return cbr.readLine();
	}

	public static void OpenFile(String string) throws IOException {
		OpenFile(new File(string));
		
	}
}
