package faction;

import state.Income;
import state.PlanetType;
import state.ResourceType;
import state.ScienceTrack;

public class HadschHallas extends Faction {

	private static final Income[] HHincomeForTS = {
			new Income(ResourceType.CREDITS, 3),
			new Income(ResourceType.CREDITS, 6),
			new Income(ResourceType.CREDITS, 10),
			new Income(ResourceType.CREDITS, 14),
			new Income(ResourceType.CREDITS, 19),
	};
	
	public HadschHallas() {
		homeplanet = PlanetType.OXIDE;
		starttrack = ScienceTrack.ECON;
		incomeForTS = HHincomeForTS;
		name = "Hadsch Hallas";
	}
}
