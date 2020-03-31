package faction;

import java.util.Vector;

import state.Coordinates;
import state.Map;

public class PlanetGroup {

	private Vector<Coordinates> planets = new Vector<Coordinates>();
	private Vector<Coordinates> satellites = new Vector<Coordinates>();
	private int buildingvalue = 0;
	private boolean federated = false;
	
	public PlanetGroup(Coordinates c, int size) {
		addBuilding(c, size);
	}
	
	public void addBuilding(Coordinates c, int size) {
		planets.add(c);
		buildingvalue += size;
	}
	
	public void addSatellite(Coordinates c) {
		satellites.add(c);
	}
	
	public void merge(PlanetGroup g) {
		planets.addAll(g.planets);
		satellites.addAll(g.satellites);
		buildingvalue += g.buildingvalue;
		if (g.federated) federated = true;
	}
	
	public void federate() {
		federated = true;
	}
	
	public boolean federated() {
		return federated;
	}
	
	public int buildingvalue() {
		return buildingvalue;
	}
	
	public int buildings() {
		return planets.size();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PlanetGroup)) return false;
		PlanetGroup pg = (PlanetGroup)o;
		if (federated != pg.federated) return false;
		if (buildingvalue != pg.buildingvalue) return false;
		
		if (planets.size() != pg.planets.size()) return false;
		if (satellites.size() != pg.satellites.size()) return false;
				
		for (Coordinates p : planets) if (!pg.planets.contains(p)) return false;
		for (Coordinates s : satellites) if (!pg.satellites.contains(s)) return false;
		
		return true;
	}
	
	public Vector<Coordinates> buildingLocations() {
		return planets;
	}
	
	public int satellites() {
		return satellites.size();
	}
	
	public Vector<Coordinates> satelliteLocations() {
		return satellites;
	}
	
	public Coordinates anchor() {
		return planets.get(0);
	}
	
	/**
	 * checks whether the current group contains the anchor planet of each of the
	 * specified planet groups (useful for checking whether this group is already part of a federation)
	 * @param groups the list of groups to check if they are part of the current group
	 * @return true if the first planet of each group is part of this planet group
	 */
	public boolean containsAllGroups(Vector<PlanetGroup> groups) {
		for (PlanetGroup pg : groups) {
			Coordinates anchor = pg.planets.get(0);
			if (!planets.contains(anchor)) return false;
		}
		return true;
	}
	
	/**
	 * checks whether the current group occupies the specified hex (planet or satellite)
	 * @param c the hex to check
	 * @return true if the planet group uses the specified hex
	 */
	public boolean containsHex(Coordinates c) {
		return planets.contains(c) || satellites.contains(c);
	}
	
	@Override
	public PlanetGroup clone() {
		PlanetGroup clone = new PlanetGroup(planets.get(0), buildingvalue);
		for (int i = 1; i < planets.size(); i++) clone.planets.add(planets.get(i));
		clone.satellites = (Vector<Coordinates>)satellites.clone();
		clone.federated = federated;
		return clone;
	}
	
	/**
	 * @param c the location to be tested for adjacency
	 * @return true if the planetgroup is adjacent to the specified location c
	 */
	public boolean adjacent(Coordinates c) {
		for (Coordinates p : planets) if (Map.adjacent(c, p)) return true;
		for (Coordinates s : satellites) if (Map.adjacent(c, s)) return true;
		return false;
	}
	
	/**
	 * return generates a unique list of building neighbors for this planet group (might not be valid hexes for game purposes)
	 * @return the list of all hexes directly adjacent to buildings in the planet group (ignores satellites)
	 */
	public Vector<Coordinates> neighbors() {
		// TODO: potentially keep this up to date instead of recalculating it on the fly
		Vector<Coordinates> neighbors = new Vector<Coordinates>();
		for (Coordinates c : planets) {
			Vector<Coordinates> planetneighbors = Map.neighboringHexes(c);
			for (Coordinates n : planetneighbors) if (!neighbors.contains(n) && (!planets.contains(n)))
				neighbors.add(n);
		}
		return neighbors;
	}
	
	/**
	 * uses only absolute distance; might not be correct on a convex map
	 * @param c the location of the hex to measure from
	 * @return the distance from the specified hex to the nearest planet in the group
	 */
	public int distance(Coordinates c) {
		int distance = Integer.MAX_VALUE;
		for (Coordinates p : planets) {
			int d = Map.distance(c, p);
			if (d < distance) distance = d;
		}
		return distance;
	}

	public String toString() {
		char[][] grid = new char[Map.WIDTH][Map.HEIGHT];

		for (Coordinates c : planets) grid[c.col()][c.row()] = '@';
		for (Coordinates c : satellites) grid[c.col()][c.row()] = '*';

		String ts = "   ";
		for (int i=0; i < Map.WIDTH; i++) 
			ts += (char)(i + 65) + "  ";
		ts += "\n";
		String even = " 0 ";
		String odd = " 0    ";
		for (int r = 0; r < Map.HEIGHT; r++) {
			for (int c = 0; c < Map.WIDTH; c++) {
				String cell = (grid[c][r] > 0) ? (grid[c][r] + " ") : "  ";
				if ((c % 2) == 0) even += cell + "    ";
				else odd += cell + "    ";
			}
			ts += even + "\n" + odd + "\n";
			even = ((r < 9) ? " " : "") + (r + 1) + " ";
			odd = even + "   ";
		}

		ts += "\n\nTotal power: " + buildingvalue + " Satellites: " + satellites.size();
		if (!federated) ts += " NOT A VALID FEDERATION";
		

		return ts + "\n\n";
	}
}
