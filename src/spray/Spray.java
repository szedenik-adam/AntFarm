package spray;

import java.util.HashSet;
import java.util.Set;

import field.*;

public abstract class Spray {
	protected int radius;
	protected int charge;

	public abstract void shoot(Field aMiddle, int round);
	
	public Spray(int radius, int charge) {
		this.radius = radius;
		this.charge = charge;
	}
	
	//Az ososztaly fuggvenye: ezzel lekerdezheto adott kozepponttol adott sugarra levo mezok halmaza
	protected Set<Field> getShootableFields(Field aMiddle) {
		HashSet<Field> fields = new HashSet<Field>();
		shootRek(aMiddle, 0, fields);
		return fields;
	}

	/** A mezok bejarasat a letisztitashoz rekurzioval oldjuk meg!
	 * Ez a fuggveny felelos a rekurzio megvalositasaert, az elsutes fuggvenyt ezt a fuggvenyt hivja meg a kezdeti "dist=0" ertekkel.
	 * cur: Az a mezo ahol jelenleg vagyunk
	 * dist: A jelenlegi tavolsagunk a kiindulo mezotol
	 * clearedField: Azoknak a mezoknek a halmaza, ahol mar jartunk
	 **/
	private void shootRek(Field cur, int dist, Set<Field> clearedFields) {
		//rekurzio kilepesi feltetele: Ha a kozepponttol radius tavolsagra vagyunk vagy lementunk a palyarol
		if (dist == this.radius || cur == null) return;
		//eltaroljuk hogy jartunk mar itt
		clearedFields.add(cur);
		//es tovabbmegyunk a szomszedos mezokre eggyel novelt tavolsaggal, ha ott meg nem jartunk
		for (Field neighbor : cur.getNeighbours())
			if (!clearedFields.contains(neighbor))
				shootRek(neighbor, dist+1, clearedFields);
	}

	/**
	 * Beallitja a sugarat.
	 * @param aRadius
	 */
	public void setRadius(int aRadius) {
		this.radius = aRadius;
	}

	/**
	 * Beallitja a sprayban levo fujasok szamat.
	 * @param aCharge
	 */
	public void setCharge(int aCharge) {
		this.charge = aCharge;
	}

	/**
	 * Visszaadja a sprayban levo fujasok szamat.
	 * @return int
	 */
	public int getCharge() {
		return this.charge;
	}

	/**
	 * Vissza adja a sugarat.
	 * @return int
	 */
	public int getRadius() {
		return this.radius;
	}
}
