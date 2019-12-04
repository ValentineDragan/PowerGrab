package uk.ac.ed.inf.powergrab;

public interface DroneLogic {
	
	// This method must return the Drone's chosen next move, based on its current Position.
	public Direction getNextMove(Position currentPos);
}
