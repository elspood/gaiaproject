package state;

public enum FederationToken {
	VP(new Income[] {new Income(ResourceType.VP, 12)}, false),
	QIC(new Income[] {new Income(ResourceType.VP, 8), new Income(ResourceType.QIC, 1)}),
	POWER(new Income[] {new Income(ResourceType.VP, 8), new Income(ResourceType.B1POWER, 2)}),
	ORE(new Income[] {new Income(ResourceType.VP, 7), new Income(ResourceType.ORE, 2)}),
	CREDIT(new Income[] {new Income(ResourceType.VP, 7), new Income(ResourceType.CREDITS, 6)}),
	KNOW(new Income[] {new Income(ResourceType.VP, 6), new Income(ResourceType.KNOWLEDGE, 2)}),
	GLEEN(false, new Income[] {new Income(ResourceType.ORE, 1),
			new Income(ResourceType.KNOWLEDGE, 1), new Income(ResourceType.CREDITS, 2)});
	
	private boolean inresearchpool = true;
	private boolean green = true;
	private Income[] income = null;
	
	FederationToken() {}
	FederationToken(boolean inpool, Income[] income) {
		init(inpool, income, true);
	}
	FederationToken(Income[] income) {
		init(true, income, true);
	}
	FederationToken(Income[] income, boolean green) {
		init(true, income, green);
	}
	
	private void init(boolean inpool, Income[] income, boolean green) {
		this.inresearchpool = inpool;
		this.income = income;
		this.green = green;
	}
	
	public boolean inResearchPool() {
		return inresearchpool;
	}
	
	public boolean spendable() {
		return green;
	}
	
	public Income[] income() {
		return income;
	}
}
