package uk.ac.ed.inf.powergrab;


public class Position {
	public double latitude;
	public double longitude;
	
	public Position(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Position nextPosition(Direction direction)
	{
		double latChange=0, longChange=0;
		double r = 0.0003;
		switch (direction) 
		{
			case N: { longChange = r*Math.cos(Math.toRadians(90)); latChange = r*Math.sin(Math.toRadians(90)); break; }
			case NNE: { longChange = r*Math.cos(Math.toRadians(67.5)); latChange = r*Math.sin(Math.toRadians(67.5)); break; }
			case NE: { longChange = r*Math.cos(Math.toRadians(45)); latChange = r*Math.sin(Math.toRadians(45)); break; }
			case ENE: { longChange = r*Math.cos(Math.toRadians(22.5)); latChange = r*Math.sin(Math.toRadians(22.5)); break; }
				
			case E: { longChange = r*Math.cos(Math.toRadians(0)); latChange = -r*Math.sin(Math.toRadians(0)); break; }
			case ESE: { longChange = r*Math.cos(Math.toRadians(22.5)); latChange = -r*Math.sin(Math.toRadians(22.5)); break; }
			case SE: { longChange = r*Math.cos(Math.toRadians(45)); latChange = -r*Math.sin(Math.toRadians(45)); break; }
			case SSE: { longChange = r*Math.cos(Math.toRadians(67.5)); latChange = -r*Math.sin(Math.toRadians(67.5)); break; }
				
			case S: { longChange = -r*Math.cos(Math.toRadians(90)); latChange = -r*Math.sin(Math.toRadians(90)); break; }
			case SSW: { longChange = -r*Math.cos(Math.toRadians(67.5)); latChange = -r*Math.sin(Math.toRadians(67.5)); break; }
			case SW: { longChange = -r*Math.cos(Math.toRadians(45)); latChange = -r*Math.sin(Math.toRadians(45)); break; }
			case WSW: { longChange = -r*Math.cos(Math.toRadians(22.5)); latChange = -r*Math.sin(Math.toRadians(22.5)); break; }
				
			case W: { longChange = -r*Math.cos(Math.toRadians(0)); latChange = r*Math.sin(Math.toRadians(0)); break; }
			case WNW: { longChange = -r*Math.cos(Math.toRadians(22.5)); latChange = r*Math.sin(Math.toRadians(22.5)); break; }
			case NW: { longChange = -r*Math.cos(Math.toRadians(45)); latChange = r*Math.sin(Math.toRadians(45)); break; }
			case NNW: { longChange = -r*Math.cos(Math.toRadians(67.5)); latChange = r*Math.sin(Math.toRadians(67.5)); break; }
		}
		return  new Position(this.latitude + latChange, this.longitude + longChange);
	}
	
	public boolean inPlayArea()
	{
		if (this.longitude < -3.192473 || this.longitude > -3.184319
			|| this.latitude < 55.942617 || this.latitude >  55.946233 )
			return false;
		else 
			return true;
	}
}
