package uk.ac.ed.inf.powergrab;

public class StatefulDrone extends Drone {

	StatefulLogic droneLogic;
	
	public StatefulDrone(Position startingPos, int seed) {
		super(startingPos, seed);
		droneLogic = new StatefulLogic(this.getCurrentPos(), this.rnd);
	}
	
	@Override
	public Direction getNextMove()
	{
		return droneLogic.getNextMove(this.getCurrentPos());
	}
	
	
}
