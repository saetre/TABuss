package test.BusTUC.Main;

import java.util.ArrayList;

import android.location.Location;

public class Singleton
{
	public   String latestAnswer;
	public  Location location;
	public  int numStops;
	public  int dist;
	ArrayList <Route> routes;
	
	public Singleton(String latestAnswer, Location location, int numStops, int dist)
	{
		this.latestAnswer = latestAnswer;
		this.location = location;
		this.numStops = numStops;
		this.dist = dist;
	}
	
	public Singleton(ArrayList <Route> routes)
	{
		this.routes = routes;
	}
	
	

	public String getLatestAnswer()
	{
		return latestAnswer;
	}


	public void setLatestAnswer(String latestAnswer)
	{
		this.latestAnswer = latestAnswer;
	}


	public Location getLocation()
	{
		return location;
	}


	public void setLocation(Location location)
	{
		this.location = location;
	}


	public int getNumStops()
	{
		return numStops;
	}


	public void setNumStops(int numStops)
	{
		this.numStops = numStops;
	}


	public int getDist()
	{
		return dist;
	}


	public void setDist(int dist)
	{
		this.dist = dist;
	}
	
	
	
}
