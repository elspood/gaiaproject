package faction;

import java.util.EnumMap;
import java.util.Vector;

import state.Action;
import state.Bank;
import state.BowlState;
import state.Coordinates;
import state.Income;
import state.PlanetType;
import state.ResourceType;
import state.RoundBooster;
import state.ScienceTrack;
import state.SpecialAction;
import state.TechTile;

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
			new Income(ResourceType.POWER, 1),
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
	protected Coordinates[] gf = new Coordinates[3];
	
	protected RoundBooster booster = null;
	protected TechTile[] techtile = new TechTile[TechTile.values().length];
	protected boolean[] coveredtile = new boolean[techtile.length];
	
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
	
	public void setPower(BowlState s) {
		bank.setPower(s);
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
	
	public int ts() {
		for (int i=0; i < 4; i++)
			if (ts[i] == null) return i;
		return 4;
	}

	public int labs() {
		for (int i=0; i < 3; i++)
			if (lab[i] == null) return i;
		return 3;
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
	
	public BowlState[] roundIncome(Vector<Income> techIncome) {
		// calculates all income and returns an array of possible board states
		// returns null if there are no choices about power income to be made
		
		// add building income
		techIncome.add(incomeForMines[mines()]);
		techIncome.add(incomeForTS[ts()]);
		techIncome.add(incomeForLabs[labs()]);
		if (acad[0] != null) techIncome.add(incomeForAcademy);
		if (pi != null) for (Income i : incomeForPI) techIncome.add(i);
		
		// add booster and tech tile income
		for (Income i : booster.income()) techIncome.add(i);
		for (int i=0; i < techtile.length; i++)
			if ((techtile[i] != null) && (coveredtile[i] == false) && (techtile[i].incomePhase()))
				for (Income inc : techtile[i].income()) techIncome.add(inc);
		
		Vector<Income> powerIncome = new Vector<Income>();
		for (Income i : techIncome)
			if ((i.type() == ResourceType.POWER) || (i.type() == ResourceType.CHARGE))
				powerIncome.add(i);
			else income(i);
		
		return bank.powerIncome(powerIncome);
	}
	
	public Action[] gaiaBowlActions() {
		int power = bank.emptyGaiaBowl();
		if (power > 0)
			income(new Income(ResourceType.POWER, power));
		return null;
	}
	
	public void income(Income inc) {
		if (inc.type() == ResourceType.GF) {
			for (int i=0; i < gf.length; i++) if (gf[i] == null) {
				gf[i] = Coordinates.HANGAR;
				return;
			}
			throw new IllegalStateException("No room for additional gaiaformer in hangar");
		}
		bank.income(inc);
	}
	
	public Vector<Coordinates> gaiaformerLocations() {
		Vector<Coordinates> locations = new Vector<Coordinates>();
		for (Coordinates c : gf) if ((c != null) && (c.col() >= 0) && (c.row() >= 0))
				locations.add(c);
		return locations;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name + " " + bank);
		if (booster != null) sb.append("Booster: " + booster + " ");
		for (int i=0; i < techtile.length; i++) if (techtile[i] != null) sb.append(techtile[i] + " ");
		sb.append("\n");
		return sb.toString();
	}
}
