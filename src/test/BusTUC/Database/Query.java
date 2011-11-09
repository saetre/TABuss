package test.BusTUC.Database;

public class Query implements Comparable<Query>{

	private int origin;
	private String destination;
	private int time;
	private int day;
	private double euclideanDistance;
	
	public double getEuclideanDistance() {
		return euclideanDistance;
	}

	public void setEuclideanDistance(int time, int day) {
		euclideanDistance = (Math.sqrt(Math.pow(this.time, time) + Math.pow(this.day, day)));
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public Query(int origin, String destination, int time, int day) {
		this.origin = origin;
		this.destination = destination;
		this.time = time;
	}
	
	public int getOrigin() {
		return origin;
	}
	public void setOrigin(int origin) {
		this.origin = origin;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public int compareTo(Query another) {
		if(this.euclideanDistance > another.euclideanDistance) return 1;
		else if(this.euclideanDistance < another.euclideanDistance) return -1;
		return 0;		
	}
	
	@Override
	public String toString(){
		return "Destination: " + this.destination + ", Origin: " + this.origin + ", Day: " + this.day + ", Time: " + this.time;
	}
	

}
