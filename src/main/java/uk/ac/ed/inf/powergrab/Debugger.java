package uk.ac.ed.inf.powergrab;

import java.util.List;

import com.mapbox.geojson.Feature;
import uk.ac.ed.inf.powergrab.MapFunctions;

public class Debugger {
	
	/* ---------------------------------------------------------- 
	 *                    DEBUGGING FUNCTIONS
	 * ---------------------------------------------------------- */
    
	// Prints out all the features extracted from the map
	protected static void printMapFeatures()
    {
        List<Feature> features = Map.getFeatureCollection().features();
        
        System.out.println("Number of features: " + features.size());
        for (Feature feature: features) {
            System.out.println(feature.toString());
        }
    }
    
    // Prints out the input arguments: date, latitude, longitude, seed, droneType
    protected static void printArgs()
    {
    	System.out.println("INPUT ARGUMENTS: \n" + InputOutputManager.getDate() + '\n' + InputOutputManager.getStartingLat() + '\n' + 
    			InputOutputManager.getStartingLong() + '\n' + InputOutputManager.getSeed() + '\n' + InputOutputManager.getDroneType());
    }
    
    // Prints out the latitude, longitude, money, power values of each station in the Map: 
    protected static void printStations()
    {
    	List<Station> stations = Map.getStations();
    	
    	System.out.println(stations.size() + " stations:");
    	for (Station station: stations)
    		System.out.println(station.getPosition().latitude + " " + station.getPosition().longitude + " " + station.getMoney() + " " + station.getPower());
    
    }
    
    // Prints out all Station IDs, ordered by their distance from the origin
    protected static void printStationsByDistance(Position origin)
    {
   	 	List<Station> sorted_stations = MapFunctions.getStationsByDistance(origin);
  	
	     System.out.println("Sorted stations: " + sorted_stations.size());
	     for (Station station: sorted_stations) {
	         System.out.println("dist: " + origin.getDist(station.getPosition()) + " --- station id: " + station.getId());
	     }
    }
    
    /** Prints out whether the drone charged from a Positive, Negative or Empty station.
     *  This method is used for debugging the Drone's performance.
     * @param station - that the drone charged from
     */
    protected static void printChargeMessage(Station station)
    {
    	String message;
		if (station.getSymbol().equals("danger"))
    		message = String.format("Mistake!!! Drone charged from the negative station (id %s) ", station.getId());
    	
		else if (station.getPower() == 0 && station.getMoney() == 0)
    		message = "Charged from empty station.";

    	else if (station.getPower() > 0 && station.getMoney() > 0)
    		message = String.format("Drone charged with Power %.6f and Money %.6f from Station id (%s)",
    				station.getPower(),station.getMoney(),station.getId());
    	else message = "Invalid Station";
		System.out.println(message);
    }
    
    // Prints out the sum of coins from all positive stations on the map
	protected static void printMaxCoins()
	{
		List<Station> stations = Map.getStations();
		stations.removeIf(s -> s.getMoney() < 0);
		double sum = 0;
		for (Station s: stations)
			if (s.getMoney() > 0)
				sum += s.getMoney();
    	System.out.printf("The maximum number of coins the drone can get is: %f\n", sum);
	}
    
}
