package test.BusTUC;

import java.util.Date;
import test.BusTUC.R;

public class BusStops {
	public int line; 
	public Date arrivalTime; 
	public boolean realTime; 
	BusStops()
	{
		
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
