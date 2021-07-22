package proto;

import java.util.Random;

//Veletlenszam generalo csomagoloosztaly.
public class Rand {

	Random rnd;
	
	public Rand()			{ rnd=new Random(); }
	public Rand(int seed)	{ rnd=new Random(seed); }
	
	public int nextInt(int maximum)
	{
		return rnd.nextInt(maximum);
	}
	
	public boolean nextBoolean()
	{
		return rnd.nextBoolean();
	}
	
	
	//A globalis elesest biztosito elemek
	public static Rand r;
	
	public static Rand init()
	{
		if(r==null) r=new Rand();
		return r;
	}
	
	public static Rand init(int seed)
	{
		if(r==null) r=new Rand(seed);
		return r;
	}
	
	//Teszteleshez veletlenszam mentes osztaly (peldanyositashoz: Rand=new Rand.NotRand() )
	public static class NotRand extends Rand {
		
		private int[] route;
		private int cur = 0;
		
		public NotRand(int[] route) { this.route = route; }

		@Override
		public int nextInt(int value)
		{
			if (route.length==0) return 0;
			else return route[(cur++)%route.length];
		}
		@Override
		public boolean nextBoolean()
		{
			return false;//mindig hamis
		}
		public boolean nextBoolean(boolean result)
		{
			return result;
		}
		public boolean valuesSet() { return route.length!=0; }
	}
}

