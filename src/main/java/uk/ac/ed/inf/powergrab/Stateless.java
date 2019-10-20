package uk.ac.ed.inf.powergrab;

public class Stateless extends Drone {

	public Stateless(Position startingPos) {
		super(startingPos);
		// TODO Auto-generated constructor stub
	}

	// Returns a list of the positions of all reachable station
	// We use the term 'reachable' to describe a station within 0.00025 degrees of the Drone, after the Drone has performed a move
	public Position[] reachable_stations()
	{
		return null;
	}
	
}
