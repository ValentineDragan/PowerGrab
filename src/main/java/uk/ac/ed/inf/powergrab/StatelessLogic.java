package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class StatelessLogic implements DroneLogic {
	
	Random rnd;
	
	public StatelessLogic(Random rnd)
	{
		this.rnd = rnd;
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
	 *    	2c. Exclude Illegal directions (which would take you out of the play area)
	 *   	2d. From the remaining Directions, return one at random
	 */
	@Override
	public Direction getNextMove(Position currentPos)
	{
		// Step 1a
		List<Station> stations = new ArrayList(MapFunctions.getStationsByDistance(currentPos));
		
		System.out.println("Total number of stations: " + stations.size());
		
		// Step 1b
		stations.removeIf(s -> currentPos.getDist(s.getPosition()) > 0.00055);
		
		/* ConcurrentModificationException
		for (Station station: stations)
		{
			System.out.println("test");
			if (currentPos.getDist(station.position) > 0.00055)
				stations.remove(station);
		}*/
		
		// Step 1c
		HashMap<Station, Direction> reachable_stations = new HashMap<Station, Direction>();
		for (Station station: stations)
		{
			Direction direction = MapFunctions.directionToReach(currentPos, station.getPosition());
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
				if (station.getSymbol().equals("danger"))
					allDirections.removeIf(d -> currentPos.nextPosition(d).inRange(station.getPosition()));
			}
			
			// Step 2c
			allDirections.removeIf(d -> !currentPos.nextPosition(d).inPlayArea());
			
			// Step 2d
			if (!allDirections.isEmpty())
			{
				return allDirections.get(rnd.nextInt(allDirections.size()));
			}
			// DEBUGGING
			else
			{
				System.out.println("Stateless returns NULL in getNextMove() !!!");
				return Direction.N;
			}
		}
	}

		// Given a list of stations, returns the Station with the highest money value
		// Returns null if there is no positive station in the list
		private Station chooseBestStation(List<Station> stations)
		{
			// check that the list is not empty
			if (stations.isEmpty())
				return null;
			
			Collections.sort(stations, new SortByMoney());
			
			Station best = stations.get(stations.size() - 1);
			if (best.getMoney() <= 0)
				return null;
			else
				return best;
		}

		// Comparator: sorts Stations based on power
		class SortByPower implements Comparator<Station>
		{
			@Override
			public int compare(Station s1, Station s2) {
				return Double.compare(s1.getPower(), s2.getPower());
			}
		}
		
		// Comparator: sorts Stations based on power
		class SortByMoney implements Comparator<Station>
		{
			@Override
			public int compare(Station s1, Station s2) {
				return Double.compare(s1.getMoney(), s2.getMoney());
			}
		}

}
