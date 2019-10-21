package uk.ac.ed.inf.powergrab;

public class Drone {
	Position currentPos;
	double power = 250.0;
	double coins = 0.0;
	int seed;
	
	public Drone(Position startingPos, int seed) {
		this.currentPos = startingPos;
		this.seed = seed;
	}
	
	
}
