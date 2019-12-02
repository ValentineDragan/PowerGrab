package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;


public class App 
{
	private static Drone theDrone;
	
	
	/** The main method that controls the entire program. Control is split between: 
	 * InputOutputManager: parses inputs and writes outputs
	 * Map: Connects to the URL and parses the GeoJSON Features
	 * GameManager: Controls the game rules and charges the drone
	 * @param day month year latitude longitude random_seed droneType (e.g. 15 09 2019 55.944425 -3.188396 5678 stateful)
	 */
    public static void main( String[] args ) throws FileNotFoundException, IOException 
    {	
    	// Parse Arguments
    	InputOutputManager.parseArgs(args);
    	Position startingPos = new Position(InputOutputManager.getStartingLat(), InputOutputManager.getStartingLong());
    	
    	// Load Map
    	Map.loadMap("http://homepages.inf.ed.ac.uk/stg/powergrab/" + InputOutputManager.getDate() + "/powergrabmap.geojson");
    	
    	// Visualise Information about Initial Data
    	System.out.println("---------------------------------\nInformation About Initial Data:");
    	//Debugger.printArgs();
    	//Debugger.printMapFeatures();
    	//Debugger.printStations();
    	//Debugger.printStationsByDistance(startingPos);
    	Debugger.printMaxCoins();
    	
    	// Initialise Input/Output Manager and Drone initial parameters
    	InputOutputManager.openTextWriter();
    	if (InputOutputManager.getDroneType().equals("stateless"))
    		theDrone = new StatelessDrone(startingPos, InputOutputManager.getSeed());
    	else
    		theDrone = new StatefulDrone(startingPos, InputOutputManager.getSeed());
    	
    	// Inspect starting position
    	if (!theDrone.getCurrentPos().inPlayArea())
    	{
    		System.out.println("Error! Illeigal starting position!");
    		System.exit(0);
    	}
    	
    	// Initialise GameManager and start the game
    	GameManager.initialise(theDrone);
    	GameManager.startGame();
		
		// Write drone path to GeoJSON file; Close the Output file
		InputOutputManager.writeGeoJson(theDrone);
		InputOutputManager.closeTextWriter();
    }   
}
