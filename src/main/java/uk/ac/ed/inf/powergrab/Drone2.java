package uk.ac.ed.inf.powergrab;

public interface Drone2 {
	
	public Direction getNextMove();
	
	public Position getCurrentPos();
	
	public double getPower();
	
	public double getCoins();
	
	public int getSeed();
	
	public Position getLastMove();
	
	
}
