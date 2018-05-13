package faction;

import state.Bank;
import state.Income;
import state.PlanetType;
import state.ResourceType;
import state.ScienceTrack;

public class Xenos extends Faction {

	private static final Income[] xenosIncomeForPI = {
			new Income(ResourceType.CHARGE, 4),
			new Income(ResourceType.QIC, 1),
	};
	
	public Xenos() {
		homeplanet = PlanetType.YELLOW;
		starttrack = ScienceTrack.AI;
		incomeForPI = xenosIncomeForPI;
		name = "Xenos";
	}
}
