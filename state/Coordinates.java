package state;

public class Coordinates {

	public static final Coordinates GAIABOWL = new Coordinates(-1, -1);
	public static final Coordinates HANGAR = new Coordinates(-2, -2);
	
	private int col;
	private int row;
	
	public Coordinates(int col, int row) {
		this.col = col;
		this.row = row;
	}
	
	public int col() {
		return col;
	}
	
	public int row() {
		return row;
	}
	
	public String toString() {
		return (char)(col + 65) + "" + row;
	}
}
