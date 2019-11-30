package uk.ac.ed.inf.powergrab;

import java.util.List;

import com.mapbox.geojson.Feature;

public class Debugger {

	/* ---------------------------------------------------------- 
	 *                    DEBUGGING FUNCTIONS
	 * ---------------------------------------------------------- */
    
    
	// Print out all scores that
	/*
	public String[] getScores(String dateBeginning, String dateEnd)
	{
		
	}*/
	
	public static void Debugger()
	{}
	
    
	public static void debug_Position(Position position)
	{
		Position nextPos;
		for (Direction dir: Direction.values())
			nextPos = position.nextPosition(dir);
	}
	
	
	// Prints out all the features extracted for the given url
    public static void debug_loadMap(String urlString)
    {
        Map.Map(urlString);
        List<Feature> features = Map.getFeatureCollection().features();
        
        System.out.println("Number of features: " + features.size());
        for (Feature feature: features) {
            System.out.println(feature.toString());
        }
    }
    
    // Prints out the input arguments
    public static void debug_printArgs(String date, double startingLat, double startingLong, int seed, String droneType)
    {
    	System.out.println("INPUT ARGUMENTS: \n" + date + '\n' + startingLat + '\n' + 
    			startingLong + '\n' + seed + '\n' + droneType);
    }
    
    // Prints out the List of all stations from a given Map class
    public static void debug_printStations(Map map)
    {
    	List<Station> stations = map.getStations();
    	
    	System.out.println(stations.size() + " stations:");
    	for (Station station: stations)
    		System.out.println(station.position.latitude + " " + station.position.longitude + " " + station.money + " " + station.power);
    
    }
    
    // Prints out all Station IDs, ordered by distance from the origin
    public static void debug_StationsByDistance(Position origin)
    {
   	 	List<Station> sorted_stations = App.getStationsByDistance(origin);
  	
	     System.out.println("Sorted stations: " + sorted_stations.size());
	     for (Station station: sorted_stations) {
	         System.out.println("dist: " + origin.getDist(station.position) + " --- station id: " + station.id);
	     }
    }
    
}
