package faction;

import java.util.EnumMap;
import java.util.Vector;

import state.Action;
import state.AdvTech;
import state.Bank;
import state.BowlState;
import state.Coordinates;
import state.EndTurnBonus;
import state.Income;
import state.PlanetType;
import state.ResourceType;
import state.RoundBooster;
import state.ScienceTrack;
import state.SpecialAction;
import state.TechTile;

public abstract class Faction {
	
	protected static Faction[] factions = {
			new HadschHallas(), new Ivits(), new Taklons(), new Terran(), new Xenos()
	};

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
	protected String name = null;
	
	protected Bank bank = new Bank();
	protected Coordinates[] mine = new Coordinates[8];
	protected Coordinates[] ts = new Coordinates[4];
	protected Coordinates[] lab = new Coordinates[3];
	protected Coordinates[] acad = new Coordinates[2];
	protected Coordinates pi = null;
	protected Coordinates[] gf = new Coordinates[3];
	
	protected int uniques = 0;
	protected int gaias = 0;
	protected EnumMap<PlanetType, Boolean> colonized = new EnumMap<PlanetType, Boolean>(PlanetType.class);
	
	protected RoundBooster booster = null;
	protected TechTile[] techtile = new TechTile[TechTile.values().length];
	protected Vector<AdvTech> advtech = new Vector<AdvTech>();
	protected boolean[] coveredtile = new boolean[techtile.length];
	
	protected int player;
	
	public void init(int player) {
		this.player = player;
	}
	
	public static Faction[] choices() {
		return factions;
	}
	
	public boolean placeMine(int col, int row, PlanetType type) {
		Coordinates c = new Coordinates(col, row);
		return placeMine(c, type);
	}
	
	public boolean placeMine(Coordinates c, PlanetType type) {
		for (int i=0; i < 8; i++) if (mine[i] == null) {
			mine[i] = c;
			if (colonized.containsKey(type)) {
				uniques++;
				colonized.put(type, true);
			}
			if (type == PlanetType.GAIA) {
				gaias++;
				int gt = TechTile.GAIA.ordinal();
				if ((techtile[gt] != null) && (!coveredtile[gt])) {
					Income[] inc = TechTile.GAIA.income();
					for (Income income : inc) income(income);
				}
			}
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
		// TODO: count lost planet, maybe refactor to track mine count
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
	
	public int pia() {
		int pia = (pi == null) ? 0 : 1;
		for (int i=0; i < 2; i++) if (acad[i] != null) pia++;
		return pia;
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
	
	/**
	 * @param techIncome additional income from any tech tracks
	 * @return an array of possible bowl states or null if there are no choices about power income to be made
	 */
	public BowlState[] roundIncome(Vector<Income> techIncome) {
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
	
	public void factionAction(Action a) {
		throw new IllegalStateException("No faction actions defined for " + name);
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
	
	private void scoreEndTurnBonus(EndTurnBonus bonus) {
		if (bonus == null) return;
		int vp = 0;
		switch(bonus) {
		case MINE: vp = mines(); break;
		case TS: vp = ts() * 2; break;
		case LAB: vp = labs() * 3; break;
		case PIA: vp = pia() * 5; break;
		case GAIA: vp = gaias; break;
		case FEDERATION: vp = bank.federations();
		case UNIQUE: vp = uniques; break;
		}
		bank.income(new Income(ResourceType.VP, vp));
	}
	
	public void endTurnBonus(EndTurnBonus booster) {
		scoreEndTurnBonus(booster);
		for (AdvTech t : advtech) scoreEndTurnBonus(t.endturn());
	}
	
	public int ore() {
		return bank.ore();
	}
	
	public int maxBurn() {
		return bank.maxBurn();
	}
	
	public int spendablePower() {
		// TODO: override this for nevlas
		return bank.spendablePower();
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
