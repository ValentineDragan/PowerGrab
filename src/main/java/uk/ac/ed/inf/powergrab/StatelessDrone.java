package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StatelessDrone implements Drone2 {

	protected Position currentPos;
	protected double power = 250.0;
	protected double coins = 0.0;
	protected int seed;
	
	private Random rnd;
	
	public List<Position> moveHistory = new ArrayList<Position>();
	
	
	public StatelessDrone(Position startingPos, int seed) {
		this.currentPos = startingPos;
		this.seed = seed;
		rnd = new Random(seed);
		
		moveHistory.add(startingPos);
	}
	
	@Override
	public Direction getNextMove() {
		
		int x = 0;
		return Direction.N;
	}

	@Override
	public Position getCurrentPos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getPower() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getCoins() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Position getLastMove() {
		// TODO Auto-generated method stub
		return null;
	}

}
