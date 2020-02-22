package state;

public class PlayerOrder {

	private static final int NODRAFT = 0;
	private static final int NORMALDRAFT = 1;
	private static final int SNAKEDRAFT = 2;
	private static final int REVERSEDRAFT = -1;
	private int[] turnorder;
	private int nextplayerafterdraft;
	private int index = 0;
	private int draft = NODRAFT;

	// TODO: support clockwise turn order AND variable turn order
	//		(game designer confirms that if variable order is used, ALL decisions happen in variable order, including leech
	private boolean clockwise;		// if true, all decisions are in player order, if false, variable turn order is used
	private int[] passorder;;
	private boolean[] passed;
	private int lastpass = 0;
	
	
	public PlayerOrder(boolean clockwise, int players) {
		turnorder = new int[players];
		passorder = new int[players];
		passed = new boolean[players];
		for (int i=0; i < players; i++) {
			turnorder[i] = i;
			passorder[i] = -1;
			passed[i] = false;
		}
		this.clockwise = clockwise;
	}
	
	public int currentPlayer() {
		return turnorder[index];
	}
	
	public void startNormalPlay() {
		draft = NODRAFT;
		index = 0;
	}
	
	public void startNormalDraft() {
		draft = NORMALDRAFT;
		index = 0;
	}
	
	public void startSnakeDraft() {
		draft = SNAKEDRAFT;
		index = 0;
	}
	
	public void startReverseDraft() {
		draft = REVERSEDRAFT;
		index = turnorder.length - 1;
	}
	
	public void setNext(int player) {
		for (int i=0; i < turnorder.length; i++) if (turnorder[i] == player) {
			index = player;
			return;
		}
		throw new IllegalArgumentException("Could not set the turn order to player with id " + player);
	}
	
	/*
	 * advances the turn order and returns the next player (or -1 if a draft/round is ended)
	 * (or -2 if all players have passed)
	 * TODO: implement a leech/income decision, once-around-the-horn counter
	 */
	public int nextPlayer() {
		if (draft == REVERSEDRAFT) index--;
		else index++;
		if (index == turnorder.length) {
			if (draft == NORMALDRAFT) return -1;
			if (draft == SNAKEDRAFT) {
				draft = REVERSEDRAFT;
				index--;
			} else index = 0;
		}
		if (index == -1) {
			draft = NODRAFT;
			// TODO: set the next player correctly for once-around-the-horn
			index = 0;
			return -1;
		}
		return turnorder[index];
	}
}
