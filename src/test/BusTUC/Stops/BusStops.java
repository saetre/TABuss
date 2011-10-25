package test.BusTUC.Stops;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import test.BusTUC.Main.Route;

import android.os.Parcel;
import android.os.Parcelable;


/*
 * General class representing a bus stop, for use with the oracle
 */
public class BusStops
{
	public int line; 
	public Date arrivalTime; 
	public boolean realTime; 
	public String dest;
	public BusStops()
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
