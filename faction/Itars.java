package faction;

import action.Action;
import state.PlanetType;

public class Itars extends Faction {
	
	private int lasttilebought = 0;	// track the last tile bought and start there when offering new options
	
	public Itars() {
		homeplanet = PlanetType.ICE;
		name = "Itars";
	}
	
	@Override
	public Action[] gaiaBowlActions() {
		if (pi == null) return null;
		
		int power = bank.gaiabowl();
		if (power < 4) {
			lasttilebought = 0;
			return null;
		}

		// TODO: return itars tech tile buying spree options
		// this choice will be recursive, but options for tech tiles must be taken in a set order, otherwise pointless branching will occur for buying tiles in different orders
		return null;
	}
}
