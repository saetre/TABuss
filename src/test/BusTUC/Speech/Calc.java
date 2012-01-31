package test.BusTUC.Speech;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Calc
{
	
	
	
	
	public DummyObj parse(String jsonString)
	{
		DummyObj dummy = new DummyObj();
		JSONObject json_obj;
		try
		{
			json_obj = new JSONObject(jsonString);
			dummy.setAnswer(json_obj.getString("theAnswer"));
			dummy.setSoundInfo(json_obj.getString("soundInfo"));
		} catch (JSONException e)
		{
			e.printStackTrace();
		} catch (NullPointerException ex)
		{
			ex.printStackTrace();
		}
		return dummy;
	}

}
