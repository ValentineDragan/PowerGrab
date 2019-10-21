package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class App 
{
	private static String date;
	private static double startingLat, startingLong;
	private static int seed;
	private static String droneType;
	private static Map map;
	
    public static void main( String[] args )
    {
    	loadArgs(args);
    	Position startingPos = new Position(startingLat, startingLong);
    	
    	map = new Map("http://homepages.inf.ed.ac.uk/stg/powergrab/2019/09/15/powergrabmap.geojson");
    	
    	
    	debug_StationsByDistance(startingPos);
    	// debug_printStations(map);
    	// debug_printArgs();
    	// debug_loadMap("http://homepages.inf.ed.ac.uk/stg/powergrab/2019/09/15/powergrabmap.geojson");
    		
    }
    
    private static void loadArgs(String args[])
    {
    	try {
	    	date = args[2] + "/" + args[1] + "/" + args[0];
	    	startingLat = Double.parseDouble(args[3]);
	    	startingLong = Double.parseDouble(args[4]);
	    	seed = Integer.parseInt(args[5]);
	    	droneType = args[6];
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
    		System.out.println("Invalid arguments! Please enter valid input arguments. \n" + "Example: 15 09 2019 55.944425 -3.188396 5678 stateless");
    		System.exit(0);
    	}
    }
    
    
    // Returns a List of all stations, ordered by distance from the origin
    public static List<Station> getStationsByDistance(Position origin)
    {
    	List<Station> stations_sorted = map.getStations();
    	Collections.sort(stations_sorted, new DistanceComparator(origin));
    	return stations_sorted;
    }
    

    // Returns the direction the origin-point needs to move in order to reach within 0.00025 degrees of the destination
    // If there are multiple directions, the function returns the one that would bring it closest to the destination
    public Direction directionToReach(Position origin, Position destination)
    {
    	Direction result = null;
    	double bestDist = Double.MAX_VALUE;
    	
    	for (Direction direction: Direction.values())
    	{
    		double dist = origin.nextPosition(direction).getDist(destination);
    		if (dist < bestDist)
    		{
    			result = direction;
    			bestDist = dist;
    		}
    	}
    	
    	if (bestDist <= 0.00025)
    		return result;
    	else
    		return null;
    }
    
    
    
    
	// Returns a Map<Direction, Double> containing all Directions which would reach within 0.00025 degrees of the destination.
	// The result is sorted in terms of distance between the current position and the destination, in ascending order.
	// If the destination is not reachable, it returns null
    /*
	public Map<Direction, Double> reaching_directions(Position destination)
	{
		Map<Direction, Double> directions = new HashMap<Direction, Double>();
		
		for (Direction direction: Direction.values()) {
			double dist = nextPosition(direction).getDist(destination);
			if (dist <= 0.00025)
				directions.put(direction, dist);
		}
		
		if (!directions.isEmpty()) {
			directions = sortByValue(directions);
			return directions;
		}
		else
			return null;
	}*/
    

	/* ---------------------------------------------------------- 
	 *                    DEBUGGING FUNCTIONS
	 * ---------------------------------------------------------- */
    
    
    // Prints out all the features extracted for the given url
    private static void debug_loadMap(String urlString)
    {
        Map map = new Map(urlString);
        List<Feature> features = map.getFeatureCollection().features();
        
        System.out.println("Number of features: " + features.size());
        for (Feature feature: features) {
            System.out.println(feature.toString());
        }
    }
    
    // Prints out the input arguments
    private static void debug_printArgs()
    {
    	System.out.println("INPUT ARGUMENTS: \n" + date + '\n' + startingLat + '\n' + 
    			startingLong + '\n' + seed + '\n' + droneType);
    }
    
    // Prints out the List of all stations from a given Map class
    private static void debug_printStations(Map map)
    {
    	List<Station> stations = map.getStations();
    	
    	System.out.println(stations.size() + " stations:");
    	for (Station station: stations)
    		System.out.println(station.position.latitude + " " + station.position.longitude + " " + station.money + " " + station.power);
    
    }
    
    // Prints out all Station IDs, ordered by distance from the origin
    private static void debug_StationsByDistance(Position origin)
    {
   	 	List<Station> sorted_stations = getStationsByDistance(origin);
  	
	     System.out.println("Sorted stations: " + sorted_stations.size());
	     for (Station station: sorted_stations) {
	         System.out.println("dist: " + origin.getDist(station.position) + " --- station id: " + station.id);
	     }
    }
}
