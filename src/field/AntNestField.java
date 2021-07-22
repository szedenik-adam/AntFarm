package field;

import view.AntNestFieldView;
import view.ViewBase;
import map.Map;
import ant.*;

public class AntNestField extends Field {
	
	private int round = 0;
	
	/**
	* LÄ‚Â©trehoz egy hangyabolyt   
	*/
	public AntNestField(Map _parent) {
		super(_parent);
		antReleaseInterval = 5; //HÄ‚Ë‡ny kÄ‚Â¶rÄ‚Â¶nkÄ‚Â©nt van Release. Ez a vÄ‚Â©gÄ‚Â©n mÄ‚Ë‡sodperc lesz.
		//nextAntReleaseTime = 0;
		foodCollected = 0;
	}

	public AntNestField(Map _parent, int _antReleaseInterval) {
		super(_parent);		
		antReleaseInterval = _antReleaseInterval;
	}

	private int antReleaseInterval;		//uj hangya kibocsÄ‚Ë‡ltÄ‚Ë‡sÄ‚Ë‡nak idÄąâ€kÄ‚Â¶ze
	//private int nextAntReleaseTime;		//kÄ‚Â¶v hangya kijÄ‚Â¶vetelÄ‚Â©nek ideje
	private int foodCollected;		//felhalmozott Ä‚Â©lelem szÄ‚Ë‡ma
	
	/*HÄ‚Ë‡ny kÄ‚Â¶r mÄ‚Ĺźlva indÄ‚Â­tunk Ä‚Ĺźj hangyÄ‚Ë‡t?
	 * */
	public int getNextAntReleaseTime()
	{
		return round % antReleaseInterval;
	}

	public void Update() {
		throw new UnsupportedOperationException();
	}

	public void AntNest(int aAntinterval) {
		throw new UnsupportedOperationException();
	}

	
	/**
	*  BeÄ‚Ë‡llÄ‚Â­tja a kibocsÄ‚Ë‡ltÄ‚Ë‡s idÄąâ€kÄ‚Â¶zÄ‚Â©t
	*/
	public void setAntReleaseInterval(int aInterval) {
		this.antReleaseInterval = aInterval;
	}
	
	/*MeghÄ‚Â­vÄ‚Ĺ‚dik akÄ‚Ë‡rhÄ‚Ë‡nyszor kÄ‚Â¶r vÄ‚Ë‡ltozÄ‚Ë‡s van.
	 * */
	/*private void roundChanged()
	{
		if ( (round % antReleaseInterval) == 0)
		{
			//Ä‚Ĺˇj hangyÄ‚Ë‡t kell indÄ‚Â­tanunk
			EntryEvent(new Ant());
		}
	}*/

	/**SzÄ‚Ĺ‚l az AntNest-nek, hogy Ä‚Ĺźj kÄ‚Â¶r van
	 * Ez egy mÄ‚Ë‡sodperc alatt akÄ‚Ë‡r tÄ‚Â¶bbszÄ‚Â¶r is elÄąâ€fordulhat. Nagyban fÄ‚Ä˝gg a jÄ‚Ë‡tÄ‚Â©k Timer-Ä‚Â©tÄąâ€l.
	 * 
	 */
	public void nextRound(int round)
	{
		if (round != this.round)
		{
			super.nextRound(round);
			this.round=round;
			if ( (round % antReleaseInterval) == 0) {
				EntryEvent(new Ant());
//				System.out.println("Ant-Added!!");
			}
		}
	}
	
	 /*ResetelhetjÄ‚Ä˝k, Ä‚Â©s beÄ‚Ë‡llÄ‚Â­thatjuk az aktuÄ‚Ë‡lis kÄ‚Â¶rt
	  * */
	/*public void setRound(int _round)
	{
		round = _round;
		roundChanged();
	}*/
	
	public int getRound()
	{
		return round;
	}
	
	/**
	*  Visszadja, hogy a mezÄąâ€re rÄ‚Ë‡ lehet lÄ‚Â©pni
	*  @return boolean
	*/
	public boolean canEnter() {
		return true;
	}
	
	
	/**
	* A rÄ‚Ë‡lÄ‚Â©pÄ‚Â©skor lefutÄ‚Ĺ‚ esemÄ‚Â©nyek  
	*/
	public void EntryEvent(Ant aAnt) {
		super.EntryEvent(aAnt);
		//ants.add(aAnt); //SUPER
		//aAnt.setPos(this); //SUPER
//		System.out.println("AntNestField::EntryEvent");	
		
		if(aAnt.getHasFood())
		{
			aAnt.setHasFood(false);
//			System.out.println("Ant " + aAnt.hashCode() + " give us meal");
			foodCollected++;
		}
	}
	
	
	/**
	*  MezÄąâ€re valÄ‚Ĺ‚ rÄ‚Ë‡lÄ‚Â©pÄ‚Â©skor meghÄ‚Â­vÄ‚Ĺ‚dÄ‚Ĺ‚ fÄ‚Ä˝ggvÄ‚Â©ny
	*  @return ants
	*/
	@Override
	public boolean leaveField(Ant aAnt) {	
//		System.out.println("AntNestField::LeaveField");
		return super.leaveField(aAnt);
		//aAnt.setPos(this);
		//return ants.remove(aAnt);
		
	}

	
	/**
	*  Vissza adja a beÄ‚Ë‡llÄ‚Â­tott kibocsÄ‚Ë‡ltÄ‚Ë‡si idÄąâ€t
	*  @return antReleaseInterval
	*/
	public int getAntReleaseInterval() {
		return this.antReleaseInterval;
	}

	
	/**
	*  Vissza adja a beszÄ‚Ë‡llÄ‚Â­tott Ä‚Â©lelem mennyisÄ‚Â©gÄ‚Â©t
	*  @return foodCollected
	*/
	public int getFoodCollected() {
		return this.foodCollected;
	}
	
	public char getCharCode() {
		return 'A'; //Ă˘â€”Ĺ˝
	}
	

	@Override
	public FieldType getFieldType()
	{
		return FieldType.AntNestField;
	}
	
	
	AntNestFieldView view;
	public ViewBase getView()
	{
		if (stateChanged || (view == null) )
		{
			view = new AntNestFieldView(this);
		}
		return view;
	}
}
