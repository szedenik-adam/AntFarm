package ant;



import proto.Rand;
import field.*;
import map.*;

public class AntEater {
	private Field pos;
	private int direction;
	public int anteaten;
	public Map parentMap;
	private int hunger = 10; //Ennyi hangya megevese utan maszik le a palyarol
	private int stuck = 0;
	
	Rand.NotRand nrand;
	
	/**
	 * AntEater Konstruktor. Inicializalja, hogy a hangyasz melyik mapon van,
	 * illetve a megvett hangyak szamat nullara allitja.
	 * @param _ref
	 */
	public AntEater(Map _ref)
	{
		//r = new Random(57897123);
		parentMap = _ref;
//		System.out.println("AntEater::ctor");
		anteaten = 0;
	}

	public AntEater(Map _ref, Rand.NotRand nrand) {
		parentMap = _ref;
//		System.out.println("AntEater::ctor_det");
		anteaten = 0;
		this.nrand = nrand;
	}

	/**
	 * A hangya elfogyasztasaert felelos metodus. Megoli azt a hangyat,
	 * amit a hangyasz eppen megeszik.
	 * @param aAnt
	 */
	public void eat() {
		anteaten += this.pos.removeAnts();
	}
	
	public void move(int round) {
		if (pos == null)
		{
			//Valahonnan kisetalt a palyarol. Majd a Map, ha ugy gondolja, lerakja egy uj helyre.
			return;
		}
		if (Map.random) move_(round);
		else {
			int nextDir = nrand.nextInt(6);
			if (nextDir>=0 && nextDir<6)
				this.setPosition(pos.getNeighbours()[nextDir]);
		}
		
	}

	/**
	 * A hangyasz mozgasaert es mozgasanak logikajaert felelos metodus.
	 */
	public void move_(int round) {
//		System.out.println("AntEater::Move");
		
		Field oldPos = pos;
		
		
		//Tovaball
		Field[] nfields = pos.getNeighbours();						//Szomszedok lekerdezese
		
		if (anteaten < hunger)
		{		
			Field bestfield = FieldWithMaximumAntOdor(nfields,round);	//A leghangyasabb mezot kivalasztja
			
			//Ha nincsenek hangyaszagok:
			if ((bestfield.getOdors().getAntOdor(round) == 0)  ) {
				
				//Ăšj iranyt keres ha nem tudja tartani az iranyat
				if(nfields[direction]==null || Rand.r.nextInt(5)==1) 
				{
					do{direction=Rand.r.nextInt(nfields.length-	1);}
					while(nfields[direction]==null);
					this.setPosition(nfields[direction]);
				}
				//Tartja az iranyt
				else this.setPosition(nfields[direction]);
				
			} else { //Ha van hangyaszag, belep a legerosebbe.				
				this.setPosition(bestfield);
				bestfield = FieldWithMaximumAntOdor(nfields,round);	//Azert kell ujra kivalasztani al egjobbat,
				//mert a setPosition meghivodott, es ha epp tol maga elott folyamatosan egy akadalyt,
				//akkor folyton valtoznak a referenciak, es a regi bestfieldre mutato referencia, mar nem el!
				int j=0; 
				while(nfields[j]!=bestfield) j++;				
				direction=j;
			}
		}
		else
		{					
			//Lesetalunk a palyarol.
			if(nfields[direction]==null) 
			{
				//Eltununk
				pos = null;
			}
			//Tartja az iranyt
			else this.setPosition(nfields[direction]);
		}
		
		if (oldPos == pos) stuck++;
		if (stuck > 3)
		{
			//Elkuldjuk barmerre
			direction = Rand.r.nextInt(nfields.length);
			stuck = 0;
		}
	}
	
	/*Hangyasz mozgasanak logikajat megvalosito fuggveny*/
	public Field FieldWithMaximumAntOdor(Field[] fieldlist, int round)
	{
		int max=-1; int maxi=-1;
		for(int i=0;i<fieldlist.length;i++){
			if(fieldlist[i]==null) continue;
			int odor=fieldlist[i].getOdors().getAntOdor(round);
			if(odor>max){max=odor; maxi=i;}
		}
		return fieldlist[maxi];
	}
	
	/**
	 * A hangyasz jelenlegi poziciojanak beallitasara szolgalo metodus.
	 * @param aField
	 */
	public void setPosition(Field aField) {		
//		System.out.println("AntEater::SetPosition");
		
		//Akadalyra fog lepni? azt tolni kell
		if (aField.getFieldType() == FieldType.ObstacleField && ((ObstacleField)aField).getKind()==ObstacleFieldType.rock)
		{
			Field newPos = parentMap.pushField(aField,direction);
			if (newPos != null)
			{
				pos = newPos;
			}
			else return;
		}
		else pos = aField;
		
		//Megeszi a mezon levo hangyakat
		while (pos.getAnts().size()>0){
			this.eat();//Ezen belul noveli az elfogyasztott hangyak szamat
			
			if (anteaten == hunger)
			{
				direction = Rand.r.nextInt(5); //Megevett 10 hangyat, lemegy a palyarol
			}
			
		}
	}

	/**
	 * A hangyasz jelenlegi helyzetet visszaado metodus.
	 * @return Field
	 */
	public Field getPosition() {
		return pos;
	}
	
	public void setHunger(int _hunger)
	{
		hunger = _hunger;
	}

	public void setAntsEaten(int i) {
		anteaten = i;
	}
}
