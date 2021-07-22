package spray;

import field.*;

public class OdorNeutralizerSpray extends Spray {

	final int round_count = 5;
	
	public OdorNeutralizerSpray(int radius, int charge){
		super(radius, charge);
	}
	
	public void shoot(Field aMiddle,int round) {
		System.out.println("OdorNeutralizerSpray::shoot\tradius=="+radius+"\tremaining charges="+charge);
		round+=round_count;
		if (charge>0)
			for (Field cur : getShootableFields(aMiddle))
				cur.removeOdor(round);
		charge--;
	}
}
