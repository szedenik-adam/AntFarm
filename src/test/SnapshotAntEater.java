package test;

import java.io.Serializable;

import org.w3c.dom.Element;

import proto.Rand;

import skeleton.FieldCoordinate;
import map.Map;
import ant.AntEater;

public class SnapshotAntEater implements Serializable {
	private int posX, posY;
	private int[] route;
	
	public SnapshotAntEater(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	public SnapshotAntEater(Element eElement) {
		posX = Integer.valueOf(eElement.getElementsByTagName("posX").item(0).getTextContent());			//X pozicio beolvasasa az element szovegebol
		posY = Integer.valueOf(eElement.getElementsByTagName("posY").item(0).getTextContent());			//Y pozicio beolvasasa az element szovegebol
		if (eElement.getElementsByTagName("route").item(0).getTextContent() != "") {
			String[] inRoute = eElement.getElementsByTagName("route").item(0).getTextContent().split(" ");
			route = new int[inRoute.length];
			for (int i=0; i<inRoute.length; i++)
				route[i] = Integer.valueOf(inRoute[i]);
		}
		else route = new int[0];
	}

	public void createAntEater(Map map, boolean random) {
		AntEater ae = random ? new AntEater(map) : new AntEater(map, new Rand.NotRand(route));
		ae.setPosition(map.getFieldByCoordinate(new FieldCoordinate(posX, posY)));
		map.setAntEater(ae);
	}
	

	public String print() {
		return "AntEater @ " + posX + "," + posY;
	}

	@Override
	public boolean equals(Object _other) {
		if (_other.getClass() != SnapshotAntEater.class) return false;
		SnapshotAntEater other = (SnapshotAntEater)_other;
		return posX==other.posX && posY==other.posY;
	}
}
