package uk.ac.ed.inf.powergrab;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Position {
	public double latitude;
	public double longitude;
	
	public Position(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	// Returns the next position of the drone when it makes a move in the specified compass direction
	public Position nextPosition(Direction direction)
	{
		double latChange=0, longChange=0;
		double r = 0.0003;
		switch (direction) 
		{
			case N: { longChange = r*cosOfAngle(90); latChange = r*sinOfAngle(90); break; }
			case NNE: { longChange = r*cosOfAngle(67.5); latChange = r*sinOfAngle(67.5); break; }
			case NE: { longChange = r*cosOfAngle(45); latChange = r*sinOfAngle(45); break; }
			case ENE: { longChange = r*cosOfAngle(22.5); latChange = r*sinOfAngle(22.5); break; }
				
			case E: { longChange = r*cosOfAngle(0); latChange = -r*sinOfAngle(0); break; }
			case ESE: { longChange = r*cosOfAngle(22.5); latChange = -r*sinOfAngle(22.5); break; }
			case SE: { longChange = r*cosOfAngle(45); latChange = -r*sinOfAngle(45); break; }
			case SSE: { longChange = r*cosOfAngle(67.5); latChange = -r*sinOfAngle(67.5); break; }
				
			case S: { longChange = -r*cosOfAngle(90); latChange = -r*sinOfAngle(90); break; }
			case SSW: { longChange = -r*cosOfAngle(67.5); latChange = -r*sinOfAngle(67.5); break; }
			case SW: { longChange = -r*cosOfAngle(45); latChange = -r*sinOfAngle(45); break; }
			case WSW: { longChange = -r*cosOfAngle(22.5); latChange = -r*sinOfAngle(22.5); break; }
				
			case W: { longChange = -r*cosOfAngle(0); latChange = r*sinOfAngle(0); break; }
			case WNW: { longChange = -r*cosOfAngle(22.5); latChange = r*sinOfAngle(22.5); break; }
			case NW: { longChange = -r*cosOfAngle(45); latChange = r*sinOfAngle(45); break; }
			case NNW: { longChange = -r*cosOfAngle(67.5); latChange = r*sinOfAngle(67.5); break; }
		}
		return  new Position(this.latitude + latChange, this.longitude + longChange);
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
	
	// Returns a Map<Direction, Double> containing all Directions which would reach within 0.00025 degrees of the destination.
	// The result is sorted in terms of distance between the current position and the destination, in ascending order.
	// If the destination is not reachable, it returns null
	public Map<Direction, Double> reaching_directions(Position destination)
	{
		Map<Direction, Double> directions = new HashMap<Direction, Double>();
		
		for (Direction direction: Direction.values()) {
			double dist = nextPosition(direction).getDist(destination);
			if (dist <= 0.00025)
				directions.put(direction, dist);
		}
		
		if (!directions.isEmpty()) {
			directions = sortByValue(directions);
			return directions;
		}
		else
			return null;
	}
	
	// Returns whether or not this position lies within 0.00025 of the destination
	public boolean inRange(Position destination)
	{
		return (getDist(destination) <= 0.00025);
	}
	
	// returns the distance from the current position to the destination
	public double getDist(Position destination)
	{
		double deltaLat = this.latitude - destination.latitude;
		double deltaLong = this.longitude - destination.longitude;
		return Math.sqrt(deltaLat*deltaLat + deltaLong*deltaLong);
	}
	
	/* ---------------------------------------------------------- 
	 *                    HELPER FUNCTIONS
	 * ---------------------------------------------------------- */
	
	// Returns the sine value of an angle, in degrees
	private double sinOfAngle(double angDeg)
	{
		return Math.sin(Math.toRadians(angDeg));
	}
	
	// Returns the cosine value of an angle, in degrees
	private double cosOfAngle(double angDeg)
	{
		return Math.cos(Math.toRadians(angDeg));
	}
	
	// Sorts a Map<Direction, Double> by value in ascending order
	private static Map<Direction, Double> sortByValue(final Map<Direction, Double> directions) {
        return directions.entrySet()
                .stream()
                .sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
