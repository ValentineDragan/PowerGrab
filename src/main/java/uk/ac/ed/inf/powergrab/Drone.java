package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Drone {
	private Position currentPos;
	private double power = 250.0;
	private double coins = 0.0;
	protected Random rnd;
	
	protected List<Position> moveHistory = new ArrayList<Position>();
	
	public Drone(Position startingPos, int seed) {
		this.currentPos = startingPos;
		rnd = new Random(seed);
		
		moveHistory.add(startingPos);
	}
	
	public Direction getNextMove() {
		return null;
	}
	
	/** Moves the drone in the specified direction and updates power and currentPos. Adds the move to the moveHistory list.
	 * If the move is illegal (goes outside the play area, or it's not the Drone's turn to move) 
	 * the program prints an error message and stops.
	 * @param direction - the direction to move into
	 */
	protected void move(Direction direction)
	{
    	// Ensure the Drone is allowed to move
    	if (!GameManager.getMoveAllowed()) {
    		System.out.println("Illegal move! It's not the Drone's turn to move!");
    		System.exit(0);
    	}
    	// Ensure the direction is not taking the drone outside the playArea
    	else if (!currentPos.nextPosition(direction).inPlayArea()) {
    		System.out.println("Illegal move! Drone would go outside the play area!");
        	System.exit(0);
    	}
    	else 
    	{
	    	power -= 1.25;
			currentPos = currentPos.nextPosition(direction);
			moveHistory.add(currentPos);
	    }
	}
	
	/** Charges the drone.
	 * @param coinsAmount - the amount to charge (can be negative)
	 * @param powerAmount - the amount to charge (can be negative)
	 */
	protected void charge(double coinsAmount, double powerAmount)
	{
		coins += coinsAmount;
		power += powerAmount;
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
	
	public Position getLastMove() {
		return moveHistory.get(moveHistory.size() - 2);
	}

}
