package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class Stateful extends Drone {

	public Stateful(Position position, int seed) {
		super(position, seed);
		// TODO Auto-generated constructor stub
		ArrayList<Station> greedyRoute = planGreedyRoute();
		for (int i=0; i<greedyRoute.size()-1; i++)
		{
			ArrayList<Direction> path = pathToNextStation(greedyRoute.get(i).position, greedyRoute.get(i+1));
		}
	}
	
	
	@Override
	public Direction getNextMove()
	{
		return null;
	}
	
	
	
	/* Plans a greedy (nearest station) route that would go through all the stations
	 * 
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
				int approximateMoves = approximateMovesToStation(currentPos, station);
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
	
	/* Used in the A* search algorithm
	*/
	private class Node {
		public Position position;
		public Node parentNode;
		public Direction directionFromParent;
		
		public Node(Position position, Node parentNode, Direction dir)
		{
			this.position = position;
			this.parentNode = parentNode;
			this.directionFromParent = dir;
		}
		
		// Two nodes are equal if they have the same position
		@Override
		public boolean equals(Object o)
		{
			return this.position.equals(((Node) o).position);
		}
	}
	
	
	/*
	 * A* search
	 * https://www.geeksforgeeks.org/a-search-algorithm/
	 * */
	private ArrayList<Direction> pathToNextStation(Position startingPos, Station goalStation)
	{
		TreeMap<Integer, ArrayList<Node>> nodeMap = new TreeMap<Integer, ArrayList<Node>>();
		ArrayList<Position> visitedPositions = new ArrayList<Position>();
		Node pathFound = null; 
		
		Node startingNode = new Node(startingPos, null, null);
		nodeMap.put(0, new ArrayList<Node>(Arrays.asList(startingNode)));
		
		while(!nodeMap.isEmpty())
		{
			// Find the unvisited node with the least f
			ArrayList<Node> currentEntryList = nodeMap.firstEntry().getValue();
			Node currentNode = currentEntryList.get(0);
			
			//System.out.println(currentNode.position.latitude + " " + currentNode.position.longitude);
			
			// Remove the respective node from the map
			currentEntryList.remove(0);
			if (currentEntryList.isEmpty())
				nodeMap.remove(nodeMap.firstKey());
			
			// generate the node's successors
			ArrayList<Node> successors = new ArrayList<Node>();
			for (Direction direction: Direction.values())
				successors.add(new Node(currentNode.position.nextPosition(direction), currentNode, direction));
			
			// for each successor
			for (Node successor: successors)
			{
				// Get the nearest station within range (null if there isn't one)
				Station inRangeStation = App.getNearestStation(successor.position);
				inRangeStation = successor.position.inRange(inRangeStation.position)?inRangeStation:null;
				
				// if successor reaches the goal, stop search
				if (inRangeStation != null && inRangeStation.equals(goalStation))
				{
					pathFound = successor; break;
				}
				

			
				// If the successor's position is not already in the map
				boolean alreadyContained = false;
				for (ArrayList<Node> entryList: nodeMap.values())
					alreadyContained = alreadyContained || entryList.contains(successor);
				if (!alreadyContained)
				{
					// calculate the f value (equal to approximateMovesToStation + g)
					// where g = 1000 if the successor reaches a negative station; 1 otherwise
					int f = approximateMovesToStation(successor.position, goalStation);
					f += (inRangeStation != null && inRangeStation.symbol.equals("danger"))?1000:1;
					// Add the successor to the map (or it's respective entry in the map)
					if (nodeMap.containsKey(f))
					{
						ArrayList<Node> entryList = nodeMap.get(f);
						entryList.add(successor);
					}
					else
						nodeMap.put(f, new ArrayList<Node>(Arrays.asList(successor)));
				}
			}
			if (pathFound != null)
			{
				System.out.println("Path found!");
				break;
			}
		}
		

		return null;
	}

	/* Returns approximately how many moves it takes to get from the current position to the station
	 */ 
	private int approximateMovesToStation(Position pos, Station station)
	{
		return (int) Math.ceil(pos.getDist(station.position) / 0.0003);
	}
	
	
}
