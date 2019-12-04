package uk.ac.ed.inf.powergrab;

public class Station {
	
	private double power;
	private double money;
	private String symbol;
	private String color;
	private Position position;
	private String id;
	
	public Station(String id, double money, double power, String sym, String col, double lon, double lat)
	{
		this.id = id;
		this.money = money;
		this.power = power;
		this.symbol = sym;
		this.color = col;
		position = new Position(lat, lon);
	}
	

	public String getId() {
		return id;
	}

	public double getPower() {
		return power;
	}
	
	// Takes away power after a Drone has charged from this Station
	protected void takePower(double amount) {
		this.power -= amount;
	}

	public double getMoney() {
		return money;
	}
	
	// Takes away money after a Drone has charged from this Station
	protected void takeMoney(double amount) {
		this.money -= amount;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getColor() {
		return color;
	}

	public Position getPosition() {
		return position;
	}
}
