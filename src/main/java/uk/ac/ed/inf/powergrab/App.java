package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class App 
{
	
	private static String date;
	private static double startingLat, startingLong;
	private static int seed;
	private static String droneType;
	
    public static void main( String[] args )
    {
    	loadArgs(args);
    	Position startingPos = new Position(startingLat, startingLong);
    	
    	Map map = new Map();
    	map.loadMap("http://homepages.inf.ed.ac.uk/stg/powergrab/2019/09/15/powergrabmap.geojson");
    	
    	// test_getAllStations(map);
    	// test_printArgs();
    	// test_loadMap();
    		
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
    
    
    
    // TESTS used for debugging
    private static void test_loadMap()
    {
        Map map = new Map();
        map.loadMap("http://homepages.inf.ed.ac.uk/stg/powergrab/2019/09/15/powergrabmap.geojson");
        
        List<Feature> features = map.feature_collection.features();
        
        System.out.println("Number of features: " + features.size() + "\n");
        
        for (Feature feature: features) {
            System.out.println(feature.toString());
        }
    }
    
    private static void test_printArgs()
    {
    	System.out.println("INPUT ARGUMENTS: \n" + date + '\n' + startingLat + '\n' + 
    			startingLong + '\n' + seed + '\n' + droneType);
    }
    
    private static void test_getAllStations(Map map)
    {
    	HashMap<Integer, Position> stations = map.getAllStations();
    	
    	for (Integer key: stations.keySet())
    	{
    		System.out.println("Station " + key + ": " + stations.get(key).latitude + " " + stations.get(key).longitude);
    	}
    
    }
}
