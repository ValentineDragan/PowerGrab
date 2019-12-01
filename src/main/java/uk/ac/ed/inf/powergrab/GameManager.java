package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;

public class GameManager {
	
	private App theApp;
	private DroneModel theDrone;
	private int moveNumber=0;
	
	public GameManager(App theApp, DroneModel theDrone)
	{
		this.theApp = theApp;
		this.theDrone = theDrone;
	}
	
	protected void startGame() throws FileNotFoundException, IOException
	{
    	// Repeat until 250 moves, or insufficient energy to move
		while (moveNumber < 250 && theDrone.getPower() >= 1.25)
		{
			// Get the drone's next move
			Direction nextMove = theDrone.getNextMove();
			// Move the drone and charge from nearest station (if in range)
			theDrone.move(nextMove);
			chargeDrone(theDrone);
			// Write in the output file
			theApp.writeDroneMove(theDrone, nextMove);		
			moveNumber += 1;
			// DEBUGGING
			System.out.println("-------------------------------------");
			System.out.println("Move " + moveNumber + ": " + nextMove);
		}
		
		// DEBUGGING
		System.out.println("Total number of coins: " + theDrone.getCoins());
		System.out.println("Total number of power: " + theDrone.getPower());
	}
	
    // Attempt to charge drone from the nearest station
    // Will print out the result
    private void chargeDrone(DroneModel drone)
    {
    	Station nearest_station = MapFunctions.getNearestStation(drone.getCurrentPos());
    	
    	// If the nearest station is within range, charge the drone
    	if (drone.getCurrentPos().inRange(nearest_station.getPosition())) 
    	{
    		if (nearest_station.getSymbol().equals("danger"))
        		System.out.println("Danger!!! Drone charged from a negative station! id: " + nearest_station.getId());
        	else if (nearest_station.getPower() == 0 && nearest_station.getMoney() == 0)
        		System.out.println("Empty station.");
        		//System.out.println("Warning! Drone charged from an empty station! id: " + nearest_station.id);
        	else
        		System.out.println("Drone charged. Power: " + nearest_station.getPower() + "; money: " + nearest_station.getMoney() + "; id: " + nearest_station.getId());
    		
    		// charge the drone
    		double coinsAmount = nearest_station.getMoney();
    		double powerAmount = Math.max(-drone.getPower(), nearest_station.getPower());
    		drone.charge(coinsAmount, powerAmount);
    		
    		// update the Station
    		Map.updateStation(nearest_station, coinsAmount, powerAmount);
    	}
    	else
    		System.out.println("No station in range");
    }
   
    
    public int getMoveNumber() {
    	return moveNumber;
    }
}
