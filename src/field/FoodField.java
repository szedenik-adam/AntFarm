package field;

import view.FoodFieldView;
import view.ViewBase;
import map.Map;
import ant.*;

public class FoodField extends Field {
	
	public final static int foodpertile=10;
	
	/**
	 *FoodField konstruktora
	*/
	public FoodField(Map _parent) {
		super(_parent);
	}
	
	/**
	 *Konstruktor, paramĂ©terek beĂˇllĂ­tĂˇsĂˇval
	*/
	public FoodField (Map _parent, int _foodCount, int _foodOriginal)
	{
		super(_parent);
		
		foodCount = _foodCount;
		foodOriginal = _foodOriginal;
	}

	private int foodCount;
	private int foodOriginal;

	/**
	 *Vissza adja, hogy be lehet-e lĂ©pni a mezĹ‘re
	 *
	*/
	public boolean canEnter() {
		return true;
	}

	/**
	 *A mezĹ‘re tĂ¶rtĂ©nĹ‘ belĂ©pĂ©skor lefutĂł fĂĽggvĂ©ny
	*/
	public void EntryEvent(Ant aAnt) {
		super.EntryEvent(aAnt); 
		
		if (this.foodCount > 0)
		{
//			System.out.println("Ant " + aAnt.hashCode() + " has eaten a bizbasz.");
			foodCount--;		
			aAnt.setHasFood(true);
		}
		else
		{
			Odor.RemoveFieldsOdor(this);
			parentMap.ConvertField(this, FieldType.Field);
		}
		
	}
	
	/**
	 *Vissza adja a mezĹ‘n talĂˇlhatĹ‘ Ă©lelem mennyisĂ©gĂ©t
	 *@return foodCount
	*/
	public int getFoodCount() {
		return this.foodCount;
	}

	/**
	 *Vissza adja, hogy eredetileg mennyi Ă©lelem volt az adott mezĹ‘n
	 *@return foodOriginal
	*/
	public int getFoodOriginal() {
		return this.foodOriginal;
	}

	/**
	 *BeĂˇllĂ­tja az Ă©lelem kezdeti Ă©rtĂ©kĂ©t a parmĂ©terben kapott Ă©rtĂ©kre
	 *@param aFoodOriginal
	*/
	public void setFoodOriginal(int aFoodOriginal) {
		this.foodOriginal = aFoodOriginal;
	}

	/**
	 *BeĂˇllĂ­tja az aktuĂˇlis Ă©lelem mennyisĂ©get a mezĹ‘n
	 *@param aFoodCount
	*/
	public void setFoodCount(int aFoodCount) {
		this.foodCount = aFoodCount;
	}
	
	public char getCharCode() {
		return 'F'; //Ä‚ËÄŹĹĽËťĂ‚Â­ Ä‚ËÄŹĹĽËťĂ‚Â¬Ä‚ËÄŹĹĽËťĂ‚Â«  Ä‚ËÄŹĹĽËťÄąÄľ Ä‚ËĂ˘â‚¬ĹˇĂ‚Â¬
	}
	

	@Override
	public FieldType getFieldType()
	{
		return FieldType.FoodField;
	}
	
	FoodFieldView view;
	public ViewBase getView()
	{
		if (stateChanged || (view == null) )
		{
			view = new FoodFieldView(this);
		}
		return view;
	}
}
