package state;

import action.SpecialAction;

public enum AdvTech {
	
	FEDTURN(null, EndTurnBonus.FEDERATION, null, null),
	SCIENCE(null, null, ActionBonus.SCIENCE, null),
	QICCASH(null, null, null, SpecialAction.QICCASH),
	MINES(OneTimeBonus.MINES, null, null, null),
	LABTURN(null, EndTurnBonus.LAB, null, null),
	SECTORE(OneTimeBonus.SECTORE, null, null, null),
	UNIQUET(null, EndTurnBonus.UNIQUE, null, null),
	GAIAONE(OneTimeBonus.GAIA, null, null, null),
	TSONE(OneTimeBonus.TS, null, null, null),
	SECTVP(OneTimeBonus.SECTVP, null, null, null),
	ACTORE(null, null, null, SpecialAction.ORE),
	FEDONE(OneTimeBonus.FEDS, null, null, null),
	ACTKNOW(null, null, null, SpecialAction.KNOWLEDGE),
	MINEACT(null, null, ActionBonus.MINING, null),
	TSACT(null, null, ActionBonus.TRADING, null),
	;
	
	private final OneTimeBonus onetime;
	private final EndTurnBonus endturn;
	private final ActionBonus action;
	private final SpecialAction special;
	
	AdvTech(OneTimeBonus onetime, EndTurnBonus endturn, ActionBonus action, SpecialAction special) {
		this.onetime = onetime;
		this.endturn = endturn;
		this.action = action;
		this.special = special;
	}
	
	public OneTimeBonus onetime() {
		return onetime;
	}
	
	public EndTurnBonus endturn() {
		return endturn;
	}
	
	public ActionBonus action() {
		return action;
	}
	
	public SpecialAction special() {
		return special;
	}
}
