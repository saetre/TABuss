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
import test.BusTUC.Speech.CBRAnswer;

import android.util.Log;

public class Calculate
{

	private JSONObject json_obj;
	private JSONArray json_arr;

	public Calculate()
	{
	}

	public CBRAnswer createCBRAnswer(String jsonString)
	{
		CBRAnswer answ = null;
		JSONObject json = null;
		try
		{
			json = new JSONObject(jsonString);
			answ = new CBRAnswer();
			if(json != null)
			{
				answ.setAnswer(json.getString("answer"));
				answ.setScore(json.getDouble("score"));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return answ;

	}

	public Route[] createRoutesServer(String jsonString, String dest)
	{
		Route[] routeSuggestions = null;
		try
		{
			json_obj = new JSONObject(jsonString);
			json_arr = new JSONArray(json_obj.getString("alts"));
			System.out.println("arrayLength: " + json_arr.length());

			if (json_arr != null)
			{
				int ArrayLength = json_arr.length();
				Log.v("arraylength", "ar:" + ArrayLength);
				// Empty array, which means no routes were found
				if (ArrayLength == 0)
				{
					routeSuggestions = new Route[1];
					routeSuggestions[0] = new Route();
					routeSuggestions[0].setBusStopName("Bussorakelet");
					return routeSuggestions;
				}
				routeSuggestions = new Route[ArrayLength];
				GeoPoint location;
				for (int i = 0; i < ArrayLength; i++)
				{

					routeSuggestions[i] = new Route();
					routeSuggestions[i].setTransfer(Boolean
							.parseBoolean(json_arr.getJSONObject(i).getString(
									"transfer")));
					routeSuggestions[i].setBusStopNumber(Integer
							.parseInt(json_arr.getJSONObject(i).getString(
									"busStopNumber")));
					routeSuggestions[i].setArrivalTime(json_arr
							.getJSONObject(i).getString("arrivalTime"));
					routeSuggestions[i].setBusStopName(json_arr
							.getJSONObject(i).getString("busStopName"));
					if (routeSuggestions[i].isTransfer() && i < ArrayLength - 1)
					{
						System.out.println("Found transfer: "
								+ routeSuggestions[i].getBusStopName()
								+ " dest: "
								+ json_arr.getJSONObject(i + 1).getString(
										"busStopName"));
						routeSuggestions[i].setDestination(json_arr
								.getJSONObject(i + 1).getString("busStopName"));
					} else
						routeSuggestions[i].setDestination(dest);

					routeSuggestions[i].setTravelTime(json_arr.getJSONObject(i)
							.getString("travelTime"));
					routeSuggestions[i].setBusNumber(Integer.parseInt(json_arr
							.getJSONObject(i).getString("busNumber")));
					routeSuggestions[i].setWalkingDistance(Integer
							.parseInt(json_arr.getJSONObject(i).getString(
									"walkingDistance")));

				}
			}
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			Log.v("jsonError", "e:" + e.toString());
			// e.printStackTrace();
		} catch (NullPointerException ex)
		{
			ex.printStackTrace();
		}
		return routeSuggestions;
	}

	

	
	public void printOutRoutes(String FLAG, Route[] finalRoutes,
			boolean totaltimeDone)
	{
		if (finalRoutes != null)
		{
			for (int i = 0; i < finalRoutes.length; i++)
			{
				Log.v(FLAG + " " + i, "Line:" + finalRoutes[i].getBusNumber());
				Log.v(FLAG + " " + i,
						"ArrivalTime:" + finalRoutes[i].getArrivalTime());
				Log.v(FLAG + " " + i,
						"BusStopName:" + finalRoutes[i].getBusStopName());
				Log.v(FLAG + " " + i,
						"BusStopNumber:" + finalRoutes[i].getBusStopNumber());
				Log.v(FLAG + " " + i,
						"Destination:" + finalRoutes[i].getDestination());
				Log.v(FLAG + " " + i,
						"TravelTime:" + finalRoutes[i].getTravelTime());
				Log.v(FLAG + " " + i,
						"WalkingDistance:"
								+ finalRoutes[i].getWalkingDistance());
				if (totaltimeDone)
				{
					Log.v(FLAG + " " + i,
							"TotalTime:" + finalRoutes[i].getTotalTime());
				}
			}
		}

	}

}
