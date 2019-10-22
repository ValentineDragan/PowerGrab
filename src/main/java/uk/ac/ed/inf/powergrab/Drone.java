package uk.ac.ed.inf.powergrab;

import java.util.Random;

public class Drone {
	protected Position currentPos;
	protected double power = 250.0;
	protected double coins = 0.0;
	protected int seed;
	
	protected Random rnd;
	
	public Drone(Position startingPos, int seed) {
		this.currentPos = startingPos;
		this.seed = seed;
		rnd = new Random(seed);
	}
	
	public void move(Direction direction)
	{
		currentPos = currentPos.nextPosition(direction);
	}

	public Position getCurrentPos() {
		return currentPos;
	}

	public double getPower() {
		return power;
	}

	public double getCoins() {
		return coins;
	}

	public int getSeed() {
		return seed;
	}
	
	
	
}
