package state;

public enum ActionType {
	DRAFTFACTION(State.FACTIONDRAFT),
	DRAFTPLANET(State.PLANETDRAFT, Coordinates.class),
	CHOOSEBOOSTER(State.BOOSTDRAFT),
	POWERINCOME(State.INCOME),
	RESOURCETRADE(null),
	LEECH,
	MINE,
	GAIAFORM,
	UPGRADE,
	FEDERATE,
	RESEARCH,
	PQACTION,
	SPECIALACTION,
	FREEOREPOWER(State.FREEOREPOWER),
	FREEBURNPOWER(State.FREEBURNPOWER),
	FREESPENDPOWER(State.FREEFINALACTION),
	PASS;
	
	private State allowedstate = State.ACTIONS;
	private Class parametertype = Integer.class;
	
	private ActionType(State allowedstate, Class parametertype) {
		this.allowedstate = allowedstate;
		this.parametertype = parametertype;
	}
	
	private ActionType(State allowedstate) {
		this.allowedstate = allowedstate;
	}
	
	private ActionType() {
		
	}
	
	public State validState() {
		return allowedstate;
	}
	
	public Class expectedParameterClass() {
		return parametertype;
	}
}
