package test.BusTUC.Stops;

import test.BusTUC.Main.Route;
import android.location.Location;

public class BusStop implements Comparable<BusStop>{
	public Location location;
	public float distance;
	public int stopID;
	public String name;
	
	public BusStop(Location location, float distance, int stopID, String name){
		this.location = location;
		this.distance = distance;
		this.stopID = stopID;
		this.name = name;
	}
	public int compareTo(BusStop otherBusStop)
	{
		if(this.distance < otherBusStop.distance) return -1;
		else if(this.distance > otherBusStop.distance) return 1;
		else return 0;
		
	}
}
