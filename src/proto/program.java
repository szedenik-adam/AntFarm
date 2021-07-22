package proto;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import proto.InteractionsEnum;
import skeleton.FieldCoordinate;
import spray.AntKillerSpray;
import spray.OdorNeutralizerSpray;
import ant.AntEater;
import field.Field;
import map.Map;

public class program {

	///Skeleton lifecycle control
	private static boolean exit = false;
	///referencia a mapra, ez maga a jatekter
	private static Map map = null;
	///Mekkora a palya? Hosszaban
	private static int mapHeight = 5; 
	//SzĂ©ltĂ©ben
	private static int mapWidth = 10;	
	
	//AlapvetĹ‘  konstansok beĂˇllĂ­tĂˇsa.
	private static int killerSprayRadius = 2;
	private static int neutralizerSprayRadius = 3;
	private static int killerSprayCharges = 30;
	private static int neutralizerSprayCharges = 50;

	
	/**FĹ‘metĂłdus. Itt zajlik minden.
	 * @param args
	 */
	public static void main(String[] args) {			
		System.out.println();
		System.out.println("NullPtr - 40 - Skeleton");
		System.out.println("Konzulens: Biro Barna");
		
		
		//Inicializalas, jĂˇtĂ©kvilĂˇg megteremtĂ©se 
		map = new Map(mapWidth,mapHeight,false);		
		
		
		//TĂˇrsadalmi cĂ©lĂş hirdetĂ©sek
		while (!exit)
		{
			map.nextRound();
			System.out.println("Food Odor Map:");
			map.PrintFoodOdorMap(System.out);
			System.out.println("Ant Odor Map:");
			map.printAntOdor(System.out);
			System.out.println("Map:");
			map.PrintMap(System.out);
			System.out.println("Az interakciĂłk kĂ¶zĂĽl vĂˇlaszthat:");		
			
			//Enumok az interakciĂłkrĂłl
			int i = 1;
			for (InteractionsEnum ste: InteractionsEnum.values())
			{
				System.out.print(i+" - "+ste+"\t");
				i++;
			}
			//For an english speaking operator, press zero.
			
			//Bekerjuk a felhasznĂˇlĂł vĂˇlasztĂˇsĂˇt
			int choice =  choice();	
			
			//ElindĂ­tjuk a kivĂˇlasztott tesztet
			UserInteract(InteractionsEnum.values()[choice-1],map.getRound());
			
		}
	}

	/** VĂ©grehajtjuk a kivĂˇlasztott tesztet
	 * <p> 
	 * VĂ©grehajtjuk a felhasznĂˇlĂł Ăˇltal kivĂˇlasztott tesztet
	 * @author Marci
	 *
	 *
	 */
	private static void UserInteract(InteractionsEnum interactionEnum, int round) {
		
		//Kis ures hely, jobb design
		for (int i = 0; i < 5; i++)	System.out.println();
		    
		System.out.println(interactionEnum.toString()); // KivĂˇlasztott teszt kiĂ­rĂˇsa
		
		//Inenn hĂ­vhatjuk a tesztmetĂłdusokat
		switch (interactionEnum)
		{
		//kilepes
		case Exit:
			Exit();
			break;
		//Spray valasztas
		case SprayChoosing:
			SprayChoosing();
			break;
		//Spray fĂşjĂˇs
		case SprayUse:
			SprayUse(round);
			break;
		//Default
			default:	//Nothing
			break;				
		}
			
	}
	
	/** Ez a metĂłdus szimulĂˇlja a spray-jekkel valĂł fĂşjĂˇst.
	 * A felhasznĂˇlĂł Ăˇltal megadott helyre, a felhasznĂˇlĂł Ăˇltal megadott spray-jel, a tĂ¶rĂ¶lt map Ăˇltal fĂşj.
	 * ValĂłs koordinĂˇtĂˇt Ă©s spraytĂ­pust kell megadni...
	 **/
	private static void SprayUse(int round) {
		System.out.println("Hova fujjunk? Irja be a fĂşjĂˇs helyĂ©nek koordinĂˇtĂˇit (\"oszlop sor\")!");
		int row = choice();
		int col = choice();
		//Hova akarunk fĂşjni? LekĂ©rdezi a map-tĹ‘l a pontos mezĹ‘t.
		Field target = map.getFieldByCoordinate(new FieldCoordinate(row, col));
		
		System.out.println("Mivel szeretne fĂşjni? 1. HangyaszagsemlegesĂ­tĹ‘ 2. HangyairtĂł");
		int choice = choice();
		
		//A fĂşjĂˇs megvalďż˝sďż˝tďż˝sa:
		if (choice == 1)
			map.getOdorNeutralizerSpray().shoot(target, round);
		else
			map.getAntKillerSpray().shoot(target, round);
	}

	
	
	/**Beolvas egy szamot a inputrol.  
	 * Beolvassa a felhasznĂˇlĂł vĂˇlasztĂˇsĂˇt az inputrĂłl.
	 * @return a szĂˇm, vagy -1 ha sikertelen a beolvasĂˇs
	 */
	private static int choice()
	{
		try
		{
			int input= Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
			if(input<1) input=1;
			return input;
		}
		catch (Exception _ex) { System.out.println(_ex.getMessage()); }
		
		return 1;
	}
	
	/** KilĂ©pĂ©s UC. A felhasznĂˇlĂł kilĂ©p a jĂˇtĂ©kbĂłl.
	 * 
	 * */
	private static void Exit()
	{
		//KĂ©rdĂ©s Ă©s opciĂłk
		System.out.println("Biztosan kilďż˝p?");
		System.out.println("1. Igen");
		System.out.println("2. Nem");
		//A vĂˇlasztĂˇs
		int choice = choice();
		
		//What to do?
		if (choice == 1) exit = true;
	}
	
	
	
	
	/**A felhasznĂˇlĂł vĂˇlaszt egy spray-t
	 Erre a kĂ©sz programban a felhasznĂˇlĂłnak nem lesz lehetĹ‘sĂ©ge,
		ezek a metĂłdusok a jĂˇtĂ©k indĂ­tĂˇsakor fognak lefutni (a spray inicializĂˇlĂˇsa).
	 * **/
	private static void SprayChoosing()
	{
		System.out.println("Melyik sprayt szeretned hasznalni!");
		System.out.println("1 - Szagtalanito");
		System.out.println("2 - Hangyaolo");
		int choice = choice() ;
		if(choice == 1)
		{
			OdorNeutralizerSpray odorspray = new OdorNeutralizerSpray(5,5);
			//A semlegesĂ­tĹ‘ spray-nek beĂˇllĂ­tjuk a megfelelĹ‘ paramĂ©tereket (fĂşjĂˇsszĂˇm/sugĂˇr)
			odorspray.setCharge(neutralizerSprayCharges);
			odorspray.setRadius(neutralizerSprayRadius);
			map.setOdorNeutralizer(odorspray);
		}
		else if(choice == 2)
		{
			AntKillerSpray antkiller = new AntKillerSpray(5,5);
			//Az irtĂł spray-nek beĂˇllĂ­tjuk a megfelelĹ‘ paramĂ©tereket (fĂşjĂˇsszĂˇm/sugĂˇr)
			antkiller.setCharge(killerSprayCharges);
			antkiller.setRadius(killerSprayRadius);
			map.setAntKillerSpray(antkiller);
		}
		else
		{
			System.out.println("Nincs ilyen vĂˇlasztĂˇsi opciĂł!");
		}
		
	}


}
