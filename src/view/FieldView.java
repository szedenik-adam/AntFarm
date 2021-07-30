package view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import field.Field;

public class FieldView extends ViewBase {


	//private static BufferedImage grassImg = ViewBase.resize(ViewBase.loadImg("src\\images\\grassfield.png"),(int)(ViewBase.size*2.5),(int)(ViewBase.size*2.5));
	private static BufferedImage grassImg = ViewBase.loadImg("grassfield.png");


	public FieldView(Field f)
	{
		super(f);
	}

	public void Render(Graphics2D g)
	{
		if(!(field.getAnts().size()==0 && lastrender==0))
		{
		//kirajzoljuk a mezĹ‘k hĂˇtterĂ©t attĂłl fĂĽggĹ‘en, hogy hĹ‘tĂ©rkĂ©p ĂĽzemmĂłdban vagyunk-e vagy sem
		if (hoterkep) {
			int odor=field.getOdors().getFoodOdor(field.parentMap.getRound());
			g.setColor(ViewBase.intToColor(odor-30, 20));
			g.fillPolygon(this.shape);
		}
		else {
			g.setClip(this.shape);
			g.drawImage(grassImg, middle.x-20, middle.y-20, null);
			g.setClip(null);
		}
		if(field.getAnts().size()==0) lastrender++; else lastrender=-2;
		}

		//A View rarajzolja magat a grafikara
		super.Render(g);

	}



}
