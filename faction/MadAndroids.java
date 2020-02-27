package faction;

import state.Income;
import state.PlanetType;
import state.ResourceConversion;
import state.ResourceType;
import state.ScienceTrack;

public class MadAndroids extends Faction {
	
	public static final String NAME = "Bescods";
	
	public MadAndroids() {
		homeplanet = PlanetType.TITANIUM;
		name = NAME;
	}

	
	@Override
	protected ResearchKnowledgeSource sourceFactionKnowledge(ResearchKnowledgeSource basesource) {
		if (specialactions.factionAvailable()) return ResearchKnowledgeSource.MADDROIDSSPEC;

		return ResearchKnowledgeSource.NONE;
	}
}
