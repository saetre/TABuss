package test.BusTUC.Stops;

import com.google.android.maps.GeoPoint;

/*
 * Class containing properties for the closest stops.
 * This to avoid doing huge lookup regarding lat/long
 * foooo
 */
public class Icon 
{
	private GeoPoint point;
	private int busStopID;
	private String stopName;
	
	public Icon(GeoPoint point, int busStopID, String stopName)
	{	
		this.point = point;
		this.busStopID = busStopID;
		this.stopName = stopName;
	}
	
	public String getStopName()
	{
		return stopName;
	}
	
	public void setStopName(String newName)
	{
		stopName = newName;
	}
	
	public GeoPoint getPoint()
	{
		return point;
	}
	
	public int getBusStopID()
	{
		return busStopID;
	}
	
	public void setPoint(GeoPoint newPoint)
	{
		point = newPoint;
	}
	
	public void setBusStopID(int newID)
	{
		busStopID = newID;
	}

}
