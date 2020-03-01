package state;

import java.util.Vector;

import action.*;
import faction.*;

public class GameState {

	private Faction[] player;
	private PlayerOrder playerorder;
	private Map map;
	private RoundScore[] roundscore;
	private FinalScore[] victoryconditions;
	private int[][] finalscore;
	private Vector<RoundBooster> boosters;
	private Research research;
	private PowerQICShop shop = new PowerQICShop();

	private int round = -1;
	private State state = null;
	private State exitstate = null;			// tracks the state to exit to once the current state is done
	private ActionType lastaction = null;
	private BowlState[] bowlstates;
	
	// TODO: make a harness that replays an entire game from an action log
	private Vector<Action> actionlist = new Vector<Action>();
	
	/*
	 * initialize from pre-defined board conditions
	 */
	public GameState(int players, Map map, RoundScore[] roundscore, FinalScore[] victoryconditions,
			Vector<RoundBooster> boosters, Research research, boolean clockwise) {
		player = new Faction[players];
		this.playerorder = new PlayerOrder(clockwise, players);
		this.finalscore = new int[victoryconditions.length][players];
		this.map = map;
		this.roundscore = roundscore;
		this.victoryconditions = victoryconditions;
		this.boosters = boosters;
		this.research = research;
		
		state = State.FACTIONDRAFT;
		playerorder.startNormalDraft();
	}
	
	/*
	 * choose game options instead of starting from pre-defined board
	 */
	public GameState(int players, boolean clockwise) {
		// TODO: support variable board setup
		state = State.GAMECONDITIONS;
	}
	
	public Action[] getChoices() {
		// loop invariant: game state must always be processed until the current state has an active player decision
		//			basically, the game 'pauses' for a player decision and auto-processes any state where no decision
		//			is needed (or a player could make a decision, but it's an objectively bad one)
		
		// TODO: lost planet is placed and additional leech is decided and taken AFTER any leech is decided from RL or A upgrade
		
		switch (state) {
		case FACTIONDRAFT: return factionDraftChoices();
		case PLANETDRAFT:
		case DRAFTXENOS:
		case DRAFTIVITS:
			return planetDraftChoices();
		case CHOOSEBOOSTER: return boosterChoices();
		case INCOME: return incomeChoices();
		case ITARSGAIA:
		case TERRANGAIA:
		case GAIA:
			return gaiaChoices();
		case STARTACTION: return actionChoices();
		}
		throw new IllegalStateException("Couldn't determine available actions from state\n" + this.toString());
	}
	
	// TODO: support a different method signature which gives an index into a set of choices for the player
	//			as a result of the getActionChoices() method, which does NO legality checking, to speed up MCTS
	//			accepting an arbitrary Action should do all the legality checks in order to validate move histories or to be used on server
	
	public void takeAction(Action a) {
		int p = playerorder.currentPlayer();
		if (p != a.player())
			throw new IllegalStateException("Player " + (a.player() + 1) + " can't " + a.type() + " ; it's player " +
					(p + 1) + "'s turn");
		ActionType t = a.type();
		if (!t.validState(state))
			throw new IllegalStateException("Could not " + t + "; current state is " + state);
		Object o = a.info();
		if ((o != null) && !o.getClass().equals(t.expectedParameterClass()))
			throw new IllegalArgumentException("Expected " + t.expectedParameterClass().getSimpleName() +
					" argument; received " + a.info().getClass().getName());
		
		// TODO: make sure that any forced decisions also get added to the action log
		actionlist.add(a);
		switch (t) {
		case DRAFTFACTION: draftFaction(a); break;
		case DRAFTPLANET: planetDraft(a); break;
		case CHOOSEBOOSTER: chooseBooster(a); break;
		case POWERINCOME:  powerIncome(a); break;
		case RESOURCETRADE: player[p].factionAction(a); break;
		case PASS: pass(a); break;
		default: throw new IllegalArgumentException("No handler specified for action " + a);
		}
	}
	
	// TODO: switch to using board coordinate system, using coordinate toString for col, row with letter, number
	private void placeMine(int col, int row, int player) {
		PlanetType pt = this.player[player].homePlanet();
		if (pt != map.get(col, row))
			throw new IllegalArgumentException("Must start on home planet; [" + col + "," + row + "] isn't " + pt);
		if (!this.player[player].buildMine(col, row, pt)) 
			throw new IllegalArgumentException("Couldn't place mine at [" + col + "," + row + "]");
	}
	
	private Action[] factionDraftChoices() {
		Vector<Action> choices = new Vector<Action>();
		Faction[] factionchoices = Faction.choices();
		for (int i=0; i < factionchoices.length; i++) if (factionchoices[i] != null)
			choices.add(new Action(playerorder.currentPlayer(), ActionType.DRAFTFACTION, i, factionchoices[i].name()));
		return choices.toArray(new Action[choices.size()]);
	}
	
	private Action[] planetDraftChoices() {
		int currentplayer = playerorder.currentPlayer();
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
			choices[i] = new Action(playerorder.currentPlayer(), ActionType.CHOOSEBOOSTER, i, boosters.get(i).toString());
		return choices;
	}
	
	private Action[] incomeChoices() {
		int currentplayer = playerorder.currentPlayer();
		System.out.println("Collecting income for " + playerDisplayName(currentplayer));
		Vector<Income> income = research.income(currentplayer);
		bowlstates = player[currentplayer].roundIncome(income);
		if (bowlstates != null) {
			Action[] choices = new Action[bowlstates.length];
			for (int i=0; i < bowlstates.length; i++) {
				choices[i] = new Action(currentplayer, ActionType.POWERINCOME, i, "Charge power to: " + bowlstates[i]);
			}
			return choices;
		}
		// if no income choices, call again with next player
		int next = playerorder.nextPlayer();
		if (next != -1) return incomeChoices();

		// if no income decisions remain for any player, move to gaia phase
		setNextGaiaPhaseState();
		return gaiaChoices();
	}
	
	private void setNextGaiaPhaseState() {
		if (state == State.INCOME) {
			state = State.ITARSGAIA;
			for (int i=0; i < this.player.length; i++) if (this.player[i] instanceof Itars) {
				playerorder.setNext(i);
				return;
			}
		}
		if (state == State.ITARSGAIA) {
			state = State.TERRANGAIA;
			for (int i=0; i < this.player.length; i++) if (this.player[i] instanceof Terran) {
				playerorder.setNext(i);
				return;
			}
		}
		if (state == State.TERRANGAIA) {
			state = state.GAIA;
			playerorder.startNormalDraft();
			return;
		}
		throw new IllegalStateException("Cannot set the next Gaia phase state starting from state " + state);
	}
	
	private Action[] gaiaChoices() {
		int currentplayer = playerorder.currentPlayer();
		Faction f = player[currentplayer];
		
		if (state == State.ITARSGAIA) {
			if (!(f instanceof Itars)) throw new IllegalStateException("Only Itars may make decisions during the Itars Gaia phase");
			Action[] choices = f.gaiaBowlActions();
			if (choices != null) return choices;
			setNextGaiaPhaseState();
			return gaiaChoices();
		}
		
		if (state == State.TERRANGAIA) {
			if (!(f instanceof Terran)) throw new IllegalStateException("Only Terrans may make decisions during the Terran Gaia phase");
			Action[] choices = f.gaiaBowlActions();
			if (choices != null) return choices;
			setNextGaiaPhaseState();
			return gaiaChoices();
		}
		
		Vector<Coordinates> gfs = f.usedGaiaformerLocations();
		int size = gfs.size();
		for (int i=0; i < size; i++) {
			Coordinates c = gfs.get(i);
			if (map.get(c) == PlanetType.TRANSDIM) {
				map.gaiaformComplete(c);
				System.out.println(playerDisplayName(currentplayer) + " gaiaformed at " + c);
			}
		}

		int power = f.emptyGaiaBowl();
		if (power > 0) System.out.println(playerDisplayName(currentplayer) + " emptied " + power + " power from the Gaia bowl");
		
		if (playerorder.nextPlayer() != -1) return gaiaChoices();
		
		completeGaiaPhase();
		
		return actionChoices();
	}
	
	private void completeGaiaPhase() {
		state = State.STARTACTION;
		playerorder.startNormalPlay();
	}
	
	private Action[] actionChoices() {
		// any action phase action should allow free power-related action choices AFTER completing to groom the state of the power bowls;
		//			that said, we should only allow this after an action that results in power charge, which is either crossing
		//			lv3 science or using tech tile special OR buying power tokens, which rules in QIC- and ore-earning actions, too
		// all other FREE actions should not be allowed after a main action because they are without loss of generality able to be done
		// 			on the following turn, and branching MCTS twice for the same free action post- and pre-turn is super wasteful
		// to prune further, pre-action free actions should never be done unless in order to enable the action on that turn
		// OK, scratch all of the above; with BOTH pruning approaches in place, genuinely useful free actions combos are being discarded
		//			the REAL simplification should be: only do free actions that enable the main action BEFORE the action, then do
		//			resource-management actions only AFTER the turn (to groom bowl state for leech or keep from overcapping on income)
		//			EXCEPTION: Nevlas may want to combine action-enabling free actions and resource management actions together (because of odd numbers)
		//				but this can be solved by just making any ore-purchasing free actions into only ore + credit or 2-ore actions
		
		
		// independently select: convert ore to power, burn power, spend power
		
		// TODO: support nevlas free actions (knowledge to gaia bowl, power actions worth 2 maybe)
		// TODO: support taklons brainstone burn/spend
		
		int currentplayer = playerorder.currentPlayer();
		Faction f = this.player[currentplayer];
		Vector<Action> choices = new Vector<Action>();
		
		// 1. build a mine
		
		
		// 2. gaiaform
		if (f.canGaiaform(map, research.navigationRange(currentplayer), research.gaiaformingCost(currentplayer)))
			choices.add(new Action(currentplayer, ActionType.GAIAFORM, null, "Place GaiaFormer"));
		
		// 3. upgrade
		
		// 4. federate
		
		// 5. research
		if (f.canResearch(research.canResearch(currentplayer)))
				choices.add(new Action(currentplayer, ActionType.RESEARCH, null, "Research"));
		
		// 6. power/QIC actions
		choices.addAll(powerQICchoices(currentplayer, f));
		
		// 7. special actions
		choices.addAll(f.specialActions());
		
		// 8. pass
		choices.add(new Action(currentplayer, ActionType.PASS, null, "Pass"));
		
		return choices.toArray(new Action[choices.size()]);
	}
	
	private Vector<Action> powerQICchoices(int player, Faction currentfaction) {
		Vector<Action> choices = new Vector<Action>();
		
		int power = currentfaction.maxBowl3Spend();
		if (shop.powerShopAvailableFor(power)) choices.add(new Action(player, ActionType.POWERSHOP, null, "Buy resources from power shop"));
		
		// Dig actions are available to purchase using the 'build a mine' action 
		
		int qic = currentfaction.qic();
		if (shop.qicShopAvailableFor(qic)) choices.add(new Action(player, ActionType.QICSHOP, null, "Buy from QIC shop"));
		
		return choices;
	}
	
	private void draftFaction(Action a) {
		int player = a.player();
		int i = (int)a.info();
		Faction[] factionchoices = Faction.choices();

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
			Vector<Income> income = research.progress(player, start);
			System.out.println(playerDisplayName(player) + " starts up " + start + " track");
			for (Income inc : income) {
				System.out.println(playerDisplayName(player) + " receives " + inc);
				f.income(inc);
			}
		}
		
		int next = playerorder.nextPlayer();
		if (next == -1) startPlanetDraft();
	}
	
	private void startPlanetDraft() {
		state = State.PLANETDRAFT;
		playerorder.startSnakeDraft();
		int player = playerorder.currentPlayer();
		// ivits don't draft during initial draft phase
		if (this.player[player] instanceof Ivits) playerorder.nextPlayer();
	}
	
	private void planetDraft(Action a) {
		int player = a.player();
		Coordinates c = (Coordinates)a.info();

		Faction f = this.player[player];
		if (f instanceof Ivits) {
			if (state != State.DRAFTIVITS) throw new IllegalStateException("Cannot draft with Ivits yet - other players are not finished drafting");
			((Ivits)f).draftPI(c);
			map.build(c, f, Building.PI);
			System.out.println(playerDisplayName(player) + " drafted a PI on home planet at " + c);
			startBoosterDraft();
			return;
		}
		
		if (!f.buildMine(c, f.homePlanet()))
			throw new IllegalStateException("No mines left to place for " + playerDisplayName(player));
		map.build(c, f, Building.MINE);
		System.out.println(playerDisplayName(player) + " drafted a mine on home planet at " + c);
		
		if (state == State.DRAFTXENOS) {
			setNextPlanetDraftState();
			return;
		}
		
		int next = playerorder.nextPlayer();
		if (this.player[next] instanceof Ivits) next = playerorder.nextPlayer();
		if (playerorder.nextPlayer() == -1) setNextPlanetDraftState();
		
	}
	
	private void setNextPlanetDraftState() {
		if (state == State.PLANETDRAFT) {
			state = State.DRAFTXENOS;
			for (int i=0; i < this.player.length; i++) if (this.player[i] instanceof Xenos) {
				playerorder.setNext(i);
				return;
			}
		}
		if (state == State.DRAFTXENOS) {
			for (int i=0; i < this.player.length; i++) if (this.player[i] instanceof Ivits) {
				state = State.DRAFTIVITS;
				playerorder.setNext(i);
				return;
			}
			startBoosterDraft();
			return;
		}
		throw new IllegalStateException("Cannot set the next planet draft state when the current state is " + state);
	}
	
	private void startBoosterDraft() {
		state = State.CHOOSEBOOSTER;
		exitstate = State.INCOME;
		playerorder.startReverseDraft();
	}
	
	private void chooseBooster(Action a) {
		int player = a.player();
		int i = (int)a.info();

		if ((i < 0) || (i >= boosters.size()))
			throw new IllegalArgumentException("Unavailable booster index: " + i + " is out of bounds");
		
		Faction f = this.player[player];
		RoundBooster b = boosters.remove(i);
		RoundBooster old = f.swapBooster(b);
		
		if (old == null) System.out.println(playerDisplayName(player) + " drafted " + b);
		else System.out.println(playerDisplayName(player) + " exchanged " + old + " for " + b);
		
		if (exitstate == State.INCOME) {
			// this should only happen at the end of pre-game booster drafting
			if (playerorder.nextPlayer() == -1) startRound();
			return;
		} else {
			// after picking a booster when passing, proceed to end-turn free actions
			// (booster selection won't happen on round 6, so no need to check for endgame here)
			state = State.ENDTURN;
		}
	}
	
	private void startRound() {
		// TODO: this probably generalizes to all rounds, but confirm how it's used
		round++;
		state = State.INCOME;
		playerorder.startNormalDraft();
	}
	
	private void powerIncome(Action a) {
		int player = a.player();
		int i = (int)a.info();

		if ((i < 0) || (i >= bowlstates.length))
			throw new IllegalArgumentException("Unavailable power charge choice index: " + i + " is out of bounds");
		
		Faction f = this.player[player];
		f.setPower(bowlstates[i]);
		
		System.out.println(playerDisplayName(player) + " charged power to: " + bowlstates[i]);

		int next = playerorder.nextPlayer();
		if (next != -1) return;

		// if no income decisions remain for any player, move to gaia phase
		setNextGaiaPhaseState();
	}
	
	private void pass(Action a) {
		// TODO: collect bonuses for passing
		playerorder.pass();
		if (round == 5) {
			state = State.ENDTURN;
		} else {
			state = State.CHOOSEBOOSTER;
			exitstate = State.ENDTURN;
		}
	}
	
	/**
	 * @return a set of final free actions, including doing nothing. final free actions
	 * can be repeated until nothing is chosen - this is better suited to MCTS since
	 * most of the time the best option is to not take any inefficient power spends or
	 * other free actions unless they're in direct service of a useful action
	 */
	private Action[] freeFinalActionChoices() {
		// TODO: start by offering to convert things at the top of the conversion chain, down to the bottom
		// TODO: trade in unused gaiaformers for ballsacks first
		// TODO: downconvert resources if cap will be reached during income phase (hadsch hallas, mostly)
		// maaaaaaaaaybe an edge case where science or ore should be converted to cash instead of being lost
		// can do math on it to see if it would happen
		int p = playerorder.currentPlayer();
		int b3 = player[p].spendablePower();
		return ResourceConversion.actionOptions(p, b3, true);
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
		sb.append(research);
		for (int i=0; i < player.length; i++) if (player[i] != null) sb.append(player[i]);
		return sb.toString();
	}
}
