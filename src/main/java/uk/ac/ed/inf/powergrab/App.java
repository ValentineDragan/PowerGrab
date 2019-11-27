package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
	//private static Map map;
	
	private static int moveNumber = 0;
	
	private static String fileName;
	private static PrintWriter textWriter;
	private static PrintWriter geojsonWriter;
	
    public static void main( String[] args ) throws FileNotFoundException, IOException
    {
    	// load Args
    	loadArgs(args);
    	Position startingPos = new Position(startingLat, startingLong);
    	
    	// load Map
    	Map.Map("http://homepages.inf.ed.ac.uk/stg/powergrab/2019/09/15/powergrabmap.geojson");
    	
    	   	
    	if (droneType.equals("stateless"))
    	{
    		// Initialise the drone and Output files
    		Stateless theDrone = new Stateless(startingPos, seed);
    		fileName = "Stateless-"+args[0]+"-"+args[1]+"-"+args[2];
    		textWriter = new PrintWriter(fileName+".txt", "UTF-8");
    		
    		// Inspect current position
    		if (!theDrone.currentPos.inPlayArea())
    			System.out.println("Error! Illeigal starting position!");
    		else
    		{
    			// Repeat until 250 moves, or insufficient energy to move
    			while (moveNumber < 250 && theDrone.power >= 1.25)
    			{
    				moveNumber += 1;
    				// Decide in which direction to move
    				Direction nextMove = theDrone.getNextMove();
    				
    				// Move the drone and charge from nearest station (if in range)
    				theDrone.move(nextMove);
    				chargeDrone(theDrone);
    				
    				// Write in the text file
    				writeDroneMove(theDrone, nextMove);
    				
    				// DEBUGGING
    				System.out.println("-------------------------------------");
    				System.out.println("Move " + moveNumber + ": " + nextMove);
    			}
    		}
    		
			// DEBUGGING
			System.out.println("Total number of coins: " + theDrone.coins);
			System.out.println("Total number of power: " + theDrone.power);
			
			// Write drone path to geojson file
			writeGeoJson(Map.getFeatureCollection(), theDrone);
    		textWriter.close();
    	}
    	else
    	{
    		Stateful theDrone = new Stateful(startingPos, seed);
    		
    	}
    	
    	
    	
    	
    	// Debugger.debug_StationsByDistance(startingPos);
    	// Debugger.debug_printStations(map);
    	// Debugger.debug_printArgs();
    	// Debugger.debug_loadMap("http://homepages.inf.ed.ac.uk/stg/powergrab/2019/09/15/powergrabmap.geojson");
    		
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
        		System.out.println("Warning! Drone charged from an empty station! id: " + nearest_station.id);
        	else
        		System.out.println("Drone charged. Power: " + nearest_station.power + "; money: " + nearest_station.money + "; id: " + nearest_station.id);
    		
    		// charge the drone
    		double coinsChargeAmount = nearest_station.money;
    		double powerChargeAmount = nearest_station.power;
    		
    		drone.coins += coinsChargeAmount;
    		drone.power += Math.max(-drone.power, powerChargeAmount);
    		
    		// update the Station
    		Map.updateStation(nearest_station, coinsChargeAmount, powerChargeAmount);
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
    	List<Station> stations_sorted = new ArrayList(Map.getStations());
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
    
    // Writes a drone's move in the text file
    private static void writeDroneMove(Drone drone, Direction direction) throws FileNotFoundException, IOException
    {
    	Position previousPos = drone.getLastMove();
    	Position currPos = drone.getCurrentPos();
    	textWriter.println(String.format("%f,%f,%s,%f,%f,%f,%f", previousPos.latitude, previousPos.longitude, direction,
    					currPos.latitude, currPos.longitude, drone.coins, drone.power));	
    }
    
    
    private static void writeGeoJson(FeatureCollection feature_collection, Drone drone) throws FileNotFoundException, IOException
    {
    	// Convert moveHistory to List<Point>
    	List<Point> points = new ArrayList<Point>();
    	for (Position position: drone.moveHistory)
    	{
    		Point point = Point.fromLngLat(position.longitude, position.latitude);
    		points.add(point);
    	}
    	
    	// Create LineString
    	LineString lineString = LineString.fromLngLats(points);
    	
    	// Add the lineString feature to the Feature Collection
    	Feature lineFeature = Feature.fromGeometry(lineString);
    	feature_collection.features().add(lineFeature);
    	
    	// Print it to the file
		geojsonWriter = new PrintWriter(fileName+".geojson", "UTF-8");
		geojsonWriter.print(feature_collection.toJson());
		geojsonWriter.close();
    }
}
