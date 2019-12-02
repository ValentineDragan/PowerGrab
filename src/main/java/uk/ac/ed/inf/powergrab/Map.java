package uk.ac.ed.inf.powergrab;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

import com.mapbox.geojson.*;

// used for converting the InputStream to String
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Map {

	private static FeatureCollection feature_collection = null;
	private static List<Station> stations = new ArrayList<Station>();
	
	/** Extracts the GeoJSON from the specified url.
	 *  The GeoJSON is parsed and loaded into a FeatureCollection. A list of all Stations is constructed.
	 * @param urlString - the String corresponding to the url containing the GeoJson.
	 */
	public static void loadMap(String urlString)
	{
		loadFeatureCollection(urlString);
		stations = loadStations();
	}
	
	
	/** Connects to the specified URL and loads the FeatureCollection into the feature_collection field. 
	 * @param mapString - the String corresponding to the url containing the GeoJson
	 */
	private static void loadFeatureCollection(String mapString)
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
			feature_collection = FeatureCollection.fromJson(mapSource);
		} catch ( java.net.MalformedURLException e) {
			e.printStackTrace();
			System.out.println(e + " caught in Map.loadMap() \n" + "String is not a well-formed URL!");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e + " caught in Map.loadMap() \n" + "Reading from the URL was interrupted!");
		}
	}
	
	/** Returns a List containing all the Station objects on the Map.
	 *  The information is read and parsed from the feature_collection.
	 * @return a List containing all Stations on the map.
	 */
	private static List<Station> loadStations()
	{
		List<Station> stations = new ArrayList<Station>();
		
		for (Feature f: feature_collection.features())
		{
			String id = f.getProperty("id").getAsString();
			double coins = f.getProperty("coins").getAsDouble();
			Double power = f.getProperty("power").getAsDouble();
			String symbol = f.getProperty("marker-symbol").getAsString();
			String color = f.getProperty("marker-color").getAsString();
			double lon = ((Point) f.geometry()).coordinates().get(0);
			double lat = ((Point) f.geometry()).coordinates().get(1);
			
			Station s = new Station(id, coins, power, symbol, color, lon, lat);
			stations.add(s);
		}
		
		return stations;
	}
	
	/** Updates the given Station after the drone charged.
	 * @param station - the Station that will be updated.
	 * @param coinsAmount - the amount in coins that was taken by the drone (negative value if the Station was positive, and vice-versa)
	 * @param powerAmount - the amount in power that was taken by the drone (negative value if the Station was positive, and vice-versa)
	 */
	protected static void updateStation(Station station, double coinsAmount, double powerAmount)
	{
		Station theStation = stations.get(stations.indexOf(station));
		theStation.takeMoney(coinsAmount);
		theStation.takePower(powerAmount);		
	}
	
	// Returns a copy of the Stations on the map. Editing this copy will not affect the real List of Stations.
	public static List<Station> getStations() {
		return new ArrayList<Station>(stations);
	}
	
	// Returns a copy of the feature_collection. Editing this copy will not affect the real FeatureCollection.
	public static FeatureCollection getFeatureCollection() {
		return FeatureCollection.fromFeatures(feature_collection.features());
	}

}
