package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;

public class GameManager {
	
	private static Drone theDrone;
	private static boolean moveAllowed = false;
	private static boolean chargeAllowed = false;
	private static int moveNumber = 0;
	
	// Initialise the Drone that the GameManager will oversee
	public static void initialise() {
		Position startingPos = new Position(InputOutputManager.getStartingLat(), InputOutputManager.getStartingLong());
		
		// Initialise Drone
    	if (InputOutputManager.getDroneType().equals("stateless"))
    		theDrone = new StatelessDrone(startingPos, InputOutputManager.getSeed());
    	else
    		theDrone = new StatefulDrone(startingPos, InputOutputManager.getSeed());
    	
    	// Inspect starting position
    	if (!theDrone.getCurrentPos().inPlayArea())
    	{
    		System.out.println("Error! Illeigal starting position!");
    		System.exit(0);
    	}
	}
	
	/** Starts the game. Each turn, the method will:
	 *    1. Get the drone's nextMove (the move it wants to perform)
	 *    2. Move the drone in the specified direction
	 *    3. Charge the drone, if within range from the nearest station
	 *    4. Writes down the move in the text output file
	 * The method also prints Debugging messages for the developer to track game moves.
	 * @throws FileNotFoundException if the text output file is not found
	 * @throws IOException if the IO operation is interrupted
	 */
	protected static void startGame() throws FileNotFoundException, IOException
	{
    	// Repeat until 250 moves, or insufficient energy to move
		while (moveNumber < 250 && theDrone.getPower() >= 1.25)
		{
			// Get the drone's next move
			Direction nextMove = theDrone.getNextMove();
			// DEBUGGING
			System.out.println("-------------------------------------");
			System.out.println("Move " + moveNumber + ": " + nextMove);
			
			// Move the drone
			moveAllowed = true;
			theDrone.move(nextMove);
			moveAllowed = false;
			
			// Charge from the nearest station, if within range
			chargeAllowed = true;
			chargeDrone(theDrone);
			chargeAllowed = false;
			
			// Write in the output file
			InputOutputManager.writeDroneMove(theDrone, nextMove);		
			moveNumber += 1;
		}
		
		// DEBUGGING
		System.out.println("Total number of coins: " + theDrone.getCoins());
		System.out.println("Total number of power: " + theDrone.getPower());
	}
	
	/** Attempts to charge the drone from the nearest station.
	 *  If the nearest station is within range, the drone will be charged. The result will be printed.
	 *  Otherwise the method prints "No station in range".
	 * 
	 * @param drone - the drone that we want to charge
	 */
    private static void chargeDrone(Drone drone)
    {
    	Station nearest_station = MapFunctions.getNearestStation(drone.getCurrentPos());
    	
    	// If the nearest station is within range, charge the drone
    	if (drone.getCurrentPos().inRange(nearest_station.getPosition())) 
    	{
    		// charge the drone
    		Debugger.printChargeMessage(nearest_station);
    		double coinsAmount = nearest_station.getMoney();
    		double powerAmount = Math.max(-drone.getPower(), nearest_station.getPower());
    		drone.charge(coinsAmount, powerAmount);
    		
    		// update the Station
    		Map.updateStation(nearest_station, coinsAmount, powerAmount);
    	}
    	else
    		System.out.println("No station in range");
    }
   
    public static Drone getTheDrone() {
    	return theDrone;
    }
    
    public static int getMoveNumber() {
    	return moveNumber;
    }
    
    public static boolean getMoveAllowed() {
    	return moveAllowed;
    }
    
    public static boolean getChargeAllowed() {
    	return chargeAllowed;
    }
}
