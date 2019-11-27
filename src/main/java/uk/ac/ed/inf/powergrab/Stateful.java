package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;

public class Stateful extends Drone {

	public Stateful(Position position, int seed) {
		super(position, seed);
		// TODO Auto-generated constructor stub
		ArrayList<Station> greedyRoute = planGreedyRoute();
	}
	
	
	@Override
	public Direction getNextMove()
	{
		return null;
	}
	
	
	
	/* Plans a greedy (nearest station) route that would go through all the stations
	 * The distance function used is an approximation of how many moves it takes to get from station A to station B
	 * Returns a planned route of what stations to visit, in order
	 * */
	private ArrayList<Station> planGreedyRoute()
	{
		Position currentPos = this.currentPos;
		ArrayList<Station> remainingStations = new ArrayList<Station>(Map.getStations());
		ArrayList<Station> plannedRoute = new ArrayList<Station>();
		int totalEstimatedMoves = 0; // used only for debugging
		
		// Repeat until all Stations have been added to the path
		while(!remainingStations.isEmpty())
		{
			Station closestStation = null;
			int minimumMoves = Integer.MAX_VALUE;
			
			// Find the closest Station from the current position
			for (Station station: remainingStations)
			{
				int approximateMoves = (int) Math.ceil(currentPos.getDist(station.position) / 0.0003);
				if (approximateMoves < minimumMoves)
				{
					minimumMoves = approximateMoves;
					closestStation = station;
				}
			}
			// Add the station to the path and update position. 
			// Remove the station from the initial List so it doesn't get visited twice
			System.out.println("Next closest station is " + minimumMoves + " moves away");
			currentPos = closestStation.position;
			plannedRoute.add(closestStation);
			remainingStations.remove(closestStation);
			
			totalEstimatedMoves += minimumMoves; // only for debugging
		}
		System.out.println("Total estimated moves: " + totalEstimatedMoves);
		
		
		return plannedRoute;
	}

}
