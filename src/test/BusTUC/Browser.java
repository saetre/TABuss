package test.BusTUC; 

import java.io.DataInputStream;
import java.io.IOException;

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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.net.ParseException;
import android.util.Log;
import android.webkit.WebView;

public class Browser
{
	WebView web;
	HttpClient m_client; 
	HttpFormat httpF; 
	Browser()
	{
		m_client = new DefaultHttpClient();
		httpF = new HttpFormat(); 
	}

	String[] getRequest(HashMap<Integer,Location> startMap, String stop, Boolean formated)
	{		
		String[] html_string = null; 
		Log.v("BUSTUCS","startmapsize:"+startMap.size());
		DecimalFormat decifo = new DecimalFormat("###");
		String wantedStart = ""; 
		String start2 = "(";
		Object[] keys = startMap.keySet().toArray();
		Arrays.sort(keys);
		wantedStart = startMap.get(keys[0]).getProvider();
		int hSize = startMap.keySet().size(); 
        for(int i = 0;i<hSize;i++)
        {
           String output2 = decifo.format(Math.ceil((Double.parseDouble(keys[i].toString())/1.7)/60));
     	   start2 = start2 + "" + startMap.get(keys[i]).getProvider()+""+"+"+output2; 
     	   if(i+1<hSize)
     	   {
     		   start2 = start2 + ","; 
     	   }
        }
    	start2 = start2 + ")";
		String wanted_string = start2 + " til " + stop; 
		String wanted_string2 = "fra gl�shaugen til nardo";
		Log.v("BUSTUCSTR", "wanted_string:"+wanted_string);
		HttpPost m_post= new HttpPost("http://www.idi.ntnu.no/~tagore/cgi-bin/busstuc/busq.cgi");
		Long time = System.nanoTime();
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
	        nameValuePairs.add(new BasicNameValuePair("lang", "eng"));  
	        nameValuePairs.add(new BasicNameValuePair("quest", wanted_string)); 
	        m_post.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
	        
			HttpResponse m_response = m_client.execute(m_post);
			html_string = httpF.request(m_response);
		} catch (ClientProtocolException e) {
			Log.v("CLIENTPROTOCOL EX", "e:"+e.toString());
		} catch (IOException e) {
			Log.v("IO EX", "e:"+e.toString());
		}
		Long newTime = System.nanoTime() - time;
		System.out.println("TIMEEEEEEEEEEEEEEEEEEEEE: " +  newTime/1000000000.0);
		return html_string; 
	}
	
	public HashMap realTimeData()
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
	        HashMap realT = new HashMap();
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
	public String sendSoapRequest(String postHeader, String soapMessage)
	{
			byte[] result = null; 
			String soap = soapMessage; 
	        HttpParams httpParameters = new BasicHttpParams();
	        int timeoutConnection = 15000;
	        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	        int timeoutSocket = 35000;
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
	        Log.v("lengt","l:"+result.length);
	        String str1 = new String(result);
	        Log.v("string",str1);
	        return str1; 
	}
	public BusStops specificRequest(int k_RealTimeId, int k_specifiedLine)
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
	        BusStops test = null; 
	        try {
			test = parseRealTimeData(str1,specifiedLine);
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
	public HashMap getRealTimeCode(String data) throws ParseException, JSONException, java.text.ParseException
	{
		int realTimeCode = 0;
		HashMap realTimeNumbers = new HashMap();
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
	         //     Log.v("Busstop", "ID:"+mobileCode+" RID:"+realTimeInt);
	              realTimeNumbers.put(mobileCode, new Integer(realTimeInt));
	            }
	        }
	 //       int check = 16011721; 
	  //      Log.v("Gl�shaugen","gs:"+realTimeNumbers.get(check));
	  //      realTimeCode = (Integer)realTimeNumbers.get(m_wantedBus);
		return realTimeNumbers; 
	}
	public BusStops parseRealTimeData(String data, int m_speciLine) throws ParseException, JSONException, java.text.ParseException{
        Pattern p = Pattern.compile(
                "<getUserRealTimeForecastResult>(.*?)</getUserRealTimeForecastResult>",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
            );
        int wantedLine = m_speciLine; 
        Matcher matcher = p.matcher(data);
        String result = null;
        while(matcher.find()){
                result = (matcher.group(1));
        }
        JSONObject j_o = null;
        JSONArray j_a = null;
        
        j_o = new JSONObject(result);
        j_a = new JSONArray(j_o.getString("Orari"));
        BusStops wantedBusStop = new BusStops(); 
        wantedBusStop.setLine(9999);
        if (j_a != null){
        	try
        	{
	            for (int i = 0; i < j_a.length(); i++){
	                
	                BusStops t = new BusStops();
	                t.line = j_a.getJSONObject(i).getInt("codAzLinea");
	                SimpleDateFormat formatter = new SimpleDateFormat("d/M/y H:mm"); 
	                Date date = (Date)formatter.parse(j_a.getJSONObject(i).getString("orario"));
	                t.arrivalTime = date;
	                String prev = j_a.getJSONObject(i).getString("statoPrevisione");
	                
	                if (prev.equals("Prev") || prev.equals("prev")){
	                    t.realTime = true;
	                }
	                else if (prev.equals("sched")){
	                    t.realTime = false;
	                }
	                
	         //       Log.d("line",String.valueOf(t.line));
	         //       Log.d("arrivalTime",String.valueOf(t.arrivalTime));
	         //       Log.d("ATB", t.toString());
	                if(t.line == wantedLine && wantedBusStop.getLine() == 9999)
	                {
	                  	wantedBusStop = t;
	                }
	            }
        	}
            catch(JSONException e)
            {
            	System.out.println("FAAAAAAAAAAAIL");
            	e.printStackTrace();
            }
        
            
        }
       
        return wantedBusStop;
	}
}


