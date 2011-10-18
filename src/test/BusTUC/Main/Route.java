package test.BusTUC.Main;

import java.util.Comparator;

import test.BusTUC.R;


public class Route implements Comparable<Route>
{
     
	private String arrivalTime; 
	private String busStopName; 
	private int busStopNumber; 
	private int busNumber; 
	private String travelTime; 
	private String destination; 
	private boolean transfer; 
	private int walkingDistance; 
    private int totalTime; 
	Route(String k_a, String k_be, int k_br, String k_tt, String k_d)
	{
		arrivalTime = k_a; 
		busStopName = k_be; 
		busStopNumber = k_br;
		travelTime = k_tt; 
		destination = k_d; 
	}
	public Route()
	{
		
	}	

	 
	
	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public int getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	public int getWalkingDistance() {
		return walkingDistance;
	}
	public void setWalkingDistance(int walkingDistance) {
		this.walkingDistance = walkingDistance;
	}
	public boolean isTransfer() {
		return transfer;
	}
	public void setTransfer(boolean transfer) {
		this.transfer = transfer;
	}
	public int getBusNumber() {
		return busNumber;
	}
	public void setBusNumber(int busNumber) {
		this.busNumber = busNumber;
	}
	public String getBusStopName() {
		return busStopName;
	}
	public void setBusStopName(String busStopName) {
		this.busStopName = busStopName;
	}
	public int getBusStopNumber() {
		return busStopNumber;
	}
	public void setBusStopNumber(int busStopNumber) {
		this.busStopNumber = busStopNumber;
	}
	public String getTravelTime() {
		return travelTime;
	}
	public void setTravelTime(String travelTime) {
		this.travelTime = travelTime;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	 @Override
	public int compareTo( Route otherRoute ) {
	    final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;

	    if ( this == otherRoute ) return EQUAL;

	    if (this.totalTime < otherRoute.totalTime) return BEFORE;
	    if (this.totalTime > otherRoute.totalTime) return AFTER;

	    return EQUAL;
	  }


}
