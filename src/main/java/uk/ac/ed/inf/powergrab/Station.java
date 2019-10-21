package uk.ac.ed.inf.powergrab;

public class Station {
	
	public String id;
	public double power;
	public double money;
	public String symbol;
	public String color;
	public Position position;
	
	public Station(String id, double p, double m, String sym, String col, double lon, double lat)
	{
		this.id = id;
		this.power = p;
		this.money = m;
		this.symbol = sym;
		this.color = col;
		position = new Position(lat, lon);
	}
}
