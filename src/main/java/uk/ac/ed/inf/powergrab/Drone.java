package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Drone {
	private Position currentPos;
	private double power = 250.0;
	private double coins = 0.0;
	private int seed;
	
	protected Random rnd;
	protected List<Position> moveHistory = new ArrayList<Position>();
	
	
	public Drone(Position startingPos, int seed) {
		this.currentPos = startingPos;
		this.seed = seed;
		rnd = new Random(seed);
		
		moveHistory.add(startingPos);
	}
	
	// To be Overridden by inheriting classes
	public Direction getNextMove()
	{
		return null;
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
	
	public Position getLastMove() {
		return moveHistory.get(moveHistory.size() - 2);
	}
	
	protected void move(Direction direction)
	{
    	// Ensure the direction is not taking the drone outside the playArea
    	if (currentPos.nextPosition(direction).inPlayArea())
    		System.out.println("Illegal move! Drone would go outside the play area!");
    	else 
    	{
	    	power -= 1.25;
			currentPos = currentPos.nextPosition(direction);
			moveHistory.add(currentPos);
	    }
	}
	
	protected void charge(double coinsAmount, double powerAmount)
	{
		coins += coinsAmount;
		power += powerAmount;
	}

}
