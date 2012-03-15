/**
 * Copyright (C) 2010-2012 Magnus Raaum, Lars Moland Eliassen, Christoffer Jun Marcussen, Rune Sætre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * README:
 * 
 */

package test.BusTUC.Calc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import test.BusTUC.Main.Route;

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
				// Empty array, which means no routes were found
				if(ArrayLength == 0)
					{
					routeSuggestions = new Route[1];
					routeSuggestions[0] = new Route();
					routeSuggestions[0].setBusStopName("Bussorakelet");
					return routeSuggestions;
					}
				routeSuggestions = new Route[ArrayLength];
				GeoPoint location;
				for(int i = 0;i<ArrayLength;i++)
				{

					routeSuggestions[i] = new Route();
					routeSuggestions[i].setTransfer(Boolean.parseBoolean(json_arr.getJSONObject(i).getString("transfer")));
					routeSuggestions[i].setBusStopNumber(Integer.parseInt(json_arr.getJSONObject(i).getString("busStopNumber")));
					routeSuggestions[i].setArrivalTime(json_arr.getJSONObject(i).getString("arrivalTime"));
					routeSuggestions[i].setBusStopName(json_arr.getJSONObject(i).getString("busStopName"));
					if(routeSuggestions[i].isTransfer() && i<ArrayLength-1)
					{
						System.out.println("Found transfer: " + routeSuggestions[i].getBusStopName() + " dest: " + json_arr.getJSONObject(i+1).getString("busStopName"));
						routeSuggestions[i].setDestination(json_arr.getJSONObject(i+1).getString("busStopName"));
					}
					else routeSuggestions[i].setDestination(dest);
					
					routeSuggestions[i].setTravelTime(json_arr.getJSONObject(i).getString("travelTime"));
					routeSuggestions[i].setBusNumber(Integer.parseInt(json_arr.getJSONObject(i).getString("busNumber")));
					routeSuggestions[i].setWalkingDistance(Integer.parseInt(json_arr.getJSONObject(i).getString("walkingDistance")));
				
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
					// Fix arrival time, if before 10 am
					if(routeSuggestions[i].getArrivalTime().length() == 3)
					{
						routeSuggestions[i].setArrivalTime("0"+routeSuggestions[i].getArrivalTime());
					}

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
		boolean check = false;
		ArrayList <Route> tempList = new ArrayList <Route>();
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
						if(temp.getWalkingDistance() > routelist[i].getWalkingDistance() && !fixed.contains(routelist[i]))
						{
							fixed.add(routelist[i]);

							System.out.println("Added first if: " + routelist[i].getBusStopName());
						}
						else if(temp.getWalkingDistance() < routelist[i].getWalkingDistance() && !fixed.contains(temp))
						{
							fixed.add(temp);
							System.out.println("Added second if: " + temp.getBusStopName());
						}
					}

					else
					{

						if(i == 1)
						{
							if(!fixed.contains(temp))
							{

								System.out.println("ADDED IN ELSE " + temp.getBusStopName());
								fixed.add(temp);
							}
						}
						else
						{
							for(int j = 0; j< fixed.size(); j++)
							{
								if(fixed.get(j).getBusNumber() == routelist[i].getBusNumber())
								{
									if(fixed.get(j).getWalkingDistance() > routelist[i].getWalkingDistance() && !fixed.contains(routelist[i]))
									{
										fixed.remove(fixed.get(j));
										fixed.trimToSize();
										fixed.add(routelist[i]);										
									}
									else continue;

								}
								else
								{
									if(!fixed.contains(routelist[i]))fixed.add(routelist[i]);
								}
							}
						}

					}

					temp = routelist[i];
				}
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
		if(finalRoutes != null)
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

	}
	@SuppressWarnings("deprecation")
	public int calculateTotalTime(String arrival, String totalTime)
	{

		int tt = 0; 
		int k_tt = Integer.parseInt(totalTime);
		Date now = new Date(); 
		if(arrival.length() == 3) arrival = "0" + arrival;
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
		System.out.println("GO SORT");
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
