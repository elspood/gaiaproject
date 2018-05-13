package state;

public class Income {

	private ResourceType type;
	private int amount;
	
	public Income(ResourceType type, int amount) {
		this.type = type;
		this.amount = amount;
	}
	
	public ResourceType type() {
		return type;
	}
	
	public int amount() {
		return amount;
	}
	
	public String toString() {
		return amount + " " + type;
	}
}
