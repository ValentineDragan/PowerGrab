package uk.ac.ed.inf.powergrab;

public class Station {
	
	private double power;
	private double money;
	private String symbol;
	private String color;
	private Position position;
	
	public Station(String id, double p, double m, String sym, String col, double lon, double lat)
	{
		this.id = id;
		this.power = p;
		this.money = m;
		this.symbol = sym;
		this.color = col;
		position = new Position(lat, lon);
	}
	
	private String id;
	public String getId() {
		return id;
	}

	public double getPower() {
		return power;
	}
	
	protected void takePower(double amount) {
		this.power -= amount;
	}

	public double getMoney() {
		return money;
	}
	
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
