package suport;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;



public class Slika {
	private BufferedImage picture;
	private int width, height;
	private String title= null;
	
	
	public Slika(BufferedImage picture) {
		super();
		this.setPicture(picture);
	}

	public Slika(String path) {
		this.loadFromFile(new File(path));
	}

	public Slika(File znakFile) {
		this.loadFromFile(znakFile);
	}

	public RGB getRGB(int x, int y){
		return new RGB(picture.getRGB(x, y));
	}
	/**
	 * resize image to the desired dimension
	 * @param scaledWidth
	 * @param scaledHeight
	 * @param preserveAlpha
	 * @return new image in desired dimensions
	 */
	public Slika resize(int scaledWidth, int scaledHeight,boolean preserveAlpha ) 
	{
		System.out.println("resizing "+this.title+"...");
		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
		Graphics2D g = scaledBI.createGraphics();
		if (preserveAlpha) {
			g.setComposite(AlphaComposite.Src);
		}
		g.drawImage(toBufferedImage(), 0, 0, scaledWidth, scaledHeight, null); 
		g.dispose();
		return new Slika(scaledBI);
	}
	
	/**
	 * draw line on the image. 
	 * @param x1 - starting x cordinate
	 * @param y1 - starting y cordinate
	 * @param x2 - ending x cordinate
	 * @param y2 - ending y cordinate
	 */
	public void drawLine(int x1, int y1, int x2, int y2, Color c){
		Graphics2D g= (Graphics2D) picture.createGraphics();
		g.setColor(c);
		g.drawLine(x1, y1, x2, y2);
		
	}
	
	/**
	 * draw rectangle
	 * @param x
	 * @param y
	 * @param w - width
	 * @param h - height
	 * @param c - color // use Color.constant
	 */
	public void drawRectangle(int x, int y, int w, int h,Color c){
		Graphics2D g= (Graphics2D) picture.createGraphics();
		g.setColor(c);
		g.drawRect(x, y, w, h);
	}
	
	/**
	 * draw elipse
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param c
	 */
	public void  drawElipse(int x, int y, int w, int h, Color c){
		Graphics2D g= (Graphics2D) picture.createGraphics();
		g.setColor(c);
		g.drawOval(x, y, w, h);
	}
	
	public void drawFilledElipse(int x, int y, int w, int h, Color c){
		Graphics2D g= (Graphics2D) picture.createGraphics();
		g.setColor(c);
		g.fillOval(x, y, w, h);
	}
	
	public void drawFilledRectangle(int x, int y, int w, int h, Color c){
		Graphics2D g= (Graphics2D) picture.createGraphics();
		g.setColor(c);
		g.fillRect(x, y, w, h);
	}
	
	public void drawFilledRoundRectangle(int x, int y, int w, int h,int arcWidth, int arcHeight, Color c){
		Graphics2D g= (Graphics2D) picture.createGraphics();
		g.setColor(c);
		g.fillRoundRect(x, y, h, h, arcWidth, arcHeight);
	}
	
	public void drawRoundRectangle(int x, int y, int w, int h,int arcWidth, int arcHeight, Color c){
		Graphics2D g= (Graphics2D) picture.createGraphics();
		g.setColor(c);
		g.drawRoundRect(x, y, h, h, arcWidth, arcHeight);
	}
	
	public Graphics2D getGraphics(){
		return picture.createGraphics();
	}
	
	/**
	 * insert img into the picture locating it at position (i,j)
	 * @param img
	 * @param i
	 * @param j
	 */
	public boolean insert(BufferedImage img, int i, int j){
		for(int y=0;y<img.getHeight();y++){
			for(int x=0;x<img.getWidth();x++){
				try {
					picture.setRGB(x+i, y+j, img.getRGB(x, y));
				} catch (Exception e) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * returns rectangle shaped subimage 
	 * @param x - x cordinate of top left corner
	 * @param y - y cordinate of top left corner
	 * @param w - width of rectangle
	 * @param h - height of rectangle
	 * @return
	 */
	public Slika getSubImage(int x, int y, int w, int h){
		return new Slika(picture.getSubimage(x, y, w, h));
	}
	
	/**
	 * insert img into the picture locating it at position (i,j)
	 * @param img
	 * @param i
	 * @param j
	 */
	public void insert(Slika img, int i, int j){
		this.insert(img.toBufferedImage(), i, j);
	}
	
	/**
	 * trim picture to minimum size by removing empty spaces
	 * @return
	 */
	public BufferedImage trim(){
		int height = picture.getHeight();
	    int width = picture.getWidth();
	    int pozadina=picture.getRGB(width -1,height-1);
		int xmax=0,xmin=width,ymax=0,ymin=height;
		for(int i=0;i<height;i++){
        	for(int j=0;j<width;j++){
        		int boja =picture.getRGB(j, i);
        		if(boja!=pozadina){
        			if(j<xmin) xmin=j;
        			if(j>xmax) xmax=j;
        			if(i<ymin) ymin=i;
        			if(i>ymax) ymax=i;
        		}
        	}
        }
		int w=xmax-xmin;
		int h=ymax-ymin;
		return picture.getSubimage(xmin, ymin, w, h);
	}
	
	/**
	 * saving in png format
	 * @param outputfile
	 * @return
	 */
	public boolean saveToFile(File outputfile){
        try {
			ImageIO.write(picture, "png", outputfile);
		} catch (IOException e) {
			return false;
		}
        return true;
	}
	
	/**
	 * saving in choosen format
	 * @param outputfile
	 * @return
	 */
	public boolean saveToFile(File outputfile, String format){
        try {
			ImageIO.write(picture, format, outputfile);
		} catch (IOException e) {
			return false;
		}
        return true;
	}
	
	/**
	 * saving in png format
	 * @param
	 * @return
	 */
	public boolean saveToFile(String filePath){
       return saveToFile(new File(filePath));
	}
	
	/**
	 * saving in chosen format
	 * @param
	 * @return
	 */
	public boolean saveToFile(String filePath, String format){
       return saveToFile(new File(filePath,format));
	}
	
	public boolean loadFromFile(File f){
		try {
		   this.setPicture(ImageIO.read(f));
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public boolean loadFromFile(String path){
		return loadFromFile(new File(path));
	}
	
	public BufferedImage toBufferedImage() {
		return picture;
	}

	public void setPicture(BufferedImage picture) {
		this.picture = picture;
		width=picture.getWidth();
		height=picture.getHeight();
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void removeTitle(){
		title=null;
	}
	
	public boolean hasTitle(){
		return title!=null;
	}

	public void setPixel(int x, int y, Color b) {
		picture.setRGB(x, y, b.getRGB());
	}

	public Slika flipHorizontal() {
		int w = getWidth();
		int h = getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.SCALE_SMOOTH);
		for(int j=0;j<h;j++)
			for(int i = 0; i<w;i++){
				int ii = w-1-i;
				bi.setRGB(ii, j, this.picture.getRGB(i, j));
			}
		return new Slika(bi);
	}
	
	public Slika flipVertical() {
		int w = getWidth();
		int h = getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.SCALE_SMOOTH);
		for(int j=0;j<w;j++)
			for(int i = 0; i<h;i++){
				int ii = h-1-i;
				bi.setRGB(j, ii, this.picture.getRGB(j,i));
			}
		return new Slika(bi);
	}
}
