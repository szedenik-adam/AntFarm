package test;


import java.io.Serializable;

import org.w3c.dom.Element;

import skeleton.FieldCoordinate;
import map.Map;
import field.AntLionField;
import field.AntNestField;
import field.Field;
import field.FieldType;
import field.FoodField;
import field.ObstacleField;
import field.ObstacleFieldType;

public class SnapshotField implements Serializable {
	private int posX, posY;
	private FieldType type;
	

	public SnapshotField(int posX, int posY, FieldType type) {
		this.posX = posX;
		this.posY = posY;
		this.type = type;
	}
	
	//Konstruktor, amely XML element alapjan hozza letre a SnapshotField objektumot
	public SnapshotField(Element eElement) {
		posX = Integer.valueOf(eElement.getElementsByTagName("posX").item(0).getTextContent());			//X pozicio beolvasasa az element szovegebol
		posY = Integer.valueOf(eElement.getElementsByTagName("posY").item(0).getTextContent());			//Y pozicio beolvasasa az element szovegebol
		String typeStr = eElement.getElementsByTagName("type").item(0).getTextContent();				//tipus tarolasa atmeneti sztringben
		if (typeStr.equals("FoodField")) type = FieldType.FoodField;									//typus sztring osszehasonlitasa az enum allapotaival
		else if (typeStr.equals("ObstacleField")) type = FieldType.ObstacleField;
		else if (typeStr.equals("AntLionField")) type = FieldType.AntLionField;
		else if (typeStr.equals("AntNestField")) type = FieldType.AntNestField;
	}

	public void createField(Map map) {
		Field newField = null;
		switch (type) {
			case AntLionField: newField = new AntLionField(map); break;
			case AntNestField: newField = new AntNestField(map); map.addAntNestField((AntNestField)newField); break;
			case FoodField: newField = new FoodField(map, 10, 10); break;
			case ObstacleField: newField = new ObstacleField(map, ObstacleFieldType.rock); break;
		}
		newField.setCoordinate(new FieldCoordinate(posX, posY));
		Snapshot.replaceField(map, posX, posY, newField);
	}
	

	@Override
	public boolean equals(Object _other) {
		if (_other.getClass() != SnapshotField.class) return false;
		SnapshotField other = (SnapshotField)_other;
		return posX==other.posX && posY==other.posY && type==other.type;
	}

	public String print() {
		return type + " @ " + posX + "," + posY;
	}
}
