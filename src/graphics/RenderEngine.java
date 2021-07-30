package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;

import javax.imageio.ImageIO;

import field.Field;

import spray.SprayEnum;
import view.ViewBase;

import map.Map;

public class RenderEngine implements Runnable{ //Thread miatt, hogy példányosítható legyen

	Form form = null;
	Map gameMap = null;
	ViewBase[][] views = null;

	static BufferedImage currentImage; //Éppen megjelenitesen levo kep, Q-1.ik kor!
	static BufferedImage renderingImage; //A kep amire renderelunk, es meg nincs kesz. Q-ik kor!
	private boolean gameOverSignaled = false;
	Graphics2D[] gfxs=new Graphics2D[2];

	static BufferedImage antEater;

	public static int i = 0; int round;

	int N = 100;
	int M = 100;
	int width;
	int height;

	private boolean hoterkep = false; //kajaszag vizualizáció

	//egy elem oldalánk a hossza pixelben:
	public final static int size = 20;

	public static Point mapCoordsToPixelCoords(int x, int y)
	{
		return new Point(40+(int)(x*Math.cos(30*Math.PI/180)*2*size)+(int)(((y&1)==1)?(Math.cos(30*Math.PI/180)*size):(0)), 50+(int)(y*size*1.5));
	}
	public static Point2D.Float mapCoordsToPixelCoordsF(int x, int y)
	{
		return new Point2D.Float(40+(float)(x*Math.cos(30*Math.PI/180)*2*size)+(float)(((y&1)==1)?(Math.cos(30*Math.PI/180)*size):(0)), 50+(y*size*1.5f));
	}

	public RenderEngine(Form _form)
	{
		System.out.println("RenderEngine::ctorka");
		form = _form;
		width=(int)_form.getSize().getWidth();
		height=(int)_form.getSize().getHeight();
		N = height/(int)(20*1.5) - 2;//height / 10;
		M = (int)width/(int)(2*Math.cos(30*Math.PI/180)*20) - 2;//width / 10;

		views=new ViewBase[N][M];
		gameMap = new Map(N, M, false);
		gameMap.setViewArray(views);
		renderingImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB); //Harmadik paraméter: képformátum
		currentImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);

		gfxs[0]=renderingImage.createGraphics();
		gfxs[1]=currentImage.createGraphics();

		//antEater=ViewBase.loadImg("images\\anteater.png");
		String[] imgPaths = new String[] {"images/anteater.png", "src/images/anteater.png"};
		antEater = null;
		for(String imgPath : imgPaths)
		{
			InputStream imgStream = URLClassLoader.getSystemResourceAsStream(imgPath);
			if(imgStream == null) continue;
			try {
				antEater = ImageIO.read(imgStream);
			}
			catch (IOException e) {
				//e.printStackTrace();
			}
		}
		if (antEater == null) {System.out.println("Failed to load AntEater image!"); System.exit(-1);}
		int ae_width=(int)(ViewBase.size*Math.cos(30*Math.PI/180)*3);
		antEater = ViewBase.resize(antEater, ae_width, (int)(antEater.getHeight()*(ae_width/(double)antEater.getWidth())));

		gfxs[0].clearRect(0, 0, width, height);
		gfxs[1].clearRect(0, 0, width, height);

		System.out.println("RenderEngine::ctor-finished");
	}

	public void setHoterkep(boolean val) { hoterkep = val; }
	public boolean getHoterkep() { return hoterkep; }

	//Window hivja meg ha jeleztunk neki
	public BufferedImage getCurrentImage()
	{
		return currentImage;
	}
	public BufferedImage getNextImage()
	{
		return renderingImage;
	}


	public void RenderMap()
	{
		if(nextround)
			synchronized(gameMap){gameMap.nextRound(); nextround=false;}

		i = 0;
		//Elkerjuk a grafikajat
		synchronized (renderingImage) {	//szinkronizáció: hogy ha még kirajzolás alatt állna a renderImage(csere előtti currentImage), ne írjon rá
			Graphics2D g = gfxs[0];//(Graphics2D)renderingImage.getGraphics();
		    //g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    //g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		    //g.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g.setColor(Color.WHITE);
			g.clearRect(0,0,width,30);g.clearRect(0,height-30,width,30);//g.clearRect(0, 0, width, height);


			//Szolunk a komponenseknek, hogy tessek renderelodni erre a szarra
			//Modelleket ki fogjuk boviteni egy getView() metódussal ami visszaadja a magukból kigenerált View-t. Most irtam barnanak (20130503_2305), megvarom a valaszat.

			/*for (Field f: gameMap.getFields())
			{
				f.getView().setHoterkep(hoterkep);
				f.getView().Render(g);
				i++;
			}*/
			for(int i=0;i<N;i++)for(int j=0;j<M;j++){
				views[i][j].setHoterkep(hoterkep);
				views[i][j].Render(g);
			}
			if (gameMap.getAntEater().getPosition()!=null) {
				int x = gameMap.getAntEater().getPosition().getCoordinate().getMapX();
				int y = gameMap.getAntEater().getPosition().getCoordinate().getMapY();

				Point middleEater = mapCoordsToPixelCoords(y, x);

			    /*g.setColor(Color.white);
				g.fillOval(middleEater.x-15, middleEater.y-15, 30, 30);
				g.setColor(new Color(100, 0, 0));
				g.fillOval(middleEater.x-14, middleEater.y-14, 28, 28);*/
				g.drawImage(antEater, middleEater.x-antEater.getWidth()/2,middleEater.y-antEater.getHeight()/2,null);

				views[x][y].Invalidate(); if(y>0) views[x][y-1].Invalidate(); if(y<M-1) views[x][y+1].Invalidate();
			}

			DisplayFPS(g);

			//g.fillOval(mousepos.x-3, mousepos.y-3, 6, 6);

			//g.dispose();//ettől nem javult a memória szivárgás -.-

			//Rárakjuk a spray-k állapotát is a képre
			g.setColor(Color.white);
			g.drawString("AntKillerSpray: "+Integer.toString(gameMap.getAntKillerSpray().getCharge()), 40, form.getHeight() - 10);

			//Másik Spray
			g.drawString("OdorNeutralizerSpray: "+Integer.toString(gameMap.getOdorNeutralizerSpray().getCharge()), 150, form.getHeight() - 10);

			//Game Over felirat ha kell
			if (gameMap.isGameOver())
			{
				Font originalFont = g.getFont();
				g.setColor(Color.YELLOW);
				g.setFont(new Font("TimesRoman", Font.BOLD,(int)(form.getWidth()*0.05)));
				g.drawString("Game Over!", ((form.getWidth() / 2) - (int)(1.5*(form.getWidth()*0.05))), form.getHeight() / 2);

				if (!gameOverSignaled)
				{
					form.gameOver();
					gameOverSignaled = true;
				}

				g.setFont(originalFont);
			}else{
				round=gameMap.getRound();
			}

			//Kör-számláló:
			g.drawString("Round:"+Integer.toString(round), form.getWidth()-80, form.getHeight() - 10);

			//synchronized (currentImage) //szinkronizáció: ha a form már nem használja a currentImage-et kirajzolásra.
			{
				//Megcseréli az éppen generálás alatt álló képet a megjelenítettel.
				Object tmp = currentImage;               Object gtmp= gfxs[0];
				currentImage = renderingImage;           gfxs[0]=gfxs[1];
				renderingImage = (BufferedImage)tmp;     gfxs[1]=(Graphics2D)gtmp;
				//Itt jelezzük, hogy elkészült a kép
				currentImage.notify();    //nemtom melyiket kellene
			}
		}

	}

	int num=0;			public int form_num=0;
	long oldtime=0;		public int form_old=0;
	int fps=0;
	String fps_text="-";
	private void DisplayFPS(Graphics g) {
		g.setColor(Color.red);
		if((++num&127) == 0) {
			long time=System.currentTimeMillis();
			int delta = (int)(time-oldtime);
			fps=128*1000/delta;
			int formfps=(form_num-form_old)*1000/delta;
			form_old=form_num;
			fps_text="fps: "+Integer.toString(fps)+" form: "+Integer.toString(formfps);
			oldtime=time;
		}
		g.drawString(fps_text, 40, 20);
		//g.drawString(Integer.toString(num++), 40, 40);
	}

	//Thread szála:
	public void run()
	{
		while(!form.isClosing)
		{

			RenderMap();
			synchronized(this) {try { this.wait(); } catch (InterruptedException e) { e.printStackTrace(); }}
		}
	}

	boolean nextround=false; //A következő kör engedélyezését jelző változó
	public void nextRound() {//Thread biztos metódus a változó elérésére
		synchronized(gameMap) { nextround=true; }
	}

	public void sprayFired(SprayEnum spraytype, Point destination) {
		//A pontot le kell kepezni valahogy palyaelemre.
		//Hogy csinaljuk? Keressuk meg, melyik a hozza legkozelebbi mezo, es fujjuk arra

		Field f = getShortestFieldToCoordinate(destination);
		f.debugClicked = true;
		gameMap.fireSpray(spraytype, f);
	}

	//Visszaadja azt az elemet, ami a koordinatahoz a legkozelebb all
	public Field getShortestFieldToCoordinate(Point coordinate)
	{
		Field selected = gameMap.getFields().get(0);
		ViewBase sview = selected.getView();
		double delta = calculateDelta(coordinate, sview.getMiddle());

		for (Field f : gameMap.getFields())
		{
			ViewBase v = f.getView();
			if (calculateDelta(coordinate, v.getMiddle()) <= delta)
			{
				//Ha kozelebb van a mostani mint az eddigi legkozelebbi
				selected = f;
				delta = calculateDelta(coordinate, v.getMiddle());
			}
		}

		//return selected; //Nem jo!

		return gameMap.getFieldByCoordinate(selected.getCoordinate());
	}

	private double calculateDelta(Point p1, Point p2)
	{
		return Math.sqrt( Math.pow(p2.getX()-p1.getX(), 2) + Math.pow(p2.getY()-p1.getY(), 2) );
	}

	Point mousepos=new Point(0,0);
	public void updateMousePos(Point point) {
		mousepos.x=point.x;
		mousepos.y=point.y;
	}

	public int getSprayCharge(SprayEnum s)
	{
		if (s == SprayEnum.AntKiller)
		{
			return gameMap.getAntKillerSpray().getCharge();
		}
		else return gameMap.getOdorNeutralizerSpray().getCharge();
	}


}
