package uk.ac.ed.inf.powergrab;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Position {
	public final double latitude;
	public final double longitude;
	
	protected static final Map<Direction, ArrayList<Double>> directionChanges;
	
	/**
	 * Pre-compute (only once) the changes in latitude and longitude for each Direction of travel
	 */
	static {
		double r = 0.0003;
		HashMap<Direction, ArrayList<Double>> map = new HashMap<Direction, ArrayList<Double>>();
		
		map.put(Direction.N, new ArrayList<Double>(Arrays.asList(r*cosOfAngle(90), r*sinOfAngle(90))));
		map.put(Direction.NNE, new ArrayList<Double>(Arrays.asList(r*cosOfAngle(67.5), r*sinOfAngle(67.5))));
		map.put(Direction.NE, new ArrayList<Double>(Arrays.asList(r*cosOfAngle(45), r*sinOfAngle(45))));
		map.put(Direction.ENE, new ArrayList<Double>(Arrays.asList(r*cosOfAngle(22.5), r*sinOfAngle(22.5))));
		
		map.put(Direction.E, new ArrayList<Double>(Arrays.asList(r*cosOfAngle(0), -r*sinOfAngle(0))));
		map.put(Direction.ESE, new ArrayList<Double>(Arrays.asList(r*cosOfAngle(22.5), -r*sinOfAngle(22.5))));
		map.put(Direction.SE, new ArrayList<Double>(Arrays.asList(r*cosOfAngle(45), -r*sinOfAngle(45))));
		map.put(Direction.SSE, new ArrayList<Double>(Arrays.asList(r*cosOfAngle(67.5), -r*sinOfAngle(67.5))));
		
		map.put(Direction.S, new ArrayList<Double>(Arrays.asList(-r*cosOfAngle(90), -r*sinOfAngle(90))));
		map.put(Direction.SSW, new ArrayList<Double>(Arrays.asList(-r*cosOfAngle(67.5), -r*sinOfAngle(67.5))));
		map.put(Direction.SW, new ArrayList<Double>(Arrays.asList(-r*cosOfAngle(45), -r*sinOfAngle(45))));
		map.put(Direction.WSW, new ArrayList<Double>(Arrays.asList(-r*cosOfAngle(22.5), -r*sinOfAngle(22.5))));
		
		map.put(Direction.W, new ArrayList<Double>(Arrays.asList(-r*cosOfAngle(0), r*sinOfAngle(0))));
		map.put(Direction.WNW, new ArrayList<Double>(Arrays.asList(-r*cosOfAngle(22.5), r*sinOfAngle(22.5))));
		map.put(Direction.NW, new ArrayList<Double>(Arrays.asList(-r*cosOfAngle(45), r*sinOfAngle(45))));
		map.put(Direction.NNW, new ArrayList<Double>(Arrays.asList(-r*cosOfAngle(67.5), r*sinOfAngle(67.5))));
		
		 directionChanges = Collections.unmodifiableMap(map);
	}
	
	public Position(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	// 
	/** Returns the next position of the drone after it moves in the specified Direction
	 * @param direction - one of the 16 Directions to move in.
	 * @return the Position in which the drone would land
	 */
	public Position nextPosition(Direction direction)
	{
		double longChange = directionChanges.get(direction).get(0);
		double latChange = directionChanges.get(direction).get(1);
		return new Position(this.latitude + latChange, this.longitude + longChange);
	}
	
	// Returns whether or not this Position lies in the PowerGrab play area
	public boolean inPlayArea()
	{
		if (this.longitude <= -3.192473 || this.longitude >= -3.184319
			|| this.latitude <= 55.942617 || this.latitude >=  55.946233 )
			return false;
		else 
			return true;
	}
	
	
	// 
	/** Returns whether or not this Position lies within 0.00025 degrees of the destination
	 * @param destination - the Position of destination (e.g. a Station's position)
	 * @return True if this Position lies within 0.00025 degrees of the destination, False otherwise
	 */
	public boolean inRange(Position destination)
	{
		return (getDist(destination) <= 0.00025);
	}
	
	/** Returns the distance from this Position to the destination
	 * @param destination - the Position of destination (e.g. a Station's position)
	 * @return the euclidean distance between the two Positions
	 */
	public double getDist(Position destination)
	{
		double deltaLat = this.latitude - destination.latitude;
		double deltaLong = this.longitude - destination.longitude;
		return Math.sqrt(deltaLat*deltaLat + deltaLong*deltaLong);
	}
	
	/* ---------------------------------------------------------- 
	 *            HELPER FUNCTIONS (Cosine and Sine)
	 * ---------------------------------------------------------- */
	
	// Returns the sine value of an angle, in degrees
	private static double sinOfAngle(double angDeg) {
		return Math.sin(Math.toRadians(angDeg));
	}
	
	// Returns the cosine value of an angle, in degrees
	private static double cosOfAngle(double angDeg) {
		return Math.cos(Math.toRadians(angDeg));
	}
}
