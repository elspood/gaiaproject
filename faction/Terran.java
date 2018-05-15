package faction;

import state.Action;
import state.Bank;
import state.BowlState;
import state.Income;
import state.PlanetType;
import state.ResourceType;
import state.ScienceTrack;

public class Terran extends Faction {
	
	private static final int MAXBOWL = 6 + 4 + 3;	// extreme edge case of gaiaforming once at every research step
	private static final int[] COST = new int[] {4, 3, 4, 1};	// q, o, k, c
	private static int[] OPTIONS = new int[MAXBOWL];
	private static int[][] GAIAINCOME = new int[1000][COST.length];
	
	static {
		for (int i=0; i < MAXBOWL; i++) {
			generateIncomeOptions(i, 0, i+1, new int[COST.length]);
			System.out.print((i + 1) + ":");
			for (int j=0; j < OPTIONS[i]; j++) {
				int idx = i + j * MAXBOWL;
				System.out.print(" {" + GAIAINCOME[idx][0]);
				for (int k=1; k < COST.length; k++)
					System.out.print("," + GAIAINCOME[idx][k]);
				System.out.print("}");
			}
			System.out.println();
		}
	}
	
	private int player;
	
	private static void generateIncomeOptions(int start, int type, int left, int[] income) {
		if (left == 0) {
			for (int i=0; i < COST.length; i++)
				GAIAINCOME[start + (OPTIONS[start] * MAXBOWL)][i] = income[i];
			OPTIONS[start]++;
			return;
		}
		
		for (int i=type; i < COST.length; i++) {
			int cost = COST[i];
			if (cost > left) continue;
			income[i]++;
			generateIncomeOptions(start, i, left - cost, income);
			income[i]--;
		}
	}
	
	public void initializeGaiaActions(int player) {
		// TODO: initialize the Gaia actions with the correct player number (hacky; maybe fix this in main state)
	}

	public Terran() {
		homeplanet = PlanetType.BLUE;
		starttrack = ScienceTrack.GAIA;
		bank = new Bank(new BowlState(4, 4, 0));
		name = "Terran";
	}
	
	@Override
	public Action[] gaiaBowlActions() {
		int power = bank.emptyGaiaBowl();
		if (power == 0) return null;
		
		// charge power into bowl 2
		income(new Income(ResourceType.POWER, power));
		income(new Income(ResourceType.CHARGE, power));
		
		if (pi == null) return null;
		
		// TODO: optimize generation of action options (populate on init)
		return null;
	}
}
