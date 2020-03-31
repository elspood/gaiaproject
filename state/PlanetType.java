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
	
	public static final int terraformingSteps(PlanetType a, PlanetType b) {
		int steps = Math.abs(a.ordinal() - b.ordinal());
		if (steps > 3) return PLANETCYCLE.length - steps;
		return steps;
	}
	
	public static void main(String[] args) {
		for (PlanetType a : PLANETCYCLE)
			for (PlanetType b : PLANETCYCLE)
				System.out.println ("Factions who live on " + a.name() + " require " + terraformingSteps(a, b) + " terraforming steps to mine on " + b.name());
	}
}
