package action;

import state.Income;
import state.ResourceType;

public enum SpecialAction {
	TERRAFORM(false),		// free single terraform mine action
	RANGE3(false),			// +3 range mine/gf action
	QICCASH,				// +5 cash, +1 qic
	ORE,					// +3 ore
	KNOWLEDGE,				// +3 knowledge
	QIC,					// +1 qic from academy
	ANDROIDRESEARCH(false),	// mad androids faction special ability
	BALCASH;				// +4 credits from Baltaks academy
	
	public static final int ADVTECHSPECIALS = 3;	// QICCASH, ORE, KNOWLEDGE
	
	private boolean canberoot = true;	// false if the special action is subservient to another type, like build mine, gaia, or research
	
	private SpecialAction() {
		
	}
	
	private SpecialAction(boolean canberoot) {
		this.canberoot = canberoot;
	}
	
	public final Action action(int player) {
		switch(this) {
		case QIC: return new Action(player, ActionType.SPECIALINCOME, new Income(ResourceType.QIC, 1), "Collect 1 QIC from academy");
		case ANDROIDRESEARCH: return new Action(player, ActionType.SPECIALINCOME, new Income(ResourceType.QIC, 1), "Collect 1 QIC from academy");
		case BALCASH: return new Action(player, ActionType.SPECIALINCOME, new Income(ResourceType.CREDITS, 4), "Collect 4 credits from academy");
		default: throw new IllegalStateException("Can't create an action from special action type: " + this);
		}
	}
	
	public boolean canberoot() {
		return canberoot;
	}
}
