package test;

import org.w3c.dom.Element;

import field.FieldType;
import field.Odor;
import skeleton.FieldCoordinate;
import map.Map;

public class SnapshotOdor {
	private int posX, posY;
	private int power;
	
	public SnapshotOdor(int posX, int posY, int power) {
		this.posX = posX;
		this.posY = posY;
		this.power = power;
	}
	
	public SnapshotOdor(Element eElement) {
		posX = Integer.valueOf(eElement.getElementsByTagName("posX").item(0).getTextContent());			//X pozicio beolvasasa az element szovegebol
		posY = Integer.valueOf(eElement.getElementsByTagName("posY").item(0).getTextContent());			//Y pozicio beolvasasa az element szovegebol
		power = Integer.valueOf(eElement.getElementsByTagName("power").item(0).getTextContent());		//a szag erossegenek eltarolasa
	}
	
	public void createOdor(Map map) {
		Odor odor = new Odor(power, 0, 0);
		map.getFieldByCoordinate(new FieldCoordinate(posX, posY)).setOdors(odor);		
	}
	

	@Override
	public boolean equals(Object _other) {
		if (_other.getClass() != SnapshotOdor.class) return false;
		SnapshotOdor other = (SnapshotOdor)_other;
		return posX==other.posX && posY==other.posY && power==other.power;
	}

	public String print() {
		return "Odor @ " + posX + "," + posY + " power=" + power;
	}

}
