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

	private FeatureCollection feature_collection = null;
	private List<Station> stations = new ArrayList<Station>();
	
	// This constructor initialises the feature_collection field by extracting the Geo-JSON from the specified URL
	// and loads a List of all Stations from the map into the stations field
	public Map(String urlString)
	{
		this.loadMap(urlString);
		this.stations = this.loadStations();
	}
	
	
	// Loads the map from the specified URL into the feature_collection field
	private void loadMap(String mapString)
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
	
	// returns a List of all Station objects contained on the map
	private List<Station> loadStations()
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
	
	protected void updateStation(Station station, double coinsAmount, double powerAmount)
	{
		Station theStation = stations.get(stations.indexOf(station));
		theStation.money -= coinsAmount;
		theStation.power -= powerAmount;
		
	}
	
	public List<Station> getStations()
	{
		return new ArrayList(this.stations);
	}
	
	// Returns a copy of the feature_collection
	public FeatureCollection getFeatureCollection()
	{
		return FeatureCollection.fromFeatures(feature_collection.features());
	}
	
}
