package field;

import view.FoodFieldView;
import view.ObstacleFieldView;
import view.ViewBase;
import map.Map;
import ant.*;

/**
* A jÄ‚Ë‡tÄ‚Â©ktÄ‚Â©ren megjelenÄąâ€ akadÄ‚Ë‡lyok osztÄ‚Ë‡lya
*/
public class ObstacleField extends Field {
	
	/**
	 * ObstacleField kontruktora
	*/
	public ObstacleField(Map parent) {
		super(parent);

		okind = ObstacleFieldType.rock;
		
		this.getOdors().setOdorBlock(true);
	}
	
	/**
	 *ObstacleField konstructora tÄ‚Â­pus beÄ‚Ë‡llÄ‚Â­tÄ‚Ë‡ssal
	 *
	*/
	public ObstacleField(Map parent, ObstacleFieldType _type) {
		
		this(parent); //1-gyel feljebb lĂ©vĹ‘ konstruktor hĂ­vĂˇsa (kĂłdduplikĂˇlĂˇs ellen)
		okind = _type;
	}

	private ObstacleFieldType okind;	//AkadÄ‚Ë‡ly tÄ‚Â­pusÄ‚Ë‡t tÄ‚Ë‡rolÄ‚Ĺ‚ vÄ‚Ë‡ltozÄ‚Ĺ‚

	/**
	 *Vissza adja, hogy erre a mezÄąâ€re nem lehet rÄ‚Ë‡ lÄ‚Â©pni
	 *@return boolean
	*/
	public boolean canEnter() {
		return false;
	}
	
	@Override
	public boolean leaveField(Ant aAnt) {
		// TODO Auto-generated method stub
		return true;
	}

	public void entryEvent(Ant aAnt) {
		
	}

	/**
	 *Vissza adja az akadÄ‚Ë‡ly tÄ‚Â­pusÄ‚Ë‡t
	 *@return okind
	*/
	public ObstacleFieldType getKind() {
		return this.okind;
	}

	/**
	 *BeÄ‚Ë‡llÄ‚Â­tja az akadÄ‚Ë‡ly tÄ‚Â­pusÄ‚Ë‡t
	 *@param aType
	*/
	public void setKind(ObstacleFieldType aType) {
		this.okind = aType;
	}
	
	public char getCharCode() {
		return 'o'; //
	}
	
	public FieldType getFieldType()
	{
		return FieldType.ObstacleField;
	}
	
	
	
	@Override
	public ViewBase getView() {
		// TODO Auto-generated method stub
		if (stateChanged || (view == null) )
		{
			view = new ObstacleFieldView(this);
		}
		return view;
	}
}
