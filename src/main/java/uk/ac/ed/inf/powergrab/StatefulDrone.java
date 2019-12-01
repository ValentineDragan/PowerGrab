package uk.ac.ed.inf.powergrab;

public class StatefulDrone extends DroneModel {

	StatefulLogic droneLogic;
	
	
	public StatefulDrone(Position startingPos, int seed) {
		super(startingPos, seed);
		
		// Random() is not used in the StatefulLogic, so 'seed' is not needed
		droneLogic = new StatefulLogic(this.getCurrentPos());
	}
	
	@Override
	public Direction getNextMove()
	{
		return droneLogic.getNextMove(this.getCurrentPos());
	}
	
	
}
