package ant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import map.Map;
import proto.Rand;
import field.*;

public class Ant {
	private Field pos;
	private boolean hasFood;
	private int forceGoRandom; //Ha valahova beszorul a hangya, Ă©s a pathbĂłl tudja, hogy egy helyben toporog, akkor ennyi lĂ©pĂ©st random fog megtenni
	private int dirHoldTime,dirIndex; //IrĂˇnytartĂˇshoz
	Rand rand = null;
	int ant_id=0;
	int lastround = 0;
	private ArrayList<Field> path;
	
	static Integer[] Tdirections = {0, 1, 2, 3, 4, 5};

	public Ant()
	{
		rand = new Rand();
		ant_id = rand.nextInt(1000);
		path = new ArrayList<Field>();
		
		forceGoRandom = 0;
		dirHoldTime=dirIndex=0;
	}
	public Ant(Rand rand)
	{
		this.rand = rand;
		ant_id = new Rand().nextInt(1000);
		path = new ArrayList<Field>();
		
		forceGoRandom = 0;
		dirHoldTime=dirIndex=0;
	}
	
	/**
	 * A hangya lepeseert felelos metodus, lekedezi a jelenlegi mezo szomszedait
	 * eldonti, hogy melyik mezore lepjen, majd kilepteti a hangyat a jelenlegi
	 * mezorol es atlepteti az ujra.
	 */
	public void move(int round) {
		if (lastround != round)
		{
			lastround = round;
			if (Map.random) moveRand(round);
			else {
				if (((Rand.NotRand)rand).valuesSet())
					moveDet(round);
				else
					moveRand(round);
			}
		}
		
	}
	
	//Akkor hivodik meg ha a rendszer random allapotban van, felhasznaloi hasznalat kozben vegzi a mozgatast minden kor elejen
	private void moveRand(int round)
	{
		Field[] szomszedok = pos.getNeighbours();
//		System.out.print("Ant::move "+ant_id+" @"+pos.getCoordinate().toString());
		
		if(dirHoldTime>0){
			dirHoldTime--;
			if (szomszedok[dirIndex]!=null && szomszedok[dirIndex].canEnter()){
				pos.leaveField(this);
				szomszedok[dirIndex].EntryEvent(this);
				return;
			}else dirHoldTime=0;
		}
		
		//if (!hasFood)
		{
			//A mapban van egy kĂ©p arrĂłl, hogy az adott X szaghoz hĂˇny Y pĂˇlyaelem tartozik
			HashMap<Integer, ArrayList<Integer>> map = new HashMap<Integer,ArrayList<Integer>>(); //Szag - Index pĂˇrosok
			
			int maxIndex = -1;
			int max = -1;
			int i = -1;
			
			//Valasszuk ki a legerosebb szagot.
			for (Field f: szomszedok)
			{
				i++;
				if (f != null)
				{
					int odor=(!hasFood)?(f.getOdors().getFoodOdor(round)):(f.getOdors().getAntNestOdor(round));
					if (!map.containsKey(odor))
					{
						map.put(odor, new ArrayList<Integer>());
						map.get(odor).add(i);
					}
					else 
					{
						map.get(odor).add(i);
					}
					
					if (odor > max)
					{
						max = odor;
						maxIndex = i;
					}
				}
				
			}
			
			//A maxIndexet vĂ©letlenszerĹ±en vĂˇlasztjuk ki abbĂłl a listĂˇbĂłl, ami a mapban van. Ha tĂ¶bb azonos indexĹ± field van, akkor nagyobb a vĂˇlasztĂ©k			
			maxIndex = map.get(max).get(rand.nextInt(map.get(max).size())); //VĂˇlasztunk egy fieldet az azonos szagerĹ‘ssĂ©gĂĽekbĹ‘l
			
			if (path.contains(szomszedok[maxIndex]))
			{
				forceGoRandom = 10;
			}
			
			//Elmegyunk abba az iranyba?
			int r = (int)((max+50)); //esely, hogy elugrunk!
			//Akkor megyĂĽnk random irĂˇnyba, ha a random generĂˇtor azt mondta, vagy ha beragadtunk, Ă©s force el akarunk menni onnan ahonnan
			if ((r >= rand.nextInt(100)) && (forceGoRandom == 0) )
			{		
				//Elugrunk
				if (szomszedok[maxIndex].canEnter())
				{
//					System.out.println("target: "+szomszedok[maxIndex].getCoordinate().toString());
					pos.leaveField(this);
					szomszedok[maxIndex].EntryEvent(this);		
				}
				else
				{
					forceGoRandom=1;//Nem Marad helyben
				}
			}
			if (forceGoRandom > 0)  // random mĂłd:
			{
				forceGoRandom--;
				
				//KĂĽlĂ¶nben merre halad?	Jobbra probal, aztan ha nem, akkor felfele. ha az sem, akkor lefele, majd balra.
				List<Integer> directions = new ArrayList<Integer>(Arrays.asList(Tdirections));
				Collections.shuffle(directions);//Random irĂˇny
				boolean sikerult = false;
				
				i = 0;
				while ( ( !sikerult) && (i<directions.size()) )
				{
					Field target = szomszedok[directions.get(i)];
					if (target != null  && target.canEnter())
					{
//						System.out.println("target: "+target.getCoordinate().toString());
						pos.leaveField(this);
						target.EntryEvent(this);
													
						sikerult = true;
						
						dirHoldTime=rand.nextInt(5);
						dirIndex=directions.get(i);
					}
					i++; //KĂ¶vetkezĹ‘ irĂˇny!
				}

				
			}
		}

		path.add(this.getPos()); //ElraktĂˇrozzuk, hogy erre mentĂĽnk.
	}
	
	//teszteloi modban, ha a rendszer determinisztikus allapotban van, hivodik meg
	private void moveDet(int round)
	{
//		System.out.println("Ant::moveDet");
		Field next = pos.getNeighbours()[rand.nextInt(1) % pos.getNeighbours().length];
		for (int i=0; i<4 && next==null; i++)
			next = pos.getNeighbours()[i];
		if (next==null) return;
		if (next.canEnter() && pos.leaveField(this))
			next.EntryEvent(this);
	}

	/**
	 * Beallitja azt a mezot, amin a hangya jelenleg all, vagyis a jelenlegi poziciojat.
	 * @param aField
	 */
	public void setField(Field aField) {
//		System.out.println("Ant::SetField ["+ant_id+"]");
		pos = aField;
	}

	/**
	 * Visszaadja, hogy a hangya eppen rendelkezik-e elelemmel, vagy sem.
	 * @return boolean
	 */
	public boolean getHasFood() {
		return this.hasFood;
	}

	/**
	 * Beallitja a hangya hasFood valtozojat. Akkor hivjuk meg, ha a hangya elelmet vett fel.
	 * @param aHasFood
	 */
	public void setHasFood(boolean aHasFood) {
		if(this.hasFood!= aHasFood){
			path.clear(); //ElfelejtjĂĽk merre mentĂĽnk
			this.hasFood = aHasFood;
		}
	}

	/**
	 * Beallitja azt a mezot, amin a hangya jelenleg all, vagyis a jelenlegi poziciojat.
	 * @param aPos
	 */
	public void setPos(Field aPos) {
//		System.out.println("Ant::SetPos ["+ant_id+"] "+aPos.getCoordinate().toString());
		this.pos = aPos;
	}

	/**
	 * Visszaadja azt a mezot, amin jelenleg a hangya all, vagyis a jelenlegi poziciojat.
	 * @return Field
	 */
	public Field getPos() {
		return this.pos;
	}
}
