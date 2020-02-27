package faction;

import java.util.Vector;

import action.Action;
import action.SpecialAction;

public class SpecialActionDeck {
	
	public static final int ACADEMY = 0;
	public static final int PI = 1;
	public static final int FACTION = 2;
	public static final int CHARGER = 3;
	public static final int BOOSTER = 4;
	public static final int STATICLOCATIONS = 5;
	
	private boolean academybuilt = false;
	private boolean haspiaction = false;
	private boolean pibuilt = false;
	private boolean hasfactionaction = false;
	private boolean hascharger = false;

	// TODO: reset these properly (e.g. PI action made available after PI built, round resets)
	// TODO: make sure all additional actions are set properly as to whether they can be root
	private Action[] action = new Action[STATICLOCATIONS + SpecialAction.ADVTECHSPECIALS];
	private boolean[] available = new boolean[action.length];
	private boolean[] canberoot = new boolean[action.length];

	public SpecialActionDeck(int player, SpecialAction academy, SpecialAction pi, SpecialAction faction) {
		for (int i=0; i < available.length; i++) {
			available[i] = false;
			canberoot[i] = true;
		}
		action[ACADEMY] = academy.action(player);
		if (pi != null) {
			action[PI] = pi.action(player);
			haspiaction = true;
			canberoot[PI] = pi.canberoot();
		}
		if (faction != null) {
			action[FACTION] = faction.action(player);
			hasfactionaction = true;
			available[FACTION] = true;
			canberoot[FACTION] = faction.canberoot();
		}
	}
	
	public void roundReset() {
		available[ACADEMY] = academybuilt;
		available[PI] = haspiaction && pibuilt;
		available[FACTION] = hasfactionaction;
		available[CHARGER] = hascharger;
	}
	
	public void unlockCharger() {
		hascharger = true;
		available[CHARGER] = true;
	}
	
	public void coverCharger() {
		hascharger = false;
		available[CHARGER] = false;
	}
	
	public void buildPI() {
		if (haspiaction) {
			pibuilt = true;
			available[PI] = true;
		}
	}
	
	/*
	 * returns available root actions (which excludes things like dig, fly, and research)
	 */
	public Vector<Action> availableRoots() {
		Vector<Action> choices = new Vector<Action>();
		for (int i=0; i < available.length; i++) {
			if (available[i] && canberoot[i]) 
				choices.add(action[i]);
		}
		return choices;
	}

	public boolean factionAvailable() {
		return available[FACTION];
	}
}
