package faction;

import state.PlanetType;
import state.ResourceConversion;

public class Nevlas extends Faction {
	
	public static final String NAME = "Nevlas";
	
	public Nevlas() {
		homeplanet = PlanetType.ICE;
		name = NAME;
	}
	
	@Override
	protected ResearchKnowledgeSource sourceFactionKnowledge(ResearchKnowledgeSource basesource) {
		// don't use faction ability if research can be afforded with current bank
		// TODO: determine if there's ever a situation where you'd want to do otherwise
		if (basesource == ResearchKnowledgeSource.BANK) return ResearchKnowledgeSource.NONE;
		
		int power = ResourceConversion.RESEARCHKNOWLEDGECOST - bank.knowledge();
		
		int p = super.spendablePower(); // we can't apply the nevlas PI bonus for knowledge conversion to gaia bowl
		if (p >= power) return ResearchKnowledgeSource.NEVLASSPEC3;
		
		int b = super.maxBurnIncome(); // we can't apply the nevlas PI bonus for knowledge conversion to gaia bowl
		if (p + b >= power) return ResearchKnowledgeSource.NEVLASSPEC2;
			
		return ResearchKnowledgeSource.NONE;
	}
	
	@Override
	public int maxBurnIncome() {
		int p = bank.maxBurnIncome();
		return (pi == null) ? p : 2 * p;
	}
	
	@Override
	public int spendablePower() {
		int p = bank.spendablePower();
		return (pi == null) ? p : 2 * p;
	}
}
