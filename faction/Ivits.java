package faction;

import state.Coordinates;
import state.PlanetType;

public class Ivits extends Faction {

	public void draftPI(Coordinates coords) {
		this.pi = coords;
	}
	
	public Ivits() {
		homeplanet = PlanetType.RED;
		name = "Ivits";
	}
}
