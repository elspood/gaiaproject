package faction;

import java.util.Vector;

import state.Coordinates;

public interface IFactionPlanetGroupManager {
	
	public static final int MAXIMUMSATELLITES = 25;
	
	/**
	 * returns true if a federation is possible with the specified maximum number of satellites
	 * @param satellites the maximum number of satellites that can be made by the faction currently
	 */
	public boolean canFederate(int satellites);
	
	/**
	 * forces a recalculation of possible federations when the specified empty space is taken by
	 * another player placing the lost planet
	 * @param c the location of the lost planet
	 */
	public void removeEmptySpace(Coordinates c);
	
	/**
	 * updates the value of the building at the specified coordinates
	 * if recalculate is false, queue all federation recalculations until recalculate is true
	 * (this is useful for bescods building their PI, for instance)
	 * @param c the location of the updated building
	 * @param delta the change in power value of the building
	 * @param recalculate true if possible federations should be recalculated
	 */
	public void updateBuildingValue(Coordinates c, int delta, boolean recalculate);
	
	/**
	 * updates the value of the building at the specified coordinates and forces a recalculation
	 * @param c the location of the updated building
	 * @param delta the change in power value of the building
	 */
	default void updateBuildingValue(Coordinates c, int delta) {
		updateBuildingValue(c, delta, true);
	}
	
	/**
	 * adds the new building at the specified coordinates and recalculates everything
	 * this might be the lost planet, so an empty space may need to be removed, as well
	 * @param c the location of the new building
	 * @param value the power value of the new building (usually 1, except maybe bescods mines on titanium)
	 */
	public void addBuilding(Coordinates c, int value);
	
	/**
	 * updates the minimum power requirement for new federations and recalculates possible new feds
	 * @param size the new minimum federation power size
	 */
	public void newFederationPower(int power);
	
	/**
	 * 
	 * @return a vector of all possible federations
	 */
	default Vector<PlanetGroup> possibleFederations() {
		return possibleFederations(MAXIMUMSATELLITES);
	}
	
	/**
	 * 
	 * @param satellites
	 * @return a vector of possible federations with the specified number of satellites or fewer
	 */
	public Vector<PlanetGroup> possibleFederations(int satellites);
	
	/**
	 * federate with the specified planetgroup and update remaining available federations / space
	 * @param g
	 */
	public void federate(PlanetGroup g) ;
	
	/**
	 * not all structures in a federation might count toward the endgame condition
	 * @return the total number of buildings in federation
	 */
	public int buildingsInFederation() ;

	/**
	 * ivits space stations also count as satellites
	 * @return the total number of satellites in all federations
	 */
	public int satellites() ;
}
