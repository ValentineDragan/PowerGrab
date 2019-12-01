package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapFunctions {

    // Return the nearest station from the origin
    protected static Station getNearestStation(Position origin)
    {
    	return getStationsByDistance(origin).get(0);
    }
    
    // Returns a List of all stations, ordered by distance from the origin
    protected static List<Station> getStationsByDistance(Position origin)
    {
    	List<Station> stations_sorted = new ArrayList(Map.getStations());
    	Collections.sort(stations_sorted, new DistanceComparator(origin));
    	return stations_sorted;
    }
    
    
    // Returns the direction the origin-point needs to move in order to reach within 0.00025 degrees of the destination
    // If there are multiple directions, the function returns the one that would bring it closest to the destination
    // Returns null if there are no directions that reach the destination
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
    
    
	protected static double totalCoinsOnMap(ArrayList<Station> stations)
	{
		double sum = 0;
		for (Station s: stations)
			if (s.getMoney() > 0)
				sum += s.getMoney();
		return sum;
	}
}
