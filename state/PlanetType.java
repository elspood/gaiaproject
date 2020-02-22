package state;

public enum PlanetType {
	
	ICE('w'),
	TITANIUM('g'),
	SWAMP('b'),
	DESERT('y'),
	VOLCANIC('o'),
	OXIDE('r'),
	TERRA('u'),
	GAIA('*'),
	TRANSDIM('+'),
	LOST('?'),
	SPACE('-'),
	NOTHING(' ');
	
	private final char abbr;
	public static final PlanetType[] PLANETCYCLE = new PlanetType[] {ICE, TITANIUM, SWAMP, DESERT, VOLCANIC, OXIDE, TERRA};
	
	PlanetType(char abbr) {
		this.abbr = abbr;
	}
	
	public String toString() {
		return abbr + "";
	}
}
