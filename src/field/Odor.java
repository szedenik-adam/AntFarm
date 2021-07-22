package field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Odor {
	private int antOdor;
	private int foodOdor;
	private int antNestOdor;
	
	private Map<Field,Integer>[] fieldOdors;
	
	private boolean isOdorNeutralizerActive = false;
	private int odorNeutralizerEndRound = 0;
	
	private int forcedAntOdor = -1;
	
	private ArrayList<Integer> antArrivals;
	
	public void forceAntOdor(int _odor)//TesztelĂ©shez
	{
		forcedAntOdor = _odor;
	}
	
	/**
	 *Odor konstruktora, parameterek beallitasaval
	*///amint kesz a vegleges ez torlesre fog kerulni
	public Odor(int a, int f, int n)
	{		
		antOdor = a;
		foodOdor = f;
		antNestOdor = n;
		
		fieldOdors =new Map[3];
		for(int i=0;i<3;i++) fieldOdors[i] = new HashMap<Field, Integer>();
		antArrivals =new ArrayList<Integer>();
	}

	public Odor()
	{
		fieldOdors =new HashMap[3];
		for(int i=0;i<3;i++) fieldOdors[i] = new HashMap<Field, Integer>();
		antArrivals =new ArrayList<Integer>();
	}

	/*
	 * Kaja vagy Hangyaboly mezĹ‘ szagĂˇt Ă©rvĂ©nyesĂ­ti a tĂ¶bbi mezĹ‘n.
	 * ĂdĂˇm engedĂ©lye nĂ©lkĂĽl a metĂłdust piszkĂˇlni tilos.
	 */
	public static void UpdateFieldOdors(Field origin,int power)
	{
//		System.out.println("Field odor update started.");
		int mapindex;  //Tudom hogy class-t lekerdezni nem szep, de a masik megoldas az az lenne, hogy a fv
			 if(origin.getClass()==FoodField.class)    mapindex=1; //parameterben megkapja a tipust ami igazabol ugyanez.
		else if(origin.getClass()==AntNestField.class) mapindex=2; //Vagy kulon fv-ben a kaja es a hangyaboly szagszamolasa
		else throw new UnsupportedOperationException("Nem jo fieldre lett meghivva!");//ami meg kodduplikalas :(
		
		power++;

		List<Field> completed=new ArrayList<Field>();
		List<Field> neighbors =new ArrayList<Field>();  neighbors.add(origin);
		
		while(--power > 0){ //  <-szĂ©lessĂ©gi bejĂˇrĂˇs
			
			//a szomszĂ©dok szomszĂ©djai (kĂ¶vetkezĹ‘ interĂˇciĂłban Ĺ‘k lesznek a szomszĂ©dok)
			List<Field> neighborsneighbors=new ArrayList<Field>();
			
			for(Field neighbor: neighbors) {
				
				//Ăśres szomszedokat figyelmen kivul hagyja
				if(neighbor==null) continue;
				
				//Folosleges hivasok ellen: kesz lista karbantartasa
				if(completed.contains(neighbor)) continue;
				completed.add(neighbor);
				
				//Objektumok es adatok kiolvasasa
				Odor neighborOdor = neighbor.getOdors();
				Map<Field,Integer> odorFieldMap = neighborOdor.fieldOdors[mapindex];
				
				//Referencia frissitese
				odorFieldMap.put(origin, power);

				//Ăšj szagerosseg beallitasa
				/*Integer oldvalue = odorFieldMap.get(origin); //osszegzo szagbeallitas
				if(oldvalue==null) oldvalue=0;
				int val= power-oldvalue;*/
				int val= Collections.max( odorFieldMap.values() );//maximumkivalaszto szagbeallitas
				if(mapindex==2) neighborOdor.antNestOdor=val;
						   else neighborOdor.foodOdor   =val;
				
				//szomszed szomszed bovitese
				neighborsneighbors.addAll(Arrays.asList(neighbor.getNeighbours()));
			}
			//Ătvaltas a szomszedok szomszedjaira
			neighbors = neighborsneighbors;
		}
		
		if(mapindex==1) {
//		  System.out.println("Food odors updated: "+origin.getCoordinate().toString());
//		  map.Map.map.PrintFoodOdorMap(System.out);
		}else{
//			System.out.println("AntNest odors updated: "+origin.getCoordinate().toString());
//			map.Map.map.PrintAntNestOdorMap(System.out);
		}
	}
	public static void RemoveFieldsOdor(Field old)
	{
		System.out.println("Field odor remove started.");
		int mapindex, power;
			 if(old.getClass()==FoodField.class)    mapindex=1;
		else if(old.getClass()==AntNestField.class) mapindex=2;
		else throw new UnsupportedOperationException("Nem jo fieldre lett meghivva!");
			 
		if(mapindex==2) power = old.getOdors().antNestOdor;
				   else power = old.getOdors().foodOdor;
			 
		List<Field> completed=new ArrayList<Field>();
		List<Field> neighbors=new ArrayList<Field>();  neighbors.add(old);
		
		while(--power > 0){ //  <-szĂ©lessĂ©gi bejĂˇrĂˇs
			
			//a szomszĂ©dok szomszĂ©djai (kĂ¶vetkezĹ‘ interĂˇciĂłban Ĺ‘k lesznek a szomszĂ©dok)
			List<Field> neighborsneighbors=new ArrayList<Field>();
			
			for(Field neighbor: neighbors) {
				
				//Ăśres szomszedokat figyelmen kivul hagyja
				if(neighbor==null) continue;
				
				//Folosleges hivasok ellen: kesz lista karbantartasa
				if(completed.contains(neighbor)) continue;
				completed.add(neighbor);
				
				//Objektumok es adatok kiolvasasa
				Odor neighborOdor = neighbor.getOdors();
				Map<Field,Integer> odorFieldMap = neighborOdor.fieldOdors[mapindex];
				
				
				if(odorFieldMap.containsKey(old))
				{
					//BejegyzĂ©s tĂ¶rlĂ©se
					odorFieldMap.remove(old);
					
					//Ăšj szagerosseg beallitasa
					int val=0;
					if(odorFieldMap.size()>0) val=Collections.max( odorFieldMap.values() );//maximumkivĂˇlasztĂł szagbeĂˇllĂ­tĂˇs
					else{
						val=0;
					}
					if(mapindex==2) neighborOdor.antNestOdor=val;
							   else neighborOdor.foodOdor   =val;
				}

				//szomszĂ©d szomszĂ©d bĹ‘vĂ­tĂ©se
				neighborsneighbors.addAll(Arrays.asList(neighbor.getNeighbours()));
			}
			//ĂtvĂˇltĂˇs a szomszĂ©dok szomszĂ©djaira
			neighbors = neighborsneighbors;
		}
	}
	
	/**
	 *Hangya szag erosseget allitja
	 *@param aInt_odor
	*///torlesre fog kerulni
	/*public void setAntOdor(int aInt_odor) {
		antOdor = aInt_odor;
	}*/
	public void addAntArrival(int round){
		antArrivals.add(round);
	}

	
	/**
	 *Az etel szag erosseget allitja
	 *@param aInt_odor
	*///torlesre fog kerulni
	public void setFoodOdor(int aInt_odor) {
		foodOdor = aInt_odor;
	}

	
	/**
	 *Hanygaboly szaganak erosseget allito fuggveny
	 *@param aInt_odor
	*///torlesre fog kerulni
	public void setAntNestOdor(int aInt_odor) {
		antNestOdor = aInt_odor;
	}

	
	/**
	 *Vissza adja a hangya szag erosseget
	 *@return antOdor
	*/
	public int getAntOdor(int round) {
		//Ha a szagsemegesito hatas engedelyezve van: 0 minden szag.
		if(isOdorNeutralizerActive) {
			// Kikapcsolja a szagsemlegesito hatast ha letelt az ideje.
			if(odorNeutralizerEndRound<=round) {isOdorNeutralizerActive = false; return this.calcAntOdor(round);}
			return 0;
		}
		return this.calcAntOdor(round);
		
	}
	/**
	 * KiszĂˇmolja a hangyaszag erĹ‘ssĂ©gĂ©t
	 * @author Ădam
	 */
	private int antOdorLastCalculation=-1;
	private int calcAntOdor(int round){
		
		if (forcedAntOdor != -1) return forcedAntOdor;    //TesztelĂ©shez hasznĂˇlt statikus Ă©rtĂ©k
		
		if(antOdorLastCalculation==round) return antOdor; //Az aktuĂˇlis kĂ¶r cache-elt Ă©rtĂ©ke
		   antOdorLastCalculation= round;
		   antOdor=0;
		for(int i=0;i<antArrivals.size();i++)			  //KiszĂˇmolja a kĂ¶r alapjĂˇn a szagerĹ‘ssĂ©get
		{
			if(antArrivals.get(i) < round-5) {antArrivals.remove(i); i--;}
			else antOdor+= antArrivals.get(i)+5-round;
		}
		if(antOdor>50) return 50;
		               return antOdor;
	}

	
	/**
	 *Vissza adja az elelem szaganak erosseget
	 *@return foodOdor
	*/
	public int getFoodOdor(int round) {
		if(isOdorNeutralizerActive) {
			if(odorNeutralizerEndRound<=round) {isOdorNeutralizerActive = false; return this.foodOdor;}
			return 0;
		}
		if(isOdorBlocked) return 0;
		return this.foodOdor;
	}

	
	/**
	 *Vissza adja a hanygaboly szaganak erosseget
	 *@return antNestOdor
	*/
	public int getAntNestOdor(int round) {
		if(isOdorNeutralizerActive) {
			if(odorNeutralizerEndRound<=round) {isOdorNeutralizerActive = false; return this.antNestOdor;}
			return 0;
		}
		if(isOdorBlocked) return 0;
		return this.antNestOdor;
	}
	
	public boolean isOdorNeutralized(){ return isOdorNeutralizerActive;	}
	
	/** SemlegesĂ­ti a mezĹ‘ szagnyomĂˇt!
	 * @author Ădam
	 */
	public void clear(int endround) {
		isOdorNeutralizerActive = true;
		odorNeutralizerEndRound = endround;
	}

	boolean isOdorBlocked=false;
	public void setOdorBlock(boolean b) {//AkadĂˇly mezĹ‘knĂ©l a szagnyomok kikapcsolĂˇsĂˇhoz hasznĂˇlt fĂĽggvĂ©ny
		isOdorBlocked=b;
	}
}
