package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class StatefulLogic implements DroneLogic {

	private Position tempPos;
	private int moveNumber=0;
	private Random rnd;

	private ArrayList<Direction> plannedMoves;
	
	public StatefulLogic(Position startingPos, Random rnd)
	{
		this.rnd = rnd;
		plannedMoves = planAllMoves(startingPos, planRoute(startingPos));
	}
	
	/** Gets the chosen next move (Direction) where the Drone wants to go.
	 * @param currPos - the current position of the Drone. Since the Stateful Drone pre-computes all its moves at the beginning, this isn't actually used.
	 * @return the chosen Direction in which the Drone wants to move.
	 */
	@Override
	public Direction getNextMove(Position currPos) {
		return plannedMoves.get(moveNumber++);
	}
	
	/** Plans in which order the Drone will visit all positive Stations on the map.
	 * The drone will move from Station to Station until all positive Stations have been visited.
	 * The algorithm used is a greedy nearest-station.
	 * @param startingPos - the starting Position of the drone
	 * @return an ordered ArrayList containing all positive Stations. The List forms a 'Route' for the Drone to follow.
	 */
	private ArrayList<Station> planRoute(Position startingPos)
	{
		// Initialise empty route and starting position
		Position currentPos = startingPos;
		ArrayList<Station> plannedRoute = new ArrayList<Station>();
		int totalEstimatedMoves = 0; // used only for debugging
		
		// Initialise a list with all the positive stations
		ArrayList<Station> remainingStations = new ArrayList<Station>(Map.getStations());
		remainingStations.removeIf(s -> s.getSymbol().equals("danger"));
		
		// DEBUGGING
		System.out.println("There are " + remainingStations.size() + " positive stations on this map.");
		
		// Repeat until all Stations have been added to the route
		while(!remainingStations.isEmpty())
		{
			Station closestStation = null;
			int minimumMoves = Integer.MAX_VALUE;
			
			// Find the closest Station from the current position
			for (Station station: remainingStations)
			{
				int approximateMoves = approximateMovesToStation(currentPos, station);
				if (approximateMoves < minimumMoves) {
					minimumMoves = approximateMoves;
					closestStation = station;
				}
			}
			// Add the station to the path and update position; Remove the station from the initial list so it doesn't get added twice
			currentPos = closestStation.getPosition();
			plannedRoute.add(closestStation);
			remainingStations.remove(closestStation);
			
			totalEstimatedMoves += minimumMoves; // Used for Debugging
		}
		// DEBUGGING
		System.out.println("Estimated moves to visit all positive stations: " + totalEstimatedMoves);	
		
		return plannedRoute;
	}
	
	/** Plans all the drone's moves based on the Route that was computed at the earlier step.
	 * The drone uses an A* algorithm to find the best path to the next Station in the Route. Repeat until all Stations have been visited.
	 * After all the Stations have been visited, ZigZag the remaining moves until 250 moves are reached.
	 * @param startingPos - the drone's starting position
	 * @param route - the ordered ArrayList of Stations representing the order in which positive Stations will be visited
	 * @return on ArrayList of Directions containing all the 250 moves of the drone.
	 */
	private ArrayList<Direction> planAllMoves(Position startingPos, ArrayList<Station> route)
	{
		System.out.println("---------------------------------\nPlanning StatefulDrone moves in StatefulLogic.planAllMoves...");		
		tempPos = startingPos;
		
		// For each station in the Route, plan the best path that will take us there (using an A* algorithm)
		ArrayList<Direction> plan = new ArrayList<Direction>();
		for (int i=0; i<route.size(); i++)
		{
			// DEBUGGING
			System.out.printf("Searching path for Station %d... ", i);
			ArrayList<Direction> path = getPathToNextStation(tempPos, route.get(i));
			plan.addAll(path);
		}
		
		System.out.printf("All positive stations visited in %d moves.\n", plan.size());
		
		// ZigZag (between the last move and its inverse) until we reach 250 moves
		int remainingMoves = 250 - plan.size() + 1;
		Direction zag = plan.get(plan.size() - 1);
		Direction zig = getInverseDirection(zag);
		
		for (int i=0; i < remainingMoves/2; i++) {
			plan.add(zig); plan.add(zag);
		}
		if (plan.size() == 251)
			plan.remove(plan.size() - 1);
		
		// DEBUGGING
		if (plan.size() == 250)
			System.out.println("All moves planned successfully!");
		else
			System.out.println("Warning!!! Not all moves have been planned.");
		
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
		// Initialise nodeMap - to store nodes that are going to be visited; alreadySelected - nodes that have been/will be visited
		TreeMap<Integer, ArrayList<Node>> nodeMap = new TreeMap<Integer, ArrayList<Node>>();
		ArrayList<Position> alreadySelected = new ArrayList<Position>();
		
		int searchIteration = 0;
		Node pathFound = null; 
		
		// Create startingNode and add it to the list
		Node startingNode = new Node(startingPos, null, null);
		nodeMap.put(0, new ArrayList<Node>(Arrays.asList(startingNode)));
		alreadySelected.add(startingPos);
		
		while(!nodeMap.isEmpty())
		{
			// Find the unvisited node with the least f
			ArrayList<Node> currentEntryList = nodeMap.firstEntry().getValue();
			Node currentNode = currentEntryList.get(0);
			// DEBUGGING
			// System.out.println("A* Search: Currently looking at Node(" + currentNode.position.latitude + " " + currentNode.position.longitude + ")");
						
			// Remove the current node from the HashMap
			currentEntryList.remove(0);
			if (currentEntryList.isEmpty())
				nodeMap.remove(nodeMap.firstKey());
			
			// generate the node's successors (16 Nodes corresponding to 16 Directions)
			ArrayList<Node> successors = new ArrayList<Node>();
			for (Direction direction: Direction.values())
				successors.add(new Node(currentNode.position.nextPosition(direction), currentNode, direction));
			
			// remove successors that go outside the Play Area
			successors.removeIf(s -> !s.position.inPlayArea());
			// remove successors that have been/will be visited
			successors.removeIf(s -> alreadySelected.contains(s.position));
			// add all remaining successors to the 'alreadySelected' list so they don't get visited twice.
			successors.forEach(s -> alreadySelected.add(s.position));
			
			// for each remaining successor
			for (Node successor: successors)
			{
				// Get the nearest station within range (null if there isn't one)
				Station inRangeStation = MapFunctions.getNearestStation(successor.position);
				inRangeStation = successor.position.inRange(inRangeStation.getPosition())?inRangeStation:null;
				
				// if successor reaches the goal, stop search
				if (inRangeStation != null && inRangeStation.equals(goalStation)) { 
					pathFound = successor; break; 
				}
				
				// calculate the f-value = g + h (g is the movement cost; h is the estimated distance to goal)
				// where g = 1000 if the successor reaches a negative station; 1 otherwise; and h = approximateMovesToStation; 
				int f = approximateMovesToStation(successor.position, goalStation);
				f += (inRangeStation != null && inRangeStation.getSymbol().equals("danger"))?1000:1;
				// Add the successor to the HashMap (where the f-value is the key)
				if (nodeMap.containsKey(f))
				{
					ArrayList<Node> entryList = nodeMap.get(f);
					entryList.add(successor);
				}
				else
					nodeMap.put(f, new ArrayList<Node>(Arrays.asList(successor)));
			}
			// When the shortest path is found, backtrack it and return it
			if (pathFound != null)
			{
				int pathLength = 0; // for DEBUGGING
				tempPos = pathFound.position;
				ArrayList<Direction> path = new ArrayList<Direction>();
				
				Node currNode = pathFound;
				while (currNode.parentNode != null)
				{
					path.add(0, currNode.directionFromParent);
					currNode = currNode.parentNode;
					pathLength += 1;
				}
				// DEBUGGING
				System.out.println("Path found of length " + pathLength);
				return path;
			}
			// In case the algorithm gets stuck
			if (searchIteration++ > 50)
			{
				System.out.println("Warning!!! Stuck in infinite loop. Returning random direction.");
				Direction randomDir = getRandomDirection(startingPos); 
				tempPos = startingPos.nextPosition(randomDir);
				return new ArrayList<Direction>(Arrays.asList(randomDir));	
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
	
	private Direction getRandomDirection(Position currentPos)
	{
		List<Direction> allDirections = new ArrayList<Direction>(Arrays.asList(Direction.values()));
		allDirections.removeIf(d -> !currentPos.nextPosition(d).inPlayArea());
		return allDirections.get(rnd.nextInt(allDirections.size()));
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
