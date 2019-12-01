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

	private String fileName;
	private PrintWriter textWriter = null;
	private PrintWriter geojsonWriter;
	
	public InputOutputManager(String fileStr) 
	{
		fileName = fileStr;
	}
	
    // Writes a drone's move in the text file
    protected void writeDroneMove(DroneModel drone, Direction direction) throws FileNotFoundException, IOException
    {
    	Position previousPos = drone.getLastMove();
    	Position currPos = drone.getCurrentPos();
    	textWriter.println(String.format("%f,%f,%s,%f,%f,%.6f,%.6f", previousPos.latitude, previousPos.longitude, 
    			direction, currPos.latitude, currPos.longitude, drone.getCoins(), drone.getPower()));
    }
    
    protected void writeGeoJson(FeatureCollection feature_collection, DroneModel drone) throws FileNotFoundException, IOException
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
    
	public void openTextWriter() throws FileNotFoundException, IOException
	{
		textWriter = new PrintWriter(fileName+".txt", "UTF-8");
		
	}
	
	public void closeTextWriter() throws FileNotFoundException, IOException
	{
		textWriter.close();
	}

	
}
