package test.BusTUC.Queries; 

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import test.BusTUC.Stops.BusDeparture;
import test.BusTUC.Stops.BusStop;

import test.BusTUC.Main.BusTUCApp;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ParseException;
import android.os.AsyncTask;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

public class Browser 
{
	WebView web;
	static HttpClient m_client; 
	static HttpFormat httpF; 
	public Browser()
	{
		m_client = new DefaultHttpClient();
		httpF = new HttpFormat(); 

	}

	

	public String[] getRequest(HashMap<Integer,Location> startMap, String stop, Boolean formated)
	{		
		String[] html_string = null; 
		DecimalFormat decifo = new DecimalFormat("###");
		String start2 = "(";
		Object[] keys = startMap.keySet().toArray();
		Arrays.sort(keys);
		// Name of busstop
		int hSize = startMap.keySet().size(); 
		for(int i = 0;i<hSize;i++)
		{
			// Walking distance in minutes
			System.out.println("WALK: "+Double.parseDouble(keys[i].toString()));
			String output2 = decifo.format(Math.ceil((Double.parseDouble(keys[i].toString())/1.7)/60));
			start2 = start2 + "" + startMap.get(keys[i]).getProvider()+""+"+"+output2; 
			System.out.println("START TO SATT: " + start2 + "  " + startMap.get(keys[i]).getProvider());
			if(i+1<hSize)
			{
				start2 = start2 + ","; 
			}
		}
		start2 = start2 + ")";
		String wanted_string = start2 + " til " + stop ; 
		String wanted_string2 = "fra gl�shaugen til nardo";
		Log.v("BUSTUCSTR", "wanted_string :"+wanted_string);
		HttpPost m_post= new HttpPost("http://www.idi.ntnu.no/~tagore/cgi-bin/busstuc/busq.cgi");
		//HttpPost m_post= new HttpPost("http://m.atb.no/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=");
		Long time = System.nanoTime();
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
			nameValuePairs.add(new BasicNameValuePair("lang", "eng"));  
			nameValuePairs.add(new BasicNameValuePair("quest", wanted_string)); 
			UrlEncodedFormEntity url = new UrlEncodedFormEntity(nameValuePairs);	        
			//  System.out.println("URLENC: " + url.toString());
			m_post.setEntity(url);  
			String responseBody = EntityUtils.toString(m_post.getEntity());       
			// Execute. Will not crash if route info is not found(which is not cool)
			HttpResponse m_response = m_client.execute(m_post);

			//Log.v("m_response", inputStreamToString(m_response.getEntity().getContent()));
			System.out.println("Wanted String: " + wanted_string);
			// Request
			html_string = httpF.request(m_response);

			// Will fail if server is busy or down
			Log.v("html_string", "Returned html: " + html_string);
			//Long newTime = System.nanoTime() - time;
			//System.out.println("TIMEEEEEEEEEEEEEEEEEEEEE: " +  newTime/1000000000.0);
		} catch (ClientProtocolException e) {
			Log.v("CLIENTPROTOCOL EX", "e:"+e.toString());
		} catch (IOException e) {
			Log.v("IO EX", "e:"+e.toString()); 

		}
		catch(NullPointerException e)
		{
			Log.v("NULL", "NullPointer");
		}
		catch(StringIndexOutOfBoundsException e)
		{
			Log.v("StringIndexOutOfBounds", "Exception");
		}
		catch(Exception e)
		{
			Log.v("FUCKINGTOLARGE", "Exception");
		}

		/*	for(int i =0; i< html_string.length;i++)
		{
			Log.v("HTMLFOO", html_string[i]);
		}*/

		return html_string; 
	}

	public String getRequestServer(String stop, Boolean formated, Location location, int numStops, int dist)
	{
		String html_string = null; 
		HttpGet m_get = new HttpGet();	    
		try {
			stop = URLEncoder.encode(stop, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//HttpPost m_post= new HttpPost("http://m.atb.no/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=");
		try {
			m_get.setURI(new URI("http://busstjener.idi.ntnu.no/MultiBRISserver/MBServlet?dest="+stop+"&lat="+location.getLatitude()+"&long="+location.getLongitude() + "&type=json&nStops="+numStops +"&maxWalkDist="+dist+"&key=SoapMacTavish"));
			//http://furu.idi.ntnu.no:1337/MultiBRISserver/MBServlet?dest=Ila&type=json&lat=63.4169548&long=10.40284478 n�
			// 			m_get.setURI(new URI("http://ec2-79-125-87-39.eu-west-1.compute.amazonaws.com:8080/MultiBRISserver/MBServlet?dest="+stop+"&type=json&lat="+location.getLatitude()+"&long="+location.getLongitude()));
			HttpResponse m_response = m_client.execute(m_get);
			// Request
			html_string = httpF.requestServer(m_response);			
			// Will fail if server is busy or down
			Log.v("html_string", "Returned html: " + html_string);
			//Long newTime = System.nanoTime() - time;
			//System.out.println("TIMEEEEEEEEEEEEEEEEEEEEE: " +  newTime/1000000000.0);
		} catch (ClientProtocolException e) {
			Log.v("CLIENTPROTOCOL EX", "e:"+e.toString());
		} catch (IOException e) {
			Log.v("IO EX", "e:"+e.toString()); 

		}
		catch(NullPointerException e)
		{
			Log.v("NULL", "NullPointer");
		}
		catch(StringIndexOutOfBoundsException e)
		{
			Log.v("StringIndexOutOfBounds", "Exception");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return html_string; 
	}
	
	
	public StringBuffer getRequestStandard(String buf)
	{		
		StringBuffer html_string = null; 

		Long time = System.nanoTime();
		try {
			HttpPost m_post= new HttpPost("http://busstjener.idi.ntnu.no/bussstuc/oracle?q="+URLEncoder.encode(buf,"UTF-8"));
			HttpResponse m_response = m_client.execute(m_post);
			//Log.v("m_response", inputStreamToString(m_response.getEntity().getContent()));
			System.out.println("Wanted String: " + buf);
			// Request
			html_string = httpF.requestStandard(m_response);

			// Will fail if server is busy or down
			Log.v("html_string", "Returned html: " + html_string);
		} catch (ClientProtocolException e) {
			Log.v("CLIENTPROTOCOL EX", "e:"+e.toString());
		} catch (IOException e) {
			Log.v("IO EX", "e:"+e.toString()); 

		}
		catch(NullPointerException e)
		{
			Log.v("NULL", "NullPointer");
		} 
		catch(StringIndexOutOfBoundsException e)
		{
			Log.v("StringIndexOutOfBounds", "Exception");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		/*	for(int i =0; i< html_string.length;i++)
		{
			Log.v("HTMLFOO", html_string[i]);
		}*/

		return html_string; 
	}
	
	public String[] getRequest(ArrayList<BusStop> startMap, String stop, Boolean formated)
	{		
		String[] html_string = null; 
		DecimalFormat decifo = new DecimalFormat("###");
		String start2 = "(";
		//Object[] keys = startMap.keySet().toArray();
		//Arrays.sort(keys);
		// Name of busstop
		int hSize = startMap.size(); 
		for(int i = 0;i<hSize;i++)
		{
			// Walking distance in minutes
			System.out.println("WALK: " + startMap.get(i).distance);
			int output2 = (int) (Math.ceil(startMap.get(i).distance/1.7)/60);
			start2 = start2 + "" + startMap.get(i).name+""+"+"+output2; 
			System.out.println("START TO SATT: " + start2 + "  " + startMap.get(i).distance);
			if(i+1<hSize)
			{
				start2 = start2 + ","; 
			}
		}
		start2 = start2 + ")";
		String wanted_string = start2 + " til " + stop ; 
		String wanted_string2 = "fra gl�shaugen til nardo";
		Log.v("BUSTUCSTR", "wanted_string:"+wanted_string);
		HttpPost m_post= new HttpPost("http://www.idi.ntnu.no/~tagore/cgi-bin/busstuc/busq.cgi");


		//HttpPost m_post= new HttpPost("http://m.atb.no/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=");
		Long time = System.nanoTime();
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
			nameValuePairs.add(new BasicNameValuePair("lang", "eng"));  
			nameValuePairs.add(new BasicNameValuePair("quest", wanted_string)); 
			UrlEncodedFormEntity url = new UrlEncodedFormEntity(nameValuePairs);	        
			//  System.out.println("URLENC: " + url.toString());
			m_post.setEntity(url);  
			String responseBody = EntityUtils.toString(m_post.getEntity());       
			// Execute. Will not crash if route info is not found(which is not cool)
			//HttpResponse m_response = m_client.execute(m_post);
			HttpResponse m_response = m_client.execute(m_post);
			//Log.v("m_response", inputStreamToString(m_response.getEntity().getContent()));
			System.out.println("Wanted String: " + wanted_string);
			// Request
			html_string = httpF.request(m_response);

			// Will fail if server is busy or down
			Log.v("html_string", "Returned html: " + html_string);
			//Long newTime = System.nanoTime() - time;
			//System.out.println("TIMEEEEEEEEEEEEEEEEEEEEE: " +  newTime/1000000000.0);
		} catch (ClientProtocolException e) {
			Log.v("CLIENTPROTOCOL EX", "e:"+e.toString());
		} catch (IOException e) {
			Log.v("IO EX", "e:"+e.toString()); 

		}
		catch(NullPointerException e)
		{
			Log.v("NULL", "NullPointer");
		}
		catch(StringIndexOutOfBoundsException e)
		{
			Log.v("StringIndexOutOfBounds", "Exception");
		}
		catch(Exception e)
		{
			Log.v("FUCKINGTOLARGE", "Exception");
		}

		/*	for(int i =0; i< html_string.length;i++)
		{
			Log.v("HTMLFOO", html_string[i]);
		}*/

		return html_string; 
	}

	public String[] getRequestString(ArrayList<BusStop> startMap, String stop, Boolean formated, String additional)
	{		
		String[] html_string = null; 
		DecimalFormat decifo = new DecimalFormat("###");
		String start2 = "(";
		//Object[] keys = startMap.keySet().toArray();
		//Arrays.sort(keys);
		// Name of busstop
		int hSize = startMap.size(); 
		for(int i = 0;i<hSize;i++)
		{
			// Walking distance in minutes
			System.out.println("WALK: " + startMap.get(i).distance);
			int output2 = (int) (Math.ceil(startMap.get(i).distance/1.7)/60);
			start2 = start2 + "" + startMap.get(i).name+""+"+"+output2; 
			System.out.println("START TO SATT: " + start2 + "  " + startMap.get(i).distance);
			if(i+1<hSize)
			{
				start2 = start2 + ","; 
			}
		}
		start2 = start2 + ")";
		String wanted_string = start2 + additional ; 
		String wanted_string2 = "fra gl�shaugen til nardo";
		Log.v("BUSTUCSTR", "wanted_string:"+wanted_string);
		HttpPost m_post= new HttpPost("http://www.idi.ntnu.no/~tagore/cgi-bin/busstuc/busq.cgi");
		//HttpPost m_post= new HttpPost("http://m.atb.no/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=");
		Long time = System.nanoTime();
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
			nameValuePairs.add(new BasicNameValuePair("lang", "eng"));  
			nameValuePairs.add(new BasicNameValuePair("quest", wanted_string)); 
			UrlEncodedFormEntity url = new UrlEncodedFormEntity(nameValuePairs);	        
			//  System.out.println("URLENC: " + url.toString());
			m_post.setEntity(url);  
			String responseBody = EntityUtils.toString(m_post.getEntity());       
			// Execute. Will not crash if route info is not found(which is not cool)
			HttpResponse m_response = m_client.execute(m_post);

			//Log.v("m_response", inputStreamToString(m_response.getEntity().getContent()));
			System.out.println("Wanted String: " + wanted_string);
			// Request
			html_string = httpF.request(m_response);

			// Will fail if server is busy or down
			Log.v("html_string", "Returned html: " + html_string);
			//Long newTime = System.nanoTime() - time;
			//System.out.println("TIMEEEEEEEEEEEEEEEEEEEEE: " +  newTime/1000000000.0);
		} catch (ClientProtocolException e) {
			Log.v("CLIENTPROTOCOL EX", "e:"+e.toString());
		} catch (IOException e) {
			Log.v("IO EX", "e:"+e.toString()); 

		}
		catch(NullPointerException e)
		{
			Log.v("NULL", "NullPointer");
		}
		catch(StringIndexOutOfBoundsException e)
		{
			Log.v("StringIndexOutOfBounds", "Exception");
		}
		catch(Exception e)
		{
			Log.v("FUCKINGTOLARGE", "Exception");
		}

		/*	for(int i =0; i< html_string.length;i++)
		{
			Log.v("HTMLFOO", html_string[i]);
		}*/

		return html_string; 
	}
	public HashMap <Integer,Integer> realTimeData()
	{ 
		final StringBuffer soap = new StringBuffer();
		soap.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		soap.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		soap.append("<soap:Body>");
		soap.append("<GetBusStopsList xmlns=\"http://miz.it/infotransit\">");    
		soap.append("<auth>");
		soap.append("<user>Lingit</user>");
		soap.append("<password>t1gn1l</password>");
		soap.append("</auth>");
		soap.append("</GetBusStopsList>");
		soap.append("</soap:Body>");
		soap.append("</soap:Envelope>");
		soap.append("");
		String str1 = sendSoapRequest("http://195.0.188.74/InfoTransit/userservices.asmx?op=GetBusStopsList",soap.toString());
		int code = 0;  
		HashMap <Integer,Integer> realT = new HashMap <Integer,Integer>();
		try {
			//	ArrayList<BusStops> test = parseRealTimeData(str1);
			realT  = getRealTimeCode(str1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return realT; 
	}
	public static String sendSoapRequest(String postHeader, String soapMessage)
	{
		byte[] result = null; 
		String soap = soapMessage; 
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 50000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 50000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters); 
		HttpPost httppost = new HttpPost(postHeader);
		httppost.setHeader("Content-Type", "text/xml; charset=utf-8");

		try {
			HttpEntity entity = new StringEntity(soap,HTTP.UTF_8);
			httppost.setEntity(entity);  
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity r_entity = response.getEntity();  

			Header[] headers = response.getAllHeaders();
			//           for(Header h:headers){
			//    Log.v("Reponse Header",h.getName() + ": " + h.getValue());
			//         }  
			if (r_entity != null) {        
				result = new byte[(int) r_entity.getContentLength()];  // read the output message
				if (r_entity.isStreaming()) {
					DataInputStream is = new DataInputStream(
							r_entity.getContent());
					is.readFully(result);
				}
			}
		} catch (Exception E) {
			Log.v("Exception While Connecting", ""+E.getMessage());
			E.printStackTrace();
		}

		httpclient.getConnectionManager().shutdown(); //shut down the connection
		//  return result;
		//Log.v("lengt","l:"+result.length);
		String str1 = new String(result);
		Log.v("string",str1);
		return str1; 
	}
	public BusDeparture specificRequest(int k_RealTimeId, int k_specifiedLine)
	{
		int realTimeId = k_RealTimeId;   
		int specifiedLine = k_specifiedLine;
		HttpPost httppost = new HttpPost("http://195.0.188.74/InfoTransit/userservices.asmx?op=getUserRealTimeForecast");
		httppost.setHeader("Content-Type", "text/xml; charset=utf-8");
		final StringBuffer soap = new StringBuffer();
		soap.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		soap.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		soap.append("<soap:Body>");
		soap.append("<getUserRealTimeForecast xmlns=\"http://miz.it/infotransit\">");
		soap.append("<auth>");
		soap.append("<user>Lingit</user>");
		soap.append("<password>t1gn1l</password>");
		soap.append("</auth>");
		soap.append("<busStopId>"+realTimeId+"</busStopId>");
		soap.append("</getUserRealTimeForecast>");
		soap.append("</soap:Body>");
		soap.append("</soap:Envelope>");
		soap.append("");
		String str1 = sendSoapRequest("http://195.0.188.74/InfoTransit/userservices.asmx?op=getUserRealTimeForecast", soap.toString());
		System.out.println("SOAP: " + soap);
		System.out.println("Str1:" +str1);
		BusDeparture test = null; 
		try {
			test = parseRealTimeData(str1,specifiedLine);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//    System.out.println("Arrivaltime in request: " + test.getArrivalTime().getHours() + "   " + test.getArrivalTime().getMinutes());
		return test; 
	}
	public HashMap<Integer, Integer> getRealTimeCode(String data) throws ParseException, JSONException, java.text.ParseException
	{
		int realTimeCode = 0;
		HashMap<Integer, Integer> realTimeNumbers = new HashMap <Integer,Integer>();
		Pattern p = Pattern.compile(
				"<GetBusStopsListResult>(.*?)</GetBusStopsListResult>",
				Pattern.DOTALL | Pattern.CASE_INSENSITIVE
		);

		Matcher matcher = p.matcher(data);
		String result = null;
		while(matcher.find()){
			result = (matcher.group(1));
		}
		JSONObject j_o = null;
		JSONArray j_a = null;
		Log.v("jsonObj",result);
		j_o = new JSONObject(result);
		j_a = new JSONArray(j_o.getString("Fermate"));
		Log.v("arrayLenght","length:"+j_a.length());
		if (j_a != null){
			for (int i = 0; i < j_a.length(); i++){
				int realTimeInt = Integer.parseInt(j_a.getJSONObject(i).getString("cinFermata"));
				int mobileCode = Integer.parseInt(j_a.getJSONObject(i).getString("codAzNodo"));
				//       Log.v("Busstop", "ID:"+mobileCode+" RID:"+realTimeInt);
				realTimeNumbers.put(mobileCode, new Integer(realTimeInt));
			}
		}
		//       int check = 16011721; 
		//      Log.v("Gl�shaugen","gs:"+realTimeNumbers.get(check));
		//      realTimeCode = (Integer)realTimeNumbers.get(m_wantedBus);
		return realTimeNumbers; 
	}

	public static ArrayList <BusDeparture> specificRequestForStop(int k_RealTimeId)
	{
		int realTimeId = k_RealTimeId;  
		System.out.println("REAL-TIME ID RECEIVED: " + realTimeId);
		HttpPost httppost = new HttpPost("http://195.0.188.74/InfoTransit/userservices.asmx?op=getUserRealTimeForecast");
		httppost.setHeader("Content-Type", "text/xml; charset=utf-8");
		final StringBuffer soap = new StringBuffer();
		soap.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		soap.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		soap.append("<soap:Body>");
		soap.append("<getUserRealTimeForecast xmlns=\"http://miz.it/infotransit\">");
		soap.append("<auth>");
		soap.append("<user>Lingit</user>");
		soap.append("<password>t1gn1l</password>");
		soap.append("</auth>");
		soap.append("<busStopId>"+realTimeId+"</busStopId>");
		soap.append("</getUserRealTimeForecast>");
		soap.append("</soap:Body>");
		soap.append("</soap:Envelope>");
		soap.append("");
		String str1 = sendSoapRequest("http://195.0.188.74/InfoTransit/userservices.asmx?op=getUserRealTimeForecast", soap.toString());
		ArrayList <BusDeparture> test = null; 
		try {
			System.out.println("Soap: " +soap);
			System.out.println("Str: " +str1);
			test = parseRealTimeDataForStop(str1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return test; 
	}

	public static ArrayList <BusDeparture> specificRequestForStopServer(int k_RealTimeId)
	{
		int realTimeId = k_RealTimeId;  
		String html_string = null; 
		HttpGet m_get = new HttpGet();	    
		//HttpPost m_post= new HttpPost("http://m.atb.no/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=");
		try {
			m_get.setURI(new URI("http://busstjener.idi.ntnu.no/MultiBRISserver/RealTime?bID=" + realTimeId +"&key=SoapMacTavish"));
			//http://furu.idi.ntnu.no:1337/MultiBRISserver/MBServlet?dest=Ila&type=json&lat=63.4169548&long=10.40284478 n�
			// 			m_get.setURI(new URI("http://ec2-79-125-87-39.eu-west-1.compute.amazonaws.com:8080/MultiBRISserver/MBServlet?dest="+stop+"&type=json&lat="+location.getLatitude()+"&long="+location.getLongitude()));
			HttpResponse m_response = m_client.execute(m_get);
			// Request
			html_string = httpF.requestServer(m_response);			
			// Will fail if server is busy or down
			Log.v("html_string", "Returned html: " + html_string);
			//Long newTime = System.nanoTime() - time;
			//System.out.println("TIMEEEEEEEEEEEEEEEEEEEEE: " +  newTime/1000000000.0);
		} catch (ClientProtocolException e) {
			Log.v("CLIENTPROTOCOL EX", "e:"+e.toString());
		} catch (IOException e) {
			Log.v("IO EX", "e:"+e.toString()); 

		}
		catch(NullPointerException e)
		{
			Log.v("NULL", "NullPointer");
		}
		catch(StringIndexOutOfBoundsException e)
		{
			Log.v("StringIndexOutOfBounds", "Exception");
		}
		catch(Exception e)
		{
			Log.v("FUCKINGTOLARGE", "Exception");
		}

		ArrayList <BusDeparture> test = null; 
		try {

			test = parseRealTimeDataForStopServer(html_string.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(NullPointerException e)
		{
			e.printStackTrace();
		}
		return test; 
	}
	@SuppressWarnings("unused")
	public static ArrayList <BusDeparture> parseRealTimeDataForStopServer(String data) throws JSONException, java.text.ParseException
	{
		ArrayList <BusDeparture> buses = new ArrayList<BusDeparture>();

		JSONObject j_o = null; 
		JSONArray j_a = null;

		j_o = new JSONObject(data);
		j_a = new JSONArray(j_o.getString("bussStops"));
		System.out.println("J_a length: " + j_a.length());
		BusDeparture wantedBusStop = new BusDeparture(); 
		wantedBusStop.setLine(9999);
		if (j_a != null){
			try
			{
				for (int i = 0; i < j_a.length(); i++)
				{

					BusDeparture t = new BusDeparture();
					t.line = j_a.getJSONObject(i).getInt("line");
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
					Date date = (Date)formatter.parse(j_a.getJSONObject(i).getString("arrivalTime"));
					t.arrivalTime = date;
					System.out.println("FOUND HOURS: " + t.arrivalTime.getHours());
					t.dest = j_a.getJSONObject(i).getString("dest");
					t.realTime = Boolean.parseBoolean(j_a.getJSONObject(i).getString("realTime"));
				
					wantedBusStop = t;
					buses.add(t);
				}
			}
			catch(JSONException e)
			{
				System.out.println("FAAAAAAAAAAAIL");
				e.printStackTrace();
			}


		}

		else
		{
			System.out.println("Could not find property in Browser");
		}

		return buses;
	}

	@SuppressWarnings("unused")
	public static ArrayList <BusDeparture> parseRealTimeDataForStop(String data) throws JSONException, java.text.ParseException
	{
		ArrayList <BusDeparture> buses = new ArrayList<BusDeparture>();
		Pattern p = Pattern.compile(
				"<getUserRealTimeForecastResult>(.*?)</getUserRealTimeForecastResult>",
				Pattern.DOTALL | Pattern.CASE_INSENSITIVE
		);
		Matcher matcher = p.matcher(data);
		String result = null;
		while(matcher.find()){
			result = (matcher.group(1));
			System.out.println("Result from soap: " + result);
		}
		JSONObject j_o = null; 
		JSONArray j_a = null;

		j_o = new JSONObject(result);
		j_a = new JSONArray(j_o.getString("Orari"));
		System.out.println("J_a length: " + j_a.length());
		BusDeparture wantedBusStop = new BusDeparture(); 
		wantedBusStop.setLine(9999);
		if (j_a != null){
			try
			{
				for (int i = 0; i < j_a.length(); i++)
				{

					BusDeparture t = new BusDeparture();
					t.line = j_a.getJSONObject(i).getInt("descrizioneLinea");
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm"); 
					Date date = (Date)formatter.parse(j_a.getJSONObject(i).getString("orario"));
					t.arrivalTime = date;
					System.out.println("FOUND HOURS: " + t.arrivalTime.getHours());
					t.dest = j_a.getJSONObject(i).getString("capDest");
					String prev = j_a.getJSONObject(i).getString("statoPrevisione");

					if (prev.equals("Prev") || prev.equals("prev"))
					{
						t.realTime = true;
					}
					else if (prev.equals("sched"))
					{
						t.realTime = false;
					}

					//       Log.d("line",String.valueOf(t.line));
					//       Log.d("arrivalTime",String.valueOf(t.arrivalTime));
					//       Log.d("ATB", t.toString());
					//  if(wantedBusStop.getLine() == 9999)
					//{
					wantedBusStop = t;
					//}
					buses.add(t);
				}
			}
			catch(JSONException e)
			{
				System.out.println("FAAAAAAAAAAAIL");
				e.printStackTrace();
			}


		}

		else
		{
			System.out.println("Could not find property in Browser");
		}

		return buses;
	}

	public BusDeparture parseRealTimeData(String data, int m_speciLine) {
		Pattern p = Pattern.compile(
				"<getUserRealTimeForecastResult>(.*?)</getUserRealTimeForecastResult>",
				Pattern.DOTALL | Pattern.CASE_INSENSITIVE
		);
		int wantedLine = m_speciLine; 
		Matcher matcher = p.matcher(data);
		String result = null;
		while(matcher.find()){
			result = (matcher.group(1));
			System.out.println("Result from soap: " + result);

		}
		JSONObject j_o = null;
		JSONArray j_a = null;


		try {
			j_o = new JSONObject(result);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			j_a = new JSONArray(j_o.getString("Orari"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BusDeparture wantedBusStop = new BusDeparture(); 
		wantedBusStop.setLine(9999);
		BusDeparture t = new BusDeparture();
		boolean foundWantedLine = false;
		if (j_a != null)
		{
			try
			{
				
				//System.out.println("len p� array: " + j_a.length());
				for (int i = 0; i < j_a.length(); i++){
					//System.out.println("In for-loop j_a");
					t = new BusDeparture();
				
					t.line = j_a.getJSONObject(i).getInt("descrizioneLinea");
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm"); 
					Date date = new Date();
					try {
						date = (Date)formatter.parse(j_a.getJSONObject(i).getString("orario"));
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					t.arrivalTime = date;
					// System.out.println("Date: " + t.arrivalTime.toString() + " line: " + t.line);
					String prev = j_a.getJSONObject(i).getString("statoPrevisione");
				
					if (prev.equalsIgnoreCase("prev")){
						t.realTime = true;
					}
					else if (prev.equals("sched")){
						t.realTime = false;
					}

					//       Log.d("line",String.valueOf(t.line));
					//       Log.d("arrivalTime",String.valueOf(t.arrivalTime));
					//       Log.d("ATB", t.toString());

					// If the SOAP contains the line we want, return it
					// Else break
					if(t.line == wantedLine && wantedBusStop.getLine() == 9999)
					{
						//System.out.println("IF Wanted line: " +wantedLine + " t.line: " + t.line);
						foundWantedLine = true;
						wantedBusStop = t;
						System.out.println("FOUND WANTED LINE: " + t.line);
					}

				}
			}
			catch(JSONException e)
			{
				System.out.println("FAAAAAAAAAAAIL");
				e.printStackTrace();
			}


		}

		//  System.out.println("Returning unmatched: " + t.getLine() + "  " + t.getLine());
		if(foundWantedLine)
		{
			return wantedBusStop;
		}
		else return t;
	}




}


