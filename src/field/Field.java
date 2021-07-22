package field;

import java.util.ArrayList;


import skeleton.FieldCoordinate;
import view.FieldView;
import view.ViewBase;
import ant.*;
import map.*;

public class Field {
	protected ArrayList<Ant> ants;
	protected Odor odors;
	protected Field[] neighbours;
	protected int lastround;
	public Map parentMap;
	public boolean debugClicked = false;
	
	/*Mivel a skeleton egy koznolos alkalmazĂˇs, 
	 * melyben nincs lehetĹ‘sĂ©g arra, hogy az egyes 
	 * pĂˇlyaelemeket kirajzoljuk, ezĂ©rt a Skeleton idejĂ©re, 
	 * egy kĂĽlĂ¶n nĂ©vtĂ©rbe, lĂ©trehoztunk egy osztĂˇlyt, mely
	 * segĂ­tsĂ©gĂ©vel kĂ¶zvetlenĂĽl azonosĂ­thatĂłak Ă©s lehĂ­vhatĂłak
	 * az egyes mezĹ‘k. Ezen osztĂˇly segĂ­tsĂ©gĂ©vel lehetĹ‘sĂ©gĂĽnk
	 * nyĂ­lik, hogy tesztelhessĂĽk, Ă©s szimulĂˇlhassuk az alkalmazĂˇs
	 * olyan rĂ©szeit is, melyeket a kĂ¶rnyezet miatt nem tudnĂˇnk.
	 * */
	private FieldCoordinate coordinate = new FieldCoordinate(-1, -1);
	public void setCoordinate(FieldCoordinate _c) { coordinate = _c; }
	public FieldCoordinate getCoordinate() { return coordinate; } 
	
	/*EgĂ©szen eddig tart a Skeleton specifikus kivezetĂ©s*/
	
	/**
	 * Map konstruktora
	*/
	public Field(Map _parent)
	{
		neighbours = new Field[6]; //HatszĂ¶gletĹ± pĂˇlya
		parentMap = _parent;
		ants = new ArrayList<Ant>();
		odors=new Odor();
	}
	
	/**
	 *Vissza adja a mezĹ‘n levĹ‘ hangyĂˇkat
	 *@return ants
	*/
	public ArrayList<Ant> getAnts(){
		return ants;
	}

	/**
	 *Vissza adja, hogy rĂˇ lehet lĂ©pni erre a mezĹ‘re
	 * @param animalType 
	 *@return boolean
	*/
	public boolean canEnter() {
		return true;
	}
	
	public void nextRound(int round)
	{
		if (lastround != round)
		{
			for (int i = 0; i <ants.size();i++)
			{
//				System.out.println("Ant::Move invoked for " + ants.get(i).hashCode());
				ants.get(i).move(round);
			}
			lastround = round;
		}
	}

	/**
	 *MezĹ‘re tĂ¶rtĂ©nĹ‘ rĂˇlĂ©pĂ©skor lefutĂł fĂĽggvĂ©ny
	 *HozzĂˇ adja a hangyĂˇt a mezĹ‘n tartĂłzkodo hangyĂˇk listĂˇjĂˇhoz
	 *@param aAnt
	*/
	public void EntryEvent(Ant aAnt) {
		ants.add(aAnt);
		aAnt.setPos(this);
//		System.out.println("Field::EntryEvent");//println
		
		//Ha az elsĹ‘ hangya jĂ¶tt, szĂłlunk a mapnak, hogy vannak rajtunk
		
		parentMap.registerAsFieldWithAnt(this);
		
//mĂ„â€šĂ‹â€ˇrtonhangyaszaga		this.odors.setAntOdor(50); //Hangya lĂ„â€šĂ‚Â©pett a mezĂ„Ä…Ă˘â‚¬Âre, 50 lesz a szag
		this.odors.addAntArrival(parentMap.getRound());

	}

	/**
	 *Mikor egy hangya elhagyja a mezĹ‘t tĂ¶rli az adott mezĹ‘ listĂˇjĂˇbol
	 *@return ants
	*/
	public boolean leaveField(Ant aAnt) {
//		System.out.print("Field::LeaveField");
		boolean willBeRemoved = ants.remove(aAnt);
		
		if (willBeRemoved)
		{
			if (ants.size()==0)
			{
				//SzĂłl a mapnek hogy rajta mĂˇr nincs hangya
				parentMap.unregisterAsFieldWithAnt(this);
//mĂ„â€šĂ‹â€ˇrtonhangyaszaga				this.odors.setAntOdor(this.odors.getAntOdor(lastround)-1); //Hangya ellĂ„â€šĂ‚Â©p rĂ„â€šÄąâ€šla, gyengĂ„â€šĂ„Ëťl a szag
			}
		}
		
		
		//

		return willBeRemoved;
	}

	/**
	 *Vissza adja a szomszĂ©dos mezĹ‘k tĂ¶mbjĂ©t
	 *@return neighbours
	*/
	public Field[] getNeighbours() {
		//System.out.println("Field::getNeighbours");//tĂşl sok hĂ­vĂˇs miatt kikommentezve
		return neighbours;
	}

	
	/**
	 *LetĂ¶rli a mezĹ‘n levĹ‘ hanygĂˇkat
	*/
	public int removeAnts() {
//		System.out.println("Field::removeAnts");
		int count = ants.size();
		ants.clear();
		parentMap.unregisterAsFieldWithAnt(this);
		return count;
	}
	
	/**SemlegesĂ­ti a mezĹ‘n lĂ©vĹ‘ szagnyomot!
	 * @author Bence */
	public void removeOdor(int endround) {
//		System.out.println("Field::removeOdor");
		odors.clear(endround);
	}
	
	
	/**
	 *Ha van a mezĹ‘n hanyga letĂ¶rĂ¶l egyet
	*/
	public void removeAnt()
	{
//		System.out.println("Field::removeAnt");
		if (ants.size()>0)
			ants.remove(0);
	}
	
	
	/**
	 *ManuĂˇlisan hozzĂˇadunk a pĂˇlyĂˇhoz egy hangyĂˇt
	 *
	*/
	public void addAnt(Ant _ant)
	{
		ants.add(_ant);
//		System.out.println("Field::AddAnt");
	}

	/**
	 *Vissza adja a mezĹ‘n talĂˇlhatĂł szagokat
	 *@return odors
	*/
	public Odor getOdors() {
		return this.odors;
	}

	
	/**
	 *BeĂˇllĂ­tja a mezĹ‘ szagĂˇt
	 *@param aodor
	*/
	public void setOdors(Odor aOdor) {
		this.odors = aOdor;
//		System.out.println("Field::setOdors");
	}
	
	/**
	 *ĂšjraszĂˇmolja a szagok erĹ‘ssĂ©gĂ©t a szomszĂ©dok alapjĂˇn.
	 *(SpeciĂˇlis mezĹ‘nĂ©l ezt a fĂĽggvĂ©nyt felĂĽl kell definiĂˇlni! [pl.: kaja])
	 *@param round
	*/
	public void recalculateOdors(int round) {
		
		int food_max = 0, antn_max = 0;
		for (Field f : this.getNeighbours())
		{
			if (f != null){
				if(f.getOdors().getFoodOdor(round)    > food_max) food_max = f.getOdors().getFoodOdor(round);
				if(f.getOdors().getAntNestOdor(round) > antn_max) antn_max = f.getOdors().getAntNestOdor(round);
			}
		}
		if(antn_max<1)antn_max=1;// hogy -1-et ne tudjon
		if(food_max<1)food_max=1;// beĂˇllĂ­tani
		this.odors.setAntNestOdor(antn_max-1); //A maximumok 1-gyel
		this.odors.setFoodOdor(food_max-1);    //csĂ¶kkentett Ă©rtĂ©kĂ©t beĂˇllĂ­tjuk
	}

	
	/**
	 *BeĂˇllĂ­tja a mezĹ‘ szamszĂ©dait
	 *@param aNeighBours
	*/
	public void setNeighbours(Field[] aNeighbours) {
		neighbours = aNeighbours;
		//System.out.println("Field::setNeighbours");//tĂşl sok hĂ­vĂˇs miatt kikommentezve
	}
	
	public void nop(){}
	
	public char getCharCode() {
		
		if(ants.size()>0) return 'h';//Ant on the field
		else return ' '; //Empty Field char
	}
	
	public FieldType getFieldType()
	{
		return FieldType.Field;
	}
	
	boolean stateChanged = false;
	
	ViewBase view = null;
	public ViewBase getView()
	{
		if (stateChanged || (view == null))
		{
			view = new FieldView(this);
		}
		return view;
	}
		
	
}
