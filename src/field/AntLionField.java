package field;

import view.AntLionFieldView;
import view.FoodFieldView;
import view.ViewBase;
import map.Map;
import ant.*;

public class AntLionField extends Field {
	public AntLionField(Map _parent) {
		super(_parent);
		// TODO Auto-generated constructor stub
	}

	private int antsKilled;

	/**
	*  Vissza adja, hogy be lehet lĂ„â€šĂ‚Â©pni erre a mezĂ„Ä…Ă˘â‚¬Âre
	*  @return boolean
	*/
	public boolean canEnter() {
		return true;
	}

	/**
	*  A belĂ„â€šĂ‚Â©pĂ„â€šĂ‚Â©skor lejĂ„â€šĂ‹â€ˇtszĂ„â€šÄąâ€šdĂ„â€šÄąâ€š esemĂ„â€šĂ‚Â©nyek, feljegyzi a belĂ„â€šĂ‚Â©pĂ„Ä…Ă˘â‚¬Â hangyĂ„â€šĂ‹â€ˇt
	*  @param aAnt
	*/
	public void entryEvent(Ant aAnt) {
//		System.out.print("AntLionField::EntryField");
		
		aAnt.setPos(this);
	}

	
	/**
	*  A hangyalesĂ„Ä…Ă˘â‚¬ÂrĂ„Ä…Ă˘â‚¬Âl valĂ„â€šÄąâ€š lelĂ„â€šĂ‚Â©pĂ„â€šĂ‚Â©skor meghĂ„â€šĂ‚Â­vĂ„â€šÄąâ€šdĂ„â€šÄąâ€š fĂ„â€šĂ„ËťggvĂ„â€šĂ‚Â©ny
	*  ElpusztĂ„â€šĂ‚Â­tja a rajta tartĂ„â€šÄąâ€šzkodĂ„â€šÄąâ€š hangyĂ„â€šĂ‹â€ˇt
	*  @param aAnt
	*  @return ants
	*/
	public boolean leaveField(Ant aAnt) {
//		System.out.print("AntLionField::LeaveField");

		return false;
	}

	
	/**
	*  A fĂ„â€šĂ„ËťggvĂ„â€šĂ‚Â©ny kiĂ„â€šĂ‚Â­rja az eddig elfogyasztott hangyĂ„â€šĂ‹â€ˇk szĂ„â€šĂ‹â€ˇmĂ„â€šĂ‹â€ˇt
	*  @return antsKilled
	*/
	public int getAntsKilled() {
		return this.antsKilled;
	}
	
	public char getCharCode() {
		return 'X'; // Ä‚ËÄŹĹĽËťĂ˘â‚¬Ĺˇ Ä‚ËÄŹĹĽËťĂ˘â‚¬Â¦ Ä‚ËÄąâ€şĂ˘â‚¬â€ť
	}
	
	@Override
	public FieldType getFieldType()
	{
		return FieldType.AntLionField;
	}
	
	AntLionFieldView view;
	public ViewBase getView()
	{
		if (stateChanged || (view == null) )
		{
			view = new AntLionFieldView(this);
		}
		return view;
	}
}
