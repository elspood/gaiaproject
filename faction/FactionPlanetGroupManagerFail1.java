package faction;

import java.util.Collections;
import java.util.Vector;

import state.Coordinates;
import state.Map;

// TODO: make a completely different planet group manager for ivits 
public class FactionPlanetGroupManagerFail1 implements IFactionPlanetGroupManager {

	
	protected int federationpower = 7;
	private int buildingsinfederation = 0;
	private int usedsatellites = 0;
	

	protected Vector<Coordinates> emptyspaces;	// a unique copy of the empty spaces to be pruned over time
	private Map map;
	// TODO: keep this sorted by decreasing size?
	protected Vector<PlanetGroup> unusedgroups = new Vector<PlanetGroup>();
	protected Vector<PlanetGroup> federatedgroups = new Vector<PlanetGroup>();
	
	// TODO: keep this sorted by increasing number of satellites required
	protected Vector<PlanetGroup> possiblefederations = new Vector<PlanetGroup>();
	protected boolean possiblefederationsdirty = true;
	
	/**
	 * 
	 * @param map an intialized map, prior to any faction drafting taking place
	 */
	public FactionPlanetGroupManagerFail1(Map map) {
		this.map = map;
		this.emptyspaces = map.emptySpace();
	}
	
	/**
	 * tests whether there is still a valid chain of planet groups with enough power after removing the specified group and satellite
	 * may recurse if enough building power remains after failing to walk a chain
	 * @param connectedgroups the initial groups in the federation
	 * @param removedgroups the groups removed so far (the initial group plus any removed during recursion)
	 * @param placedsatellites the initial satellites in the federation
	 * @param removedsatellites satellites removed so far (the initial satellite plus any removed during recursion)
	 * @param remainingpower the current remaining power of connected groups that haven't been removed
	 * @return true if the federation is still valid with the planet + satellite combination removed
	 */
	private boolean federationStillValid(Vector<PlanetGroup> connectedgroups, Vector<PlanetGroup> removedgroups,
			Vector<Coordinates> placedsatellites, Vector<Coordinates> removedsatellites, int remainingpower) {
		// pick the next remaining group as a starting point
		PlanetGroup g = null;
		int index = 0;
		do {
			g = connectedgroups.get(index++);
		} while (removedgroups.contains(g));

		// initialize vectors to track the new planets and satellites in the federation starting with the next group
		removedgroups.add(g);
		int power = g.buildingvalue();
		Vector<PlanetGroup> newgroups = new Vector<PlanetGroup>();
		newgroups.add(g);
		Vector<Coordinates> newsatellites = new Vector<Coordinates>();
		
		traverse:
		while (true) {
			for (Coordinates s : placedsatellites) if (!removedsatellites.contains(s)) {
				// check if the new satellite is adjacent to any we've added so far
				for (Coordinates s1 : newsatellites) if (Map.adjacent(s, s1)) {
					newsatellites.add(s);
					removedsatellites.add(s);
					continue traverse;
				}
				// check if the new satellite is adjacent to any groups we've added so far
				for (PlanetGroup g1 : newgroups) if (g1.adjacent(s)) {
					newsatellites.add(s);
					removedsatellites.add(s);
					continue traverse;
				}
			}
			for (PlanetGroup pg : connectedgroups) if (!removedgroups.contains(pg)) {
				// check if the new group is adjacent to any satellites we've added so far
				for (Coordinates s1 : newsatellites) if (pg.adjacent(s1)) {
					newgroups.add(pg);
					removedgroups.add(pg);
					power += pg.buildingvalue();
					if (power >= federationpower) return true;
					continue traverse;
				}
				// planet groups can't be adjacent by definition
			}
			// if we've reached this far, the new federation wasn't big enough
			if (remainingpower - power >= federationpower) {
				// if there's still enough power left to federate with, we try again with what's left
				// (the lists of removed groups and removed satellites have been expanded during the loop)
				return federationStillValid(connectedgroups, removedgroups, placedsatellites, removedsatellites, remainingpower - power);
			}
			return false;
		}
	}
	
	/**
	 * performs a rule 3 check (is this still a valid federation after removing one planet group and one satellite?)
	 * @param connectedgroups the planet groups in the candidate federation
	 * @param currentpower the total power of the candidate federation
	 * @param placedsatellites the satellites used in the federation
	 * @return true if the federation cannot have a planet group + satellite removed and remain a valid federation
	 */
	private boolean rule3Valid(Vector<PlanetGroup> connectedgroups, int currentpower, Vector<Coordinates> placedsatellites) {
		for (PlanetGroup g : connectedgroups) {
			// must have enough power in the federation to remove a building/group
			int remainingpower = currentpower - g.buildingvalue();
			if (remainingpower < federationpower) continue;
			for (Coordinates s : placedsatellites) if (g.adjacent(s)) {
				// this looks inefficient, but each function call might recurse and so needs a fresh copy of the removed group vector
				Vector<PlanetGroup> removedgroup = new Vector<PlanetGroup>();
				removedgroup.add(g);
				Vector<Coordinates> removedsatellites = new Vector<Coordinates>();
				removedsatellites.add(s);
				if (federationStillValid(connectedgroups, removedgroup, placedsatellites, removedsatellites, remainingpower)) return false;
			}
		}
		return true;
	}
	
	/**
	 * recursively generates all possible minimum federations; if the federations are valid, they are marked as 'federated';
	 * the minimum possible federation(s) containing all the chosen planets may be illegal due to rule 3
	 * @param minimumfeds vector containing the formations of all possible minimum federations found so far
	 * @param chosengroups the planet groups chosen for the federation (must contain all of these)
	 * @param possiblesatellites current list of possible empty spaces to place the next satellite
	 * @param connectedgroups list of planet groups connected so far (may include groups not in the chosen list)
	 * @param placedsatellites locations of satellites placed so far
	 * @param leastsatellites least number of satellites used to connect all groups so far
	 * @param valid whether or not the current federation is valid (false if the minimum fed size is reached before connecting all groups)
	 * @return the smallest possible number of satellites used to connect all of the groups with the specified starting conditions
	 */
	private int connectGroups(Vector<PlanetGroup> minimumfeds, Vector<PlanetGroup> chosengroups, Vector<Coordinates> possiblesatellites,
			Vector<PlanetGroup> connectedgroups, int currentpower, Vector<Coordinates> placedsatellites, int leastsatellites, boolean valid) {
		
		// don't try to add more satellites if more would be used than the current minimum
		if (placedsatellites.size() == leastsatellites) return leastsatellites;
		
		//System.out.println(possiblesatellites.size() + " possible satellite locations to start with after placing " + placedsatellites.size() + " so far");
		//for (Coordinates c : possiblesatellites) System.out.print(c + " ");
		//System.out.println();
		
		// score satellites in terms of most likely improvement
		for (Coordinates s : possiblesatellites) {
			int score = 0;
			for (PlanetGroup g : unusedgroups) {
				if (!connectedgroups.contains(g) && (g.adjacent(s))) {
					if (chosengroups.contains(g)) score += 25;
					// score flat bonus points for adding planet groups - these almost always get us closer to our chosen groups
					score += 5;
				}
			}
			// prefer extending just-placed satellites for better depth-first searching
			int placed = placedsatellites.size();
			if ((placed > 0) && map.adjacent(s, placedsatellites.get(placed - 1))) score += 5;
			// assign points based on distance to nearest unconnected chosen group
			int mindist = Integer.MAX_VALUE;
			for (PlanetGroup g : chosengroups) if (!connectedgroups.contains(g)) {
				int distance = g.distance(s);
				if (distance < mindist) mindist = distance;
			}
			score -= mindist;
			s.setScore(score);
		}
		Collections.sort(possiblesatellites);
		
		for (Coordinates s : possiblesatellites) {
			placedsatellites.add(s);
			int groupsadded = 0;
			int newpower = currentpower;
			for (PlanetGroup g : unusedgroups) {
				if (!connectedgroups.contains(g) && (g.adjacent(s))) {
					newpower += g.buildingvalue();
					connectedgroups.add(g);
					groupsadded++;
				}
			}
			
			// TODO: remove logging code here
			PlanetGroup g1 = connectedgroups.get(0).clone();
			for (int i=1; i < connectedgroups.size(); i++) g1.merge(connectedgroups.get(i));
			for (Coordinates c : placedsatellites) g1.addSatellite(c);
			System.out.println(g1);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// determine if all chosen planet groups have now been connected
			boolean complete = groupsadded > 0;
			if (complete) {
				for (PlanetGroup g : chosengroups) {
					if (!connectedgroups.contains(g)) {
						complete = false;
						break;
					}
				}
			}
			
			if (complete) {
				int placed = placedsatellites.size();
				if (placed < leastsatellites) {
					minimumfeds.clear();
					leastsatellites = placed;
				}
				PlanetGroup g = connectedgroups.get(0).clone();
				for (int i=1; i < connectedgroups.size(); i++) g.merge(connectedgroups.get(i));
				for (Coordinates c : placedsatellites) g.addSatellite(c);
				if (valid && rule3Valid(connectedgroups, newpower, placedsatellites))
					g.federate();
				minimumfeds.add(g);
			} else {
				// the new federation won't be valid if it has reached the minimum power because new groups and satellites will still be added (fails rule 3)
				// we still keep going because we need to find the minimum number of satellites required to federate ALL of the chosen planets
				boolean isvalid = valid && (newpower < federationpower);
				
				// create a new vector of possible satellites, at the very least adding all the just-placed satellite's neighbors and removing itself;
				// we must also add empty cells next to newly-added planet groups
				// we can be smarter by not allowing the addition of new satellites that touch any two previously-placed satellites in certain circumstances
				Vector<Coordinates> candidates = (Vector<Coordinates>)possiblesatellites.clone();
				candidates.remove(s);
				for (Coordinates neighbor : map.neighbors(s)) {
					if (!candidates.contains(neighbor) && !placedsatellites.contains(neighbor) && emptyspaces.contains(neighbor)) candidates.add(neighbor);
				}
				int size = connectedgroups.size();
				for (int i=0; i < groupsadded; i++) {
					PlanetGroup g = connectedgroups.get(size - i - 1);
					Vector<Coordinates> neighbors = g.neighbors();
					for (Coordinates n : neighbors) if (!candidates.contains(n) && !placedsatellites.contains(n) && emptyspaces.contains(n))
							candidates.add(n);
				}
				// recurse and add more satellites until all groups are added
				int least = connectGroups(minimumfeds, chosengroups, candidates, connectedgroups, newpower, placedsatellites, leastsatellites, isvalid);
				if (least < leastsatellites) leastsatellites = least;
			}
			for (int i=0; i < groupsadded; i++) connectedgroups.remove(connectedgroups.size() - 1);
			placedsatellites.remove(placedsatellites.size() - 1);
		}
		return leastsatellites;
	}
	
	private void addPossibleFederations(Vector<PlanetGroup> groups) {
		if (groups.size() == 1) {
			// if a group is a federation all on its own, no need to use satellites
			possiblefederations.add(groups.get(0).clone());
			return;
		}
		for (PlanetGroup pg : possiblefederations) if (pg.containsAllGroups(groups)) return;
		/*
		String log = "Adding possible federations with groups: ";
		for (PlanetGroup g : groups) {
			int idx = unusedgroups.indexOf(g);
			log += (char)(idx + 65) + ",";
		}
		System.out.println(log);
		*/
		Vector<PlanetGroup> minimumfeds = new Vector<PlanetGroup>();
		Vector<Coordinates> possiblesatellites = new Vector<Coordinates>();
		
		// start by adding just the first group and then expand out from there;
		// adding all possible satellites to the initial set might cause search to go forever in non-contiguous space
		// it is still possible for the search to end up searching the same branches repeatedly, just in a different order
		PlanetGroup g = groups.get(0);
		Vector<Coordinates> buildings = g.buildingLocations();
		for (Coordinates c : buildings) {
			Vector<Coordinates> neighbors = map.neighbors(c);
			for (Coordinates n : neighbors) if (!possiblesatellites.contains(n) && emptyspaces.contains(n))
					possiblesatellites.add(n);
		}
		Vector<PlanetGroup> connectedgroups = new Vector<PlanetGroup>();
		connectedgroups.add(g);
		Vector<Coordinates> placedsatellites = new Vector<Coordinates>();
		connectGroups(minimumfeds, groups, possiblesatellites, connectedgroups, g.buildingvalue(), placedsatellites, MAXIMUMSATELLITES - usedsatellites, true);
		possiblefederations.addAll(minimumfeds);
	}
	
	protected void recalculatePossibleFederations(Vector<PlanetGroup> groups, int startindex, int powercount) {
		for (int i = startindex; i < unusedgroups.size(); i++) {
			PlanetGroup g = unusedgroups.get(i);
			groups.add(g);
			int newpower = powercount + g.buildingvalue();
			if (newpower >= federationpower) addPossibleFederations(groups);
			// maximum possible federation size is three clusters that are all one fewer than the minimum size
			// with more clusters, the maximum size is lower, but harder to calculate
			// for better efficiency, need to bring the limit way down to minimize forming rule-3-invalid feds
			if (newpower <= (federationpower - 1) * 3) recalculatePossibleFederations(groups, i + 1, newpower);
			groups.remove(groups.size()-1);
		}
	}
	
	protected void recalculatePossibleFederations() {
		// TODO: only update federations that are affected by the change
		//		upgrades might invalidate existing federations; upgrades will require retesting all possible groups with the improved planetgroup
		// 		new mines might add to an existing planet group, invalidating it or making the satellite requirement change; also requires retesting all possible groups
		//		lost mine might block satellite chains; requires re-forming all crossing satellite groups
		//		current player lost mine just breaks everything
		possiblefederations.clear();
		recalculatePossibleFederations(new Vector<PlanetGroup>(), 0, 0);
	}
	
	public boolean canFederate() {
		if (possiblefederationsdirty) {
			// TODO: recalculate possible federations only after upgrades, mines, or opposing players building lost mine
			//		these recalculations will be different depending on the thing that changed, so make a new function for each
			recalculatePossibleFederations();
			possiblefederationsdirty = false;
		}
		// TODO: sort possible feds by required satellites; check if that number of tokens/qic is available for satellites
		return possiblefederations.size() > 0;
	}

	@Override
	public boolean canFederate(int satellites) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeEmptySpace(Coordinates c) {
		// TODO: if empty spaces removed are part of other possible federations, update them
		// TODO: queue up a bunch of these if many empty spaces removed at once (e.g. federation formation/merging)
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateBuildingValue(Coordinates c, int delta, boolean recalculate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addBuilding(Coordinates c, int value) {
		Vector<PlanetGroup> adjacent = new Vector<PlanetGroup>();
		for (PlanetGroup g : unusedgroups) if (g.adjacent(c)) adjacent.add(g);
		emptyspaces.remove(c);
		
		if (adjacent.size() == 0) {
			unusedgroups.add(new PlanetGroup(c, value));
			return;
		}
		
		// TODO: move newly merged group to 'federated' group and update count of federated buildings if necessary
		PlanetGroup main = adjacent.get(0);
		main.addBuilding(c, value);
		for (int i = 1; i < adjacent.size(); i++) {
			PlanetGroup g = adjacent.get(i);
			main.merge(g);
			unusedgroups.remove(g);
		}
		
		if (main.federated()) {
			// TODO: find a much more efficient way to remove empty spaces next to newly joined federation buildings
			Vector<Coordinates> toremove = new Vector<Coordinates>();
			for (Coordinates e : emptyspaces) if (main.adjacent(e)) toremove.add(e);
			for (Coordinates e : toremove) removeEmptySpace(e);
		}
	}

	@Override
	public void newFederationPower(int power) {
		// TODO: update federation power requirement for xenos after PI is built
		federationpower = power;
	}

	@Override
	public Vector<PlanetGroup> possibleFederations(int satellites) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void federate(PlanetGroup g) {
		for (int i=unusedgroups.size()-1; i >=0; i--) {
			PlanetGroup ug = unusedgroups.get(i);
			if (g.containsHex(ug.anchor())) unusedgroups.remove(i);
		}
		g.federate();
		federatedgroups.add(g);
		buildingsinfederation += g.buildings();
		usedsatellites += g.satellites();
		
		// TODO: make this more efficient (the same hex will be asked to be removed many times)
		Vector<Coordinates> used = new Vector<Coordinates>();
		used.addAll(g.buildingLocations());
		used.addAll(g.satelliteLocations());
		for (Coordinates c : used) {
			Vector<Coordinates> neighbors = map.neighbors(c);
			for (Coordinates n : neighbors) emptyspaces.remove(n);
		}
	}

	@Override
	public int buildingsInFederation() {
		return buildingsinfederation;
	}

	@Override
	public int satellites() {
		return usedsatellites;
	}
	
	public String toString() {
		int totalgroups = unusedgroups.size() + federatedgroups.size();
		int[] value = new int[totalgroups];
		boolean[] federated = new boolean[totalgroups];
		char[][] grid = new char[Map.WIDTH][Map.HEIGHT];
		
		Vector<PlanetGroup> pgs = new Vector<PlanetGroup>();
		pgs.addAll(unusedgroups);
		pgs.addAll(federatedgroups);
		
		for (int i=0; i < totalgroups; i++) {
			PlanetGroup g = pgs.get(i);
			value[i] = g.buildingvalue();
			federated[i] = g.federated();
			Vector<Coordinates> planets = g.buildingLocations();
			for (Coordinates c : planets) grid[c.col()][c.row()] = (char)(i + 65);
			Vector<Coordinates> satellites = g.satelliteLocations();
			for (Coordinates c : satellites) grid[c.col()][c.row()] = '*';
		}
		for (Coordinates c : emptyspaces) grid[c.col()][c.row()] = '-';
		
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
		
		ts += "\n\n";
		
		for (int i=0; i < totalgroups; i++) {
			ts += (char)(i + 65) + ":" + value[i] + (federated[i] ? "*" : "") + " ";
		}
		
		if (!possiblefederationsdirty && (possiblefederations.size() == 0)) ts += "\nNO VALID FEDERATIONS FOUND";
		else {
			int count = 1;
			for (PlanetGroup g : possiblefederations) ts += "\n\nFEDERATION " + (count++) + ":\n" + g.toString();
		}
		return ts + "\n";
	}
}
