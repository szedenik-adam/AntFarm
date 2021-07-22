package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import map.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ant.Ant;

import field.AntLionField;
import field.AntNestField;
import field.Field;
import field.FoodField;
import field.ObstacleField;
import field.ObstacleFieldType;
import field.Odor;

import skeleton.FieldCoordinate;

public class Snapshot implements Serializable {
	
	//a jatekter beallitasainak parameterei:
	private int mapHeight = 5;
	private int mapWidth = 5;
	
	//a jatekter elemeinek adatait tarolo objektumok:
	private ArrayList<SnapshotField> fields = new ArrayList<SnapshotField>();
	private ArrayList<SnapshotAnt> ants = new ArrayList<SnapshotAnt>();
	private ArrayList<SnapshotOdor> odors = new ArrayList<SnapshotOdor>();
	private SnapshotAntEater antEater;
	
	public Snapshot() {}
	
	//ennek a konstruktornak a feladata egy Map objektum alapjan letrehozni egy snapshotot
	public Snapshot(Map map) {
		for (int i=0; i<map.getHeight(); i++)
			for (int k=0; k<map.getWidth(); k++) {
				Field curField = map.getFieldByCoordinate(new FieldCoordinate(i, k));
				if (curField.getClass() != Field.class)
					fields.add(new SnapshotField(i, k, curField.getFieldType()));
				for (Ant curAnt : curField.getAnts())
					ants.add(new SnapshotAnt(i, k, curAnt.getHasFood()));
				if (curField.getOdors().getAntOdor(map.getRound())!=0)
					odors.add(new SnapshotOdor(i, k, curField.getOdors().getAntOdor(0)));
			}
		
		if (map.getAntEater()!=null && map.getAntEater().getPosition()!=null) {
			System.out.println(map.getCoordinateByField(map.getAntEater().getPosition()));
			FieldCoordinate coord = map.getCoordinateByField(map.getAntEater().getPosition());
			antEater = new SnapshotAntEater(coord.getMapX(), coord.getMapY());
		}
	}
	
	//a fuggveny amely letrehozza a palyat a tarolt adatok alapjan:
	map.Map createMap(boolean random) {
		map.Map map = new map.Map(mapHeight, mapWidth, true);
		
		for (SnapshotField cur : fields)
			cur.createField(map);
		
		for (SnapshotAnt cur : ants)
			cur.createAnt(map, random);
		
		for (SnapshotOdor cur : odors)
			cur.createOdor(map);
		
		if (antEater != null)
			antEater.createAntEater(map, random);
		
		return map;
	}
	
	//egy XML fajlbol deszerializalja onmagat
	void load(File fXmlFile) throws Exception {
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
	 
		NodeList nList = doc.getElementsByTagName("field");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				//le kell kezelni az XML field elemeket
				Element eElement = (Element) nNode;
				fields.add(new SnapshotField(eElement));
			}
		}
		
		nList = doc.getElementsByTagName("ant");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				//le kell kezelni az XML ant elemeket
				Element eElement = (Element) nNode;
				ants.add(new SnapshotAnt(eElement));
			}
		}
		
		nList = doc.getElementsByTagName("odor");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				//le kell kezelni az odor elemeket
				Element eElement = (Element) nNode;
				odors.add(new SnapshotOdor(eElement));
			}
		}
		
		nList = doc.getElementsByTagName("antEater");
		if (nList.getLength()>0) {
			Node nNode = nList.item(0);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				//le kell kezelni az antEater elemet
				Element eElement = (Element) nNode;
				antEater = new SnapshotAntEater(eElement);
			}
		}
	}
	
	//szerializalja onmagat egy XML fajlba
	void save(FileOutputStream os) { 
		
	}
	
	/**igazzal ter vissza, ha a masik snapshot megegyezik vele
	*a teszteles eredmenyenek ellenorzesere hasznalhato
	*param: az also 4 bitje az opciok megletenek informaciojat hordozza:
	*--- 0-as bit: -o (nem veszi figyelembe a szagnyomokat)
	*--- 1-es bit: -a (nem veszi figyelembe a hangyak helyzetet)
	*--- 2-es bit: -f (nem veszi figyelembe a specialis mezok helyzetet es allapotat)
	*--- 3-as bit: -e (nem veszi figyelembe a hangyaszsunt)
	*/
	boolean equals(Snapshot other, int param) {
		if ((param & 1) == 0)
			if (!sameElements(odors, other.odors))
				return false;
		if ((param & 2) == 0) 
			if (!sameElements(ants, other.ants))
				return false;
		if ((param & 4) == 0) 
			if (!sameElements(fields, other.fields))
				return false;
		if ((param & 8) == 0)
			if (!antEater.equals(other.antEater))
				return false;
		return true;
	}
	
	private <T>boolean sameElements(Collection<T> a, Collection<T> b) {
		if (a.size() != b.size()) 
			return false;
		Set<T> bOut = new HashSet<T>();
		for (T ia : a) {
			boolean hasSame = false;
			for (T ib : b) {
				if (ia.equals(ib) && !bOut.contains(ib)) {
					hasSame = true;
					bOut.add(ib);
					break;
				}
			}
			if (hasSame == false)
				return false;
		}
		return true;
	}
	
	String print() {
		StringBuffer bf = new StringBuffer();
		bf.append("Map size: " + mapHeight + "x" + mapWidth + TestRun.newLine);
		
		if (antEater != null)
			bf.append(antEater.print() + TestRun.newLine);
		
		for (SnapshotAnt cur : ants)
			bf.append(cur.print() + TestRun.newLine);
		
		for (SnapshotOdor cur : odors)
			bf.append(cur.print() + TestRun.newLine);
		for (SnapshotField cur : fields)
			bf.append(cur.print() + TestRun.newLine);
		
		return bf.substring(0);
	}
	
	/**Egy mezo kicserelesere alkalmas fuggveny, szuksegunk van ra a teszteles megvalositasahoz.
	 * A hangyakat es a szagokat nem masolja at, mert nincs a tesztprogram mukodese soran erre nincs szuksegunk,
	 * mert ezeket a mezok utan tesszuk le a snapshotbol!
	 * @param map Palya
	 * @param row Mezo sorszama.
	 * @param col Mezo oszlopszama.
	 * @param newf Az uj mezo amit az adott cellaba tenni szeretnenk.
	 */
	public static void replaceField(Map map, int row, int col, Field newf) {
		
		Field oldf = map.getFieldByCoordinate(new FieldCoordinate(row, col));
		
		newf.setNeighbours(oldf.getNeighbours());
		System.out.println("Field neighbors length = " + oldf.getNeighbours().length + ", type = " + oldf.getClass());
		//newf.setOdors(oldf.getOdors());
		//newf.removeAnts();
		//for (Ant ant : oldf.getAnts())
		//	newf.addAnt(ant);
		
		for (Field cur : oldf.getNeighbours())
			if (cur!=null)
				for (int i=0; i<cur.getNeighbours().length; i++)
					if (cur.getNeighbours()[i] == oldf) {
						cur.getNeighbours()[i] = newf;
						break;
					}

		map.setFieldByCoordinate(new FieldCoordinate(row, col), newf);
	}
}










