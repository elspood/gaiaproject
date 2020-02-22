package faction;

import state.Coordinates;
import state.PlanetType;

public class Ivits extends Faction {

	public void draftPI(Coordinates coords) {
		// TODO: does this need any state checks?
		this.pi = coords;
	}
	
	public Ivits() {
		homeplanet = PlanetType.OXIDE;
		name = "Ivits";
	}
}
