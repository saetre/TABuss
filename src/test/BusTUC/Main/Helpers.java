package test.BusTUC.Main;

import java.io.BufferedReader;
import java.io.FileInputStream;
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
import test.BusTUC.Calc.Sort;
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
	 * @param phoneNumber
	 * @param message
	 * @param context
	 */
	public static void sendSMS(String phoneNumber, String message, Context context)
	{        
		       
		SmsManager sms = SmsManager.getDefault();
		
		try
		{
			sms.sendTextMessage(phoneNumber, null, message, null, null);   
		}
		catch (Exception e) 
		{
			Toast.makeText(context, "Meding ikke sendt", Toast.LENGTH_LONG).show();        
		}
	}


	public static String[] readLines(InputStream is) throws IOException {

		URL url = null;

		BufferedReader bufferedReader = null;

		bufferedReader = new BufferedReader(
				new InputStreamReader(is, "UTF-8"));         


		//  new InputStreamReader(new FileInputStream(filename), "iso-8859-1"));
		List<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {

			if (line.startsWith("<item>")) {
				String[] trim = line.split(">");
				String[] tmp = trim[1].split("</");
				lines.add(tmp[0]);
			}
		}
		bufferedReader.close();
		return lines.toArray(new String[lines.size()]);
	}


	public static ClosestStopOnMap[] getList(String[][]coords, String provider, int numStops,int dist, Location currentLocation)
	{
		ArrayList <BusStop> busStops = Helpers.getLocationsArray(coords, "", currentLocation,1000,numStops, true);
		ClosestStopOnMap []cl = new ClosestStopOnMap[numStops];
		for(int i = 0;i<numStops;i++)
		{
			cl[i] = new ClosestStopOnMap(new GeoPoint(
					(int)	(busStops.get(i).location.getLatitude()* 1E6),
					(int)	(busStops.get(i).location.getLongitude() * 1E6)),
					(int) busStops.get(i).stopID,
					busStops.get(i).name);

		}
		return cl;

	}

	public static HashMap<String,Integer> getMostFrequentDestination(ArrayList<String> destination){
		System.out.println("ARRAYLIST INPUT! : "+destination.size());
		HashMap<String,Integer> temp = new HashMap<String,Integer>();
		for(String d: destination){
			if(temp.containsKey(d)){			
				temp.put(d, temp.get(d)+1);
			}else{
				temp.put(d, 1);
			}
		}
		return temp;
	}
	public static String getTimeNow(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("HH:ss");
		return format.format(cal.getTime());
	}

	public static int minutesFromDate(Date date){
		return date.getHours()*60+date.getMinutes();
	}

	public static String translateRequest(String from) throws Exception
	{
		String to = "";
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer("");
		try {
			HttpClient client = new DefaultHttpClient();
			String url = URLEncoder.encode("http://translate.google.com/#no|en|"+from);
			HttpGet request = new HttpGet(url);
			//    request.setURI(new URI("http://translate.google.com/%23no%7Cen%7C"+from));
			//request = 
			HttpResponse response = client.execute(request);
			in = new BufferedReader
			(new InputStreamReader(response.getEntity().getContent()));

			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();
			System.out.println(page);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("STRINGBUILDER " + sb);
		return sb.toString();

	}
	// Add dictionary to app. If not stored in SD-card previously, do so
	public static ArrayList <String> createDictionary(String[][] gpsCords, String folderName)
	{
		ArrayList <String> dictionary = new ArrayList <String>();

		for(int i=0; i<gpsCords.length; i++)
		{
			dictionary.add(gpsCords[i][1] + "\n");
		}

		// Remove duplicates
		HashSet<String> set = new HashSet<String>();
		set.addAll(dictionary);
		// Clear and add back to ArrayList 
		dictionary.clear();
		dictionary.addAll(set);
		for(int i=0; i<dictionary.size(); i++)
		{
			//	System.out.println("SECOND: " + dictionary.get(i));
		}
		SDCard.generateNoteOnSD("dictionary_finalv2", dictionary, folderName); 

		return dictionary;
	}

	public static ArrayList <String> getDictionary(String name, String folderName)
	{
		ArrayList <String> dictionary = SDCard.getFilesFromSD(folderName);
		System.out.println("STR: " + dictionary.size());
		return dictionary;

	}


	public static ArrayList <String> getTrainingSet()
	{
		ArrayList <String> speechList = SDCard.getFilesFromSD("speech_test");
		return speechList;	
	}

	public static ArrayList <String> addToTrainingSet(ArrayList <String> receivedWords, ArrayList <String> trainingSet)
	{
		ArrayList <String> updatedList = new ArrayList <String>();
		if(trainingSet.size() == 0)
		{
			trainingSet.addAll(receivedWords);			
			SDCard.generateNoteOnSD("speech_test", trainingSet, "speech");
		}
		else
		{
			System.out.println("ADDED: " + receivedWords.get(0));
			trainingSet.addAll(receivedWords);
		}

		updatedList = SDCard.getFilesFromSD("speech_test");
		for(int i=0; i<updatedList.size(); i++)
		{
			System.out.println("New list: " + updatedList.get(i));
		}
		return updatedList;

	}
	public static ArrayList <String> parseData(ArrayList <Route> value)
	{
		ArrayList <String> text = new ArrayList <String>();
		System.out.println("VALUE SIZE: " + value.size());
		boolean isTransfer = false;
		for(int i=0; i<value.size(); i++)
		{
			// If hour is 24, change to 00.
			if(Integer.parseInt(value.get(i).getArrivalTime().substring(0, 2)) == 24 )
			{
				int hour = Integer.parseInt(value.get(i).getArrivalTime().substring(0, 2));
				String newTime = "00"+value.get(i).getArrivalTime().substring(2, 4);
				value.get(i).setArrivalTime(newTime);
			}
			if(!value.get(i).isTransfer())
			{
				if(isTransfer)
				{
					System.out.println("I " + i);
					text.add((i+1)  +": OVERGANGSFORSLAG " + (i) + ": Ta Buss "+value.get(i).getBusNumber()+" fra "+value.get(i).getBusStopName()+" klokken "+value.get(i).getArrivalTime()+". Du vil n� "+value.get(i).getDestination()+" ca "+value.get(i).getTravelTime()+ " minutter senere.\n");

				}
				else if(value.get(i).getWalkingDistance() != 0)
				{
					text.add((i+1)+": Ta Buss "+value.get(i).getBusNumber()+" fra "+value.get(i).getBusStopName()+" ("+value.get(i).getWalkingDistance()+" meter)"+" klokken "+value.get(i).getArrivalTime()+". Du vil n� "+value.get(i).getDestination()+" ca "+value.get(i).getTravelTime()+ " minutter senere.\n");
				}

				else
				{
					text.add((i+1)+": Ta Buss "+value.get(i).getBusNumber()+" fra "+value.get(i).getBusStopName()+" klokken "+value.get(i).getArrivalTime()+". Du vil n� "+value.get(i).getDestination()+" ca "+value.get(i).getTravelTime()+ " minutter senere.\n");
				}

			}
			else
			{		
				if(!isTransfer)
				{
					text.add((i+1) +": Ta Buss "+value.get(i).getBusNumber()+" fra "+value.get(i).getBusStopName()+" ("+value.get(i).getWalkingDistance()+" meter)"+ " klokken "+value.get(i).getArrivalTime()+". Du vil n� "+value.get(i).getDestination()+" ca "+value.get(i).getTravelTime()+ " minutter senere.\n");
				}
				else
				{
					text.add((i+1) +": OVERGANG: Ta Buss "+value.get(i).getBusNumber()+" fra "+value.get(i).getBusStopName()+" klokken "+value.get(i).getArrivalTime()+". Du vil n� "+value.get(i).getDestination()+" ca "+value.get(i).getTravelTime()+ " minutter senere.\n");
				}
				isTransfer = true;

			}
		}
		System.out.println("RETURN END");
		return text;

	}

	public static ArrayList<BusSuggestion> parseDataObject(ArrayList <Route> value)
	{
		ArrayList <BusSuggestion> suggestions = new ArrayList <BusSuggestion>();
		System.out.println("VALUE SIZE: " + value.size());
		boolean isTransfer = false;

		for(int i=0; i<value.size(); i++)
		{
			BusSuggestion suggestion = new BusSuggestion();
			// If hour is 24, change to 00.
			if(Integer.parseInt(value.get(i).getArrivalTime().substring(0, 2)) == 24 )
			{
				int hour = Integer.parseInt(value.get(i).getArrivalTime().substring(0, 2));
				String newTime = "00"+value.get(i).getArrivalTime().substring(2, 4);
				value.get(i).setArrivalTime(newTime);
			}

			suggestion.line = value.get(i).getBusNumber();
			suggestion.origin = value.get(i).getBusStopName();//+" klokken "+
			suggestion.arrivaltime = value.get(i).getArrivalTime().substring(0, 2)+":"+value.get(i).getArrivalTime().substring(2,4);//+". Du vil n� "+
			suggestion.destination= value.get(i).getDestination();//+" ca "+
			int tmptime = Integer.parseInt(value.get(i).getArrivalTime());
			int tmptime2 = Integer.parseInt(value.get(i).getTravelTime());
			int hours = tmptime/100 + (tmptime%100+tmptime2)/60;
			int minutes = (tmptime%100+tmptime2)%60;
			suggestion.departuretime = hours +":"+ minutes;

			if(value.get(i).isTransfer() && !isTransfer){
				suggestion.isTransfer = "Overgang";
				isTransfer = true;
			}
			else suggestion.isTransfer ="Ankomst";
			if(value.get(i).getWalkingDistance() != 0)
			{
				suggestion.origin+="("+value.get(i).getWalkingDistance()+"m)";
			}

			suggestions.add(suggestion);


		}
		System.out.println("RETURN END");
		return suggestions;
	}



	/*
	 * Compute real-time, based on input routes
	 */
	public static ArrayList <Route> computeRealTime(Route [] foundRoutes, Route [] routes, HashMap <Integer, Integer>realTimeCodes, Browser k_browser, boolean afterTransfer)
	{
		Calculate calculator = new Calculate();
		Route [] returnRoutes = new Route[foundRoutes.length];
		ArrayList<Route> temp = new ArrayList <Route>();
		// Sets the travel and total time for each route
		try
		{
			returnRoutes = Helpers.setTimeForRoutes(foundRoutes, realTimeCodes, k_browser, calculator, afterTransfer);
			if(returnRoutes == null) return null;
			calculator.printOutRoutes("AFTERREALTIME",foundRoutes, true);
			if(!foundRoutes[0].isTransfer())
			{
				Route[] printRoute = calculator.sortByTotalTime(returnRoutes);
				for(int i=0; i<printRoute.length; i++)
				{
					temp.add(printRoute[i]);
				}
				System.out.println("SIZE OF ARRAYLIST : " + temp.size());
				return temp;
			}
			else
			{
				for(int i=0; i<foundRoutes.length; i++)
				{
					temp.add(foundRoutes[i]);
				}
				System.out.println("SIZE OF ARRAYLIST : " + temp.size());
				return temp;
			}



		} catch(Exception e)
		{
			e.printStackTrace();
			//Toast.makeText(this, "Real-time fail", Toast.LENGTH_LONG).show();
			ArrayList <String> err = new ArrayList <String>();
			err.add(e.toString());
			SDCard.generateNoteOnSD("errorRealTime", err, "errors");
		}

		return null;
	}

	/*
	 * Create a JSon object from input string
	 */
	public static Route[] createJSON(String jsonSubString, Calculate calculator)
	{
		Route[]routes = calculator.createRoutes(jsonSubString);
		return routes;
	}
	/*
	 * Create a JSon object from input string. 
	 * Used together with Retro's server
	 */
	public static Route[] createJSONServer(String jsonSubString, Calculate calculator, String dest)
	{
		Route[]routes = calculator.createRoutesServer(jsonSubString, dest);
		return routes;
	}

	public static ArrayList <Route> handleMissedTransfer(ArrayList <Route> value)
	{
		ArrayList <Route> finalRoutes = new ArrayList <Route>();
		boolean noTransfer = true;
		for(int i=0; i<value.size(); i++)
		{
			if(noTransfer)
			{
				if(value.get(i).isTransfer())
				{
					noTransfer = false;
				}
				else return value;
			}
			else
			{
				Route transfer = value.get(i);
				Route firstDest = value.get(i-1);
				int walk = 2;
				System.out.println("Reisetid fra " + value.get(i-1).getBusStopName() +": " + value.get(i-1).getArrivalTime() + " Reisetid: "+ value.get(i-1).getTravelTime() + " og Avgang " + value.get(i).getBusStopName() + " er: "+ value.get(i).getArrivalTime());
				System.out.println("Sammenligner verdier: " + (Integer.parseInt(value.get(i-1).getArrivalTime())+ Integer.parseInt(value.get(i-1).getTravelTime()) +walk)+ " og "+ (Integer.parseInt(value.get(i).getArrivalTime())));
				// Assume we neeed minimum two minutes to get to the next bus stop
				if((Integer.parseInt(firstDest.getArrivalTime())+ Integer.parseInt(firstDest.getTravelTime()) +walk)>= ( Integer.parseInt(transfer.getArrivalTime())))
				{
					String beforeTwelve = "0";
					System.out.println("PR�VER � FINNE NY");
					if(value.get(i-1).getArrivalTime().length() == 3)
					{
						beforeTwelve = beforeTwelve+value.get(i-1).getArrivalTime();
					}
					else beforeTwelve = value.get(i-1).getArrivalTime();
					int arrivalTimeHours = Integer.parseInt(beforeTwelve.substring(0, 2));
					int travelTime = Integer.parseInt(value.get(i-1).getTravelTime());
					System.out.println("Travel time: " + travelTime);
					int arrivalTimeMinutes = Integer.parseInt(beforeTwelve.substring(2,4))+ (arrivalTimeHours * 60) + travelTime;
					System.out.println("Arrival minutes: "+ arrivalTimeMinutes);

					int newHours = (int) Math.ceil((arrivalTimeMinutes )/ 60);
					System.out.println("New hours: " + newHours);
					// Check if hour is past 23. If so, adjust
					if(newHours > 23) newHours = newHours - 24;
					int newMinutes = ((arrivalTimeMinutes) %60) ;
					// If minutes have a zero, which is not received, append
					StringBuffer buf = new StringBuffer("" + newMinutes);
					if(buf.length() == 1) buf.insert(0, "0");
					String newTime = String.valueOf(newHours) + String.valueOf(buf.toString());
					int arrivalTime = Integer.parseInt(newTime)+ walk;
					String departureStop = value.get(i).getBusStopName();
					String destination = value.get(i).getDestination();
					String query ="fra "+ departureStop+","+  "til "+ destination + " etter " + arrivalTime;

					BusStop stop = new BusStop(null, 0, value.get(i).getBusStopNumber(), value.get(i).getBusStopName());
					ArrayList <BusStop> newList = new ArrayList <BusStop>();
					newList.add(stop);
					// Run new query
					try
					{
						// Remove route we won't catch anyway
						value.remove(value.get(i));

						// Update list
						value.trimToSize();

						ArrayList <Route> routes = Helpers.runString(destination, newList, Homescreen.k_browser, Homescreen.realTimeCodes, query);						
						finalRoutes = new ArrayList <Route>();
						// Assure that no routes leave before we arrive at the stop
						for(int j =0; j<routes.size(); j++)
						{
							// Add to final list
							finalRoutes.add(routes.get(j));

						}
						System.out.println("FINAL ROUTES SIZE: " + finalRoutes.size());
						for(int j=0; j<finalRoutes.size(); j++)
						{
							System.out.println("FINAL ROUTES: " + finalRoutes.get(j).getBusNumber());
						}
						// Add final routes to original list
						value.addAll(finalRoutes);
						for(int j=0; j<value.size(); j++)
						{
							System.out.println("RETURN ROUTES: " + value.get(j).getBusNumber());
						}
						return value;

					}
					catch(Exception e)
					{
						e.printStackTrace();
						ArrayList <String> err = new ArrayList <String>();
						err.add(e.toString());
						SDCard.generateNoteOnSD("errorParseDataTransf", err, "errors");
						return null;
					}

				}

			}
		}
		return value;
	}


	/*
	 * Will run a query directly towards BussTUC with standard natural language syntax
	 * 
	 */
	public static StringBuffer runStandard(String input)
	{
		StringBuffer buf = new StringBuffer();
		Browser k_browser = new Browser();
		// Perform action on clicks
		if(!input.equals(""))
		{
			try
			{
				// System.out.println("K-browserobj " + k_browser.toString() + "realtimelength: " + realTimeCodes.size()); 

				long time = System.nanoTime();
				buf = k_browser.getRequestStandard(input);   
				return buf;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		else System.out.println("EMPTY TEXT");
		//System.out.println("HER SKAL VI IKKE HAVNE");
		return null;
	}


	/*
	 * Will run a query directly towards BussTUC
	 * 
	 */
	public static ArrayList <Route> run(String input, ArrayList<BusStop> tSetExclude, Browser k_browser, HashMap <Integer, Integer> realTimeCodes)
	{
		Route[] finalRoutes;
		ArrayList <Route> returnRoutes = new ArrayList<Route>();
		// Perform action on clicks
		if(!tSetExclude.isEmpty())
		{
			try
			{
				// System.out.println("K-browserobj " + k_browser.toString() + "realtimelength: " + realTimeCodes.size()); 

				long time = System.nanoTime();
				String[] html_page = k_browser.getRequest(tSetExclude,input,false);   
				//tSetExclude
				long newTime = System.nanoTime() - time;
				System.out.println("TIME ORACLEREQUEST: " +  newTime/1000000000.0);
				//System.out.println("TEKST: " + editTe.getText().toString() );
				//System.out.println("HTML LENGTH: " + html_page.length); 
				StringBuilder str = new StringBuilder(); 
				// Parses the returned html
				if(!Helpers.parseHtml(html_page, str)) return null;
				else
				{	          
					int indexOf = str.lastIndexOf("}");
					String jsonSubString = str.substring(0, indexOf+1); 
					jsonSubString = jsonSubString.replaceAll("\\}", "},");
					jsonSubString = jsonSubString.substring(0, jsonSubString.length()-1);
					Log.v("manipulatedString","New JSON:"+jsonSubString);
					int wantedBusStop = 0;  
					Calculate calculator = new Calculate();           
					// Create routes based on jsonSubString
					Route[] routes = createJSON(jsonSubString, calculator);
					System.out.println("SETTING WALKING DIST");
					// Set walking dist
					Helpers.setWalkingDistance(routes, tSetExclude);
					calculator.printOutRoutes("BEFORE",routes, false);
					finalRoutes = calculator.suggestRoutes(routes);
					calculator.printOutRoutes("AFTER",finalRoutes, false);
					// Compute real time
					returnRoutes =  computeRealTime(finalRoutes, routes,  realTimeCodes, k_browser, false);
					ArrayList <Route> fixedRealTime = handleMissedTransfer(returnRoutes);
					return fixedRealTime;

				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ArrayList <String> err = new ArrayList <String>();
				err.add(e.toString());
				SDCard.generateNoteOnSD("errorHelpers::Run", err, "errors");
				return null;
			}
		}
		else System.out.println("EMPTY LIST");
		//System.out.println("HER SKAL VI IKKE HAVNE");
		return null;
	}

	/*
	 * Runs query against Retro's server
	 * According methods such as createJSONServer are modified versions of the existing
	 */
	public static ArrayList <Route> runServer(String input, Browser k_browser, Location location, int numStops, int dist)
	{
		Route[] finalRoutes;
		// Perform action on clicks

		try
		{
			long time = System.nanoTime();
			String html_page = k_browser.getRequestServer(input,false, location, numStops, dist);   
			// No internet connection
			if(html_page == null)
			{
				ArrayList <Route> temp = new ArrayList <Route>();
				temp.add(new Route());
				temp.get(0).setBusStopName("Nettilgang");
				return temp;
			}
			long newTime = System.nanoTime() - time;
			System.out.println("TIME ORACLEREQUEST: " +  newTime/1000000000.0);	
			Calculate calculator = new Calculate();          

			// Create routes based on jsonSubString
			Route[] routes = createJSONServer(html_page.toString(), calculator, input);	       
			if(routes != null)
			{
				calculator.printOutRoutes("BEFORE",routes, false);
				ArrayList <Route> returnRoutes = new ArrayList <Route>();
				for(int i=0; i<routes.length; i++)
				{
					returnRoutes.add(routes[i]);
				}
				return returnRoutes;
			}
			else return null;
		}
		//}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}

	/*
	 * Will run a query with an additional String
	 */
	public static ArrayList <Route> runString(String input, ArrayList<BusStop> tSetExclude, Browser k_browser, HashMap <Integer, Integer> realTimeCodes, String additional)
	{
		Route[] finalRoutes;
		// Perform action on clicks
		if(!tSetExclude.isEmpty())
		{
			try
			{
				// System.out.println("K-browserobj " + k_browser.toString() + "realtimelength: " + realTimeCodes.size()); 

				long time = System.nanoTime();
				String[] html_page = k_browser.getRequestString(tSetExclude,input,false, additional);   
				//tSetExclude
				long newTime = System.nanoTime() - time;
				System.out.println("TIME ORACLEREQUEST: " +  newTime/1000000000.0);
				//System.out.println("TEKST: " + editTe.getText().toString() );
				//System.out.println("HTML LENGTH: " + html_page.length); 
				StringBuilder str = new StringBuilder(); 
				// Parses the returned html
				if(!Helpers.parseHtml(html_page, str)) return null;
				else
				{	          
					int indexOf = str.lastIndexOf("}");
					String jsonSubString = str.substring(0, indexOf+1); 
					jsonSubString = jsonSubString.replaceAll("\\}", "},");
					jsonSubString = jsonSubString.substring(0, jsonSubString.length()-1);
					Log.v("manipulatedString","New JSON:"+jsonSubString);
					int wantedBusStop = 0;  
					Calculate calculator = new Calculate();           

					// Create routes based on jsonSubString
					Route[] routes = createJSON(jsonSubString, calculator);
					for(int i=0; i<routes.length; i++)
					{
						System.out.println("CALCULATED ROUTES: " + routes[i].getBusStopName());
					}
					// Set walking dist
					Helpers.setWalkingDistance(routes, tSetExclude);
					calculator.printOutRoutes("BEFORE",routes, false);
					finalRoutes = calculator.suggestRoutes(routes);
					for(int i=0; i<finalRoutes.length; i++)
					{
						System.out.println("CALCULATED FINALROUTES " + finalRoutes[i].getBusStopName());
					}
					calculator.printOutRoutes("AFTER",finalRoutes, false);
					// Compute real time
					ArrayList <Route> returnRoutes =  computeRealTime(finalRoutes, routes,  realTimeCodes, k_browser, true);
					/*     ArrayList <Route> returnRoutes = new ArrayList <Route>();
		         for (int i = 0; i < finalRoutes.length; i++) {
					returnRoutes.add(finalRoutes[i]);
				}*/
					return returnRoutes;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ArrayList <String> err = new ArrayList <String>();
				err.add(e.toString());
				SDCard.generateNoteOnSD("errorRunString", err, "errors");
				return null;
			}
		}
		else System.out.println("EMPTY LIST");
		System.out.println("HER SKAL VI IKKE HAVNE");
		return null;

	}


	/*
	 * Get the closest stops based on location, distance and num stops.
	 */
	public static ArrayList<BusStop> getLocationsArray(String[][] k_gpsCords, String provider, Location currentLocation, int maxDistance,int numStops, boolean duplicates){
		ArrayList<BusStop> busStops = new ArrayList<BusStop>();
		String previousAdded = "";
		for(int i=0;i<k_gpsCords.length;i++){
			Location tempLocation = new Location(provider);
			tempLocation.setLatitude(Double.parseDouble(k_gpsCords[i][3]));
			tempLocation.setLongitude(Double.parseDouble(k_gpsCords[i][2]));
			float distance = currentLocation.distanceTo(tempLocation);
			if(distance< maxDistance){
				if(duplicates || !isInArrayList(k_gpsCords[i][1],busStops)){
					busStops.add(new BusStop(tempLocation,distance,Integer.parseInt(k_gpsCords[i][0]), k_gpsCords[i][1]));
				}
			}
			previousAdded = k_gpsCords[i][1];
		}
		Collections.sort(busStops);
		ArrayList <BusStop> retList = new ArrayList <BusStop>();
		if(busStops.size() == 0) return busStops;
		else
		{
			for(int i=0; i<numStops; i++)
			{
				retList.add(busStops.get(i));
			}
			return retList;
		}
	}

	/*
	 * Get all locations parsed into BusStop objects. Used when calculating distance between transfer stops
	 */
	public static ArrayList <BusStop> getAllLocations(String[][] k_gpsCords, String provider)
	{
		ArrayList <BusStop>busStops = new ArrayList <BusStop>();

		for(int i=0; i<k_gpsCords.length; i++)
		{
			Location tempLocation = new Location(provider);
			tempLocation.setLatitude(Double.parseDouble(k_gpsCords[i][3]));
			tempLocation.setLongitude(Double.parseDouble(k_gpsCords[i][2]));
			busStops.add(new BusStop(tempLocation,0,Integer.parseInt(k_gpsCords[i][0]), k_gpsCords[i][1]));

		}
		return busStops;
	}

	public static boolean isInArrayList(String stopname, ArrayList<BusStop> stops){
		for(BusStop s:stops){
			if(stopname.equalsIgnoreCase(s.name)) return true;
		}
		return false;

	}

	// Method used to find the coordinates to the current location
	public static String[] showLocation(Location location) {

		String[] currentLocation = new String[2]; 

		if (location != null) {
			String lat = Double.toString(location.getLatitude());
			String lng = Double.toString(location.getLongitude());
			Log.v("LAT", "latitude:"+lat);
			Log.v("Longitude", "longitude:"+lng);
			currentLocation[0] = lat;
			currentLocation[1] = lng;
			Log.v("GPS", currentLocation[0]+":"+currentLocation[1]);
		} else {
			currentLocation[0] = "-1";
			currentLocation[1] = "-1";
			Log.v("LOC","Location not available.");
		}
		return currentLocation;
	} 

	// Add user icon to map
	public static void addUser(GeoPoint loc, MapOverlay mapOverlay, Drawable icon) 
	{
		OverlayItem item = new OverlayItem(loc, "", null);	
		icon.setBounds(0,0, 32, 37);
		item.setMarker(icon);

		mapOverlay.addItem(item);
		System.out.println("NUM ITEMS:  " + mapOverlay.size());

	}

	// Add bus stop icons to map
	public static void addStops(ClosestStopOnMap loc, Drawable icon, MapOverlay mapOverlay) 
	{
		icon.setBounds(0,0,20,20);
		OverlayItem item = new OverlayItem(loc.getPoint(), "", null);	

		item.setMarker(icon);
		mapOverlay.addItem(item);
		//System.out.println("ADDING STOP: " + mapOverlay.size());

	}


	/*
	 *  Method which parses the return message from BussTUC
	 *  Not used with retros server
	 */
	public static boolean parseHtml(String[]html_page, StringBuilder str)
	{
		for(int i = 0;i<html_page.length;i++) 
		{
			Log.v("CONTENT"+i, html_page[i]); 
			if(!html_page[i].contains("</body>"))
			{
				str.append(html_page[i] + "\n"); 
			}
			// Simple error handling. If the object contains "error", return false
			if(html_page[0].equalsIgnoreCase("error"))
			{
				// Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();
				System.out.println("NOT FOUND ERROR FOO");
				// Toast.makeText(this, "Query timed out", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return true;
	}



	public static void setWalkingDistance(Route[]routes, ArrayList<BusStop> locationsArray)
	{
		for(int i = 0;i<routes.length;i++)
		{
			int intBusStopNumber = routes[i].getBusStopNumber(); 

			for(BusStop s: locationsArray)
			{
				String stopID = String.valueOf(s.stopID);
				String stopID2 = String.valueOf(routes[i].getBusStopNumber());
				// Compare last digits in bus stop nr, to set distance
				if(Integer.parseInt(stopID.substring(stopID.length()-3, stopID.length())) == Integer.parseInt(stopID2.substring(stopID2.length()-3,stopID2.length())) && s.distance !=0)
				{
					routes[i].setWalkingDistance((int)s.distance);
				}else{         		 
					//	routes[i].setWalkingDistance(1000); 
				}
			}
		}
	}



	@SuppressWarnings("unchecked")
	public static Route[] setTimeForRoutes(Route[]finalRoutes, HashMap <Integer, Integer> realTimeCodes, Browser k_browser, final Calculate calculator, boolean afterTransfer)

	{
		// Copy of input routes
		final Route [] tempRoutes = new Route[finalRoutes.length];
		final Browser tempBrowser = k_browser;
		final boolean a_transfer = afterTransfer;
		for (int i = 0; i < tempRoutes.length; i++) 
		{
			tempRoutes[i] = finalRoutes[i];
		}

		// Route array to return
		Route [] retRoutes = new Route[tempRoutes.length];

		// Iterate through received routes
		long first = System.nanoTime();
		//BusStops nextBus = new BusStops();
		ArrayList <Thread> threadList = new ArrayList <Thread>();
		try
		{
			for(int i = 0;i<tempRoutes.length;i++)
			{
				int tempId = Integer.parseInt(realTimeCodes.get(tempRoutes[i].getBusStopNumber()).toString());
				int wantedLine = tempRoutes[i].getBusNumber();
				final int tId = tempId;
				final int wLine = wantedLine;

				final int j = i;

				// Create new threads for sending queries to the real-time system.


				Thread thread =   new Thread(new Runnable() {
					public void run() {

						final BusDeparture tempNextBus = tempBrowser.specificRequest(tId,wLine);     

						// If not part of a transfer query, i.e new query based on not reaching or bus
						if(!a_transfer)
						{
							tempRoutes[j].setArrivalTime(tempNextBus.getArrivalTime().getHours()+""+String.format("%02d",tempNextBus.getArrivalTime().getMinutes())+"");
							int k_totalTime = calculator.calculateTotalTime(tempRoutes[j].getArrivalTime(), tempRoutes[j].getTravelTime());
							tempRoutes[j].setTotalTime(k_totalTime); 
						}
						else
						{
							// If before 12 AM, BussTUC parses hours as integer
							if(tempRoutes[j].getArrivalTime().length() == 3)
							{
								String arrivalTime = "0"+tempRoutes[j].getArrivalTime();
								tempRoutes[j].setArrivalTime(arrivalTime);
							}
						}
						// Set total time for routes

					}    	    

				});
				thread.start();      	  
				threadList.add(thread);
			}
		}
		catch(Exception e)
		{
			return finalRoutes;
		}

		System.out.println("NUM THREADS: " + threadList.size());

		for (Thread t : threadList) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		long second = System.nanoTime() - first;
		System.out.println("TIME RETRIEVING REAL-TIME: " +second/1000000000.0);
		// Remove suggestions where buses passing same stops have huge total time difference
		// Has not been tested yet


		ArrayList <Route> list = removeStupid(tempRoutes);
		retRoutes = list.toArray(new Route[list.size()]);
		System.out.println("Final size: " + retRoutes.length);
		return retRoutes;


	}

	/*
	 * Remove stupid suggestions
	 */
	public static ArrayList <Route> removeStupid(Route [] tempRoutes)
	{
		ArrayList<Route> list = new ArrayList<Route>(Arrays.asList(tempRoutes));
		System.out.println("Init size: " + tempRoutes.length);

		// Remove stupid suggestions
		Route temp = new Route();
		int count = 0;
		for (int i = 0; i < list.size(); i++) 
		{
			temp = list.get(i);
			System.out.println("Travel total times: " + list.get(i).getBusStopName()+ " nr: " + list.get(i).getBusNumber() + " to " + list.get(i).getDestination() + "   " + list.get(i).getTotalTime());
			// If Route[i-1] has 
			for(int j=0; j<list.size(); j++)
			{
				// If same bus stop name
				if(list.get(j).getBusStopName().equals(temp.getBusStopName()))
				{
					count++;
					// And not itself
					if(count >1)
					{
						// If the found has twice the total time, and total time is minimum 20 minutes
						if(list.get(j).getTotalTime() >= (temp.getTotalTime() *2) && temp.getTotalTime() > 20)
						{
							System.out.println("Comaring: " + list.get(j).getTotalTime() + " and "+ temp.getTotalTime() *2);
							System.out.println("Removed i: " + list.get(j).getBusStopName() + "  " + list.get(j).getTotalTime()   + "  " + list.get(j).getBusNumber()+ " Opposed to: "  + temp.getBusStopName() + "  " + temp.getTotalTime() );

							list.remove(list.get(j));
							list.trimToSize();
						}
						// If temp has twice the travel time
						else if(list.get(j).getTotalTime()*2 <= temp.getTotalTime() && list.get(j).getTotalTime() > 20)
						{
							System.out.println("Comaring: " + list.get(j).getTotalTime() + " and "+ temp.getTotalTime() *2);
							System.out.println("Removed temp: " + temp.getBusStopName() + "  " + temp.getTotalTime()  + "  " + temp.getBusNumber()+ " Opposed to: "  + list.get(j).getBusStopName() + "  " + list.get(j).getTotalTime());

							list.remove(temp);
							list.trimToSize();
						}
					}
				}

				temp = list.get(i);
			}


		}
		return list;
	}

	public static boolean containsIgnoreCase(List <String> l, String s)
	{
		Iterator <String> it = l.iterator();
		while(it.hasNext())
		{
			if(it.next().equalsIgnoreCase(s))
				return true;
		}
		return false;
	}




}
