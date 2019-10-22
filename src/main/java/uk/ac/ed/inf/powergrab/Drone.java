package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mapbox.geojson.LineString;

public class Drone {
	protected Position currentPos;
	protected double power = 250.0;
	protected double coins = 0.0;
	protected int seed;
	
	protected Random rnd;
	
	public List<Position> moveHistory = new ArrayList<Position>();
	
	
	public Drone(Position startingPos, int seed) {
		this.currentPos = startingPos;
		this.seed = seed;
		rnd = new Random(seed);
		
		moveHistory.add(startingPos);
	}
	
	public void move(Direction direction)
	{
		power -= 1.25;
		currentPos = currentPos.nextPosition(direction);
		moveHistory.add(currentPos);
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
	
	
	
}
