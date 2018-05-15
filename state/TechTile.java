package state;

public enum TechTile {
	OREQIC(new Income[] {new Income(ResourceType.ORE, 1), new Income(ResourceType.QIC, 1)}, false),
	SECSCI(new Income[] {new Income(ResourceType.KNOWLEDGE, 1)}, false),
	BUILDING(null, false),
	VP7(new Income[] {new Income(ResourceType.VP, 7)}, false),
	OREPOW(new Income[] {new Income(ResourceType.ORE, 1), new Income(ResourceType.CHARGE, 1)}),
	KNOWCASH(new Income[] {new Income(ResourceType.KNOWLEDGE, 1), new Income(ResourceType.CREDITS, 1)}),
	GAIA(new Income[] {new Income(ResourceType.VP, 3)}, false),
	CREDITS(new Income[] {new Income(ResourceType.CREDITS, 4)}),
	POWER(null, false);
	
	private Income[] income;
	private boolean incomephase;
	
	private TechTile(Income[] i) {
		income = i;
		incomephase = true;
	}
	
	private TechTile(Income[] i, boolean onetime) {
		income = i;
		this.incomephase = onetime;
	}
	
	public Income[] income() {
		return income;
	}
	
	public boolean incomePhase() {
		return incomephase;
	}
}
