package state;

public enum State {
	GAMECONDITIONS,		// set up initial board generation conditions, including randomness parameters
	FACTIONDRAFT,		// draft factions
	// TODO: support board rearranging for last player?
	PLANETDRAFT,		// snake draft home planets	
	BOOSTDRAFT,			// reverse order draft booster tiles
						// repeat x 6: income, gaia, actions, cleanup
	INCOME,				
	GAIA,
	ACTIONS,
	FREEOREPOWER,
	FREEBURNPOWER,
	FREEFINALACTION,
	LEECH,
	CLEANUP
}
