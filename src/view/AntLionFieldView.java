package view;

import java.awt.Color;
import java.awt.Graphics2D;

import field.AntLionField;

public class AntLionFieldView  extends ViewBase {
	

	public AntLionFieldView(AntLionField _field)
	{
		super(_field);
	}
	
	
	@Override
	public void Render(Graphics2D g) {
		g.setColor(Color.RED);
		g.fillOval(middle.x-15, middle.y-15, 30, 30);
		
		super.Render(g);
	}

}
