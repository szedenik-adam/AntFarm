package view;

import field.Field;
import graphics.RenderEngine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import ant.Ant;

public class ViewBase {

	int y, x; //Elem poziciója a map-on
	public Point middle;//Elem helyének közepe //Ideiglenesen publikus
	public Polygon shape;//A mező alakja
	private Point ptop, pbottom, pleft1, pleft2, pright1, pright2;
	protected boolean hoterkep = false; //értéke igaz, ha hőtérképes kirajzolást szeretnénk a mezőn
	protected int lastrender=-2;//Utolsó kirajzolásról információ
	protected boolean no_odor_draw=false;
	public static final int size = RenderEngine.size; //Elem mérete (egyik oldalának a hossza)
	
	static final Color field_selected=new Color(255,255,255,255);
	static final Color field_normal=new Color(50,150,50,255);
	
	Field field = null;
	Random rand = null;
	
	public ViewBase()
	{
		
	}
	
	public ViewBase(Field f)
	{
		this.field = f;
		this.y = f.getCoordinate().getMapX();
		this.x = f.getCoordinate().getMapY();
		rand = new Random();
		
		//Pontok beállítása
		//middle=new Point(40+(int)(x*Math.cos(30*Math.PI/180)*2*size)+(int)(((y&1)==1)?(Math.cos(30*Math.PI/180)*size):(0)), 50+(int)(y*size*1.5));
		middle = RenderEngine.mapCoordsToPixelCoords(x, y);
		ptop=new Point(middle.x,middle.y-size);
	    pbottom=new Point(middle.x,middle.y+size);
	    pleft1=new Point(middle.x-(int)(Math.cos(30*Math.PI/180)*size),middle.y-(int)(Math.sin(30*Math.PI/180)*size));
	    pleft2=new Point(middle.x-(int)(Math.cos(30*Math.PI/180)*size),middle.y+(int)(Math.sin(30*Math.PI/180)*size));
	    pright1=new Point(middle.x+(int)(Math.cos(30*Math.PI/180)*size),middle.y-(int)(Math.sin(30*Math.PI/180)*size));
	    pright2=new Point(middle.x+(int)(Math.cos(30*Math.PI/180)*size),middle.y+(int)(Math.sin(30*Math.PI/180)*size));
	
	    shape=new Polygon(new int[]{ptop.x,pright1.x,pright2.x,pbottom.x,pleft2.x,pleft1.x},new int[]{ptop.y,pright1.y,pright2.y,pbottom.y,pleft2.y,pleft1.y},6);
	}
	
	public void Render(Graphics2D g)
	{		
		//Körvonal kirajzolása
		if (field.debugClicked) 
		{
			field.debugClicked = false;
			g.setColor(field_selected); //Fehér
			lastrender=-2;
		}else g.setColor(field_normal);
		/*if(lastrender<1)*/ g.drawPolygon(this.shape);

	    //hangyák kirajzolása:
	    if (field.getAnts().size() > 0)
		{
	    	for (Ant a: field.getAnts()) {
	    		int shiftX = a.hashCode()%10-10;
	    		int shiftY = a.hashCode()%12-10;
				g.setColor(a.getHasFood() ? Color.yellow : Color.DARK_GRAY);
				g.fillOval(middle.x+shiftX, middle.y+shiftY, 10, 10);
				g.setColor(Color.black);
				g.drawOval(middle.x+shiftX, middle.y+shiftY, 10, 10);
				lastrender=-2;
	    	}
		}

	    
	   /* for (Field f: field.getNeighbours())
	    {
	    	g.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
	    	//mindegyikbe húzunk egy vonalat
	    	
	    	if (f!=null)
	    	{
		    	ViewBase view = f.getView();
		    	if (view != null)
		    		if (view.middle != null)
		    			g.drawLine(middle.x, middle.y, view.middle.x, view.middle.y);
	    	}
	    }*/
	    //g.drawString(Integer.toString(field.getOdors().getAntNestOdor(field.parentMap.getRound())), middle.x,middle.y); //kirajzolja a mező szagát
	    //g.drawString(Integer.toString(field.getOdors().getFoodOdor(field.parentMap.getRound())), middle.x,middle.y); //kirajzolja a mező szagát
	    

	    
	    
	    //g.setColor(Color.red);
	    //g.drawString(this.field.getCoordinate().toString(), middle.x, middle.y);//kirajzolja a mező koordinátáját
		//g.setColor(new Color(0, 255, 0));
		//g.fillRect(left*width, top*height, width, height);
	    
	    
	}
	public void Invalidate(){ this.lastrender=-2; }
		
	
	//Super cool color scale by Adam from: http://stackoverflow.com/questions/2374959/algorithm-to-convert-any-positive-integer-to-an-rgb-value
	final static float Gamma = 0.80f;
	final static int IntensityMax = 255;
	public static Color intToColor(int num, int max)
	{
		Color result;
		float r=0,g=0,b=0;
		
		num=((780-380)*num)/max  +380;
		
		if(num>=380 && num<=439){
			r=-(num-440)/(float)(440-380); g=0; b=1;
		}else if(num<=489){
			r=0; g=((num-440))/(float)(490-440); b=1;
		}else if(num<=509){
			r=0;g=1;b=-(num-510)/(float)(510-490);
		}else if(num<=579){
			r=(num-510)/(float)(580-510); g=1; b=0;
		}else if(num<=644){
			r=1; g=-(num-645)/(float)(645-580); b=0;
		}else if(num<=780){
			r=1; g=0; b=0;
		}
		
		float factor=0;
		if(num<=419){
			factor=0.3f+0.7f*(num-380)/(420-380);
		}else if(num<=700){
			factor=1f;
		}else if(num<=780){
			factor=0.3f+0.7f*(780-num)/(780-700);
		}

		if(r>=0) r=(int)Math.floor(IntensityMax * Math.pow(r * factor, Gamma));
		if(g>=0) g=(int)Math.floor(IntensityMax * Math.pow(g * factor, Gamma));
		if(b>=0) b=(int)Math.floor(IntensityMax * Math.pow(b* factor, Gamma));
		
		if(r<0)r=20; if(b<0)b=20; if(g<0)g=20;
		
		result=new Color((int)r,(int)g,(int)b);

		return result;
	}
	
	public Point getMiddle()
	{
		return middle;
	}
	
	public void setHoterkep(boolean val) { hoterkep = val; }
	
	public static BufferedImage loadImg(String filePath)
	{
		Image tankimg = Toolkit.getDefaultToolkit().createImage(new File(filePath).getAbsolutePath());
		while(tankimg.getHeight(null) == -1)
			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
		
		BufferedImage tank_base = new BufferedImage(tankimg.getHeight(null), tankimg.getWidth(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D gtank_base=tank_base.createGraphics();
		gtank_base.drawImage(tankimg, 0, 0, null);
		
		return tank_base;
		/*BufferedImage result=null;
		try {
			result=ImageIO.read(new File("src\\images\\anteater.png"));
		} catch (IOException e) { e.printStackTrace(); }
		return result;*/
	}
	public static BufferedImage resize(BufferedImage source, int width, int height) {
		
        Image img2 = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);//source.getType());
        Graphics2D g = img.createGraphics();
        try {
            g.drawImage(img2, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return img;
    }

}
