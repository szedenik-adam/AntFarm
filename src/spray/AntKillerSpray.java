package spray;

import field.*;

public class AntKillerSpray extends Spray {

	public AntKillerSpray(int radius, int charge){
		super(radius, charge);
	}
	
	/**
	 * A spray elsuteseert felelos metodus. A parameterkent atadott mezo
	 * egy bizonyos sugaraban megoli a hangyaka.
	 * @param aMiddle
	 */
	public void shoot(Field aMiddle, int round) {
		System.out.println("OdorNeutralizerSpray::shoot\tradius=="+radius+"\tremaining charges="+charge);
		if (charge>0)
			for (Field cur : getShootableFields(aMiddle)) {
				cur.removeAnts();
			}
		charge--;
	}
	
}
