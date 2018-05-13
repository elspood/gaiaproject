package state;
import java.util.Random;
import java.util.Vector;

public enum RoundBooster {
	
	KNOWORE(new Income[] {new Income(ResourceType.ORE, 1), new Income(ResourceType.KNOWLEDGE, 1)}, null, null),
	POWORE(new Income[] {new Income(ResourceType.ORE, 1), new Income(ResourceType.B1POWER, 2)}, null, null),
	QICCASH(new Income[] {new Income(ResourceType.CREDITS, 2), new Income(ResourceType.QIC, 1)}, null, null),
	TERRA(new Income[] {new Income(ResourceType.CREDITS, 2)}, SpecialAction.TERRAFORM, null),
	RANGE(new Income[] {new Income(ResourceType.CHARGE, 2)}, SpecialAction.RANGE3, null),
	MINE(new Income[] {new Income(ResourceType.ORE, 1)}, null, EndTurnBonus.MINE),
	TS(new Income[] {new Income(ResourceType.ORE, 1)}, null, EndTurnBonus.TS),
	LAB(new Income[] {new Income(ResourceType.KNOWLEDGE, 1)}, null, EndTurnBonus.LAB),
	PIA(new Income[] {new Income(ResourceType.CHARGE, 4)}, null, EndTurnBonus.PIA),
	GAIA(new Income[] {new Income(ResourceType.CREDITS, 4)}, null, EndTurnBonus.GAIA),
	;

	private final Income[] income;
	private final SpecialAction action;
	private final EndTurnBonus bonus;
	
	RoundBooster(Income[] income, SpecialAction action, EndTurnBonus bonus) {
		this.income = income;
		this.action = action;
		this.bonus = bonus;
	}
	
	public Income[] income() {
		return income;
	}
	
	public SpecialAction action() {
		return action;
	}
	
	public EndTurnBonus bonus() {
		return bonus;
	}
	
	public static Vector<RoundBooster> randomize(long seed, int players) {
		Vector<RoundBooster> pool = new Vector<RoundBooster>();
		for (RoundBooster rb : RoundBooster.values())
			pool.add(rb);

		Random rand = new Random(seed);
		int remove = pool.size() - (players + 3);
		for (int i=0; i < remove; i++) {
			int next = rand.nextInt(pool.size());
			pool.remove(next);
		}
		
		return pool;
	}
}
