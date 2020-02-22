package faction;

import state.Income;
import state.PlanetType;
import state.ResourceType;
import state.ScienceTrack;

public class Xenos extends Faction {

	private static final Income[] PIINCOME = {
			new Income(ResourceType.CHARGE, 4),
			new Income(ResourceType.QIC, 1),
	};
	
	public Xenos() {
		homeplanet = PlanetType.DESERT;
		starttrack = ScienceTrack.AI;
		incomeForPI = PIINCOME;
		name = "Xenos";
	}
}
