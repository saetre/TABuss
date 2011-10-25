package test.BusTUC.Stops;

import java.util.Date;


/*
 * General class representing a bus stop, for use with the oracle
 */
public class BusDeparture {
	public int line; 
	public Date arrivalTime; 
	public boolean realTime; 
	public String dest;
	public BusDeparture()
	{
		
	}
	
	public String getDest()
	{
		return dest;
	}
	
	public void setDest(String dest)
	{
		this.dest = dest;
	} 
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public Date getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public boolean isRealTime() {
		return realTime;
	}
	public void setRealTime(boolean realTime) {
		this.realTime = realTime;
	}
}
