package action;

public class Action {

	private int player;
	private ActionType type;
	private Object info;
	private String text;
	
	public Action(int player, ActionType type, Object info, String text) {
		this.player = player;
		this.type = type;
		this.info = info;
		this.text = text;
	}
	
	public int player() {
		return player;
	}
	
	public ActionType type() {
		return type;
	}
	
	public Object info() {
		return info;
	}
	
	public String text() {
		return text;
	}
	
	public String toString() {
		return "Player " + (player + 1) + " " + type + " " + text;
	}
}
