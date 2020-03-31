package state;

import action.Action;
import action.ActionType;

public class ResourceConversion {

	public static final int POWERTOQICCOST = 4;
	public static final int POWERTOORECOST = 3;
	public static final int POWERTOKNOWLEDGECOST = 4;
	public static final int POWERTOCREDITCOST = 1;
	public static final int RESEARCHKNOWLEDGECOST =  4;
	public static final int RANGEPERQIC = 2;
	private static final int MAXTRADE = 30;			// extreme edge case of gaiaforming once at every research step
													// yields a max terran bowl of 6 + 4 + 3 = 13 power
													// max Hadsch Hallas trade is all 30 credits
													// power bowl is theoretically unlimited, but practically never >30
	private static final int ACTIONPOOLSIZE = 25;	// determined empirically from options in max trade
	private static final int NOTRADE = -1;
	
	
	// TODO: create a version of this for nevlas, who can trade one power for 1K and 1 token for 2 power
	private static final int[] COST = new int[] {POWERTOQICCOST, POWERTOORECOST, POWERTOKNOWLEDGECOST, POWERTOCREDITCOST};
	private static final ResourceType[] TYPE = new ResourceType[] {
			ResourceType.QIC, ResourceType.ORE, ResourceType.KNOWLEDGE, ResourceType.CREDITS
	};
	private static int[] OPTIONS = new int[MAXTRADE];
	private static int[][][] TRADEINCOME = new int[MAXTRADE][ACTIONPOOLSIZE][COST.length];
	private static Action[][][] playeraction;
	
	static {
		for (int i=0; i < MAXTRADE; i++) {
			generateIncomeOptions(i, 0, i+1, new int[COST.length]);
			/*
			System.out.print((i + 1) + ":");
			for (int j=0; j < OPTIONS[i]; j++) {
				System.out.print(" {" + TRADEINCOME[i][j][0]);
				for (int k=1; k < COST.length; k++)
					System.out.print("," + TRADEINCOME[i][j][k]);
				System.out.print("}");
			}
			System.out.println();
			*/
		}

	    playeraction = new Action[4][MAXTRADE][ACTIONPOOLSIZE];
		// TODO: (hacky; maybe fix this in main state so we don't have to generate one per player)
	    
		for (int i=0; i < 4; i++)
			for (int j=0; j < MAXTRADE; j++)
				for (int k=0; k < OPTIONS[k]; k++)
					playeraction[i][j][k] = tradeAction(i, j, k);
	}
	
	private static void generateIncomeOptions(int start, int type, int left, int[] income) {
		if (left == 0) {
			for (int i=0; i < COST.length; i++)
				TRADEINCOME[start][OPTIONS[start]][i] = income[i];
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
	
	private static Action tradeAction(int player, int energy, int option) {
		int[] income = TRADEINCOME[energy][option];
		String text = "";
		for (int i=0; i < income.length; i++) if (income[i] !=0)
			text += income[i] + TYPE[i].toString();
		return new Action(player, ActionType.RESOURCETRADE, energy + option * MAXTRADE, text);
	}
	
	public static int spent(int idx) {
		return idx % MAXTRADE;
	}
	
	public static Income[] fromActionIndex(int idx) {
		if (idx == NOTRADE) return null;
		int[] income = TRADEINCOME[idx % MAXTRADE][idx / MAXTRADE];
		Income[] temp = new Income[COST.length];
		int index = 0;
		for (int i=0; i < income.length; i++) {
			int inc = income[i];
			if (i == 0) continue;
			temp[index++] = new Income(TYPE[i], inc);
		}
		Income[] increturn = new Income[index];
		System.arraycopy(temp, 0, increturn, 0, index);
		return increturn;
	}
	
	public static Action[] actionOptions(int player, int resources) {
		return actionOptions(player, resources, false);
	}
	
	public static Action[] actionOptions(int player, int resources, boolean partialtradesallowed) {
		if (resources > MAXTRADE) {
			System.err.println("Truncated attempt to trade more than maximum resource trade limit " + MAXTRADE);
			resources = MAXTRADE;
		}
		if (!partialtradesallowed) return playeraction[player][resources - 1];
		
		int actions = 1;
		for (int i=0; i < resources; i++)
			actions += playeraction[player][i].length;
		Action[] action = new Action[actions];
		
		action[0] = new Action(player, ActionType.RESOURCETRADE, NOTRADE, "No trade");
		actions = 1;
		for (int i=0; i < resources; i++) {
			Action[] next = playeraction[player][i];
			System.arraycopy(next, 0, action, actions, next.length);
			actions += next.length;
		}
		return action;
	}
}
