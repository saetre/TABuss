package test.BusTUC.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import test.BusTUC.Favourites.SDCard;
import test.BusTUC.Queries.Browser;
import test.BusTUC.Stops.BusDeparture;
import test.BusTUC.Stops.BusStop;
import test.BusTUC.Stops.ClosestHolder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
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
	public static ArrayList <String> createDictionary(String[][] gpsCords)
	{
		ArrayList <String> dictionary = SDCard.getFilesFromSD("dictionary_test");
		
		if(dictionary.size() == 0)
		{
			for(int i=0; i<gpsCords.length; i++)
	        {
	        	dictionary.add(gpsCords[i][1]);
	        }
			for(int i=0; i<dictionary.size(); i++)
			{
			//	System.out.println("FIRST: " + dictionary.get(i));
			}
			// Remove duplicates
			HashSet set = new HashSet();
			set.addAll(dictionary);
			// Clear and add back to ArrayList 
			dictionary.clear();
			dictionary.addAll(set);
			for(int i=0; i<dictionary.size(); i++)
			{
			//	System.out.println("SECOND: " + dictionary.get(i));
			}
			SDCard.generateNoteOnSD("dictionary_test", dictionary, "dictionary"); 
		}
	    return dictionary;
	}
	
	// Will parse the route list received, and return text list
	public static ArrayList <String> parseData(ArrayList <Route> value)
	{
		ArrayList <String> text = new ArrayList <String>();
		boolean noTransfer = true;
		   for(int i=0; i<value.size(); i++)
		   {
			   if(noTransfer)//(!value.get(i).isTransfer())
			   {
		        	text.add((i+1)+": Ta Buss "+value.get(i).getBusNumber()+" fra "+value.get(i).getBusStopName()+" ("+value.get(i).getWalkingDistance()+" meter)"+" klokken "+value.get(i).getArrivalTime()+". Du vil n� "+value.get(i).getDestination()+" ca "+value.get(i).getTravelTime()+ " minutter senere.\n");
		        	if(value.get(i).isTransfer())
		        	{
		        		noTransfer = false;
		        	}
			   }
			   else
			   {
				   if(Integer.parseInt(value.get(i-1).getArrivalTime()+ value.get(i-1).getTravelTime()) > Integer.parseInt(value.get(i).getArrivalTime()))
		      		  {
		      			  text.set(i-1,text.get(i-1) + "\n"+(i+1)+ ": Oh shit, bussen har alt dratt");

		      			  // Send new query based on updated info
		      			 /* Sort sort = new Sort();
		      			  System.out.println("Creating: " + printRoute[i-1].getBusStopName() + "  " + Homescreen.gpsCords.length);
		      			  HashMap<Integer,HashMap <Integer, Location>> newCoords = getLocationsBasedOnString(Homescreen.gpsCords, printRoute[i].getBusStopName());
		      			 
		      			  System.out.println("new coords size: " + newCoords.size());

		      			  HashMap<Integer,Location> newTsetExclude = new HashMap <Integer, Location>();
		      			  Object[] keys = newCoords.keySet().toArray();
		      			  int currentVal = Integer.parseInt(newCoords.get(keys[0]).keySet().toArray()[0].toString());
		      			  newTsetExclude.put(currentVal, newCoords.get(keys[0]).get(currentVal));
		      			  System.out.println("new set size: " + newTsetExclude.size());
		      			  
		      			  // Idea is to set new query containing the second stop -> destination with updated times.
		      			  // The problem is which query to send to busTUC. So for now, an error message is shown if the second bus has left before arrival to that stop
		      			  int arrivalTime = Integer.parseInt(printRoute[i-1].getArrivalTime());
		      			  int travelTime = Integer.parseInt(printRoute[i-1].getTravelTime());
		      			  int sum = arrivalTime + travelTime;
		      			  run(printRoute[i].getDestination() + " etter " + sum, newTsetExclude, newCoords, k_browser, realTimeCodes);*/
		      		

		      		  }
		      		  else
		      		  {
		      			  
		      			  text.add((i+1)+": OVERGANG: Ta Buss "+value.get(i).getBusNumber()+" fra "+value.get(i).getBusStopName()+" klokken "+value.get(i).getArrivalTime()+". Du vil n� "+value.get(i).getDestination()+" ca "+value.get(i).getTravelTime()+ " minutter senere.\n");
		      		  }
			   }
		   }
		   return text;
	
		}	
	
	
	
	 public static ArrayList <Route> computeRealTime(Route [] foundRoutes, Route [] routes, HashMap realTimeCodes, Browser k_browser)
	    {
	    	Calculate calculator = new Calculate();
	        Route [] returnRoutes = new Route[foundRoutes.length];
	        ArrayList<Route> temp = new ArrayList <Route>();
	        // Sets the travel and total time for each route
	        try
	        {
	        	// If nullpointer:
	            /*
	             *Change list usage to finalRoutes
	             */
	        	returnRoutes = Helpers.setTimeForRoutes(foundRoutes, realTimeCodes, k_browser, calculator);
		        calculator.printOutRoutes("AFTERREALTIME",foundRoutes, true);
		
		        Route[] printRoute = calculator.sortByTotalTime(returnRoutes);
		        for(int i=0; i<printRoute.length; i++)
		        {
		        	temp.add(printRoute[i]);
		        }
		        System.out.println("SIZE OF ARRAYLIST : " + temp.size());
				return temp;
		        
		       } catch(Exception e)
		       {
		        	e.printStackTrace();
		        	//Toast.makeText(this, "Real-time fail", Toast.LENGTH_LONG).show();
		       }
	        
	        return null;
	    }
	
	 public static Route[] createJSON(String jsonSubString, Calculate calculator)
	 {
		 Route[]routes = calculator.createRoutes(jsonSubString);
		 return routes;
	 }
	    

	   
	    public static ArrayList <Route> run(String input, ArrayList<BusStop> tSetExclude,  HashMap<Integer,HashMap<Integer,Location>> locationsArray, Browser k_browser, HashMap realTimeCodes)
	    {
	    	Route[] finalRoutes;
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
		          // Set walking dist
		          Helpers.setWalkingDistance(routes, tSetExclude);
		          calculator.printOutRoutes("BEFORE",routes, false);
		         finalRoutes = calculator.suggestRoutes(routes);
		          calculator.printOutRoutes("AFTER",finalRoutes, false);
		          // Compute real time
		        ArrayList <Route> returnRoutes =  computeRealTime(finalRoutes, routes,  realTimeCodes, k_browser);
		
		          return returnRoutes;
		         }
	    		}
	    		catch(Exception e)
	    		{
	    			e.printStackTrace();
	    			return null;
	    		}
	  	     }
	    	System.out.println("HER SKAL VI IKKE HAVNE");
	        return null;

	    }
	  
	
	 
	    public static HashMap<Integer,Location> testLocations(String[][] k_gpsCords, String provider, Location currentLocation)
	    {
	    	String tempCords[][] = k_gpsCords; 
	        int clength = tempCords.length; 
	        Log.v("CORDL", "C:"+clength); 
	        
	        HashMap<Integer,HashMap<Integer,Location>> newMap = new HashMap<Integer,HashMap<Integer,Location>>();
	        Location closestLocation[] = new Location[clength]; 
	        HashMap<Integer,Integer> counter = new HashMap<Integer,Integer>();
	     	HashMap<Integer, Location> hMap = new HashMap<Integer,Location>(); 

	        for(int i = 0;i<clength;i++)
	        {
	     	   closestLocation[i] = new Location(provider);
	     	   closestLocation[i].setProvider(tempCords[i][1]); // Bus stop name
	     	   closestLocation[i].setLatitude(Double.parseDouble(tempCords[i][3])); // 1 i gps2.xml
	     	   closestLocation[i].setLongitude(Double.parseDouble(tempCords[i][2])); // 2 i gps2.xml
	     	   int alt = Integer.parseInt(tempCords[i][0]);
	     	   closestLocation[i].setAltitude(alt); // Add bus stop ID as altitude
	     	   int distance = (int)closestLocation[i].distanceTo(currentLocation);
	     	   hMap.put(distance, closestLocation[i]);
	     	   String busStopId = tempCords[i][0]; // 0 i gps2.xml

	             
	        } 
	        return hMap; 
	    }
	    

	
	   // Creates the HashMap for the locations. 
	    // Outer key is bus stop ID
	    // Inner key is distance to stop
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
		for(int i=0; i<numStops; i++)
		{
			retList.add(busStops.get(i));
		}
		return retList;
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
    public static void addStops(ClosestHolder loc, Drawable icon, MapOverlay mapOverlay) 
    {
    	icon.setBounds(0,0, 15, 15);
    	OverlayItem item = new OverlayItem(loc.getPoint(), "", null);	
    	
		item.setMarker(icon);
		mapOverlay.addItem(item);
		//System.out.println("ADDING STOP: " + mapOverlay.size());
        
    }
    
    
    // Method which parses the return message from BussTUC
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

       	 		if(Integer.parseInt(stopID.substring(stopID.length()-2, stopID.length())) == Integer.parseInt(stopID2.substring(stopID2.length()-2,stopID2.length())))
       	 		{
       	 			routes[i].setWalkingDistance((int)s.distance);
       	 		}else{         		 
       	 			//routes[i].setWalkingDistance(-1); 
       	 		}
       	 	}
        }
    }
    
    
    // If nullpointer:
    /*
     * Change return to void.
     * Change list usage to finalRoutes
     */
    public static Route[] setTimeForRoutes(Route[]finalRoutes, HashMap realTimeCodes, Browser k_browser, final Calculate calculator)
    {
    	// Copy of input routes
    	final Route [] tempRoutes = new Route[finalRoutes.length];
    	final Browser tempBrowser = k_browser;
    	for (int i = 0; i < tempRoutes.length; i++) 
    	{
    		tempRoutes[i] = finalRoutes[i];
		}
    	
    	// Route array to return
    	Route [] retRoutes = new Route[tempRoutes.length];
    	
    	// Iterate through received routes
    	long first = System.nanoTime();
      	//BusStops nextBus = new BusStops();
    	  ArrayList <Thread> threadList = new ArrayList();
    	for(int i = 0;i<tempRoutes.length;i++)
        {
      	 // System.out.println("FANT BUSSTOPP: " +finalRoutes[i].getBusStopNumber());
    	 // System.out.println("Realtimecodes: " + realTimeCodes.size());
      	  int tempId = Integer.parseInt(realTimeCodes.get(tempRoutes[i].getBusStopNumber()).toString());
      	  int wantedLine = tempRoutes[i].getBusNumber();
      	  final int tId = tempId;
      	  final int wLine = wantedLine;
      	  System.out.println("WantedLine: " + wantedLine);
      	  System.out.println("TMPID: " + tempId);

      	  final int j = i;
      	  
      	  // Create new threads for sending queries to the real-time system.
      	Thread thread =   new Thread(new Runnable() {
      	    public void run() {
      	    	
      	    	final BusDeparture tempNextBus = tempBrowser.specificRequest(tId,wLine);     
      	  //  	System.out.println("Nextbus: " + nextBus);
            	  tempRoutes[j].setArrivalTime(tempNextBus.getArrivalTime().getHours()+""+String.format("%02d",tempNextBus.getArrivalTime().getMinutes())+"");
            	  int k_totalTime = calculator.calculateTotalTime(tempRoutes[j].getArrivalTime(), tempRoutes[j].getTravelTime());
            	  tempRoutes[j].setTotalTime(k_totalTime);             	 
      	    }    	    
      	    
      	  });
      	  thread.start();      	  
      	  threadList.add(thread);
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
   
    	ArrayList<Route> list = new ArrayList<Route>(Arrays.asList(tempRoutes));
    	System.out.println("Init size: " + tempRoutes.length);
    	// Testing the removal of stupid suggestions
    	/*
    	String foo = "";
    	int counter = 0;
    	for (int i = 0; i < list.size(); i++)
    	{
    		foo = list.get(i).getBusStopName();
    			for(int j=0; j<list.size(); j++)
    			{
					if(list.get(j).getBusStopName().equalsIgnoreCase(foo))
					{
						counter ++;
						if(counter > 1)
						{
							System.out.println("Found more of same " + list.get(j).getBusStopName() + "  and " + foo + "  "  +list.get(j).getBusNumber());
							System.out.println("Totaltime before: " + list.get(j).getTotalTime());
							list.get(j).setTotalTime(100);
							System.out.println("Totaltime after: " + list.get(j).getTotalTime());
							break;
						}
					}
    			
    			}
    		
		}*/
    	
    	
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
    	
    	retRoutes = list.toArray(new Route[list.size()]);
    	System.out.println("Final size: " + retRoutes.length);
    	return retRoutes;
    	
    	
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
