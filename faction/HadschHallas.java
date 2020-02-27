package faction;

import state.Income;
import state.PlanetType;
import state.ResourceConversion;
import state.ResourceType;
import state.ScienceTrack;

public class HadschHallas extends Faction {
	
	public static final int KNOWLEDGECREDITCOST = 4;
	public static final int ORECREDITCOST = 3;
	public static final int QICCREDITCOST = 4;
	public static final String NAME = "Hadsch Hallas";

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
		name = NAME;
	}

	
	@Override
	protected ResearchKnowledgeSource sourceFactionKnowledge(ResearchKnowledgeSource basesource) {
		// don't use faction ability if research can be afforded with current bank
		if (basesource == ResearchKnowledgeSource.BANK) return ResearchKnowledgeSource.NONE;
		
		int k = ResourceConversion.RESEARCHKNOWLEDGECOST - bank.knowledge();
		if (bank.credits() >=  k * KNOWLEDGECREDITCOST) return ResearchKnowledgeSource.HHSPEC;

		return ResearchKnowledgeSource.NONE;
	}
}
