package faction;

import state.PlanetType;

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
