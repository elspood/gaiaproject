package state;

public enum State {
	GAMECONDITIONS,		// set up initial board generation conditions, including randomness parameters
	LASTPLAYERROTATE,	// TODO: support board rearranging for last player? 
	FACTIONDRAFT,		// draft factions
	PLANETDRAFT,		// snake draft home planets
	DRAFTXENOS,			// place third xenos planet
	DRAFTIVITS,			// place ivits PI
	CHOOSEBOOSTER,		// reverse order draft booster tiles, or choose booster after passing
	
						// repeat x 6: income, gaia, actions, cleanup
	INCOME,				// power charge decisions
	ITARSGAIA,			// itars decide whether to buy tech tiles with gaia power
	TERRANGAIA,			// terrans decide income from gaia bowl
	GAIA,				// gaia formation, power returns to bowls
	STARTACTION,		// begin an action (including any free action needed to afford it)
	LEECH,				// round of leech choices immediately after build/upgrade activity
	CHOOSETECH,			// choose a tech tile from building academy, lab, or other action
	CHOOSERESEARCH,		// choose a research track, if choices are allowed by the action
	CHOOSEFEDTILE,		// choose a federation tile after federating
	LOSTPLANET,			// this is a separate state, in order to potentially handle
						// two rounds of leech on the same player's turn
	ENDTURN,			// allow end-of-turn free actions to groom power bowl or prevent overcap
	CLEANUP				// reset shop, update round bonus, clear faction actions, endgame scoring
}
