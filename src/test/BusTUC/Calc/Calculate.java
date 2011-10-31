package test.BusTUC.Calc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import test.BusTUC.R;
import test.BusTUC.Main.Route;

import android.location.Location;
import android.util.Log;

public class Calculate {

	private JSONObject json_obj; 
	private JSONArray json_arr; 

	public Calculate()
	{
	}

	public Route[] createRoutesServer(String jsonString, String dest)
	{
		Route[] routeSuggestions = null; 
		try {
			json_obj = new JSONObject(jsonString);
			json_arr = new JSONArray(json_obj.getString("alts"));
			System.out.println("arrayLength: " + json_arr.length());
			if(json_arr != null)
			{
				int ArrayLength = json_arr.length(); 
				Log.v("arraylength","ar:"+ArrayLength);
				routeSuggestions = new Route[ArrayLength];
				GeoPoint location;
				for(int i = 0;i<ArrayLength;i++)
				{

					routeSuggestions[i] = new Route();
					routeSuggestions[i].setTransfer(Boolean.parseBoolean(json_arr.getJSONObject(i).getString("transfer")));
					routeSuggestions[i].setBusStopNumber(Integer.parseInt(json_arr.getJSONObject(i).getString("busStopNumber")));
					routeSuggestions[i].setArrivalTime(json_arr.getJSONObject(i).getString("arrivalTime"));
					routeSuggestions[i].setBusStopName(json_arr.getJSONObject(i).getString("busStopName"));
					routeSuggestions[i].setDestination(dest);
					routeSuggestions[i].setTravelTime(json_arr.getJSONObject(i).getString("travelTime"));
					routeSuggestions[i].setBusNumber(Integer.parseInt(json_arr.getJSONObject(i).getString("busNumber")));
					routeSuggestions[i].setWalkingDistance(Integer.parseInt(json_arr.getJSONObject(i).getString("walkingDistance")));
					if(routeSuggestions[i].isTransfer())
					{
						double lat = Double.parseDouble(json_arr.getJSONObject(i).getString("depLatitude")); 
						double lon = Double.parseDouble(json_arr.getJSONObject(i).getString("depLongitude"));
						location = new GeoPoint((int) lat,(int) lon);
						routeSuggestions[i].setLocation(location);
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.v("jsonError","e:"+e.toString());
			//e.printStackTrace();
		}
		catch(NullPointerException ex)
		{
			System.out.println("NULL I Calculate.createRouteServer()");
		}
		return routeSuggestions;
	}
	public Route[] createRoutes(String jsonString)
	{
		Route[] routeSuggestions = null; 
		try {
			json_obj = new JSONObject(jsonString);
			json_arr = new JSONArray(json_obj.getString("departures"));
			if(json_arr != null)
			{
				int ArrayLength = json_arr.length()-1; 
				Log.v("arraylength","ar:"+ArrayLength);
				routeSuggestions = new Route[ArrayLength];
				for(int i = 0;i<ArrayLength;i++)
				{
					Log.v("i","i:"+Integer.parseInt(json_arr.getJSONObject(i).getString("busstopnumber")));
					Log.v("i","i-TRANSFER:"+json_obj.get("transfer"));
					routeSuggestions[i] = new Route();
					routeSuggestions[i].setTransfer(Boolean.parseBoolean(json_obj.get("transfer").toString()));
					routeSuggestions[i].setBusStopNumber(Integer.parseInt(json_arr.getJSONObject(i).getString("busstopnumber")));
					routeSuggestions[i].setArrivalTime(json_arr.getJSONObject(i).getString("time"));
					routeSuggestions[i].setBusStopName(json_arr.getJSONObject(i).getString("busstopname"));
					routeSuggestions[i].setDestination(json_arr.getJSONObject(i).getString("destination"));
					routeSuggestions[i].setTravelTime(json_arr.getJSONObject(i).getString("duration"));
					routeSuggestions[i].setBusNumber(Integer.parseInt(json_arr.getJSONObject(i).getString("busnumber")));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.v("jsonError","e:"+e.toString());
			//e.printStackTrace();
		}
		return routeSuggestions;

	}
	/*
	 * Method removes suggestions where the same bus passes stops further away than the closest
	 * If transfer, return original list
	 */
	public Route[]suggestRoutes(Route [] routelist)
	{
		ArrayList<Route> fixed = new ArrayList <Route>();
		System.out.println("RECEIVED SUGGESTROUTES: " + routelist.length);
		Route temp = null;
		for(int i=0; i<routelist.length; i++)
		{
			if(routelist[i].isTransfer() || routelist.length == 1) return routelist;
			else
			{
				
				if(i==0)
				{
					temp = routelist[i];				
				}
				else
				{
					System.out.println("COMPARING: " + temp.getBusNumber() + " and: " + routelist[i].getBusNumber());
					System.out.println("WALK: " + temp.getWalkingDistance() + " and " + routelist[i].getWalkingDistance());
					if(temp.getWalkingDistance() == 0) fixed.add(temp);
					if(temp.getBusNumber() == routelist[i].getBusNumber())
					{
						if(temp.getWalkingDistance() > routelist[i].getWalkingDistance() && !fixed.contains(routelist[i]))fixed.add(routelist[i]);
						else if(temp.getWalkingDistance() < routelist[i].getWalkingDistance() && !fixed.contains(temp)) fixed.add(temp);
					}
				
					else
					{
						System.out.println("ADDED IN ELSE");
						fixed.add(routelist[i]);
					}
				}

				temp = routelist[i];
			}
		}
		Route[] retArray = new Route[fixed.size()];
		for(int i=0; i<fixed.size(); i++)
		{
			retArray[i] = fixed.get(i);
		}
		System.out.println("RETARRAY: " + retArray.length);
		return retArray;
	}

	public void printOutRoutes(String FLAG, Route[] finalRoutes, boolean totaltimeDone)
	{
		for(int i = 0;i<finalRoutes.length;i++)
		{
			Log.v(FLAG+" "+i,"Line:"+finalRoutes[i].getBusNumber());
			Log.v(FLAG+" "+i,"ArrivalTime:"+finalRoutes[i].getArrivalTime());
			Log.v(FLAG+" "+i,"BusStopName:"+finalRoutes[i].getBusStopName());
			Log.v(FLAG+" "+i,"BusStopNumber:"+finalRoutes[i].getBusStopNumber());
			Log.v(FLAG+" "+i,"Destination:"+finalRoutes[i].getDestination());
			Log.v(FLAG+" "+i,"TravelTime:"+finalRoutes[i].getTravelTime());
			Log.v(FLAG+" "+i,"WalkingDistance:"+finalRoutes[i].getWalkingDistance());
			if(totaltimeDone)
			{
				Log.v(FLAG+" "+i,"TotalTime:"+finalRoutes[i].getTotalTime());
			}
		}
	}
	public int calculateTotalTime(String arrival, String totalTime)
	{
		int tt = 0; 
		int k_tt = Integer.parseInt(totalTime);
		Date now = new Date(); 
		String hours = arrival.substring(0, 2);
		//	 Log.v("HOURS","hours:"+hours);
		String minutes = arrival.substring(2, 4);
		//	 Log.v("MINUTES","minutes:"+minutes);
		int nowHours,nowMinutes,aHours,aMinutes = 0; 
		nowHours = now.getHours();
		nowMinutes = now.getMinutes();
		//	 Log.v("NOW","NOW:"+nowHours+":"+nowMinutes);
		aHours = Integer.parseInt(hours);
		aMinutes = Integer.parseInt(minutes);
		//	 Log.v("ARRIVAL","ARRIVAL:"+aHours+":"+aMinutes);
		int difference = 0;
		if(nowHours == aHours)
		{
			difference = aMinutes - nowMinutes; 
		}
		else 
		{
			int nowint = nowHours * 60 + nowMinutes; 
			int aint = aHours * 60 + aMinutes; 
			difference = aint - nowint; 
		}
		//   Log.v("NOW","DIFF:"+difference+"- TOTAL:"+k_tt);
		tt = difference + k_tt; 
		return tt; 
	}

	public Route[] sortByTotalTime(Route[] routeSuggestion)
	{

		Route[] rs = routeSuggestion; 
		
		for(int i=0; i<rs.length; i++)
		{
			System.out.println("Before: " + rs[i].getTotalTime());
			if(rs[i].isTransfer()) return rs;
		}
		// Overriden compareTo in Route
		Arrays.sort(rs);
		for(int i=0; i<rs.length; i++)
		{
			System.out.println("After: " + rs[i].getTotalTime());
		}

		return rs; 
	}
}
