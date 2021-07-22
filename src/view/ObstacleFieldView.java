package view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import field.ObstacleField;
import field.ObstacleFieldType;

public class ObstacleFieldView extends ViewBase {

	private static BufferedImage rockImg = ViewBase.loadImg("src\\images\\rock.png");
	private static BufferedImage waterImg = ViewBase.loadImg("src\\images\\water2.jpg");
	
	public ObstacleFieldView(ObstacleField _f)
	{
		super(_f);
	}
	
	@Override
	public void Render(Graphics2D g) 
	{
		if(!(field.getAnts().size()==0 && lastrender==0))
		{
		if (((ObstacleField)field).getKind() == ObstacleFieldType.rock) {
			g.setClip(this.shape);
			g.drawImage(rockImg, middle.x-20, middle.y-20, null);
			g.setClip(null);
		} else {
			g.setClip(this.shape);
			g.drawImage(waterImg, middle.x-20, middle.y-20, null);
			g.setClip(null);
		}
		}
		if(field.getAnts().size()==0) lastrender++; else lastrender=-2;
		
		super.Render(g);	
	}
	
}
