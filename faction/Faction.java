package faction;

import java.util.EnumMap;
import java.util.Vector;

import action.Action;
import action.ActionType;
import action.SpecialAction;
import state.AdvTech;
import state.CanResearch;
import state.Coordinates;
import state.EndTurnBonus;
import state.Income;
import state.Map;
import state.PlanetType;
import state.ResourceConversion;
import state.ResourceType;
import state.RoundBooster;
import state.ScienceTrack;
import state.TechTile;

public abstract class Faction {

	public static final int MAXMINES = 8;
	public static final int MAXTS = 4;
	public static final int MAXLABS = 3;
	
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
	protected Income incomeforacademy = new Income(ResourceType.KNOWLEDGE, 2);
	protected SpecialAction specialactionforacademy = SpecialAction.QIC;
	protected SpecialAction specialactionforpi = null;		// special action only unlocked by PI
	protected SpecialAction specialactionforfaction = null;	// faction action always available
	protected SpecialActionDeck specialactions;
	protected ScienceTrack starttrack = null;
	protected String name = null;
	
	protected Bank bank = new Bank();
	protected Coordinates[] mine = new Coordinates[MAXMINES];
	protected int mines = 0;
	protected Coordinates[] ts = new Coordinates[MAXTS];
	protected int traders = 0;
	protected Coordinates[] lab = new Coordinates[MAXLABS];
	protected int labs = 0;
	protected Coordinates[] acad = new Coordinates[2];
	protected Coordinates pi = null;
	protected Coordinates[] gf = new Coordinates[3];
	protected int gaiaformers = 0;
	protected Coordinates lostplanet = null;
	
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
		specialactions = new SpecialActionDeck(player, specialactionforacademy, specialactionforpi, specialactionforfaction);
	}
	
	public static Faction[] choices() {
		return factions;
	}
	
	public boolean buildMine(int col, int row, PlanetType type) {
		Coordinates c = new Coordinates(col, row);
		return buildMine(c, type);
	}

	public boolean buildMine(Coordinates c, PlanetType type) {
		if (mines == MAXMINES) return false;
		mine[mines++] = c;
		if (!colonized.containsKey(type)) {
			uniques++;
			colonized.put(type, true);
		}
		// TODO: move all scoring for 'build a mine' to a single function
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

	public void setPower(BowlState s) {
		bank.setPower(s);
	}
	
	public RoundBooster swapBooster(RoundBooster next) {
		RoundBooster old = booster;
		booster = next;
		return old;
	}
	
	public int mines() {
		return mines + ((lostplanet != null) ? 1 : 0);
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
		if (acad[0] != null) techIncome.add(incomeforacademy);
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
	
	
	protected ResearchKnowledgeSource sourceResearch() {
		int k = bank.knowledge();
		if (k >= ResourceConversion.RESEARCHKNOWLEDGECOST) return ResearchKnowledgeSource.BANK;
		
		int power = (ResourceConversion.RESEARCHKNOWLEDGECOST - k) * ResourceConversion.FREEKNOWLEDGECOST;
		int p = spendablePower();
		if (p >= power) return ResearchKnowledgeSource.BOWL3;
		
		int b = maxBurnIncome();
		if (p + b >= power) return ResearchKnowledgeSource.BOWL2;
			
		return ResearchKnowledgeSource.NONE;
	}
	
	public boolean canResearch(CanResearch canresearch) {
		Vector<Action> actions = researchActions(canresearch);
		return actions.size() > 0;
	}
	
	public boolean canMine() {
		if (mines == MAXMINES) return false;
		return true;
	}

	public boolean canGaiaform(Map map, int baserange, int gaiaformingcost) {
		boolean available = false;
		for (int i=0; i < gaiaformers; i++) if (gf[i].equals(Coordinates.HANGAR)) {
			available = true;
			break;
		}
		if (!available) return false;
		
		Vector<Coordinates> unused = map.unusedTransdims();
		if (unused.size() == 0) return false;
		if (specialactions.rangeAvailable()) baserange += SpecialAction.RANGEINCREASE;
		
		int range = baserange + maxGaiaformingRangeExtension(gaiaformingcost);
		
		for (Coordinates c : unused) if (reachable(map, c, range)) return true;
		
		return false;
	}
	
	/*
	 * checks every possible starting building for reachability to the target
	 * TODO: override for ivits to also start from space stations
	 */
	public boolean reachable(Map map, Coordinates target, int navigation) {
		for (int i=0; i < mines; i++) 
			if (map.range(target, mine[i]) <= navigation) return true;
		for (int i=0; i < traders; i++) 
			if (map.range(target, ts[i]) <= navigation) return true;
		for (int i=0; i < labs; i++) 
			if (map.range(target, lab[i]) <= navigation) return true;
		for (int i=0; i < 2; i++) 
			if ((acad[i] != null) && map.range(target, acad[i]) <= navigation) return true;
		if ((pi != null) && (map.range(target, pi) <= navigation)) return true;
		if ((lostplanet != null) && (map.range(target, lostplanet) <= navigation)) return true;

		return false;
	}
	
	/*
	 * calculates the maximum possible range extension while still retaining the power token + gaiaformer requirement
	 */
	private int maxGaiaformingRangeExtension(int minpowertokens) {
		// TODO: override for gleens, when they can't get QIC for range
		// TODO: override for HH, who can use cash for QIC
		// TODO: override for Baltaks, who can use GFs for QIC (careful to leave one to gaiaform with)
		return bank.maxRangeExtension(minpowertokens);
	}

	/*
	 * returns a vanilla 'start research' action if 4 knowledge in bank
	 * tries to do free actions for K if not
	 */
	public Vector<Action> researchActions(CanResearch canresearch) {
		switch(canresearch) {
		case WITHFED: if (bank.unusedFederations() == 0) return null;
		case YES:
			return researchActions();
		case NO:
		default:
			return null;	
		}
	}

	/*
	 * returns a vanilla 'start research' action if 4 knowledge in bank
	 * tries to do free actions for K if not
	 */
	private Vector<Action> researchActions() {
		Vector<Action> actions = new Vector<Action>();
		
		ResearchKnowledgeSource source = sourceResearch();
		if (source != ResearchKnowledgeSource.NONE) {
			actions.add(new Action(player, ActionType.RESEARCH, source, "Research with knowledge from " + source));
		}
		
		source = sourceFactionKnowledge(source);
		if (source != ResearchKnowledgeSource.NONE) {
			actions.add(new Action(player, ActionType.RESEARCH, source, "Research with knowledge from " + source));
		}
		
 		return actions;
	}
	
	protected ResearchKnowledgeSource sourceFactionKnowledge(ResearchKnowledgeSource basesource) {
		return ResearchKnowledgeSource.NONE;
	}
	
	/*
	 * returns a list of special actions available from tech tiles, faction specials, and academies
	 * excludes range, dig, and research actions; these are triggered by their respective root actions
	 */
	public Vector<Action> specialActions() {
		return specialactions.availableRoots();
	}
	
	public Action[] gaiaBowlActions() {
		// most factions have no action decisions in Gaia phase
		return null;
	}
	
	public void income(Income inc) {
		if (inc.type() == ResourceType.GF) {
			gf[gaiaformers++] = Coordinates.HANGAR;
			return;
		}
		bank.income(inc);
	}
	
	public Vector<Coordinates> usedGaiaformerLocations() {
		Vector<Coordinates> locations = new Vector<Coordinates>();
		for (int i=0; i < gaiaformers; i++)
			if (!gf[i].equals(Coordinates.HANGAR) && !gf[i].equals(Coordinates.GAIABOWL))
				locations.add(gf[i]);
		return locations;
	}
	
	protected void restoreGaiaPower(int power) {
		Income i = new Income(ResourceType.POWER, power);
		income(i);
	}
	
	public int emptyGaiaBowl() {
		int power = bank.emptyGaiaBowl();
		if (power > 0) restoreGaiaPower(power);
		return power;
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
	
	public int qic() {
		return bank.qic();
	}
	
	/*
	 * returns the maximum amount of power that can be loaded into bowl 3 by burning
	 */
	public int maxBurnIncome() {
		return bank.maxBurnIncome();
	}
	
	/*
	 * returns the amount of power currently available in bowl 3 for spending
	 */
	public int spendablePower() {
		return bank.spendablePower();
	}
	
	/*
	 * returns the amount of power available to spend after bowl 2 burning actions
	 */
	public int maxBowl3Spend() {
		return maxBurnIncome() + spendablePower();
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
