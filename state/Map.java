package state;

import java.util.Random;
import java.util.Vector;
import faction.Faction;

public class Map {
	
	public static final int WIDTH = 21;
	public static final int HEIGHT = 13;
	
	public static final int UNKNOWNRANGE = -1;
	
	public static final PlanetType[][] F1 = {
			{null, PlanetType.SPACE, PlanetType.DESERT, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SWAMP, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.OXIDE},
			{PlanetType.SPACE, PlanetType.TERRA, PlanetType.SPACE, PlanetType.VOLCANIC},
			{null, PlanetType.SPACE, PlanetType.TRANSDIM, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F2 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.VOLCANIC, PlanetType.SPACE, PlanetType.SWAMP, PlanetType.OXIDE},
			{PlanetType.TITANIUM, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.ICE, PlanetType.SPACE, PlanetType.TRANSDIM},
			{null, PlanetType.SPACE, PlanetType.DESERT, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F3 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE, PlanetType.TERRA},
			{PlanetType.TRANSDIM, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.DESERT},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.ICE, PlanetType.SPACE},
			{null, PlanetType.SPACE, PlanetType.TITANIUM, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F4 = {
			{null, PlanetType.SPACE, PlanetType.ICE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.VOLCANIC, PlanetType.SPACE},
			{PlanetType.TITANIUM, PlanetType.OXIDE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.SWAMP, PlanetType.SPACE},
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.TERRA},
	};
	
	public static final PlanetType[][] F5 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE, PlanetType.VOLCANIC},
			{PlanetType.ICE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.DESERT},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{null, PlanetType.TRANSDIM, PlanetType.OXIDE, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F6 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SWAMP, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE},
			{PlanetType.TRANSDIM, PlanetType.TERRA, PlanetType.SPACE, PlanetType.TRANSDIM},
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.DESERT},
	};
	
	public static final PlanetType[][] F7 = {
			{null, PlanetType.TRANSDIM, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.OXIDE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.TITANIUM},
			{PlanetType.SWAMP, PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE},
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F8 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.VOLCANIC, PlanetType.TRANSDIM},
			{PlanetType.TERRA, PlanetType.ICE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.TITANIUM, PlanetType.SPACE},
			{null, PlanetType.TRANSDIM, PlanetType.SPACE, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F9 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SWAMP},
			{PlanetType.VOLCANIC, PlanetType.SPACE, PlanetType.TITANIUM, PlanetType.SPACE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.TRANSDIM, PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE},
			{null, PlanetType.ICE, PlanetType.SPACE, PlanetType.SPACE},
	};
	
	public static final PlanetType[][] F10 = {
			{null, PlanetType.SPACE, PlanetType.SPACE, PlanetType.TERRA},
			{PlanetType.SPACE, PlanetType.DESERT, PlanetType.SPACE, PlanetType.OXIDE},
			{PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE, PlanetType.SPACE},
			{PlanetType.TRANSDIM, PlanetType.SPACE, PlanetType.GAIA, PlanetType.SPACE},
			{null, PlanetType.TRANSDIM, PlanetType.SPACE, PlanetType.SPACE},
	};
	
	private PlanetType[][] grid = new PlanetType[WIDTH][HEIGHT]; // !!!!! index order is col, row
	private Faction[][] occupant = new Faction[WIDTH][HEIGHT];
	private Building[][] building = new Building[WIDTH][HEIGHT];
	
	private Vector<Coordinates> unusedtransdims = new Vector<Coordinates>();
	private Vector<Coordinates> emptyplanets = new Vector<Coordinates>();
	private Vector<Coordinates> emptyspace = new Vector<Coordinates>();
	
	// TODO: make most of the map static to support static range references and otherwise avoid passing map parameters around
	private int[][] range = new int[WIDTH * HEIGHT][WIDTH * HEIGHT];
	private static int[][] distance = new int[WIDTH * HEIGHT][WIDTH * HEIGHT];
	
	static {
		calculateDistanceMatrix();
	}
	
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
	
	public PlanetType get(Coordinates c) {
		return get(c.col(), c.row());
	}
	
	public Vector<Coordinates> unusedTransdims() {
		return unusedtransdims;
	}
	
	public Vector<Coordinates> emptySpace() {
		return (Vector<Coordinates>)emptyspace.clone();
	}
	
	public void startGaiaforming(Coordinates c) {
		unusedtransdims.remove(c);
	}
	
	public void gaiaformComplete(Coordinates c) {
		grid[c.col()][c.row()] = PlanetType.GAIA;
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
	
	// TODO: refactor this to maintain a realtime inventory of available planets
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
					if (tile[c][r] == PlanetType.TRANSDIM) unusedtransdims.add(new Coordinates(ct, rt));
					else if (tile[c][r] == PlanetType.SPACE) emptyspace.add(new Coordinates(ct, rt));
					else if (tile[c][r] != PlanetType.NOTHING) emptyplanets.add(new Coordinates(ct, rt));
					//System.out.println(ct + "," + rt + ": " + tile[c][r] + " (" + c + "," + r + ")");
				}
		}
		
		calculateRangeMatrix();
	}
	
	public static int distance(Coordinates x1, Coordinates x2) {
		return distance(x1.col(), x1.row(), x2.col(), x2.row());
	}
	
	public static int distance(int c1, int r1, int c2, int r2) {
		int i = c1 + r1 * WIDTH;
		int j = c2 + r2 * WIDTH;
		return distance[i][j];
	}
	
	public int range(Coordinates x1, Coordinates x2) {
		return range(x1.col(), x1.row(), x2.col(), x2.row());
	}
	
	public int range(int c1, int r1, int c2, int r2) {
		int i = c1 + r1 * WIDTH;
		int j = c2 + r2 * WIDTH;
		return range[i][j];
	}
	


	private static void calculateDistanceMatrix() {
		for (int i=0; i < WIDTH * HEIGHT; i++) {
			distance[i][i] = 0;
			for (int j=0; j < i; j++) {
				distance[i][j] = UNKNOWNRANGE;
				distance[j][i] = UNKNOWNRANGE;
			}
		}

		boolean updated = true;
		int range = 0;
		while (updated) {
			updated = false;
			range++;
			// System.out.println("Connecting hexes at range " + distance);
			for (int i=0; i < WIDTH * HEIGHT; i++) {
				int c1 = i % WIDTH;
				int r1 = i / WIDTH;
				for (int j=0; j < i; j++) {
					int c2 = j % WIDTH;
					int r2 = j / WIDTH;
					if (range == 1) {
						if (adjacent(c1, r1, c2, r2)) {
							distance[i][j] = 1;
							distance[j][i] = 1;
							updated = true;
						}
					} else if (distance[i][j] == UNKNOWNRANGE) {
						Vector<Coordinates> neighbors = neighboringHexes(c1, r1);
						for (Coordinates x : neighbors) {
							int i1 = x.col() + x.row() * WIDTH;
							if (distance[i1][j] == range - 1) {
								distance[i][j] = range;
								distance[j][i] = range;
								updated = true;
								break;
							}
						}
					}
				}
			}
		}
	}

	private void calculateRangeMatrix() {
		for (int i=0; i < WIDTH * HEIGHT; i++) {
			range[i][i] = 0;
			for (int j=0; j < i; j++) {
				range[i][j] = UNKNOWNRANGE;
				range[j][i] = UNKNOWNRANGE;
			}
		}

		boolean updated = true;
		int distance = 0;
		while (updated) {
			updated = false;
			distance++;
			// System.out.println("Connecting hexes at range " + distance);
			for (int i=0; i < WIDTH * HEIGHT; i++) {
				int c1 = i % WIDTH;
				int r1 = i / WIDTH;
				if (grid[c1][r1] == PlanetType.NOTHING) continue;
				for (int j=0; j < i; j++) {
					int c2 = j % WIDTH;
					int r2 = j / WIDTH;
					if (grid[c2][r2] == PlanetType.NOTHING) continue;
					if (distance == 1) {
						if (adjacent(c1, r1, c2, r2)) {
							range[i][j] = 1;
							range[j][i] = 1;
							updated = true;
						}
					} else if (range[i][j] == UNKNOWNRANGE) {
						Vector<Coordinates> neighbors = neighbors(c1, r1);
						for (Coordinates x : neighbors) {
							int i1 = x.col() + x.row() * WIDTH;
							if (range[i1][j] == distance - 1) {
								range[i][j] = distance;
								range[j][i] = distance;
								updated = true;
								break;
							}
						}
					}
				}
			}
		}

		// System.out.println("Last path found at range " + distance);
	}

	public static Vector<Coordinates> neighboringHexes(Coordinates x) {
		return neighboringHexes(x.col(), x.row());
	}
		
	public static Vector<Coordinates> neighboringHexes(int col, int row) {
		Vector<Coordinates> neighbors = new Vector<Coordinates>();
		for (int i = Math.max(0, col - 1); i <= Math.min(WIDTH - 1, col + 1); i++)
			for (int j = Math.max(0, row - 1); j <= Math.min(HEIGHT - 1, row + 1); j++)
				if (adjacent(col, row, i, j)) neighbors.add(new Coordinates(i, j));
		return neighbors;
	}

	/*
	 * return only non-NOTHING neighboring hexes
	 */
	public Vector<Coordinates> neighbors(Coordinates x) {
		return neighbors(x.col(), x.row());
	}
	
	/**
	 * returns only non-NOTHING neighboring hexes
	 */
	public Vector<Coordinates> neighbors(int col, int row) {
		Vector<Coordinates> neighbors = new Vector<Coordinates>();
		for (int i = Math.max(0, col - 1); i <= Math.min(WIDTH - 1, col + 1); i++)
			for (int j = Math.max(0, row - 1); j <= Math.min(HEIGHT - 1, row + 1); j++)
				if (adjacent(col, row, i, j) && (grid[i][j] != PlanetType.NOTHING))
					neighbors.add(new Coordinates(i, j));
		return neighbors;
	}
	
	public static boolean adjacent(Coordinates x1, Coordinates x2) {
		return adjacent(x1.col(), x1.row(), x2.col(), x2.row());
	}
	
	public static boolean adjacent(int c1, int r1, int c2, int r2) {
		if (c1 == c2)
			return (Math.abs(r1 - r2) == 1);
		if (r1 == r2)
			return (Math.abs(c1 - c2) == 1);
		if (c1 % 2 == 0) {
			if (Math.abs(c1 - c2) == 1) return (r1 - r2) == 1;
		} else {
			if (Math.abs(c1 - c2) == 1) return (r2 - r1) == 1;
		}
		return false;
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
	
	private static void adjacencyTest(Coordinates x) {
		int count = 0;
		int col = x.col();
		int row = x.row();
		for (int i = col - 2; i <= col + 2; i++) {
			for (int j = row - 2; j <= row + 2; j++) {
				Coordinates y = new Coordinates(i, j);
				boolean adjacent = adjacent(x, y);
				if (adjacent) count++;
				System.out.print(x + " is " + (adjacent ? "" : "NOT ") + "adjacent to " + y + "; ");
			}
			System.out.println();
		}
		System.out.println(count + " total adjacent hexes to " + x);
	}
	
	private void rangeTest() {
		Random rand = new Random();
		for (int i=0; i < 10; i++) {
			Coordinates x, y;
			do {
				int c1 = rand.nextInt(WIDTH);
				int r1 = rand.nextInt(HEIGHT);
				x = new Coordinates(c1, r1);
			} while (get(x) == PlanetType.NOTHING) ;
			do {
				int c2 = rand.nextInt(WIDTH);
				int r2 = rand.nextInt(HEIGHT);
				y = new Coordinates(c2, r2);
			} while (get(y) == PlanetType.NOTHING);
			
			System.out.println(x + " is " + range(x, y) + " hexes from " + y);
		}
	}
	
	public static void main(String[] args) {
		Map m = new Map();
		System.out.println("map initialized: ");
		System.out.println(m);
		
		Coordinates even = new Coordinates(8, 6);
		adjacencyTest(even);
		Coordinates odd = new Coordinates(9, 6);
		adjacencyTest(odd);
		
		m.rangeTest();
	}
}
