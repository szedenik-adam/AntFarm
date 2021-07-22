package skeleton;

public class FieldCoordinate {
	private int mapX;	//Pozicio X koordinataja
	private int mapY;	//Pozicio Y koordinataja
	
	
	/**
	 *Vissza adja az X koordinatat
	 *@return mapX
	*/
	public int getMapX() { return mapX; }
	
	
	/**
	 *Vissza adja az Y koordinatat
	 *@return mapY
	*/
	public int getMapY() { return mapY; } 

	
	/**
	 *Beallitja a pozicio X koordinatajat
	 *@param _x
	*/
	public void setMapX(int _x) { mapX = _x; }

	
	/**
	 *Beallitja a pozicio Y koordinatajat
	 *@param _y
	*/
	public void setMapY(int _y) { mapY = _y; }
	
	
	/**
	 *FieldCoordinate konstruktor, parameterek beallitasaval
	*/
	public FieldCoordinate(int i, int j)
	{
		mapX = i;
		mapY = j;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format(mapX+","+mapY);
	}
}
