package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

import com.mapbox.geojson.*;

// used for converting the InputStream to String
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
  
public class Map {

	FeatureCollection feature_collection = null;
	
	// Loads the map from the specified URL into the feature_collection field
	public void loadMap(String mapString)
	{
		// Read the URL
		try 
		{
			URL mapUrl = new URL(mapString);
			HttpURLConnection conn = (HttpURLConnection) mapUrl.openConnection();
			
			// connect to web server			
			conn.setReadTimeout(10000); //milliseconds
			conn.setConnectTimeout(15000); //milliseconds
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			
			// read the text
			InputStream stream = conn.getInputStream();
			String mapSource = IOUtils.toString(stream, StandardCharsets.UTF_8);
			this.feature_collection = FeatureCollection.fromJson(mapSource);
		} catch ( java.net.MalformedURLException e) {
			e.printStackTrace();
			//System.out.println(e + " caught in Map.loadMap() \n" + "String is not a well-formed URL!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Returns a HashMap containing the <indexes, positions> of all stations on the map
	public HashMap<Integer, Position> getAllStations()
	{
		HashMap<Integer, Position> stations = new HashMap<Integer, Position>();
		
		int index=0;
		for (Feature feature: feature_collection.features())
		{
			Point p = (Point) feature.geometry();
			Double lon = p.coordinates().get(0);
			Double lat = p.coordinates().get(1);
			
			Position pos = new Position(lat, lon);
			stations.put(index, pos);
			index += 1;
		}
		
		return stations;
	}
	
}
