package state;

public class Coordinates {

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
