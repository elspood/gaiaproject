package state;

public enum ResourceType {
	VP('v'),
	ORE('o'),
	CREDITS('c'),
	KNOWLEDGE('k'),
	QIC('q'),
	FED('f'),
	GF('g'),
	GVP('v'),
	LPLANET('p'),
	CHARGE('+'),
	POWER('*');
	
	private char displaychar;
	
	private ResourceType(char display) {
		displaychar = display;
	}
	
	public String toString() {
		return displaychar + "";
	}
}
