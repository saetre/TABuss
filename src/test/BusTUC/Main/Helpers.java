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
 * - To compile:	javac -d . -cp "httpcomponents-client-4.2-beta1/lib/httpmime-4.2-beta1.jar:android.jar:." -encoding UTF-8 src/test/BusTUC/*.java
 * - To run:		java  -cp  .:httpcomponents-client-4.2-beta1/lib/httpmime-4.2-beta1.jar test/BusTUC/Main/BusTUCApp
 * 
 */

package test.BusTUC.Main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import java.text.DecimalFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.lang.Thread;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import test.BusTUC.Calc.Calculate;
import test.BusTUC.Database.Query;
import test.BusTUC.Favourites.SDCard;
import test.BusTUC.Queries.Browser;
import test.BusTUC.Stops.BusDeparture;
import test.BusTUC.Stops.BusStop;
import test.BusTUC.Stops.BusSuggestion;
import test.BusTUC.Stops.ClosestStopOnMap;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/*
 * Class containing helper functions used in BusTUCApp. Accessed static to avoid
 * object creation
 */
public class Helpers
{
	
	/**
	 * Method for sending a text message to the sms-oracle
	 * 
	 * @param phoneNumber
	 * @param message
	 * @param context
	 */
	public static void sendSMS(String phoneNumber, String message,
			Context context)
	{

		SmsManager sms = SmsManager.getDefault();

		try
		{
			sms.sendTextMessage(phoneNumber, null, message, null, null);
		} catch (Exception e)
		{
			Toast.makeText(context, "Meding ikke sendt", Toast.LENGTH_LONG)
					.show();
		}
	}

	public static String[] readLines(InputStream is) throws IOException
	{

		URL url = null;

		BufferedReader bufferedReader = null;

		bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		// new InputStreamReader(new FileInputStream(filename), "iso-8859-1"));
		List<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = bufferedReader.readLine()) != null)
		{

			if (line.startsWith("<item>"))
			{
				String[] trim = line.split(">");
				String[] tmp = trim[1].split("</");
				lines.add(tmp[0]);
			}
		}
		bufferedReader.close();
		return lines.toArray(new String[lines.size()]);
	}

	public static ClosestStopOnMap[] getList(String[][] coords,
			String provider, int numStops, int dist, Location currentLocation)
	{
		ArrayList<BusStop> busStops = Helpers.getLocationsArray(coords, "",
				currentLocation, 1000, numStops, true);
		ClosestStopOnMap[] cl = new ClosestStopOnMap[numStops];
		if (busStops.size() == 0)
			return cl;
		else
		{
			for (int i = 0; i < numStops; i++)
			{
				cl[i] = new ClosestStopOnMap(new GeoPoint(
						(int) (busStops.get(i).location.getLatitude() * 1E6),
						(int) (busStops.get(i).location.getLongitude() * 1E6)),
						(int) busStops.get(i).stopID, busStops.get(i).name);

			}
		}
		return cl;

	}

	public static HashMap<String, Integer> getMostFrequentDestination(
			ArrayList<String> destination)
	{
		System.out.println("ARRAYLIST INPUT! : " + destination.size());
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		for (String d : destination)
		{
			if (temp.containsKey(d))
			{
				temp.put(d, temp.get(d) + 1);
			} else
			{
				temp.put(d, 1);
			}
		}
		return temp;
	}

	public static String getTimeNow()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("HH:ss");
		return format.format(cal.getTime());
	}

	@SuppressWarnings("deprecation")
	public static int minutesFromDate(Date date)
	{
		return date.getHours() * 60 + date.getMinutes();
	}

	// Add dictionary to app. If not stored in SD-card previously, do so
	public static ArrayList<String> createDictionary(String[][] gpsCords,
			String folderName)
	{
		ArrayList<String> dictionary = new ArrayList<String>();

		for (int i = 0; i < gpsCords.length; i++)
		{
			dictionary.add(gpsCords[i][1] + "\n");
		}

		// Remove duplicates
		HashSet<String> set = new HashSet<String>();
		set.addAll(dictionary);
		// Clear and add back to ArrayList
		dictionary.clear();
		dictionary.addAll(set);
		for (int i = 0; i < dictionary.size(); i++)
		{
			// System.out.println("SECOND: " + dictionary.get(i));
		}
		SDCard.generateNoteOnSD("dictionary_finalv2", dictionary, folderName);

		return dictionary;
	}

	public static ArrayList<String> getDictionary(String name, String folderName)
	{
		ArrayList<String> dictionary = SDCard.getFilesFromSD(folderName);
		System.out.println("STR: " + dictionary.size());
		return dictionary;

	}



	public static ArrayList<BusSuggestion> parseDataObject(
			ArrayList<Route> value)
	{
		ArrayList<BusSuggestion> suggestions = new ArrayList<BusSuggestion>();
		System.out.println("VALUE SIZE: " + value.size());
		boolean isTransfer = false;

		for (int i = 0; i < value.size(); i++)
		{
			BusSuggestion suggestion = new BusSuggestion();
			// If hour is 24, change to 00.
			if (Integer.parseInt(value.get(i).getArrivalTime().substring(0, 2)) == 24)
			{
				int hour = Integer.parseInt(value.get(i).getArrivalTime()
						.substring(0, 2));
				String newTime = "00"
						+ value.get(i).getArrivalTime().substring(2, 4);
				value.get(i).setArrivalTime(newTime);
			}

			suggestion.line = value.get(i).getBusNumber();
			suggestion.origin = value.get(i).getBusStopName();// +" klokken "+
			suggestion.arrivaltime = value.get(i).getArrivalTime()
					.substring(0, 2)
					+ ":" + value.get(i).getArrivalTime().substring(2, 4);// +". Du vil n� "+
			suggestion.destination = value.get(i).getDestination();// +" ca "+
			int tmptime = Integer.parseInt(value.get(i).getArrivalTime());
			int tmptime2 = Integer.parseInt(value.get(i).getTravelTime());
			int hours = tmptime / 100 + (tmptime % 100 + tmptime2) / 60;
			int minutes = (tmptime % 100 + tmptime2) % 60;
			String fixedMinutes = "";
			System.out.println("Fixed " + minutes);
			if (String.valueOf(minutes).length() == 1)
			{
				fixedMinutes = "0" + minutes;
				System.out.println("Fixedminutes: " + fixedMinutes);

			} else if (String.valueOf(minutes).length() == 0)
			{
				fixedMinutes = "00";
				System.out.println("Fixedminutes: " + fixedMinutes);
			} else
				fixedMinutes = String.valueOf(minutes);
			suggestion.departuretime = hours + ":" + fixedMinutes;

			if (value.get(i).isTransfer() && !isTransfer)
			{
				suggestion.isTransfer = "Overgang";
				isTransfer = true;
			} else
				suggestion.isTransfer = "Ankomst";
			if (value.get(i).getWalkingDistance() != 0)
			{
				suggestion.walkingDistance = value.get(i).getWalkingDistance();
				// suggestion.origin+="("+value.get(i).getWalkingDistance()+"m)";
			}

			suggestions.add(suggestion);

		}
		System.out.println("RETURN END");
		return suggestions;
	}


	/*
	 * Create a JSon object from input string. Used together with Retro's server
	 */
	public static Route[] createJSONServer(String jsonSubString,
			Calculate calculator, String dest)
	{
		Route[] routes = calculator.createRoutesServer(jsonSubString, dest);
		return routes;
	}


	/*
	 * Will run a query directly towards BussTUC with standard natural language
	 * syntax
	 */
	public static StringBuffer runStandard(String input)
	{
		StringBuffer buf = new StringBuffer();
		Browser k_browser = new Browser();
		// Perform action on clicks
		if (!input.equals(""))
		{
			try
			{
				// System.out.println("K-browserobj " + k_browser.toString() +
				// "realtimelength: " + realTimeCodes.size());

				long time = System.nanoTime();
				buf = k_browser.getRequestStandard(input);
				return buf;
			} catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		} else
			System.out.println("EMPTY TEXT");
		return null;
	}

	
	/*
	 * Runs query against Retro's server According methods such as
	 * createJSONServer are modified versions of the existing
	 */
	public static ArrayList<Route> runServer(String input,
			Location location, int numStops, int dist, Context context)
	{
		Route[] finalRoutes;
		// Perform action on clicks

		try
		{
			Browser k_browser = new Browser();
			long time = System.nanoTime();
			String html_page = k_browser.getRequestServer(input, false,
					location, numStops, dist, context);
			// No internet connection
			if (html_page == null)
			{
				ArrayList<Route> temp = new ArrayList<Route>();
				temp.add(new Route());
				temp.get(0).setBusStopName("Nettilgang");
				return temp;
			}
			long newTime = System.nanoTime() - time;
			System.out.println("TIME ORACLEREQUEST: " + newTime / 1000000000.0);
			Calculate calculator = new Calculate();

			// Create routes based on jsonSubString
			Route[] routes = createJSONServer(html_page.toString(), calculator,
					input);
			if (routes != null)
			{
				calculator.printOutRoutes("BEFORE", routes, false);
				ArrayList<Route> returnRoutes = new ArrayList<Route>();
				for (int i = 0; i < routes.length; i++)
				{
					returnRoutes.add(routes[i]);
				}
				return returnRoutes;
			} else
				return null;
		}
		// }
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}

	/*
	 * Get the closest stops based on location, distance and num stops.
	 */
	public static ArrayList<BusStop> getLocationsArray(String[][] k_gpsCords,
			String provider, Location currentLocation, int maxDistance,
			int numStops, boolean duplicates)
	{
		ArrayList<BusStop> busStops = new ArrayList<BusStop>();
		String previousAdded = "";
		for (int i = 0; i < k_gpsCords.length; i++)
		{
			Location tempLocation = new Location(provider);
			tempLocation.setLatitude(Double.parseDouble(k_gpsCords[i][3]));
			tempLocation.setLongitude(Double.parseDouble(k_gpsCords[i][2]));
			float distance = currentLocation.distanceTo(tempLocation);
			if (distance < maxDistance)
			{
				if (duplicates || !isInArrayList(k_gpsCords[i][1], busStops))
				{
					busStops.add(new BusStop(tempLocation, distance, Integer
							.parseInt(k_gpsCords[i][0]), k_gpsCords[i][1]));
				}
			}
			previousAdded = k_gpsCords[i][1];
		}
		Collections.sort(busStops);
		ArrayList<BusStop> retList = new ArrayList<BusStop>();
		if (busStops.size() == 0)
			return busStops;
		else
		{
			for (int i = 0; i < numStops; i++)
			{
				retList.add(busStops.get(i));
			}
			return retList;
		}
	}

	/*
	 * Get all locations parsed into BusStop objects. Used when calculating
	 * distance between transfer stops
	 */
	public static ArrayList<BusStop> getAllLocations(String[][] k_gpsCords,
			String provider)
	{
		ArrayList<BusStop> busStops = new ArrayList<BusStop>();

		for (int i = 0; i < k_gpsCords.length; i++)
		{
			Location tempLocation = new Location(provider);
			tempLocation.setLatitude(Double.parseDouble(k_gpsCords[i][3]));
			tempLocation.setLongitude(Double.parseDouble(k_gpsCords[i][2]));
			busStops.add(new BusStop(tempLocation, 0, Integer
					.parseInt(k_gpsCords[i][0]), k_gpsCords[i][1]));

		}
		return busStops;
	}

	public static boolean isInArrayList(String stopname,
			ArrayList<BusStop> stops)
	{
		for (BusStop s : stops)
		{
			if (stopname.equalsIgnoreCase(s.name))
				return true;
		}
		return false;

	}

	// Method used to find the coordinates to the current location
	public static String[] showLocation(Location location)
	{

		String[] currentLocation = new String[2];

		if (location != null)
		{
			String lat = Double.toString(location.getLatitude());
			String lng = Double.toString(location.getLongitude());
			Log.v("LAT", "latitude:" + lat);
			Log.v("Longitude", "longitude:" + lng);
			currentLocation[0] = lat;
			currentLocation[1] = lng;
			Log.v("GPS", currentLocation[0] + ":" + currentLocation[1]);
		} else
		{
			currentLocation[0] = "-1";
			currentLocation[1] = "-1";
			Log.v("LOC", "Location not available.");
		}
		return currentLocation;
	}

	// Add user icon to map
	public static void addUser(GeoPoint loc, MapOverlay mapOverlay,
			Drawable icon)
	{
		OverlayItem item = new OverlayItem(loc, "", null);
		icon.setBounds(0, 0, 32, 37);
		item.setMarker(icon);

		mapOverlay.addItem(item);
		System.out.println("NUM ITEMS:  " + mapOverlay.size());

	}

	// Add bus stop icons to map
	public static void addStops(ClosestStopOnMap loc, Drawable icon,
			MapOverlay mapOverlay)
	{
		icon.setBounds(0, 0, 20, 20);
		OverlayItem item = new OverlayItem(loc.getPoint(), "", null);

		item.setMarker(icon);
		mapOverlay.addItem(item);
		// System.out.println("ADDING STOP: " + mapOverlay.size());

	}



	public static void setWalkingDistance(Route[] routes,
			ArrayList<BusStop> locationsArray)
	{
		for (int i = 0; i < routes.length; i++)
		{
			int intBusStopNumber = routes[i].getBusStopNumber();

			for (BusStop s : locationsArray)
			{
				String stopID = String.valueOf(s.stopID);
				String stopID2 = String.valueOf(routes[i].getBusStopNumber());
				// Compare last digits in bus stop nr, to set distance
				if (Integer.parseInt(stopID.substring(stopID.length() - 3,
						stopID.length())) == Integer.parseInt(stopID2
						.substring(stopID2.length() - 3, stopID2.length()))
						&& s.distance != 0)
				{
					routes[i].setWalkingDistance((int) s.distance);
				} 
			}
		}
	}


	public static boolean containsIgnoreCase(List<String> l, String s)
	{
		Iterator<String> it = l.iterator();
		while (it.hasNext())
		{
			if (it.next().equalsIgnoreCase(s))
				return true;
		}
		return false;
	}

}
