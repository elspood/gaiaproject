package action;

import state.Coordinates;
import faction.ResearchKnowledgeSource;
import state.State;
import state.Income;

public enum ActionType {
	DRAFTFACTION(new State[] {State.FACTIONDRAFT}),
	DRAFTPLANET(new State[] {State.PLANETDRAFT, State.DRAFTIVITS, State.DRAFTXENOS}, Coordinates.class),
	CHOOSEBOOSTER(new State[] {State.CHOOSEBOOSTER}),
	POWERINCOME(new State[] {State.INCOME}),
	RESOURCETRADE(Income.class),
	LEECH,
	MINE,
	GAIAFORM,
	UPGRADE,
	FEDERATE,
	RESEARCH(ResearchKnowledgeSource.class),
	POWERSHOP,
	QICSHOP,
	SPECIALACTION,
	SPECIALINCOME(Income.class),
	FACTIONFREE,
	FACTIONSPECIAL,
	PASS,
	ENDTURN(new State[] {State.ENDTURN});
	
	private State[] allowedstate = new State[] {State.STARTACTION};
	private Class parametertype = Integer.class;
	
	private ActionType(Class parametertype) {
		this.parametertype = parametertype;
	}
	
	private ActionType(State[] allowedstate, Class parametertype) {
		this.allowedstate = allowedstate;
		this.parametertype = parametertype;
	}
	
	private ActionType(State[] allowedstate) {
		this.allowedstate = allowedstate;
	}
	
	private ActionType() {
		
	}
	
	public boolean validState(State state) {
		if (state == null) return false;
		if (allowedstate == null) return true;
		for (int i=0; i < allowedstate.length; i++) if (allowedstate[i] == state) return true;
		return false;
	}
	
	public Class expectedParameterClass() {
		return parametertype;
	}
}
