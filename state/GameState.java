package state;

import java.util.Vector;
import faction.Faction;
import faction.HadschHallas;
import faction.Ivits;
import faction.Taklons;
import faction.Terran;
import faction.Xenos;

public class GameState {

	private Faction[] player;
	private int[] turnorder;
	private int[] passorder = new int[] {-1, -1, -1, -1};
	private int lastpass = 0;
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
	private ActionType lastaction = null;
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
		return this.player[player].placeMine(col, row, pt);
	}
	
	private Action[] factionDraftChoices() {
		Vector<Action> choices = new Vector<Action>();
		for (int i=0; i < factionchoices.length; i++) if (factionchoices[i] != null)
			choices.add(new Action(currentplayer, ActionType.DRAFTFACTION, i, factionchoices[i].name()));
		return choices.toArray(new Action[choices.size()]);
	}
	
	private Action[] planetDraftChoices() {
		Coordinates[] available = map.availablePlanets(player[currentplayer].homePlanet());
		Action[] choices = new Action[available.length];
		int index = 0;
		for (Coordinates c : available)
			choices[index++] = new Action(currentplayer, ActionType.DRAFTPLANET, c, "Claim home planet at " + c);
		return choices;
	}
	
	private Action[] boosterChoices() {
		Action[] choices = new Action[boosters.size()];
		for (int i=0; i < choices.length; i++)
			choices[i] = new Action(currentplayer, ActionType.CHOOSEBOOSTER, i, boosters.get(i).toString());
		return choices;
	}
	
	private Action[] incomeChoices() {
		System.out.println("Collecting income for " + playerDisplayName(currentplayer));
		Vector<Income> income = tech.income(currentplayer);
		bowlstates = player[currentplayer].roundIncome(income);
		if (bowlstates != null) {
			Action[] choices = new Action[bowlstates.length];
			for (int i=0; i < bowlstates.length; i++) {
				choices[i] = new Action(currentplayer, ActionType.POWERINCOME, i, "Charge power to: " + bowlstates[i]);
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
		
		return completeGaiaPhase(true);
	}
	
	private Action[] completeGaiaPhase(boolean returnnext) {
		System.out.println("Completed gaia phase for " + playerDisplayName(currentplayer));
		currentplayer++;
		
		// if no gaia choices, call again with next player (or wait for next call, depending)
		if (currentplayer < player.length) {
			if (returnnext) return gaiaChoices();
			return null;
		}

		// if no gaia decisions for last player, move to action phase
		currentplayer = 0;
		state = State.ACTIONS;
		return (returnnext) ? actionChoices() : null;
	}
	
	private Action[] actionChoices() {
		Vector<Action> choices = new Vector<Action>();
		
		
		// pass options
		if (round == 5) choices.add(new Action(turnorder[currentplayer], ActionType.PASS, null, "Pass"));
		else {
			for (int i=0; i < 3; i++) {
				RoundBooster booster = boosters.get(i);
				choices.add(new Action(turnorder[currentplayer], ActionType.PASS, i, "Pass, take booster " + booster));
			}
		}
		return choices.toArray(new Action[choices.size()]);
	}
	
	private boolean draft(Action a) {
		int player = a.player();
		int i = (int)a.info();

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
		f.init(player);

		for (int j=0; j < factionchoices.length; j++) {
			if ((factionchoices[j] != null) && (factionchoices[j].homePlanet() == type))
					factionchoices[j] = null;
		}
		
		System.out.println((player + 1) + " drafted " + f.name());
		
		ScienceTrack start = f.startTrack();
		if (start != null) {
			Vector<Income> income = tech.progress(player, start);
			System.out.println(playerDisplayName(player) + " starts up " + start + " track");
			for (Income inc : income) {
				System.out.println(playerDisplayName(player) + " receives " + inc);
				f.income(inc);
			}
		}
		
		currentplayer++;
		if (currentplayer == this.player.length) {
			currentplayer = 0;
			if (this.player[0] instanceof Ivits) currentplayer++;
			state = State.PLANETDRAFT;
		}
		return true;
	}
	
	private boolean planetDraft(Action a) {
		int player = a.player();
		Coordinates c = (Coordinates)a.info();

		Faction f = this.player[player];
		if (f instanceof Ivits) {
			if (draft == -2) {
				Ivits i = (Ivits)f;
				i.draftPI(c);
				System.out.println(playerDisplayName(player) + " drafted a PI on home planet at " + c);
			} else throw new IllegalStateException("Cannot draft with Ivits yet - other players are not finished drafting");
		} else {
			if (!f.placeMine(c, f.homePlanet()))
				throw new IllegalStateException("No mines left to place for " + playerDisplayName(player));
			map.build(c, f, Building.MINE);
			System.out.println(playerDisplayName(player) + " drafted a mine on home planet at " + c);
		}

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
			for (int i=0; i < this.player.length; i++) if (this.player[i] instanceof Ivits) {
				currentplayer = i;
				draft = -2;
				break;
			}
		}
		if (currentplayer < 0) {
			currentplayer = this.player.length - 1;
			state = State.BOOSTDRAFT;
		}
		return true;
	}
	
	private boolean chooseBooster(Action a) {
		int player = a.player();
		int i = (int)a.info();

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
	
	private boolean powerIncome(Action a) {
		int player = a.player();
		int i = (int)a.info();

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
	
	private boolean pass(Action a) {
		int player = a.player();
		
		RoundBooster next = null;
		Object o = a.info();
		if (o != null) {
			int i = 0;
			if (o instanceof Integer) i = (int)o;
			else throw new IllegalArgumentException("Expected integer argument; received "
				 + a.info().getClass().getName());
			if ((i < 0) || (i > 2))
				throw new IllegalArgumentException("Invalid booster choice: " + i);
			next = boosters.remove(i);
		}
		RoundBooster current = this.player[player].swapBooster(next);
		boosters.add(current);
		
		EndTurnBonus b = current.bonus();
		this.player[player].endTurnBonus(b);
		
		// TODO: trade in unused gaiaformers for ballsacks first
		// TODO: downconvert resources if cap will be reached during income phase (hadsch hallas, mostly)
		// maaaaaaaaaybe an edge case where science or ore should be converted to cash instead of being lost
		// can do math on it to see if it would happen
		
		passorder[lastpass] = player;
		return false;
	}
	
	private Action[] orePowerChoices() {
		int p = turnorder[currentplayer];
		int ore = player[p].ore();
		Action[] action = new Action[ore+1];
		for (int i=0; i <= ore; i++)
			action[i] = new Action(p, ActionType.FREEOREPOWER, i, (i == 0) ? "Skip" : "Convert " + i + " ore to power");
		return action;
	}
	
	private Action[] burnPowerChoices() {
		int p = turnorder[currentplayer];
		int b2 = player[p].maxBurn();
		Action[] action = new Action[b2+1];
		for (int i=0; i <= b2; i++)
			action[i] = new Action(p, ActionType.FREEBURNPOWER, i, (i == 0) ? "Skip" : "Burn " + i + " power");
		return action;
	}
	
	/**
	 * @return a set of final free actions, including doing nothing. final free actions
	 * can be repeated until nothing is chosen - this is better suited to MCTS since
	 * most of the time the best option is to not take any inefficient power spends or
	 * other free actions unless they're in direct service of a useful action
	 */
	private Action[] freeFinalActionChoices() {
		// TODO: override this for nevlas?
		int p = turnorder[currentplayer];
		int b3 = player[p].spendablePower();
		return ResourceConversion.actionOptions(p, b3, true);
	}
	
	public Action[] getActionChoices() {
		switch (state) {
		case FACTIONDRAFT: return factionDraftChoices();
		case PLANETDRAFT: return planetDraftChoices();
		case BOOSTDRAFT: return boosterChoices();
		case INCOME: return incomeChoices();
		case ACTIONS: return actionChoices();
		case FREEOREPOWER: return orePowerChoices();
		case FREEBURNPOWER: return burnPowerChoices();
		case FREEFINALACTION: return freeFinalActionChoices();
		}
		throw new IllegalStateException("Couldn't determine available actions from state\n" + this.toString());
	}
	
	public void takeAction(Action a) {
		// TODO: confirm other actions don't rely on turnorder instead of player order
		int p = (state == State.ACTIONS) ? turnorder[currentplayer] : currentplayer;
		if (p != a.player())
			throw new IllegalStateException("Player " + (a.player() + 1) + " can't " + a.type() + " ; it's player " +
					(p + 1) + "'s turn");
		ActionType t = a.type();
		State s = t.validState();
		if ((state != s) && (s != null))
			throw new IllegalStateException("Could not " + t + "; current state is " + state);
		Object o = a.info();
		if ((o != null) && !o.getClass().equals(t.expectedParameterClass()))
			throw new IllegalArgumentException("Expected " + t.expectedParameterClass().getSimpleName() +
					" argument; received " + a.info().getClass().getName());
		
		actionlist.add(a);
		boolean stateupdated = false;
		switch (t) {
		case DRAFTFACTION: stateupdated = draft(a); break;
		case DRAFTPLANET: stateupdated = planetDraft(a); break;
		case CHOOSEBOOSTER: stateupdated = chooseBooster(a); break;
		case POWERINCOME: stateupdated = powerIncome(a); break;
		case RESOURCETRADE:
			player[currentplayer].factionAction(a);
			completeGaiaPhase(false);
			stateupdated = true;
			break;
		case PASS: stateupdated = pass(a); break;
		default: throw new IllegalArgumentException("No handler specified for action " + a);
		}
		if (stateupdated) return;
		lastaction = t;
		transitionState(p);
	}
	
	private void transitionState(int player) {
		// any action phase action should allow free power action choices after completing; that said, we should only allow
		// 			this after an action that results in power charge, which is either crossing lv3 science or using tech tile special
		// independently select: convert ore to power, burn power, spend power
		
		// TODO: support nevlas free actions (knowledge to gaia bowl, power actions worth 2 maybe)
		// TODO: support taklons brainstone burn/spend
		if (state == State.ACTIONS) {
			// TODO: if last round, don't take certain free actions after passing (maximize use of resources)
			// done: using ore for power
			state = State.FREEOREPOWER;
			if ((this.player[player].ore() == 0) || ((round == 5) && (lastaction == ActionType.PASS)))
				transitionState(player);
		} else if (state == State.FREEOREPOWER) {
			state = State.FREEBURNPOWER;
			if (this.player[player].maxBurn() == 0) transitionState(player);
		} else if (state == State.FREEBURNPOWER) {
			state = State.FREEFINALACTION;
			if (this.player[player].spendablePower() == 0) transitionState(player);
		} else if (state == State.FREEFINALACTION) {
			state = State.ACTIONS;
			if (lastaction == ActionType.PASS)
				turnorder[currentplayer] = -1;
			for (int i=1; i < turnorder.length; i++) {
				currentplayer++;
				if (currentplayer == turnorder.length) currentplayer = 0;
				if (turnorder[currentplayer] != -1) return;
			}
			// everyone has passed
			state = State.CLEANUP;
		} else throw new IllegalStateException("Tried to transition unknown state " + state);
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
