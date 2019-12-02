package uk.ac.ed.inf.powergrab;

public interface DroneLogic {
	
	// This method must return the Drone's chosen next move.
	// This method will be called by the GameManager each turn.
	public Direction getNextMove(Position currentPos);
}
