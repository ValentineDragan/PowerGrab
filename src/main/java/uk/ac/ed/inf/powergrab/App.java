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
	
	public static int moveNumber = 1;

	private static String fileName;
	private static Drone theDrone;
	
    public static void main( String[] args ) throws FileNotFoundException, IOException 
    {	
    	// Parse Arguments
    	parseArgs(args);
    	Position startingPos = new Position(startingLat, startingLong);
    	
    	// Load Map
    	Map.Map("http://homepages.inf.ed.ac.uk/stg/powergrab/" + date + "/powergrabmap.geojson");
    	
    	// Initialise Input/Output Manager and Drone initial parameters
    	InputOutputManager.InputOutputManager(fileName);
    	InputOutputManager.openTextWriter();
    	if (droneType.equals("stateless"))
    		theDrone = new StatelessDrone(startingPos, seed);
    	else
    		theDrone = new StatefulDrone(startingPos, seed);
    	
    	// Inspect starting position
    	if (!theDrone.getCurrentPos().inPlayArea())
    	{
    		System.out.println("Error! Illeigal starting position!");
    		System.exit(0);
    	}
    	
    	// Initialise GameManager and start the game
    	GameManager.GameManager(theDrone);
    	GameManager.startGame();
    	

		
		// Write drone path to geojson file; Close the Output file
		InputOutputManager.writeGeoJson(Map.getFeatureCollection(), theDrone);
		InputOutputManager.closeTextWriter();
    }
    	
    	
    	// Debugger.debug_StationsByDistance(startingPos);
    	// Debugger.debug_printStations(map);
    	// Debugger.debug_printArgs();
    	// Debugger.debug_loadMap("http://homepages.inf.ed.ac.uk/stg/powergrab/2019/09/15/powergrabmap.geojson");

    
    private static void parseArgs(String args[])
    {
    	try {
	    	date = args[2] + "/" + args[1] + "/" + args[0];
	    	startingLat = Double.parseDouble(args[3]);
	    	startingLong = Double.parseDouble(args[4]);
	    	seed = Integer.parseInt(args[5]);
	    	droneType = args[6];
	    	fileName = args[6]+"-"+args[0]+"-"+args[1]+"-"+args[2];
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
    		System.out.println("Invalid arguments! Please enter valid input arguments. \n" + "Example: 15 09 2019 55.944425 -3.188396 5678 stateless");
    		System.exit(0);
    	}
    }    
}
