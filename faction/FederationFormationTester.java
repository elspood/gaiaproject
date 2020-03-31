package faction;

import state.Coordinates;
import state.Map;

public class FederationFormationTester {

	
	public static void main(String[] args) {
		System.out.println("Default map:");
		Map map = new Map();
		System.out.println(map);
		
		int cases = 0;
		System.out.println("TEST CASE " + (++cases) + ": fewest satellites requires crossing another planet group");
		FactionPlanetGroupManager pgm = new FactionPlanetGroupManager(map);
		pgm.addBuilding(new Coordinates(7,6), 1);
		pgm.addBuilding(new Coordinates(7,8), 2);
		pgm.addBuilding(new Coordinates(7,7), 1);
		pgm.addBuilding(new Coordinates(9,5), 1);
		pgm.addBuilding(new Coordinates(10,6), 1);
		pgm.addBuilding(new Coordinates(11,4), 1);
		pgm.addBuilding(new Coordinates(13,4), 1);
		pgm.addBuilding(new Coordinates(12,4), 1);
		System.out.println(pgm);
		pgm.recalculatePossibleFederations();
		System.out.println(pgm);

		System.out.println("TEST CASE " + (++cases) + ": shortest path takes a very long way around an existing federation, "
				+ "another planet group in the direction but not needed");

		pgm = new FactionPlanetGroupManager(map);
		pgm.addBuilding(new Coordinates(8, 1), 1);
		pgm.addBuilding(new Coordinates(7,8), 1);
		pgm.addBuilding(new Coordinates(8,9), 1);
		pgm.addBuilding(new Coordinates(9,5), 1);
		pgm.addBuilding(new Coordinates(10,6), 1);
		pgm.addBuilding(new Coordinates(13,9), 1);
		pgm.addBuilding(new Coordinates(15,9), 1);
		pgm.addBuilding(new Coordinates(16,9), 1);
		pgm.addBuilding(new Coordinates(15,2), 1);
		
		Coordinates c = new Coordinates(11,4);
		PlanetGroup fed = new PlanetGroup(c, 1);
		pgm.addBuilding(c, 1);
		c = new Coordinates(13,4);
		fed.addBuilding(c, 1);
		pgm.addBuilding(c, 1);
		c = new Coordinates(12,4);
		fed.addBuilding(c, 1);
		pgm.addBuilding(c, 1);
		c = new Coordinates(9,2);
		fed.addBuilding(c, 1);
		pgm.addBuilding(c, 1);
		c = new Coordinates(10,2);
		fed.addBuilding(c, 1);
		pgm.addBuilding(c, 1);
		c = new Coordinates(15,6);
		fed.addBuilding(c, 2);
		pgm.addBuilding(c, 2);
		fed.addSatellite(new Coordinates(10, 3));
		fed.addSatellite(new Coordinates(11, 3));
		fed.addSatellite(new Coordinates(14, 5));
		fed.addSatellite(new Coordinates(14, 6));
		pgm.federate(fed);

		System.out.println(pgm);
		pgm.recalculatePossibleFederations();
		System.out.println(pgm);

		System.out.println("TEST CASE " + (++cases) + ": planet group cut off from all others by preexisting federation");

		System.out.println("TEST CASE " + (++cases) + ": not enough satellites remaining to form federation");

		System.out.println("TEST CASE " + (++cases) + ": most efficient federation is a single satellite, "
				+ "but some groups can be connected with a different single satellite");

		System.out.println("TEST CASE " + (++cases) + ": removing a planet group in the middle still leaves enough total power, "
				+ "but breaks the federation chain");

		System.out.println("TEST CASE " + (++cases) + ": 19 individual unfederated planet groups, including lost planet - does this terminate ever?");

		System.out.println("TEST CASE " + (++cases) + ": Ivits and 25 individual unfederated planet groups (lost planet + "
				+ "space stations) - does this terminate ever?");

		System.out.println("TEST CASE " + (++cases) + ": Multiple satellite arrangements with the same minimum number of "
				+ "satellites for the same planet group selection");

		System.out.println("TEST CASE " + (++cases) + ": add new building that joins together 3 federated and unfederated groups");

		System.out.println("TEST CASE " + (++cases) + ": test convex-shaped map federating around a corner?");
	}
}
