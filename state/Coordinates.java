package state;

public class Coordinates {

	public static final Coordinates GAIABOWL = new Coordinates(-16612, -141234);
	public static final Coordinates HANGAR = new Coordinates(-2414, -2341234);
	
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
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Coordinates)) return false;
		Coordinates c = (Coordinates)o;
		return (col == c.col) && (row == c.row);
	}
	
	public String toString() {
		return (char)(col + 65) + "" + row;
	}
}
