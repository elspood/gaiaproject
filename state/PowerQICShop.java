package state;

public class PowerQICShop {
	
	public static final int ORE = 0;
	public static final int CREDITS = 1;
	public static final int K2 = 2;
	public static final int K3 = 3;
	public static final int POWER = 4;
	public static final int POWERRESOURCEACTIONS = 5;
	
	public static final int DIG = 5;
	public static final int DOUBLEDIG = 6;
	
	public static final int PLANETTYPEVP = 7;
	public static final int RESCOREFED = 8;
	public static final int TECHTILE = 9;
	public static final int QICACTIONS = 3;
	
	public static final int SHOPACTIONS = 10;
	public static final int[] COST = new int[] {4, 4, 4, 7, 3, 3, 5, 2, 3, 4};
	
	private static final Income[] INCOME = new Income[] {
			new Income(ResourceType.ORE, 2),
			new Income(ResourceType.CREDITS, 7),
			new Income(ResourceType.KNOWLEDGE, 2),
			new Income(ResourceType.KNOWLEDGE, 3),
			new Income(ResourceType.POWER, 2)
	};
	
	private boolean[] available = new boolean[SHOPACTIONS];
	
	public PowerQICShop() {
		resetShop();
	}
	
	public void resetShop() {
		for (int i=0; i < SHOPACTIONS; i++) available[i] = true;
	}
	
	public boolean powerShopAvailableFor(int availablepower) {
		for (int i=0; i < POWERRESOURCEACTIONS; i++) {
			if (available[i] && (COST[i] <= availablepower)) return true;
		}
		return false;
	}
	
	public boolean qicShopAvailableFor(int availableqic) {
		for (int i=PLANETTYPEVP; i < PLANETTYPEVP + QICACTIONS; i++) {
			if (available[i] && (COST[i] <= availableqic)) return true;
		}
		return false;
	}

}
