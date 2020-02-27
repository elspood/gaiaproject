package faction;

import action.Action;
import action.ActionType;
import state.Income;
import state.PlanetType;
import state.ResourceConversion;
import state.ResourceType;
import state.ScienceTrack;

public class Terran extends Faction {

	public Terran() {
		homeplanet = PlanetType.TERRA;
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
		if (pi == null) return null;
		
		int power = bank.gaiabowl();
		if (power == 0) return null;
		
		return ResourceConversion.actionOptions(player, power);
	}
	
	@Override
	protected void restoreGaiaPower(int power) {
		Income i = new Income(ResourceType.POWER, power);
		income(i);
		i = new Income(ResourceType.CHARGE, power);
		income(i);
	}
}
