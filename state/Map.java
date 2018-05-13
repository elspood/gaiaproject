package state;

import java.util.Vector;
import faction.Faction;

public class Map {
	
	public static final int WIDTH = 21;
	public static final int HEIGHT = 13;
	
	public static final PlanetType[][] F1 = {
			{null, PlanetType.SPACE, PlanetType.YELLOW, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.BROWN, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.RED},
			{PlanetType.SPACE, PlanetType.BLUE, PlanetType.SPACE, PlanetType.ORANGE},
			{null, PlanetType.SPACE, PlanetType.TRANSDIM, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F2 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.ORANGE, PlanetType.SPACE, PlanetType.BROWN, PlanetType.RED},
			{PlanetType.GREY, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.WHITE, PlanetType.SPACE, PlanetType.TRANSDIM},
			{null, PlanetType.SPACE, PlanetType.YELLOW, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F3 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE, PlanetType.BLUE},
			{PlanetType.TRANSDIM, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.YELLOW},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.WHITE, PlanetType.SPACE},
			{null, PlanetType.SPACE, PlanetType.GREY, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F4 = {
			{null, PlanetType.SPACE, PlanetType.WHITE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.ORANGE, PlanetType.SPACE},
			{PlanetType.GREY, PlanetType.RED, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.BROWN, PlanetType.SPACE},
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.BLUE},
	};
	
	public static final PlanetType[][] F5 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE, PlanetType.ORANGE},
			{PlanetType.WHITE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.YELLOW},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{null, PlanetType.TRANSDIM, PlanetType.RED, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F6 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.BROWN, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE},
			{PlanetType.TRANSDIM, PlanetType.BLUE, PlanetType.SPACE, PlanetType.TRANSDIM},
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.YELLOW},
	};
	
	public static final PlanetType[][] F7 = {
			{null, PlanetType.TRANSDIM, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.RED, PlanetType.SPACE, PlanetType.SPACE, PlanetType.GREY},
			{PlanetType.BROWN, PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE},
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F8 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.ORANGE, PlanetType.TRANSDIM},
			{PlanetType.BLUE, PlanetType.WHITE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.GREY, PlanetType.SPACE},
			{null, PlanetType.TRANSDIM, PlanetType.SPACE, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F9 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.BROWN},
			{PlanetType.ORANGE, PlanetType.SPACE, PlanetType.GREY, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.TRANSDIM, PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE},
			{null, PlanetType.WHITE, PlanetType.SPACE, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F10 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.BLUE},
			{PlanetType.SPACE, PlanetType.YELLOW, PlanetType.SPACE, PlanetType.RED},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.TRANSDIM, PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE},
			{null, PlanetType.TRANSDIM, PlanetType.SPACE, PlanetType.SPACE},
	};
	
	private PlanetType[][] grid = new PlanetType[WIDTH][HEIGHT]; // !!!!! index order is col, row
	private Faction[][] occupant = new Faction[WIDTH][HEIGHT];
	private Building[][] building = new Building[WIDTH][HEIGHT];
	
	public Map() {
		init(
				F1, 11, 2, 0,
				F2, 8, 6, 0,
				F3, 13, 6, 0,
				F4, 10, 10, 0,
				F5, 16, 3, 0,
				F6, 18, 7, 0,
				F7, 15, 10, 0,
				F8, 5, 9, 0,
				F9, 3, 5, 0,
				F10, 6, 2, 0
				);
	}
	
	public Map(Object...layout) {
		init(layout);
	}
	
	public PlanetType get(int col, int row) {
		return grid[col][row];
	}
	
	public void build(Coordinates c, Faction f, Building b) {
		int col = c.col();
		int row = c.row();
		Faction f1 = occupant[col][row];
		
		// TODO: support lantids
		if (f1 != null) {
			if (f1 != f)
				throw new IllegalStateException(c + " already occupied by " + f1);
		} else {
			occupant[col][row] = f;
		}
		building[col][row] = b;
	}
	
	public Coordinates[] availablePlanets(PlanetType type) {
		Vector<Coordinates> planets = new Vector<Coordinates>();
		for (int i=0; i < WIDTH; i++)
			for (int j=0; j < HEIGHT; j++)
				if ((grid[i][j] == type) && (occupant[i][j] == null))
					planets.add(new Coordinates(i,j));
		return planets.toArray(new Coordinates[planets.size()]);
	}
	
	private PlanetType[][] rotateTile(PlanetType[][] tile, int orientation) {
		if (orientation == 0) return tile;
		// TODO: support orientation change
		return null;
	}
	
	private void init(Object... layout) {
		if (layout.length % 4 != 0)
			throw new IllegalArgumentException("Illegal map layout definition; required: tile ID, col, row, orientation");
		for (int i=0; i < WIDTH; i++)
			for (int j=0; j < HEIGHT; j++)
				grid[i][j] = PlanetType.NOTHING;
		
		for (int i=0; i < layout.length; i += 4) {
			if (!(layout[i] instanceof PlanetType[][]))
				throw new IllegalArgumentException("Expected: PlanetType[][] (tile ID) at index " + i);
			PlanetType[][] tile = (PlanetType[][])layout[i];
			int col = 0, row = 0, rotation = 0;
			try {
				col = (Integer)layout[i + 1];
				row = (Integer)layout[i + 2];
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("Expected: col, row location at index " + (i + 1) + "," + (i + 2));
			}
			try {
				rotation = (Integer)layout[i+3];
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("Expected: orientation 0..5 at index " + (i + 3));
			}
			tile = rotateTile(tile, rotation);
			//System.out.println("Tile " + (i/4 + 1) + " at " + col + "," + row);
			for (int c = 0; c < 5; c++)
				for (int r = 0; r < 5; r++) {
					PlanetType t = null;
					try {
						t = tile[c][r];
					} catch (ArrayIndexOutOfBoundsException e) {
						continue;
					}
					if (t == null) continue;
					int ct = col - 2 + c;
					int rt = row - 2 + r;
					if ((col - ct) % 2 != 0)
						rt += (col % 2 == 0) ? 0 : 1;
					if (grid[ct][rt] != PlanetType.NOTHING)
						throw new IllegalArgumentException("Overlapping tile at [" + ct + "," + rt + "] placing tile " + 
								(i/4 + 1) + "\n" + this);
					grid[ct][rt] = tile[c][r];
					//System.out.println(ct + "," + rt + ": " + tile[c][r] + " (" + c + "," + r + ")");
				}
		}
	}
	
	public String toString() {
		String ts = "   ";
		for (int i=0; i < WIDTH; i++) 
			ts += (char)(i + 65) + "  ";
		ts += "\n";
		String even = " 0 ";
		String odd = " 0    ";
		for (int r = 0; r < HEIGHT; r++) {
			for (int c = 0; c < WIDTH; c++) {
				String planet = (occupant[c][r] == null) ? grid[c][r] + " " :
					occupant[c][r].homePlanet().toString().toUpperCase() + building[c][r];
					
				if ((c % 2) == 0) even += planet + "    ";
				else odd += planet + "    ";
			}
			ts += even + "\n" + odd + "\n";
			even = ((r < 9) ? " " : "") + (r + 1) + " ";
			odd = even + "   ";
		}
		return ts;
	}
	
	public static void main(String[] args) {
		Map m = new Map();
		System.out.println("map initialized: ");
		System.out.println(m);
	}
}
