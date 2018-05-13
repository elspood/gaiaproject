package faction;

import java.util.EnumMap;

import state.Bank;
import state.Coordinates;
import state.Income;
import state.PlanetType;
import state.ResourceType;
import state.RoundBooster;
import state.ScienceTrack;
import state.SpecialAction;

public abstract class Faction {

	protected PlanetType homeplanet;
	protected Income[] incomeForMines = {
			new Income(ResourceType.ORE, 1),
			new Income(ResourceType.ORE, 2),
			new Income(ResourceType.ORE, 3),
			new Income(ResourceType.ORE, 3),
			new Income(ResourceType.ORE, 4),
			new Income(ResourceType.ORE, 5),
			new Income(ResourceType.ORE, 6),
			new Income(ResourceType.ORE, 7),
			new Income(ResourceType.ORE, 8),
	};
	protected Income[] incomeForTS = {
			new Income(ResourceType.CREDITS, 0),
			new Income(ResourceType.CREDITS, 3),
			new Income(ResourceType.CREDITS, 7),
			new Income(ResourceType.CREDITS, 11),
			new Income(ResourceType.CREDITS, 16),
	};
	protected Income[] incomeForLabs = {
			new Income(ResourceType.KNOWLEDGE, 1),
			new Income(ResourceType.KNOWLEDGE, 2),
			new Income(ResourceType.KNOWLEDGE, 3),
			new Income(ResourceType.KNOWLEDGE, 4),
	};
	protected Income[] incomeForPI = {
			new Income(ResourceType.CHARGE, 4),
			new Income(ResourceType.B1POWER, 1),
	};
	protected Income incomeForAcademy = new Income(ResourceType.KNOWLEDGE, 2);
	protected SpecialAction actionForAcademy = SpecialAction.QIC;
	protected ScienceTrack starttrack = null;
	protected EnumMap<PlanetType, Boolean> colonized = new EnumMap<PlanetType, Boolean>(PlanetType.class);
	protected String name = null;
	
	protected Bank bank = new Bank();
	protected Coordinates[] mine = new Coordinates[8];
	protected Coordinates[] ts = new Coordinates[4];
	protected Coordinates[] lab = new Coordinates[3];
	protected Coordinates[] acad = new Coordinates[2];
	protected Coordinates pi = null;
	protected RoundBooster booster = null;
	
	public boolean placeMine(int col, int row) {
		Coordinates c = new Coordinates(col, row);
		return placeMine(c);
	}
	
	public boolean placeMine(Coordinates c) {
		for (int i=0; i < 8; i++) if (mine[i] == null) {
			mine[i] = c;
			return true;
		}
		return false;
	}
	
	public RoundBooster swapBooster(RoundBooster next) {
		RoundBooster old = booster;
		booster = next;
		return old;
	}
	
	public int mines() {
		for (int i=0; i < 8; i++)
			if (mine[i] == null) return i;
		return 8;
	}
	
	public String name() {
		if (name == null) return this.getClass().getName();
		return name;
	}
	
	public PlanetType homePlanet() {
		return homeplanet;
	}
	
	public ScienceTrack startTrack() {
		return starttrack;
	}
	
	public void income(Income i) {
		bank.income(i);
	}
}
