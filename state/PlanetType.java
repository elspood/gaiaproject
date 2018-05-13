package state;

public enum PlanetType {
	
	WHITE('w'),
	GREY('g'),
	BROWN('b'),
	YELLOW('y'),
	ORANGE('o'),
	RED('r'),
	BLUE('u'),
	GAIA('*'),
	TRANSDIM('+'),
	LOST('?'),
	SPACE('-'),
	NOTHING(' ');
	
	private final char abbr;
	
	PlanetType(char abbr) {
		this.abbr = abbr;
	}
	
	public String toString() {
		return abbr + "";
	}
}
