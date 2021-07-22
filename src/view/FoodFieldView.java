package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import field.FoodField;

public class FoodFieldView extends ViewBase {

	private static BufferedImage foodImg = ViewBase.loadImg("src\\images\\cheese.png");
	
	public FoodFieldView(FoodField _field)
	{
		super(_field);
	}
	
	@Override
	public void Render(Graphics2D g)
	{
		if(!(field.getAnts().size()==0 && lastrender==0))
		{
		g.setClip(this.shape);
		g.drawImage(foodImg, middle.x-20, middle.y-20, null);
		g.setClip(null);
		
		String remainingFood = String.valueOf(((FoodField)field).getFoodCount());
		g.setColor(Color.black);
		g.drawString(remainingFood, middle.x-5, middle.y+5);
		}
		if(field.getAnts().size()==0) lastrender++; else lastrender=-2;
		
		super.Render(g);
	}
	
}
