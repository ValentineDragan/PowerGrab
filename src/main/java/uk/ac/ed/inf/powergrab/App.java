package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class App 
{
	private static String date;
	private static double startingLat, startingLong;
	private static int seed;
	private static String droneType;
	private static Map map;
	
	private static int moveNumber = 0;
	
    public static void main( String[] args )
    {
    	// load Args
    	loadArgs(args);
    	Position startingPos = new Position(startingLat, startingLong);
    	
    	// load Map
    	map = new Map("http://homepages.inf.ed.ac.uk/stg/powergrab/2019/09/15/powergrabmap.geojson");
    	
    	// debug_StationsByDistance(startingPos);
    	
    	// System.out.println(droneType == "stateless");
    	if (droneType.equals("stateless"))
    	{
    		Stateless theDrone = new Stateless(startingPos, seed);
    		// Inspect current position
    		if (!theDrone.currentPos.inPlayArea())
    			System.out.println("Error! Illeigal starting position!");
    		else
    		{
    			// Repeat until 250 moves, or insufficient energy to move
    			while (moveNumber < 250 && theDrone.power >= 1.25)
    			{
    				moveNumber += 1;
    				Direction nextMove = theDrone.getNextMove();
    				
    				System.out.println("Move " + moveNumber + ": " + nextMove);
    				
    				theDrone.move(nextMove);
    				chargeDrone(theDrone);
    			}
    			System.out.println("Total number of coins: " + theDrone.coins);
    			System.out.println("Total number of power: " + theDrone.power);
    		}
    	}
    	else
    	{
    		Stateful theDrone = new Stateful(startingPos, seed);
    		System.out.println("stateful drone not yet implemented!");
    	}
    	
    	
    	
    	
    	
    	// debug_printStations(map);
    	// debug_printArgs();
    	// debug_loadMap("http://homepages.inf.ed.ac.uk/stg/powergrab/2019/09/15/powergrabmap.geojson");
    		
    }
    
    
    // Moves the drone in the chosen direction
    public static void makeMove(Drone drone, Direction direction)
    {
    	// Ensure the direction is not taking the drone outside the playArea
    	if (!drone.currentPos.nextPosition(direction).inPlayArea())
    		System.out.println("Illegal move! Drone would go outside the play area!");
    	else
    		drone.move(direction);
    }
    
    
    // Attempt to charge drone from the nearest station
    // Will print out the result
    public static void chargeDrone(Drone drone)
    {
    	List<Station> stations_sorted = getStationsByDistance(drone.currentPos);
    	Station nearest_station = stations_sorted.get(0);
    	
    	// If the nearest station is within range, charge the drone
    	if (drone.currentPos.inRange(nearest_station.position)) 
    	{
    		if (nearest_station.symbol.equals("danger"))
        		System.out.println("Danger! Drone charged from a negative station! id: " + nearest_station.id);
        	else if (nearest_station.power == 0 && nearest_station.money == 0)
        		System.out.println("Warning! Dron charged from an empty station! id: " + nearest_station.id);
        	else
        		System.out.println("Drone charged. Power: " + nearest_station.power + "; money: " + nearest_station.money + "; id: " + nearest_station.id);
    		
    		// charge the drone
    		double coinsChargeAmount = nearest_station.money;
    		double powerChargeAmount = nearest_station.power;
    		
    		drone.coins += coinsChargeAmount;
    		drone.power += Math.max(-drone.power, powerChargeAmount);
    		
    		// update the Station
    		map.updateStation(nearest_station, coinsChargeAmount, powerChargeAmount);
    	}
    	else
    		System.out.println("No station in range");
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
    	List<Station> stations_sorted = new ArrayList(map.getStations());
    	Collections.sort(stations_sorted, new DistanceComparator(origin));
    	return stations_sorted;
    }
    

    // Returns the direction the origin-point needs to move in order to reach within 0.00025 degrees of the destination
    // If there are multiple directions, the function returns the one that would bring it closest to the destination
    // Returns null if there are no directions that reach the destination
    public static Direction directionToReach(Position origin, Position destination)
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
