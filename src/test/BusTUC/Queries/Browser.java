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
import test.BusTUC.Main.Helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ParseException;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

public class Browser {
	WebView web;
	static HttpClient m_client;
	static HttpFormat httpF;

	public Browser() {
		m_client = new DefaultHttpClient();
		httpF = new HttpFormat();

	}

	public String getRequestServer(String stop, Boolean formated,
			Location location, int numStops, int dist, Context context) {
		String html_string = null;
		HttpGet m_get = new HttpGet();
		try {
			stop = URLEncoder.encode(stop, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// HttpPost m_post= new
		// HttpPost("http://m.atb.no/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=");
		try {
			final TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String t_id = tm.getDeviceId();
			String tmp = "TABuss";
			String p_id = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);
			m_get.setURI(new URI(
					"http://busstjener.idi.ntnu.no/MultiBRISserver/MBServlet?dest="
							+ stop + "&lat=" + location.getLatitude()
							+ "&long=" + location.getLongitude()
							+ "&type=json&nStops=" + numStops + "&maxWalkDist="
							+ dist + "&key=" + tmp + p_id));
			HttpResponse m_response = m_client.execute(m_get);
			// Request
			html_string = httpF.requestServer(m_response);
			// Will fail if server is busy or down
			Log.v("html_string", "Returned html: " + html_string);
			// Long newTime = System.nanoTime() - time;
			// System.out.println("TIMEEEEEEEEEEEEEEEEEEEEE: " +
			// newTime/1000000000.0);
		} catch (ClientProtocolException e) {
			Log.v("CLIENTPROTOCOL EX", "e:" + e.toString());
		} catch (IOException e) {
			Log.v("IO EX", "e:" + e.toString());

		} catch (NullPointerException e) {
			Log.v("NULL", "NullPointer");
		} catch (StringIndexOutOfBoundsException e) {
			Log.v("StringIndexOutOfBounds", "Exception");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return html_string;
	}

	public StringBuffer getRequestStandard(String buf) {
		StringBuffer html_string = null;

		Long time = System.nanoTime();
		try {
			HttpPost m_post = new HttpPost(
					"http://busstjener.idi.ntnu.no/busstuc/oracle?q="
							+ URLEncoder.encode(buf, "UTF-8"));
			HttpResponse m_response = m_client.execute(m_post);
			System.out.println("Wanted String: " + buf);
			// Request
			html_string = httpF.requestStandard(m_response);

			// Will fail if server is busy or down
			Log.v("html_string", "Returned html: " + html_string);
		} catch (ClientProtocolException e) {
			Log.v("CLIENTPROTOCOL EX", "e:" + e.toString());
		} catch (IOException e) {
			Log.v("IO EX", "e:" + e.toString());

		} catch (NullPointerException e) {
			Log.v("NULL", "NullPointer");
		} catch (StringIndexOutOfBoundsException e) {
			Log.v("StringIndexOutOfBounds", "Exception");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html_string;
	}

	public static ArrayList<BusDeparture> specificRequestForStopServer(
			int k_RealTimeId) {
		int realTimeId = k_RealTimeId;
		String html_string = null;
		HttpGet m_get = new HttpGet();
		ArrayList<BusDeparture> test = null;
		if(m_client == null)
		{
			m_client = new DefaultHttpClient();

		}
		if(httpF == null)
		{
			httpF = new HttpFormat();

		}
		try {
			m_get.setURI(new URI(
					"http://busstjener.idi.ntnu.no/MultiBRISserver/RealTime?bID="
							+ realTimeId + "&key=SoapMacTavish"));
			System.out.println("SENDING REQUEST " + realTimeId);

			HttpResponse m_response = m_client.execute(m_get);
			System.out.println("RESPONSE RECEIVED");
			// Request
			html_string = httpF.requestServer(m_response);
			// Will fail if server is busy or down
			Log.v("html_string", "Returned html: " + html_string);			

			test = parseRealTimeDataForStopServer(html_string.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return test;
	}

	@SuppressWarnings({ "unused", "deprecation" })
	public static ArrayList<BusDeparture> parseRealTimeDataForStopServer(
			String data) throws JSONException, java.text.ParseException {
		ArrayList<BusDeparture> buses = new ArrayList<BusDeparture>();

		JSONObject j_o = null;
		JSONArray j_a = null;

		j_o = new JSONObject(data);
		j_a = new JSONArray(j_o.getString("bussStops"));
		System.out.println("J_a length: " + j_a.length());
		BusDeparture wantedBusStop = new BusDeparture();
		wantedBusStop.setLine(9999);
		if (j_a != null) {
			try {
				for (int i = 0; i < j_a.length(); i++) {

					BusDeparture t = new BusDeparture();
					t.line = j_a.getJSONObject(i).getInt("line");
					SimpleDateFormat formatter = new SimpleDateFormat(
							"dd/MM/yyyy HH:mm:ss");
					Date date = (Date) formatter.parse(j_a.getJSONObject(i)
							.getString("arrivalTime"));
					t.arrivalTime = date;
					System.out.println("FOUND HOURS: "
							+ t.arrivalTime.getHours());
					t.dest = j_a.getJSONObject(i).getString("dest");
					t.realTime = Boolean.parseBoolean(j_a.getJSONObject(i)
							.getString("realTime"));

					wantedBusStop = t;
					buses.add(t);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		else {
			System.out.println("Could not find property in Browser");
		}

		return buses;
	}

}
