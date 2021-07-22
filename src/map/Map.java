package map;

import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

import proto.Rand;

import ant.*;
import field.*;
import skeleton.FieldCoordinate;
import spray.*;
import view.ViewBase;

/**
 *A jatekter alapjat kepezo osztaly
 */
public class Map {
	
	/**
 	*A jatekteret alkot� objektumok
 	*/
	private OdorNeutralizerSpray odorNeutralizer;	//Szag semleges�to spray
	private AntKillerSpray antKiller;		//Hangya irto spray
	private AntEater antEater;			//Hangyasz sun
	private Field fields[][];			//Mezok matrixa
	private ArrayList<AntNestField> antNests;	//Hangyabolyok listaja			
	private int n;					//mtx egyik parametere
	private int m;					//mtx masik parametere
	private ArrayList<Field> fieldsWithAnts;
	private Rand r;
	private int antNestsCount = 2;
	private ViewBase[][] views = null;
	private int foodFieldCount = -1;
	private ArrayList<Field> fieldsWithFood;
	
	public static Map map;//global map (only for debug!!)
	public static boolean random = true; //globalis valtozo, erteke jelzi hogy a rendszer tartalmaz-e random elemeket vagy determinisztikusan mukodik
	public static int foodfieldtotal=4;
	
	public void registerAsFieldWithAnt(Field f)
	{
		if (!fieldsWithAnts.contains(f)) fieldsWithAnts.add(f);
	}
	
	public void unregisterAsFieldWithAnt(Field f)
	{
		if (fieldsWithAnts.contains(f)) fieldsWithAnts.remove(f);
	}
	
	
	/**
 	*A Map konstruktora, valtozok beallitasa, palya beolvasasa fajlbol
 	*
 	*/
	public Map(int _n, int _m, boolean clear)
	{		
	
		System.out.println("Map::ctor");
		n = _n;
		m = _m;
		
		fields = new Field[n][m];
		
		r = Rand.init();//123558);
		map = this;
		
		fieldsWithAnts = new ArrayList<Field>();
		
		//Spray inicializalas
		odorNeutralizer = new OdorNeutralizerSpray(5,20);	//Szagsemlegesito spray
		antKiller = new AntKillerSpray(5,20);				//Hangyairto spray
		
		//Nem ir felul mas objektumot (csak a nullokat)
		//if(clear==true) {
		//  loadMapFromFile(testfile);
		//  return;
		//}
		
		List<FoodField> foods = null;
		if (clear==false) {
			generateObstacleFields(100);//r.nextInt(n));
			foods = generateFoodFields(foodfieldtotal);
		}
		addEmptyFields();
		//load helyett vege
		for(int i=0;i<n;i++) for(int j=0;j<m;j++){
			fields[i][j].setCoordinate(new FieldCoordinate(i,j));
		}
			
		createNeighbourhoods();
		//createOdors();
		if (clear==false)
			for(FoodField food: foods) Odor.UpdateFieldOdors( food ,50);
		
		antEater = new AntEater(this);
		if (Map.random)
			antEater.setPosition(fields[0][0]);
		
		antNests = new ArrayList<AntNestField>();
		
		if (clear==false)
			for (int i = 0; i < antNestsCount; i++)
			{
				//Random koordinatakra dobunk egy AntNestField-et
				int w = r.nextInt(n);
				int h = r.nextInt(m);
				if(foods.contains(fields[w][h])){i--; continue;}//Ha kaja, akkor nem alakítja hangyabollyá
				
				fields[w][h]=ConvertField(fields[w][h],FieldType.AntNestField);
				antNests.add((AntNestField) fields[w][h]);
				Odor.UpdateFieldOdors(fields[w][h], 30);
			}	
		System.out.println("Map::ctor-finished");
		
		/*ViewBase.loadImg("src\\images\\grassfield.png");
		System.out.println("ViewBase.loadImg-OK");
		try   { BufferedImage aantEater=ImageIO.read(new File("src\\images\\anteater.png")); } 
		catch (IOException e) { e.printStackTrace(); }
		System.out.println("antEater-image-load-OK");*/
	}
	

	/**
 	*Vissza adja a palyahoz tartozo hangyasz sunt
 	*/
	public AntEater getAntEater()
	{
		return antEater;
	}
	
	public boolean isGameOver()
	{
		return (foodFieldCount <= 0);
	}
	
	/**
 	*Felterkepezi a jatekteret es beallitja az egyes mezok szomszedossagi listajat
 	*/
	private void createNeighbourhoods()
	{
		System.out.println("Map::createNeighbourhoods()");
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m ; j++)
			{
				Field[] nb = new Field[6];
				
				if(i%2 == 0)
				{
					//Páros sorokra
					if( (i-1 >= 0) && (j-1 >=0) )nb[0] = fields[i-1][j-1];
					if( (i-1 >= 0)  )            nb[1] = fields[i-1][j];
					if(i+1 < n)                  nb[3] = fields[i+1][j];
					if( (i+1 <  n) && (j-1 >=0) )nb[4] = fields[i+1][j-1];
				} else {
					//Páratlan
					if( (i-1 >= 0) )           nb[0] = fields[i-1][j];
					if( (i-1 >= 0)&& (j+1 < m))nb[1] = fields[i-1][j+1];	
					if( (i+1 < n) && (j+1 < m))nb[3] = fields[i+1][j+1];	
					if( (i+1 < n)  )           nb[4] = fields[i+1][j];
				}
				if(j+1< m)   nb[2] = fields[i][j+1];
				if(j-1>=0)   nb[5] = fields[i][j-1];
				
				fields[i][j].setNeighbours(nb);

			}
	}
	
	/**
 	*A parameterul kapott kordinatara helyez egy hangyat
 	*@param _fc
 	*/
	public void putAntToCoordinate(FieldCoordinate _fc)
	{
		Field selected = fields[_fc.getMapX()][_fc.getMapY()];
		Ant _ant = new Ant(); 
		_ant.setPos(selected);
		selected.EntryEvent(_ant);
	}
	
	/**
 	*Hangyat helyez a hangybolyba
 	*@param _index
 	*/
	public void putAntIntoAntNest( int _index)
	{
		AntNestField anf = antNests.get(_index);
		Ant ant = new Ant();		
		anf.addAnt(ant);	
		ant.setPos(anf);
		
		antNests.remove(_index);
		antNests.add(_index,anf);
	}
	
	/**
 	*A parameterul kapott koordinatara helyez egy hangyabolyt
 	*@param _fc
 	*/
	public void putAntNestToCoordinate(FieldCoordinate _fc)
	{
		Field selected = fields[_fc.getMapX()][_fc.getMapY()];
		fields[_fc.getMapX()][_fc.getMapY()] = ConvertField(selected, FieldType.AntNestField);
		createNeighbourhoods(); //Frissitjuk a referenciakat
		
		antNests.add((AntNestField)fields[_fc.getMapX()][_fc.getMapY()]);

	}
	
	/**
 	*Átvizsgalja a palyat es vissza adja az ures mezoket
 	*@return emptyFields
 	*/
	public ArrayList<Field> getEmptyFields()
	{
		ArrayList<Field> emptyFields = new ArrayList<Field>();
		
		for(int i = 0; i < n; i++)
			for (int j = 0; j <m ;j++)
			{				
				if (fields[i][j].canEnter() )
				{
					//Az �sszes akad�lyt k�v�ve, b�rhova tehet� hangya
					emptyFields.add(fields[i][j]);
				}
			}
		
		return emptyFields;
	}

	/**
 	*Beallitja a szagsemlegesito sprayt
 	*@param aSpray
 	*/
	public void setOdorNeutralizer(OdorNeutralizerSpray aSpray) {
		this.odorNeutralizer = aSpray;
//		System.out.println("Map::setOdorNeutralizer");
	}

	/**
 	*Beallitja a hangya irto sprayt
 	*@param aSpray
 	*/
	public void setAntKillerSpray(AntKillerSpray aSpray) {
		antKiller = aSpray;
//		System.out.println("Map::setAntKillerSpray");
	}

	/**
 	*Hangyasz sun beallitasa
 	*@param aAntEater
 	*/
	public void setAntEater(AntEater aAnteater) {
		this.antEater = aAnteater;
	}

	/**
 	*Vissza adja a hangyabolyokat
 	*@return antNests
 	*/
	public ArrayList<AntNestField> getAntNests() {
		return this.antNests;
	}

	/**
 	*Akadalyt tartalmazo mezoket hoz letre
 	*/
	private List<ObstacleField> generateObstacleFields(int count) {
//		System.out.println("Map::generateObstacleFields()");
		
		List<ObstacleField> result= new ArrayList<ObstacleField>();
		
		for (int i = 0; i < count; i++)
		{
			//Generalunk random koordinatat
			int x = r.nextInt(n);
			int y = r.nextInt(m);
			if(x==0 && y==0){i--; continue;}//Hangyászsünt blokkolná
			
			fields[x][y] = new ObstacleField(this);
			
			if (r.nextBoolean() == true) ((ObstacleField)fields[x][y]).setKind(ObstacleFieldType.rock);
			else ((ObstacleField)fields[x][y]).setKind(ObstacleFieldType.water);
			
			result.add((ObstacleField)fields[x][y]);
		}
		return result;
	}

	/**
 	*Élelemet tartalmazo mezoket hoz letre
 	*/
	private List<FoodField> generateFoodFields(int count) {
		
		List<FoodField> result = new ArrayList<FoodField>();
		for (int i = 0; i < count; i++)
		{
			//Generalunk random koordinatat
			int x = r.nextInt(n);
			int y = r.nextInt(m);
			if(fields[x][y]!=null){i--; continue;}
			fields[x][y] = new FoodField(this);
			((FoodField)fields[x][y]).setFoodOriginal(FoodField.foodpertile);
			((FoodField)fields[x][y]).setFoodCount(FoodField.foodpertile);
			result.add((FoodField)fields[x][y]);
		}
		this.foodFieldCount = count;
		return result;
	}

	/**
 	*Üres mezőket helyez el a pályán
 	*/
	private void addEmptyFields() {
		System.out.println("Map::addEmptyFields()");
		for (int i = 0; i <n ;i++) for (int j = 0; j<m; j++)
				if(fields[i][j]==null) fields[i][j] = new Field(this);
	}
	
	/**
 	*Vissza adja a hangya irtó sprayt
 	*@return antKiller
 	*/
	public AntKillerSpray getAntKillerSpray() {
		return antKiller;	
	}

	/**
 	*Vissza adja a szag semlegesítő sprayt
 	*@return odorNeutralizer
 	*/
	public OdorNeutralizerSpray getOdorNeutralizerSpray() {
		return odorNeutralizer;	
	}


	/**
 	*Átalakitja a parameterul kapott mezot a parameterul kapott tipusura
 	*/
	public Field ConvertField(Field aField, FieldType _ft) {
//		System.out.println("Map::ConvertField()");
		Field fnew=aField;
		
		if ((aField instanceof FoodField) && (_ft != FieldType.FoodField) )
		{
			//Elvesztünk egy food fieldet
			foodFieldCount--;
		}
		
		switch (_ft)
		{
		case AntNestField:
		//Barmit antnest- konvertalunk (Új hangyaboly keletkezik)
			AntNestField anf = new AntNestField(this);
			fnew=anf;
			break;
		case ObstacleField:
		//Kove konvertal (Hangyasz eltolt kove megjelenik)
			ObstacleField obs =new ObstacleField(this);
			fnew = obs;
			break;
		case Field:
		//Üres mezove konvertal (Hangyasz a kovet miutan utan)
			fnew= new Field(this);
			break;
		}
			fnew.setNeighbours(aField.getNeighbours());
			fnew.setCoordinate(aField.getCoordinate());
			
			//Régi szag objektum átadása:
			fnew.setOdors(aField.getOdors());
		    //Szagok beállítása:
			if(_ft!= FieldType.ObstacleField){	//Ha nem akadályt hozunk létre
				fnew.recalculateOdors(round);	//újraszámoljuk a szagot (Obstacle->Field Obstacle:0)
				   fnew.getOdors().setOdorBlock(false);//Ha eddig tiltva volt a szag, engedélyezzük
			}else {fnew.getOdors().setOdorBlock(true);}
			/*else
			if (fnew.getFieldType() != FieldType.ObstacleField)		//Ha Obstacle-t csinálunk
				fnew.setOdors(aField.getOdors());					//Nem adunk neki szagot
			*/
			//Frissiteni kell a matrixban az uj referenciat
			fields[aField.getCoordinate().getMapX()][aField.getCoordinate().getMapY()] = fnew;
			
			if (aField.getNeighbours() != null)
			{
				//szomszédok hivatkozásainak frissítése: (mert változik a referencia)
				for(Field neighbor: aField.getNeighbours()) {
					//szomszédok lekérése
					if (neighbor != null)
					{
						Field[] neighbors2= neighbor.getNeighbours();
						//régi referencia megkeresése
						int j=0; while(j<neighbors2.length && neighbors2[j]!=aField) j++;
						if(j==neighbors2.length) throw new RuntimeException("Nincs szomszéd (pedig kellene lennie)");
						//referencia átírása az újra
						neighbors2[j]=fnew;
						neighbor.setNeighbours(neighbors2);
					}
				}
			}
			//Ha van referenciánk a view-okra, frissítjük
			if(views!=null) views[aField.getCoordinate().getMapX()][aField.getCoordinate().getMapY()] = fnew.getView();
		return fnew;
	}

	/**
 	*Beolvass a parameterul kapott fajlt es felepiti belole a palyat
 	*/
	public void loadMapFromFile(String _fn)
	{
//		System.out.println("Map::loadMapFromFile()");
		try {
			FileReader fr = new FileReader(_fn);
			BufferedReader br = new BufferedReader(fr);
						
			String sor = "";
			int i = 0;
			int j = 0;
			while ( (sor = br.readLine()) != null)
			{
				String[] oszlopokEgySorban = sor.split(" ");
				
				for (String s : oszlopokEgySorban)
				{
					
					//Sima mezo
					if (s.equals("x"))
					{
						fields[i][j] = new Field(this);
					}
					//Akadaly water
					else if (s.equals("w"))
					{
						fields[i][j] = new ObstacleField(this,ObstacleFieldType.water);
					}
					//Akadaly rock
					else if (s.equals("r"))
					{
						fields[i][j] = new ObstacleField(this, ObstacleFieldType.rock);						
					}
					j++;
				}
				i++;
				j = 0;
			}			
			br.close();
			
		} catch (FileNotFoundException et) {
			et.printStackTrace();
		}
		catch ( IOException ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	//Skeleton only method
	/**
 	*Vissza ad egy mezot a koordinataja alapjan
 	*@return fields
 	*/
	public Field getFieldByCoordinate(FieldCoordinate fc)
	{
		return fields[fc.getMapX()][fc.getMapY()];
	}

	public void setFieldByCoordinate(FieldCoordinate fc, Field f) {
		fields[fc.getMapX()][fc.getMapY()] = f;
	}
	
	//Palya sorositasa
	public void PrintMap(PrintStream out) {
		dumpMatrix();
		PrintMapSmall();
		
		System.out.println();
	}
	public String PrintMapSmall()
	{
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<m;i++)
		{
		  sb.append(">");
		  for(int j=0;j<n;j++) 
		  {
			 
			  if (antEater.getPosition() == fields[j][i])
			  {
				  sb.append("H");
			  }else sb.append(fields[j][i].getCharCode());	
		  }
		  sb.append("<\r\n");
		}
		return sb.toString();
	}
	
	public void PrintFoodOdorMap(PrintStream out) {
		for(int i=0;i<m;i++)
		{
		  StringBuilder sb=new StringBuilder(">");
		  for(int j=0;j<n;j++) 
		  {
			 sb.append("["+fields[j][i].getOdors().getFoodOdor(0)+"]"); //getFoodOdor	
		  }
		  out.println(sb.toString()+"<");
		}
		
		System.out.println();
	}
	public void PrintAntNestOdorMap(PrintStream out) {
		for(int i=0;i<m;i++)
		{
		  StringBuilder sb=new StringBuilder(">");
		  for(int j=0;j<n;j++) 
		  {
			 sb.append("["+fields[j][i].getOdors().getAntNestOdor(0)+"]");	
		  }
		  out.println(sb.toString()+"<");
		}
		
		System.out.println();
	}
	
	//Kiirja a hangyak szamat
	public void dumpMatrix()
	{
		for (int i = 0; i < m; i++)
		{
			System.out.println();
			for (int j = 0; j <n ; j++)
			{
				System.out.print(" "+fields[j][i].getAnts().size()+ " " );
			}
		}
		System.out.println();
	}


	private int round=0;
	public void nextRound() {
		
		if (Map.random && (antEater.getPosition() == null ) && (Rand.r.nextInt(5) >= 3) )
		{
			//Új helyre rakjuk a hangyaszt
			antEater.setPosition(fields[0][0]);
			antEater.setAntsEaten(0);
		}
		
		round++;
		for(AntNestField nest : antNests)
		{
		  nest.nextRound(round);
		}
		for(int i = 0; i < fieldsWithAnts.size(); i++)
		{
			fieldsWithAnts.get(i).nextRound(round);
		}
		
		antEater.move(round);
		
	}
	public int getRound() { return round; }
	
	//Hangyasz altal hasznalt metodus, a kavics tolasara.
	public Field pushField(Field _field, int _direction)
	{		
		Field nb = _field.getNeighbours()[_direction];
		if (nb!=null)
		{
			Field f = ConvertField(_field, FieldType.Field);
			ConvertField(nb, FieldType.ObstacleField);
			return f;
		}
		
		return null; //Nem tudjuk tovabb tolni, mert kitolnank a palyarol az elemet
	}

	public void printAntOdor(PrintStream out) {
		int round = this.getRound();
		for(int i=0;i<m;i++)
		{
		  StringBuilder sb=new StringBuilder(">");
		  for(int j=0;j<n;j++) 
		  {
			 sb.append("["+fields[j][i].getOdors().getAntOdor(round)+"]");	
		  }
		  out.println(sb.toString()+"<");
		}
		
		System.out.println();
	}
	
	/**
	 * Visszaadja a parameterul kapott mezo koordinatajat a palyan.
	 * @auth=Bence
	 */
	public FieldCoordinate getCoordinateByField(Field field) {
		for (int i=0; i<n; i++)
			for (int k=0; k<m; k++)
				if (fields[i][k]==field)
					return new FieldCoordinate(i, k);
		return null;
	}
	
	public int getHeight() { return n; }
	public int getWidth() { return m; }
	
	public void addAntNestField(AntNestField field) {
		antNests.add(field);
	}
	
	public ArrayList<Field> getFields()
	{
		ArrayList<Field> rfields = new ArrayList<Field>();
		
		for(int i = 0; i < n; i++)
			for (int j = 0; j <m ;j++)
				if (fields[i][j]!=null) rfields.add(fields[i][j]);
						
		return rfields;
		
	}

	public void fireSpray(SprayEnum spraytype, Field f) {
		
		Spray spray = null;
		
		if (spraytype == SprayEnum.AntKiller)
		{
			spray = getAntKillerSpray();			
		}
		else spray = getOdorNeutralizerSpray();
		
		spray.shoot(f, this.getRound());
		
	}

	public void setViewArray(ViewBase[][] views) {
		
		this.views=views;
		
		for(int i=0;i<n;i++)
		for(int j=0;j<m;j++)
		{
			views[i][j]=fields[i][j].getView();
		}
		
	}

}

