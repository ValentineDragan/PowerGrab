package uk.ac.ed.inf.powergrab;
import java.util.Comparator;

public class DistanceComparator implements Comparator<Station> {
	Position origin;
	
	public DistanceComparator(Position origin)
	{
		this.origin = origin;
	}
	
	@Override
	public int compare(Station s1, Station s2) {
		double dist1 = origin.getDist(s1.position);
		double dist2 = origin.getDist(s2.position);
		return Double.compare(dist1, dist2);
	}

}
