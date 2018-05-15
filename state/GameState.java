package state;
import java.util.Vector;

import faction.Faction;
import faction.HadschHallas;
import faction.Taklons;
import faction.Terran;
import faction.Xenos;

public class GameState {

	private Faction[] player;
	private int[] turnorder;
	private int[] passorder;
	private Map map;
	private RoundScore[] roundscore;
	private FinalScore[] victoryconditions;
	private int[][] finalscore;
	private Vector<RoundBooster> boosters;
	private Research tech;

	private int currentplayer = 0;
	private int round = -1;
	private int draft = 1;
	private State state = State.FACTIONDRAFT;
	private BowlState[] bowlstates;
	
	private Vector<Action> actionlist = new Vector<Action>();
	
	private Faction[] factionchoices = {
			new HadschHallas(), new Taklons(), new Terran(), new Xenos()
	};
	
	public GameState(int players, Map map, RoundScore[] roundscore, FinalScore[] victoryconditions,
			Vector<RoundBooster> boosters, Research tech) {
		player = new Faction[players];
		turnorder = new int[players];
		passorder = new int[players];
		this.finalscore = new int[victoryconditions.length][players];
		this.map = map;
		this.roundscore = roundscore;
		this.victoryconditions = victoryconditions;
		this.boosters = boosters;
		this.tech = tech;
	}
	
	public boolean placeMine(int col, int row, int player) {
		PlanetType pt = this.player[player].homePlanet();
		if (pt != map.get(col, row))
			throw new IllegalArgumentException("Must start on home planet; [" + col + "," + row + "] isn't " + pt);
		return this.player[player].placeMine(col, row);
	}
	
	private Action[] factionDraftChoices() {
		Vector<Action> choices = new Vector<Action>();
		for (int i=0; i < factionchoices.length; i++) if (factionchoices[i] != null)
			choices.add(new Action(currentplayer, ActionType.DRAFT, i, factionchoices[i].name()));
		return choices.toArray(new Action[choices.size()]);
	}
	
	private Action[] planetDraftChoices() {
		Coordinates[] available = map.availablePlanets(player[currentplayer].homePlanet());
		Action[] choices = new Action[available.length];
		int index = 0;
		for (Coordinates c : available)
			choices[index++] = new Action(currentplayer, ActionType.HOMEPLANET, c, "Claim home planet at " + c);
		return choices;
	}
	
	private Action[] boosterChoices() {
		Action[] choices = new Action[boosters.size()];
		for (int i=0; i < choices.length; i++)
			choices[i] = new Action(currentplayer, ActionType.CHOOSEBONUS, i, boosters.get(i).toString());
		return choices;
	}
	
	private Action[] incomeChoices() {
		System.out.println("Collecting income for " + playerDisplayName(currentplayer));
		Vector<Income> income = tech.income(currentplayer);
		bowlstates = player[currentplayer].roundIncome(income);
		if (bowlstates != null) {
			Action[] choices = new Action[bowlstates.length];
			for (int i=0; i < bowlstates.length; i++) {
				choices[i] = new Action(currentplayer, ActionType.CHARGEPOWER, i, "Charge power to: " + bowlstates[i]);
			}
			return choices;
		}
		// if no income choices, call again with next player
		currentplayer++;
		if (currentplayer < player.length) return incomeChoices();

		// if no income decisions for any player, move to gaia phase
		currentplayer = 0;
		state = State.GAIA;
		return gaiaChoices();
	}
	
	private Action[] gaiaChoices() {
		Faction f = player[currentplayer];
		Vector<Coordinates> gfs = f.gaiaformerLocations();
		int size = gfs.size();
		for (int i=0; i < size; i++) {
			Coordinates c = gfs.get(i);
			if (map.get(c) == PlanetType.TRANSDIM) {
				map.gaiaform(c);
				System.out.println(playerDisplayName(currentplayer) + " gaiaformed at " + c);
			}
		}
		Action[] ga = f.gaiaBowlActions();
		if (ga != null) return ga;
		
		System.out.println("Completed gaia phase for " + playerDisplayName(currentplayer));
		currentplayer++;
		
		// if no gaia choices, call again with next player
		if (currentplayer < player.length) return gaiaChoices();

		// if no gaia decisions for last player, move to action phase
		currentplayer = 0;
		state = State.ACTIONS;
		return actionChoices();
	}
	
	private Action[] actionChoices() {
		return null;
	}
	
	private boolean draft(Action a) {
		int player = a.player();
		if (state != State.FACTIONDRAFT)
			throw new IllegalStateException("Could not draft faction; current state is " + state);
		int i = 0;
		if (a.info() instanceof Integer) i = (int)a.info();
		else throw new IllegalArgumentException("Expected integer argument; received " + a.info().getClass().getName());

		try {
			if (factionchoices[i] == null)
				throw new IllegalStateException("Faction is unavailable to draft for player " + playerDisplayName(player));
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Unavailable faction index: " + i + " is out of bounds");
		}
		
		Faction f = factionchoices[i];
		factionchoices[i] = null;
		
		this.player[player] = f;
		PlanetType type = f.homePlanet();

		for (int j=0; j < factionchoices.length; j++) {
			if ((factionchoices[j] != null) && (factionchoices[j].homePlanet() == type))
					factionchoices[j] = null;
		}
		
		System.out.println(playerDisplayName(player) + " drafted " + f.name());
		
		ScienceTrack start = f.startTrack();
		if (start != null) {
			Vector<Income> income = tech.progress(player, start);
			System.out.println(playerDisplayName(player) + " starts up " + start + " track");
			for (Income inc : income) {
				System.out.println(playerDisplayName(player) + " receives " + inc);
				this.player[player].income(inc);
			}
		}
		
		currentplayer++;
		if (currentplayer == this.player.length) {
			currentplayer = 0;
			state = State.PLANETDRAFT;
		}
		return true;
	}
	
	private boolean planetDraft(Action a) {
		int player = a.player();
		if (state != State.PLANETDRAFT)
			throw new IllegalStateException("Could not draft home planet; current state is " + state);
		Coordinates c = null;
		if (a.info() instanceof Coordinates) c = (Coordinates)a.info();
		else throw new IllegalArgumentException("Expected coordinates; received " + a.info().getClass().getName());

		// TODO: place PI for ivits instead, only if draft state is -2
		Faction f = this.player[player];
		if (!f.placeMine(c))
			throw new IllegalStateException("No mines left to place for " + playerDisplayName(player));
		map.build(c, f, Building.MINE);
		
		System.out.println(playerDisplayName(player) + " drafted home planet at " + c);

		// TODO: make Ivits go last
		if ((f instanceof Xenos) && (f.mines() == 3)) {
			currentplayer = -2;
		} else {
			currentplayer += draft;
		}
		if (currentplayer == this.player.length) {
			draft = -1;
			currentplayer--;
		} else if (currentplayer == -1) {
			for (int i=0; i < this.player.length; i++) if (this.player[i] instanceof Xenos) {
				currentplayer = i;
				break;
			}
			if (currentplayer == -1) currentplayer = -2;
		}
		if (currentplayer == -2) {
			// check for ivits in game and play them last if necessary
			draft = -2;
		}
		if (currentplayer < 0) {
			currentplayer = this.player.length - 1;
			state = State.BOOSTDRAFT;
		}
		return true;
	}
	
	private boolean chooseBooster(Action a) {
		int player = a.player();
		if (state != State.BOOSTDRAFT)
			throw new IllegalStateException("Could not draft booster; current state is " + state);
		int i = 0;
		if (a.info() instanceof Integer) i = (int)a.info();
		else throw new IllegalArgumentException("Expected integer argument; received " + a.info().getClass().getName());

		if ((i < 0) || (i >= boosters.size()))
			throw new IllegalArgumentException("Unavailable booster index: " + i + " is out of bounds");
		
		Faction f = this.player[player];
		RoundBooster b = boosters.remove(i);
		f.swapBooster(b);
		
		System.out.println(playerDisplayName(player) + " drafted " + b);
		
		currentplayer--;
		if (currentplayer == -1) {
			currentplayer = 0;
			round = 0;
			state = State.INCOME;
		}
		return true;
	}
	
	private boolean chargePower(Action a) {
		int player = a.player();
		if (state != State.INCOME)
			throw new IllegalStateException("Could not charge power; current state is " + state);
		int i = 0;
		if (a.info() instanceof Integer) i = (int)a.info();
		else throw new IllegalArgumentException("Expected integer argument; received " + a.info().getClass().getName());

		if ((i < 0) || (i >= bowlstates.length))
			throw new IllegalArgumentException("Unavailable power charge choice index: " + i + " is out of bounds");
		
		Faction f = this.player[player];
		f.setPower(bowlstates[i]);
		
		System.out.println(playerDisplayName(player) + " charged power to: " + bowlstates[i]);
		
		currentplayer++;
		if (currentplayer == this.player.length) {
			currentplayer = 0;
			state = State.GAIA;
		}
		return true;
	}
	
	public Action[] getActionChoices() {
		switch (state) {
		case FACTIONDRAFT: return factionDraftChoices();
		case PLANETDRAFT: return planetDraftChoices();
		case BOOSTDRAFT: return boosterChoices();
		case INCOME: return incomeChoices();
		}
		throw new IllegalStateException("Couldn't determine available actions from state\n" + this.toString());
	}
	
	public boolean takeAction(Action a) {
		if (currentplayer != a.player())
			throw new IllegalStateException("Player " + (a.player() + 1) + " can't " + a.type() + " ; it's player " +
					(currentplayer + 1) + "'s turn");
		actionlist.add(a);
		switch (a.type()) {
		case DRAFT: return draft(a);
		case HOMEPLANET: return planetDraft(a);
		case CHOOSEBONUS: return chooseBooster(a);
		case CHARGEPOWER: return chargePower(a);
		}
		throw new IllegalArgumentException("No handler specified for action " + a);
	}
	
	public String playerDisplayName(int player) {
		Faction f = this.player[player];
		if (f == null) return "Player " + (player + 1);
		return f.name() + "[" + (player + 1) + "]";
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(map);
		if (round < 0) sb.append("*" + state + " ");
		for (int i=0; i < roundscore.length; i++) {
			sb.append((i+1) + ":" + roundscore[i]);
			if (i == round) sb.append("*");
			sb.append(" ");
		}
		sb.append("[");
		for (int i=0; i < victoryconditions.length; i++)
			sb.append(victoryconditions[i] + " ");
		sb.append("]\nBoosters: ");
		for (RoundBooster b : boosters) sb.append(b + " ");
		sb.append("\n");
		sb.append(tech);
		for (int i=0; i < player.length; i++) if (player[i] != null) sb.append(player[i]);
		return sb.toString();
	}
}
