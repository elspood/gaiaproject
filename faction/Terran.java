package faction;

import state.Action;
import state.ActionType;
import state.Bank;
import state.BowlState;
import state.Income;
import state.PlanetType;
import state.ResourceConversion;
import state.ResourceType;
import state.ScienceTrack;

public class Terran extends Faction {

	public Terran() {
		homeplanet = PlanetType.BLUE;
		starttrack = ScienceTrack.GAIA;
		bank = new Bank(new BowlState(4, 4, 0));
		name = "Terran";
	}
	
	@Override
	public void factionAction(Action a) {
		if (a.type() == ActionType.RESOURCETRADE) {
			int idx = (int)a.info();
			Income[] income = ResourceConversion.fromActionIndex(idx);
			for (Income i : income) income(i);
			return;
		}
		throw new IllegalArgumentException("No faction action defined for " + a.type());
	}
	
	@Override
	public Action[] gaiaBowlActions() {
		int power = bank.emptyGaiaBowl();
		if (power == 0) return null;
		
		// charge power into bowl 2
		income(new Income(ResourceType.POWER, power));
		income(new Income(ResourceType.CHARGE, power));
		
		if (pi == null) return null;
		return ResourceConversion.actionOptions(player, power);
	}
}
