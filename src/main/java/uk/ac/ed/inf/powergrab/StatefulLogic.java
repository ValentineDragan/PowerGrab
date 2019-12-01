package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;

public class StatefulLogic implements DroneLogic {

	private ArrayList<Direction> plannedMoves;
	private Position tempPos;
	private int moveNumber=0;

	public StatefulLogic(Position startingPos)
	{
		planAllMoves(startingPos);
	}
	
	@Override
	public Direction getNextMove(Position currPos)
	{
		return plannedMoves.get(moveNumber++);
	}
	
	private ArrayList<Station> planRoute(Position startingPos)
	{
		// Initialise empty route and starting position
		Position currentPos = startingPos;
		ArrayList<Station> plannedRoute = new ArrayList<Station>();
		int totalEstimatedMoves = 0; // used only for debugging
		// Initialise a list with all the positive stations
		ArrayList<Station> remainingStations = new ArrayList<Station>(Map.getStations());
		remainingStations.removeIf(s -> s.getSymbol().equals("danger"));
		
		// Repeat until all Stations have been added to the route
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
			// Remove the station from the initial list so it doesn't get added twice
			System.out.println("Next closest station is " + minimumMoves + " moves away");
			currentPos = closestStation.getPosition();
			plannedRoute.add(closestStation);
			remainingStations.remove(closestStation);
			
			totalEstimatedMoves += minimumMoves; // only for debugging
		}
		System.out.println("Total estimated moves: " + totalEstimatedMoves);	
		
		return plannedRoute;
	}
	
	private ArrayList<Direction> planAllMoves(Position startingPos)
	{
		tempPos = startingPos;
		// Plan in which order we will visit the positive stations (Greedy nearest-station method)
		ArrayList<Station> route = planRoute(startingPos);
		
		// For each station in the Route, plan the best path that will take us there (using an A* algorithm)
		ArrayList<Direction> plan = new ArrayList<Direction>();
		for (int i=0; i<route.size(); i++)
		{
			ArrayList<Direction> path = getPathToNextStation(tempPos, route.get(i));
			plan.addAll(path);
		}
		System.out.println("PLANNED MOVES SIZE BEFORE FILL: " + plan.size());
		
		// Zig-Zag (between the last move and its inverse) until we reach 250 moves
		int remainingMoves = 250 - plan.size() + 1;
		Direction zag = plan.get(plan.size() - 1);
		Direction zig = getInverseDirection(zag);
		System.out.println("ZIG ZAG: " + zig + " " + zag);
		
		for (int i=0; i < remainingMoves/2; i++)
		{
			plan.add(zig); plan.add(zag);
		}
		if (plan.size() == 251)
			plan.remove(plan.size() - 1);
		System.out.println("PLANNED MOVES SIZE AFTER FILL: " + plan.size());
		
		return plan;
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
	private ArrayList<Direction> getPathToNextStation(Position startingPos, Station goalStation)
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
			// remove successors that go outside the Play Area
			successors.removeIf(s -> !s.position.inPlayArea());
			
			// for each remaining successor
			for (Node successor: successors)
			{
				// Get the nearest station within range (null if there isn't one)
				Station inRangeStation = MapFunctions.getNearestStation(successor.position);
				inRangeStation = successor.position.inRange(inRangeStation.getPosition())?inRangeStation:null;
				
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
					f += (inRangeStation != null && inRangeStation.getSymbol().equals("danger"))?1000:1;
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
			// When the shortest path is found, reconstruct it and return it
			if (pathFound != null)
			{
				System.out.println("Path found!");
				tempPos = pathFound.position;
				ArrayList<Direction> path = new ArrayList<Direction>();
				
				Node currNode = pathFound;
				while (currNode.parentNode != null)
				{
					path.add(0, currNode.directionFromParent);
					currNode = currNode.parentNode;
				}
				return path;
			}
		}
		return null;
	}

	/* Returns approximately how many moves it takes to get from the current position to the station
	 */ 
	private int approximateMovesToStation(Position pos, Station station)
	{
		return (int) Math.ceil(pos.getDist(station.getPosition()) / 0.0003);
	}
	
	private Direction getInverseDirection(Direction dir)
	{
		switch (dir) 
		{
			case N: return Direction.S;
			case NNE: return Direction.SSW;
			case NE: return Direction.SW;
			case ENE: return Direction.WSW;
			
			case E: return Direction.W;
			case ESE: return Direction.WNW;
			case SE: return Direction.NW;
			case SSE: return Direction.NNW;
			
			case S: return Direction.N;
			case SSW: return Direction.NNE;
			case SW: return Direction.NE;
			case WSW: return Direction.ENE;
			
			case W: return Direction.E;
			case WNW: return Direction.ESE;
			case NW: return Direction.SE;
			case NNW: return Direction.SSE;
			
			default: return null;
		}
		
	}
	
}
