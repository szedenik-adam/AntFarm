package view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import field.AntNestField;

public class AntNestFieldView extends ViewBase {

	private static BufferedImage antNestImg = ViewBase.loadImg("antnest.png");

	public AntNestFieldView(AntNestField _f)
	{
		super(_f);
	}

	@Override
	public void Render(Graphics2D g) {
		if(!(field.getAnts().size()==0 && lastrender==0))
		{
		g.setClip(this.shape);
		g.drawImage(antNestImg, middle.x-23, middle.y-20, null);
		g.setClip(null);
		}
		if(field.getAnts().size()==0) lastrender++; else lastrender=-2;

		super.Render(g);
	}

}
