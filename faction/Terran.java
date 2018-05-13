package faction;

import state.Bank;
import state.Income;
import state.PlanetType;
import state.ResourceType;
import state.ScienceTrack;

public class Terran extends Faction {

	public Terran() {
		homeplanet = PlanetType.BLUE;
		starttrack = ScienceTrack.GAIA;
		bank = new Bank(new Income[] {new Income(ResourceType.B1POWER,2)});
		name = "Terran";
	}
}
