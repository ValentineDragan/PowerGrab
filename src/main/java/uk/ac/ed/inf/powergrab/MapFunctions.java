package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapFunctions {

	/** Return the nearest station from the origin. This method is used to determine the Station that's closest to the Drone.
	 * @param origin - the Position of origin (e.g. the Drone's position).
	 * @return the Station that's closest to the origin.
	 */
    protected static Station getNearestStation(Position origin) {
    	return getStationsByDistance(origin).get(0);
    }
    
    /** Returns a List of all Stations ordered by distance from the origin.
     * This is used in the Stateless Drone to determine which Stations are within 1 move away.
     * @param origin - the Position of origin (e.g. the Drone's position)
     * @return a List of Stations ordered by distance from the origin. Also contains negative stations!
     */
    protected static List<Station> getStationsByDistance(Position origin)
    {
    	List<Station> stations_sorted = new ArrayList<Station>(Map.getStations());
    	Collections.sort(stations_sorted, new DistanceComparator(origin));
    	return stations_sorted;
    }
    
    
  
    /** Return which Direction would bring the origin Position within range of the destination.
     * This is used in the Stateless Drone to determine which Direction reaches the nearest positive station.
     * If there are multiple directions, the method returns the one that would bring it closest to the destination.
     * @param origin - the Position of origin (e.g. the Drone's position)
     * @param destination - the Position of destination (e.g. the Station's position)
     * @return Returns the Direction that would reach the destination. If there are multiple, the function returns the one that would take us 
     * closer to the destination. Returns null If there are no Direction that would reach the destination. 
     */
    protected static Direction directionToReach(Position origin, Position destination)
    {
    	Direction result = null;
    	double bestDist = Double.MAX_VALUE;
    	
    	for (Direction direction: Direction.values())
    	{
    		double dist = origin.nextPosition(direction).getDist(destination);
    		if (dist <= 0.00025 && dist < bestDist)
    		{
    			result = direction;
    			bestDist = dist;
    		}
    	}
    
    	return result;
    }
}
