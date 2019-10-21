package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Stateless extends Drone {

	App appManager;
	
	public Stateless(Position startingPos, int seed, App app) {
		super(startingPos, seed);
		appManager = app;
		// TODO Auto-generated constructor stub
	}
	
	/* STATELESS DRONE STRATEGY:
	 *    1. Find which stations are within 1 move away
	 *    	1a. Sort all stations based on distance from the drone
	 *    	1b. Exclude stations which are more than 0.00055 degrees away (not reachable)
	 *    	1c. For each reachable station, compute the Direction which would reach it
	 *    
	 *    2. Return the best Direction
	 *    	If there is at least one positive station:
	 *    	2a. Return the Direction that would reach the best station
	 *    
	 *    	Otherwise:
	 *    	2b. Exclude Directions which would reach a negative station
	 *   	2c. From the remaining Directions, pick one at random
	 *   	If the drone is within playArea after moving in the selected direction
	 *   		2d. Return the chosen random Direction
	 *   	Otherwise: Repeat from 2c
	 */
	public Direction getNextMove()
	{
		// Step 1a
		List<Station> stations = appManager.getStationsByDistance(this.currentPos);
		
		// Step 1b
		for (Station station: stations)
		{
			if (currentPos.getDist(station.position) > 0.00055)
				stations.remove(station);
		}
		
		// Step 1c
		HashMap<Station, Direction> reachable_stations = new HashMap<Station, Direction>();
		for (Station station: stations)
		{
			Direction direction = appManager.directionToReach(currentPos, station.position);
			if (direction != null)
				reachable_stations.put(station, direction);
		}
		
		// Step 2
		Station best = chooseBestStation(new ArrayList<Station>(reachable_stations.keySet()));
		
		if (best != null)
			// Step 2a
			return reachable_stations.get(best);
		else
		{
			// Step 2b
			List<Direction> allDirections = new ArrayList<Direction>(Arrays.asList(Direction.values()));
			for (Station station: reachable_stations.keySet())
			{
				if (station.symbol == "danger")
				{
					allDirections.remove(reachable_stations.get(station));
				}
			}
			
			// Step 2c and 2d
			Direction result = null;
			do {
				Random rnd = new Random(this.seed);
				result = allDirections.get(rnd.nextInt(allDirections.size()));
			} while (!currentPos.nextPosition(result).inPlayArea());
			
			// DEBUGGING
			if (result == null)
			{
				System.out.println("Stateless returns NULL in getNextMove() !!!");
				return Direction.N;
			}
			return result;
		}
	}
	
	
	// Given a list of stations, return the best station
	// If power < 62.5, returns the Station with the highest power value
	// Otherwise, returns the Station with the highest money value
	public Station chooseBestStation(List<Station> stations)
	{
		if (this.power < 62.5)
			Collections.sort(stations, new SortByPower());
		else
			Collections.sort(stations, new SortByMoney());
		
		Station best = stations.get(stations.size() - 1);
		if (best.symbol == "danger")
			return null;
		else
			return best;
	}

	// Comparator: sorts Stations based on power
	class SortByPower implements Comparator<Station>
	{
		@Override
		public int compare(Station s1, Station s2) {
			// TODO Auto-generated method stub
			return Double.compare(s1.power, s2.power);
		}
	}
	
	// Comparator: sorts Stations based on power
	class SortByMoney implements Comparator<Station>
	{
		@Override
		public int compare(Station s1, Station s2) {
			// TODO Auto-generated method stub
			return Double.compare(s1.money, s2.money);
		}
	}

	
	
	
}
