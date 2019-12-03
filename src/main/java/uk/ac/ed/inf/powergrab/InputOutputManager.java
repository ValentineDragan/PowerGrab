package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class InputOutputManager {

	protected static String date;
	private static double startingLat, startingLong;
	private static int seed;
	private static String droneType;
	
	private static String fileName;
	private static PrintWriter textWriter = null;
	private static PrintWriter geojsonWriter;
	
	/** Parse the input arguments and use them to initialise the class variables.
	 *  Also constructs the fileName of the output files.	 * 
	 * @param args - the arguments received from the user's input
	 */
    protected static void parseArgs(String args[])
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

	/** Writes down the Drone's move in the text file.
	 *  The moves are written one by one.
	 * @param drone - the Drone that performed the move
	 * @param direction - the Direction in which the Drone moved
	 * @throws FileNotFoundException - if the text file is not found
	 * @throws IOException - if the IO operation is interrupted
	 */
    protected static void writeDroneMove(Drone drone, Direction direction) throws FileNotFoundException, IOException
    {
    	Position previousPos = drone.getLastMove();
    	Position currPos = drone.getCurrentPos();
    	textWriter.println(String.format("%f,%f,%s,%f,%f,%.6f,%.6f", previousPos.latitude, previousPos.longitude, 
    			direction, currPos.latitude, currPos.longitude, drone.getCoins(), drone.getPower()));
    }
    
    /** Draws the drone's movement on the Map and writes it in the GeoJson file
     *  The entire movement is drawn at the end by accessing the drone's moveHistory.
     * @param drone - the drone whose movement will be drawn
     * @throws FileNotFoundException - if the output file is not found
     * @throws IOException - if the IO operation is interrupted
     */
    protected static void writeGeoJson(Drone drone) throws FileNotFoundException, IOException
    {
    	FeatureCollection feature_collection = Map.getFeatureCollection();
    	
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
    
	protected static void openTextWriter() throws FileNotFoundException, IOException {
		textWriter = new PrintWriter(fileName+".txt", "UTF-8");
	}
	
	protected static void closeTextWriter() throws FileNotFoundException, IOException {
		textWriter.close();
	}
	
    public static String getDate() {
		return date;
	}

	public static void setDate(String date) {
		InputOutputManager.date = date;
	}

	public static double getStartingLat() {
		return startingLat;
	}

	public static double getStartingLong() {
		return startingLong;
	}

	public static int getSeed() {
		return seed;
	}

	public static String getDroneType() {
		return droneType;
	}
}
