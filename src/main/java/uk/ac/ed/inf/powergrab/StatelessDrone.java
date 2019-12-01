package uk.ac.ed.inf.powergrab;

import java.util.Random;

public class StatelessDrone extends Drone{

	StatelessLogic droneLogic;
	
	public StatelessDrone(Position startingPos, int seed) {
		super(startingPos, seed);
		droneLogic = new StatelessLogic(this.rnd);
	}
	
	@Override
	public Direction getNextMove()
	{
		return droneLogic.getNextMove(this.getCurrentPos());
	}

}
