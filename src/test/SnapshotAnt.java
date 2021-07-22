package test;

import java.io.Serializable;

import org.w3c.dom.Element;

import field.Field;

import proto.Rand;

import skeleton.FieldCoordinate;
import map.Map;
import ant.Ant;

public class SnapshotAnt implements Serializable {
	private int posX, posY;
	private int[] route;
	private boolean hasFood;
	
	public SnapshotAnt(int posX, int posY, boolean hasFood) {
		this.posX = posX;
		this.posY = posY;
		this.hasFood = hasFood;
	}
	
	public SnapshotAnt(Element eElement) {
		posX = Integer.valueOf(eElement.getElementsByTagName("posX").item(0).getTextContent());			//X pozicio beolvasasa az element szovegebol
		posY = Integer.valueOf(eElement.getElementsByTagName("posY").item(0).getTextContent());			//Y pozicio beolvasasa az element szovegebol
		hasFood = eElement.getElementsByTagName("hasFood").item(0).getTextContent().equals("true");		//Y pozicio beolvasasa az element szovegebol
		if (eElement.getElementsByTagName("route").item(0).getTextContent() != "") {
			String[] inRoute = eElement.getElementsByTagName("route").item(0).getTextContent().split(" ");
			route = new int[inRoute.length];
			for (int i=0; i<inRoute.length; i++)
				route[i] = Integer.valueOf(inRoute[i]);
		}
		else route = new int[0];
	}

	public void createAnt(Map map, boolean random) {
		Field curField = map.getFieldByCoordinate(new FieldCoordinate(posX, posY));
		Ant ant = random ? new Ant() : new Ant(new Rand.NotRand(route));
		ant.setPos(curField);
		curField.addAnt(ant);
		map.registerAsFieldWithAnt(curField);
	}
	
	public String print() {
		return "Ant @ " + posX + "," + posY + " hasFood=" + hasFood;
	}
	
	@Override
	public boolean equals(Object _other) {
		if (_other.getClass() != SnapshotAnt.class) return false;
		SnapshotAnt other = (SnapshotAnt)_other;
		return posX==other.posX && posY==other.posY && hasFood==other.hasFood;
	}
}
