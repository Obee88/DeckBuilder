package suport;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import custom.classes.CardInfo;
import custom.classes.ShowingCard;

public class Printer {
	private static Printer instance;

	private Printer() throws Exception {
	}
	
	public static Printer getInstance() throws Exception {
		if(instance==null)
			instance = new Printer();
		return instance;
	}
	
	public Slika generatePrintingPage(List<ShowingCard> list) throws Exception {
		List<Slika> prednje = new ArrayList<Slika>();
		List<Slika> straznje = new ArrayList<Slika>();
		for(int i=0; i<8; i++){
			try{
				ShowingCard sc = list.get(i);
				prednje.add(getSlikaFromUrl(sc.cardInfo.getLinkForDownloading(), sc.name));
			}catch(IndexOutOfBoundsException ex){
				prednje.add(getBlank());
			}
		}
		return spoji8prednjih(prednje.toArray(new Slika[0]));
	}

    public Slika generatePrintingPageFromInfos(List<CardInfo> list) throws Exception {
        List<Slika> prednje = new ArrayList<Slika>();
        List<Slika> straznje = new ArrayList<Slika>();
        for(int i=0; i<8; i++){
            try{
                CardInfo sc = list.get(i);
                prednje.add(getSlikaFromUrl(sc.getLinkForDownloading(), sc.name));
            }catch(IndexOutOfBoundsException ex){
                prednje.add(getBlank());
            }
        }
        return spoji8prednjih(prednje.toArray(new Slika[0]));
    }
	
	private Slika getBlank() {
		Slika s = new Slika(new BufferedImage(1, 1, 1));
		s.resize(312, 445, false);
		return s;
	}

	public static Slika spoji8prednjih(Slika[] s) {
        int w=240,h =332, sp =1;
        s=smanji(s);
        w=s[0].getWidth();
        h=s[0].getHeight();
        BufferedImage bi = new BufferedImage((int)(1.05*(5*sp+4*w)),(int)(1.05*(3*sp+2*h)),BufferedImage.SCALE_SMOOTH);
        Slika ukupna = new Slika(bi);
        ukupna.drawFilledRectangle(0, 0, ukupna.getWidth(), ukupna.getHeight(), Color.WHITE);
        ukupna.insert(s[0],sp,sp);
        ukupna.insert(s[1],2*sp+1*w,sp);
        ukupna.insert(s[2],3*sp+2*w,sp);
        ukupna.insert(s[3],4*sp+3*w,sp);
        ukupna.insert(s[4],sp,2*sp+h);
        ukupna.insert(s[5],2*sp+1*w,2*sp+h);
        ukupna.insert(s[6],3*sp+2*w,2*sp+h);
        ukupna.insert(s[7],4*sp+3*w,2*sp+h);
        BufferedImage ret  = new BufferedImage(ukupna.getWidth()+40, ukupna.getHeight()+68, BufferedImage.SCALE_SMOOTH);
        Slika retS = new Slika(ret);
        retS.drawFilledRectangle(0, 0, retS.getWidth(), retS.getHeight(), Color.WHITE);
        retS.insert(ukupna, 20+(10-sp)*3/2,34);
        return retS;
    }

    private static Slika[] smanji(Slika[] s) {
    	int r = 0;
    	Slika[] slikeVece = new Slika[s.length];
    	for (int i = 0; i < slikeVece.length; i++) {
			Slika mala = s[i];
			Slika velika  = new Slika(new BufferedImage(mala.getWidth()+2*r, mala.getHeight()+2*r, Image.SCALE_SMOOTH));
			velika.drawFilledRectangle(0, 0, velika.getWidth(), velika.getHeight(), Color.GRAY);
			velika.insert(mala.toBufferedImage(), r, r);
			slikeVece[i] = velika;
		}
    	return slikeVece;
	}

	public static Slika spoji8straznjih(Slika[] s) {
        int w=240,h =332, sp =10;
        s=smanji(s);
        w=s[0].getWidth();
        h=s[0].getHeight();
        BufferedImage bi = new BufferedImage(5*sp+4*w,3*sp+2*h,BufferedImage.SCALE_SMOOTH);
        Slika ukupna = new Slika(bi);
        ukupna.drawFilledRectangle(0, 0, ukupna.getWidth(), ukupna.getHeight(), Color.white);
        ukupna.insert(s[4],sp,3);
        ukupna.insert(s[5],2*sp+1*w,3);
        ukupna.insert(s[6],3*sp+2*w,3);
        ukupna.insert(s[7],4*sp+3*w,3);
        ukupna.insert(s[0],sp,2*sp+h);
        ukupna.insert(s[1],2*sp+1*w,2*sp+h);
        ukupna.insert(s[2],3*sp+2*w,2*sp+h);
        ukupna.insert(s[3],4*sp+3*w,2*sp+h);
        BufferedImage ret  = new BufferedImage(ukupna.getWidth()+40, ukupna.getHeight()+68, BufferedImage.SCALE_SMOOTH);
        Slika retS = new Slika(ret);
        retS.drawFilledRectangle(0, 0, retS.getWidth(), retS.getHeight(), Color.WHITE);
        retS.insert(ukupna, 20, 31);
        return retS;
    }
    
    private static Slika getSlikaFromUrl(String imageUrl, String name) throws Exception {
        BufferedImage bi = null;
        try {
            URL url = new URL(imageUrl);
            bi = ImageIO.read(url);
        } catch(Exception e){
            throw new Exception(imageUrl +" ne valja!");
        }
        Slika ret = new Slika(bi);
        ret.setTitle(name);
        return ret;
    }
    
    public static String flipLink(String downloadLink) {
        String[] parts = downloadLink.split("/");
        int len = parts.length;
        if(parts[len-1].contains("a"))
        	parts[len-1] = parts[len-1].replace("a","b");
        else if(parts[len-1].contains("b"))
        	parts[len-1] = parts[len-1].replace("b","a");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if(i==0)
                sb.append(parts[i]);
            else
                sb.append("/").append(parts[i]);
        }
        return sb.toString();
    }
}
