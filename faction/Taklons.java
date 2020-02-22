package faction;

import state.PlanetType;
import state.Bank;
import state.BowlState;

public class Taklons extends Faction {

	public Taklons() {
		bank = new Bank(new BowlState(true, 2, 4, 0));
		homeplanet = PlanetType.SWAMP;
		name = "Taklons";
	}
}
